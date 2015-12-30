/**
 *
 * make a call every to catch swiss train locations and send it to kafka broker
 *
 * all in promises
 */

var rp = require('request-promise');
var _ = require('lodash');
var Promise = require('promise');
var util = require('util');
var encoding = require("encoding");
var utf8 = require('utf8');

var kafka = require('kafka-node');

var kafkaHost = process.env.KAFKA_HOST;
var kafkaPort = process.env.KAFKA_PORT || '2181';

if(! kafkaHost){
    console.error('no environment variable KAFKA_HOST passed');
    process.exit(1);
}


var client = new kafka.Client(kafkaHost+':'+kafkaPort);
var producer = new kafka.Producer(client);

var kafkaTopic = 'cff_train_position';
var urlFNY = 'http://fahrplan.sbb.ch/bin/query.exe/fny?look_minx=5850000&look_maxx=10540000&look_miny=45850000&look_maxy=47800000&performLocating=1&performFixedLocating=7';

if(process.env.MODE === 'DEV'){
    urlFNY = 'http://fahrplan.sbb.ch/bin/query.exe/fny?look_minx=6385532.065906713&look_maxx=6884036.704578587&look_miny=46441434.48378557&look_maxy=46653942.475391746&performLocating=1&performFixedLocating=7&';
    console.log("MODE=DEV")
} else {
    console.log("MODE=PRODUCTION")
}

var produce = function (messages) {
    if (!_.isArray(messages)) {
        messages = [messages];
    }

    messages = _.map(messages, function (m) {
        return JSON.stringify(m);
    });
    return new Promise(function (resolve, reject) {
        producer.send(
            [{topic: kafkaTopic, messages: messages}]
            , function (err, data) {
                if (err) {
                    reject(err);
                }
                resolve(data);
            }
        )
        ;
    });
};

var getFNY = function () {
    var tstamp = Date.now();
    return rp({
        uri: urlFNY,
        json: true,
        encoding:'binary'
    }).then(function (data) {
        return _.map(data.look.trains, function(t){
            t.timeStamp=tstamp;
            return t;
        });
    });
};

var pushRandom = function () {
    produce([1, 2, 3]);
};


producer.on('ready', function () {
    var doIt = function () {
        getFNY().then(produce)
            .then(function (ack) {
                console.log(new Date(), 'produced', ack);
            })
            .catch(function (error) {
                console.error('ERROR pipe', error);
                console.error(util.inspect(error));
            });
    };
    doIt();
    setInterval(doIt, 20*1000);

}).
on('error', function (err) {
    console.error('[ERROR] producer:', err)
});
