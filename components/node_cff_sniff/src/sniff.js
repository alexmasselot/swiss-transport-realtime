'use strict';
/**
 *
 * make a call every to catch swiss train locations and station boards and send it to kafka broker
 *
 * all in promises
 */

var rp = require('request-promise');
var _ = require('lodash');
var Promise = require('promise');
var util = require('util');
var encoding = require("encoding");
var utf8 = require('utf8');

var KafkaClient = require('./KafkaClient');

if (!process.env.KAFKA_HOST) {
    console.error('no environment variable KAFKA_HOST passed');
    process.exit(1);
}
var kafkaHost = process.env.KAFKA_HOST || 'kafka';
var kafkaPort = process.env.KAFKA_PORT || '9092';
var kafkaClient = new KafkaClient(kafkaHost, kafkaPort);


var TrainPositionSniffer = require('./TrainPositionSniffer');
var StationBoardSniffer = require('./StationBoardSniffer');
setTimeout(function () {
    kafkaClient.initProducer()
        .then(function () {
            var trainPositionSniffer;
            var stationBoardSniffer;

            if (process.env.MODE === 'DEV') {
                trainPositionSniffer = new TrainPositionSniffer(kafkaClient, {
                    mode: 'DEV',
                    interval: 30
                });
                stationBoardSniffer = new StationBoardSniffer(kafkaClient, {
                    requestPerMinute: 12,
                    stopTypes: ['train_dev', 'ferry_dev']
                });
            } else {
                trainPositionSniffer = new TrainPositionSniffer(kafkaClient);
                stationBoardSniffer = new StationBoardSniffer(kafkaClient);
            }
            trainPositionSniffer.loop();
            stationBoardSniffer.loop();
        }).catch(function (err) {
        console.error('ERROR in initProducer', err);
    });
}, 5000);
