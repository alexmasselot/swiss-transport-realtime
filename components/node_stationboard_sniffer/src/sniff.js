/**
 *
 * make a call every to catch swiss train locations and send it to kafka broker
 *
 * all in promises
 */
'use strict';

var rp = require('request-promise');
var _ = require('lodash');
var Promise = require('promise');
var util = require('util');
var encoding = require("encoding");
var utf8 = require('utf8');
var csv = require('fast-csv');
var kafka = require('kafka-node');
var sprintf = require('sprintf');
var fs = require('fs')

var kafkaHost = process.env.KAFKA_HOST;
var kafkaPort = process.env.KAFKA_PORT || '2181';
var transportationsTypes = ["ice_tgv_rj", "ec_ic", "ir", "re_d", "ship", "s_sn_r"].join("&transportations[]=")
var stationBoardUrl = "http://transport.opendata.ch/v1/stationboard?id=%s&transportations[]=" + transportationsTypes
console.log(stationBoardUrl);
//300 per minutes => juste en dessous = 280
var openDataInterval = 60000 / 280;
var maxConcurrentOpenDataRequest = 5;
var kafkaStopTopic = 'cff_stop';

var client = new kafka.Client(kafkaHost + ':' + kafkaPort);
var producer = new kafka.Producer(client);

if (!kafkaHost) {
    console.error('no environment variable KAFKA_HOST passed');
    process.exit(1);
}

var errorLog = function (err) {
    console.error(err);
    process.exit(2);
}

var initKafkaClient = function () {
    return new Promise(function (resolve, reject) {
        var tentative = 0;
        var intervalId = setInterval(function () {
            if (client.ready) {
                console.log("Client ready !");
                clearInterval(intervalId);
                resolve(client);
            } else {
                if (tentative > 10) {
                    clearInterval(intervalId);
                    reject("Kafka failure, too many attemps...");
                }
                tentative = tentative + 1;
                console.log("Wait for Kafka client initalisation (try=" + tentative + ")...");
            }
        }, 1000);

    });
}

var loadStops = function (type) {
    return new Promise(function (resolve, reject) {
        var stream = fs.createReadStream("data/" + type + "_stops.txt");
        var stops = [];
        var csvStream = csv({headers: true, objectMode: true})
            .on("data", function (data) {
                if (data.stop_id.indexOf(":") === -1) {
                    data.type_of_stop = type;
                    stops.push(data);
                }
            })
            .on("end", function () {
                console.log("LOADED " + stops.length + " " + " stops");
                resolve(stops);
            })
            .on('error', function (e) {
                reject(e)
            });
        stream.pipe(csvStream);
    });
};

var loop = function (list, delay, callback) {
    var i = 0, j = 0;
    var n = list.length;
    var nb_request = 0, totalStops = 0, miss = 0, lastStops = 0;
    var lastLoop = Date.now()

    var finished = function (nbStops) {
        nb_request = nb_request - 1;
        totalStops = nbStops;
        if (lastStops === 0) lastStops = totalStops;
    }

    setInterval(function () {
        if (nb_request < maxConcurrentOpenDataRequest) {
            nb_request = nb_request + 1;
            callback(list[i], finished);
            i += 1;
            j += 1;
            if (i === n) {
                i = 0;
                console.log(new Date() + " Process " + n + " stations, restart form start, cumulated stops=" + totalStops);
            }
            if (j == 200) {
                console.log(new Date() + " Process " + 200 + " stations, " + (totalStops - lastStops) + " stops in "
                    + (Date.now() - lastLoop) / 1000 + "s, missRequest: " + miss + " cumulated stops=" + totalStops);
                lastStops = totalStops;
                totalStops = 0;
                miss = 0;
                j = 0;
            }
        } else {
            miss = miss + 1;
            //console.info("Previous request(s) not finished, skip this time, current = " + nb_request);
        }
    }, delay);
};

var getDashboard = function (stopId) {
    var tstamp = Date.now();
    return rp({
        uri: sprintf(stationBoardUrl, stopId),
        json: true,
        encoding: 'binary'
    }).then(function (data) {
        return _.map(data.stationboard, function (t) {
            t.timeStamp = tstamp;
            return t;
        });
    });
};


var produce = function (messages) {
    if (!_.isArray(messages)) {
        messages = [messages];
    }

    messages = _.map(messages, function (m) {
        return JSON.stringify(m);
    });
    return new Promise(function (resolve, reject) {
        producer.send(
            [{topic: kafkaStopTopic, messages: messages}]
            , function (err, ack) {
                if (err) {
                    reject(err);
                }
                //console.log(new Date(), 'produced', JSON.stringify(ack));
                resolve(ack.cff_stop["0"]);
            }
        )
    });
};

initKafkaClient()
    .then(function () {
        return Promise.all([loadStops('train'), loadStops('ferry')])
    })
    .then(function (all) {
        var concated = [];
        _.each(all, function (a) {
            concated = concated.concat(a);
        })
        return concated;
    })
    .then(function (stops) {
        console.log('All stops concatenated', stops.length)
        return stops;
    })
    .then(function (allStops) {
        loop(allStops, openDataInterval, function (stop, callback) {
            getDashboard(stop.stop_id).
            then(produce).
            then(callback).
            catch(function (err) {
                callback();
                errorLog(err);
            })
        });
    })
    .catch(errorLog);

