"use strict";
/**
 *
 * cycle through station baord and publish to kafka
 */
'use strict';

var rp = require('request-promise');
var _ = require('lodash');
var Promise = require('promise');
var util = require('util');
var encoding = require("encoding");
var utf8 = require('utf8');
var csv = require('fast-csv');
var sprintf = require('sprintf');
var fs = require('fs')

var StationBoardSniffer = function (kafkaClient, options) {
    var _this = this;

    options = _.assign({
        kafkaTopic: 'cffstationboard',
        mode: 'PROD',
        transportationsTypes: ["ice_tgv_rj", "ec_ic", "ir", "re_d", "ship", "s_sn_r"],
        stopTypes: ['train', 'ferry'],
        requestPerMinute: 280
    }, options);

    _this.kafkaClient = kafkaClient;
    _this.kafkaTopic = options.kafkaTopic || 'cff_stop';
    _this.transportationsTypes = options.transportationsTypes;

    _this.stationBoardUrl = "http://transport.opendata.ch/v1/stationboard?id=%s&transportations[]=" + _this.transportationsTypes.join("&transportations[]=");
    //300 per minutes => juste en dessous = 280
    _this.interval = 60000 / options.requestPerMinute;
    _this.maxConcurrentOpenDataRequest = 5;
    _this.stopTypes = options.stopTypes;

    console.info('StationBoardSniffer: kafkaTopic=', _this.kafkaTopic, ' delay (ms)=', _this.interval, 'transportationsTypes=', options.transportationsTypes);
    return _this;
};

/**
 * return the list of stops
 * @returns a promise of an array of all concatenated stops
 */
StationBoardSniffer.prototype.loadStops = function () {
    var _this = this;

    return Promise.all(
        _.map(_this.stopTypes, function (type) {
            return new Promise(
                function (resolve, reject) {
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
                            console.log("stops " + type + ': ' + stops.length);
                            resolve(stops);
                        })
                        .on('error', function (e) {
                            reject(e)
                        });
                    stream.pipe(csvStream);
                })
        })
    ).then(function (all) {
        var concated = [];
        _.each(all, function (a) {
            concated = concated.concat(a);
        });
        console.info('All stops concatenated', concated.length);
        return concated;
    });
};

/**
 *
 * @param stopId
 * @returns {*}
 */
StationBoardSniffer.prototype.getDashboard = function (stopId) {
    var _this = this;
    var tstamp = Date.now();
    var uri = sprintf(_this.stationBoardUrl, stopId);
    return rp({
        uri: uri,
        json: true,
        encoding: 'binary'
    }).then(function (data) {
        return _.map(data.stationboard, function (t) {
            t.timeStamp = tstamp;
            //console.log(t.passList.length);
            if (t.passList.length > 1) {
                t.nextStation = t.passList[0]
                delete(t.passList);
            }
            return t;
        });
    });
};

StationBoardSniffer.prototype.loop = function () {
    var _this = this;
    var loopHandler = function (list, delay, callback) {
        var i = 0, j = 0;
        var n = list.length;
        var nb_request = 0, totalStops = 0, miss = 0, lastStops = 0;
        var lastLoop = Date.now();

        var finished = function (nbStops) {
            nb_request = nb_request - 1;
            totalStops = nbStops;
            if (lastStops === 0) lastStops = totalStops;
        };

        setInterval(function () {
            if (nb_request < _this.maxConcurrentOpenDataRequest) {
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
                    miss = 0;
                    j = 0;
                }
            } else {
                miss = miss + 1;
                //console.info("Previous request(s) not finished, skip this time, current = " + nb_request);
            }
        }, delay);
    };

    _this.loadStops()
        .then(function (stops) {
            loopHandler(stops, _this.interval, function (stop, callback) {
                _this.getDashboard(stop.stop_id).then(function (messages) {
                    return _this.kafkaClient.produce(_this.kafkaTopic, messages);
                }).then(callback).catch(function (err) {
                    if (err.name) {
                        console.log('StationBoardSniffer: Error in loadStops: ' + err.name + ' / ' + err.message);
                    } else {
                        console.error('StationBoardSniffer: Error in loadStops', err);
                    }
                    callback();
                })
            });
        });
    return _this;
};

module.exports = StationBoardSniffer;