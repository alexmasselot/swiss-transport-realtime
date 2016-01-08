"use strict";

var Kafka = require('kafka-node');
var _ = require('lodash');
var Promise = require('promise');


/**
 * instanciatte a new Kafka client with connection to a kafka Brokker
 * @param kafkaHost
 * @param kafkaPort (2181)
 * @returns {KafkaClient}
 * @constructor
 */
var KafkaClient = function (kafkaHost, kafkaPort) {
    var _this = this;
    _this.client = new Kafka.Client(kafkaHost + ':' + kafkaPort);
    return _this;

};

/**
 * returns a promise of a kafka producer.
 * wait for the Kafka server to be reachable
 * @return set the producer properties and returns a Promise when ready

 */
KafkaClient.prototype.initProducer = function () {
    var _this = this;

    return new Promise(function (resolve, reject) {
        var tentative = 0;
        var intervalId = setInterval(function () {
            if (_this.client.ready) {
                console.info("Client ready !");
                clearInterval(intervalId);
                var producer = new Kafka.Producer(_this.client);
                _this.producer = producer;
                resolve(producer);
                //producer.on('ready', function () {
                //    console.info('producer is ready too')
                //    _this.producer = producer;
                //    resolve(producer);
                //})
                //producer.on('error', function (err) {
                //    reject('producer did not reach ready state' + err);
                //});
            } else {
                if (tentative > 10) {
                    clearInterval(intervalId);
                    reject("Kafka failure, too many attemps...");
                }
                tentative = tentative + 1;
                console.info("Wait for Kafka client initalisation (try=" + tentative + ")...");
            }
        }, 1000);

    });
};


/**
 * sed messages to the broker
 * if messages is an array, it will be broken into individual messages
 * @param topic
 * @param messages
 * @return a Promise
 */
KafkaClient.prototype.produce = function (topic, messages) {
    var _this = this;
    if (!_.isArray(messages)) {
        messages = [messages];
    }
    messages = _.map(messages, function (m) {
        return JSON.stringify(m);
    });

    return new Promise(function (resolve, reject) {
        _this.producer.send(
            [{topic: topic, messages: messages}]
            , function (err, data) {
                if (err) {
                    console.error(err)
                    reject(err);
                }
                resolve(data);
            }
        )
        ;
    });
};


module.exports = KafkaClient;