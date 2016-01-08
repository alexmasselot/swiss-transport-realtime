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
var kafkaHost = process.env.KAFKA_HOST || (console.error('no environment variable KAFKA_HOST passed') && process.exit(1));
var kafkaPort = process.env.KAFKA_PORT || '2181';
var kafkaClient = new KafkaClient(kafkaHost, kafkaPort);


var TrainPositionSniffer = require('./TrainPositionSniffer');

kafkaClient.initProducer()
    .then(function () {
        var sniffer = new TrainPositionSniffer(kafkaClient, {kafkaTopic:'test', mode:'DEV', interval:10});
        sniffer.loop();
    }).catch(function (err) {
    console.error(err);
});
