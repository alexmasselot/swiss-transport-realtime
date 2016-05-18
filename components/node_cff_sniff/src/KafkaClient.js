"use strict";

var Kafka = require('kafka-node');
var _ = require('lodash');
var Promise = require('promise');
var promiseRetry = require('promise-retry');

/**
 * instanciatte a new Kafka client with connection to a kafka Brokker
 * @param kafkaHost
 * @param kafkaPort (2181)
 * @returns {KafkaClient}
 * @constructor
 */
var KafkaClient = function (kafkaHost, kafkaPort, name) {
    var _this = this;
    _this.kafkaConfig={
        host:kafkaHost,
        port:kafkaPort
    };
    _this.name = name;
    _this.client = new Kafka.Client(_this.kafkaConfig.host + ':' + _this.kafkaConfig.port);
    return _this;

};

/**
 * returns a promise of a kafka producer.
 * wait for the Kafka server to be reachable
 * @return set the producer properties and returns a Promise when ready

 */
KafkaClient.prototype.initProducer = function () {
    var _this = this;

    return promiseRetry(function (retry, number) {
        return new Promise(function (resolve, reject) {
            if (_this.client.ready) {
                var producer = new Kafka.HighLevelProducer(_this.client);
                _this.producer = producer;

                //resolve(producer);
                producer.on('ready', function () {
                    console.info('producer is ready')
                    _this.producer = producer;
                    resolve(producer);
                });
                producer.on('error', function (err) {
                    reject('producer did not reach ready state' + err);
                });
            } else {
                console.info("Wait for Kafka client initalisation (try=" + (number+1) + ")...");
                reject('waiting for kafka client ready '+number)
            }

        }).catch(retry);
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
                    console.error('ERROR in KafkaClient.js', err);
                    if(_.isArray(err) && err[0] === 'LeaderNotAvailable'){
                        console.error('Caught LeaderNotAvailable');
                    }
                    return _this.initProducer()
                        .then(function(){
                            reject('LeaderNotAvailable was caught and a new initProducer was solved')
                        })
                        .catch(function(err2){
                            reject('LeaderNotAvailable was caught and a new initProducer was also producing an error: ', err2);

                        });
                }
                resolve(data);
            }
        )
        ;
    });
};


module.exports = KafkaClient;