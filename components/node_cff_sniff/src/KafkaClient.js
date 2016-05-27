"use strict";

var Kafka = require('no-kafka');
var _ = require('lodash');
var Promise = require('promise');

/**
 * instanciatte a new Kafka client with connection to a kafka Brokker
 * @param kafkaHost
 * @param kafkaPort (2181)
 * @returns {KafkaClient}
 * @constructor
 */
var KafkaClient = function (kafkaHost, kafkaPort, name) {
    var _this = this;
    _this.kafkaConfig = {
        host: kafkaHost,
        port: kafkaPort
    };
    _this.name = name;
    return _this;

};

/**
 * returns a promise of a kafka producer.
 * wait for the Kafka server to be reachable
 * @return set the producer properties and returns a Promise when ready

 */
KafkaClient.prototype.initProducer = function () {
    var _this = this;

    _this.producer = new Kafka.Producer({connectionString: _this.kafkaConfig.host + ':' + _this.kafkaConfig.port});
    return _this.producer.init().then(function () {
        return _this.producer;
    })
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
    _.chain(messages)
        .map(function (m) {
            return JSON.stringify(m);
        })
        .forEach(function (message) {
            _this.producert.send({
                topic: topic,
                partition: 0,
                message: {value: message}
            });

        });


};


module.exports = KafkaClient;