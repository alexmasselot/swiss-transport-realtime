"use strict";

var Kafka = require('kafka-node');
var _ = require('lodash');
var Promise = require('promise');

var speedup = 10;
var kafkaHost = 'kafka';
var kafkaPort = 2181;
var topic = 'cff_train_position';

var prmReadEvent = new Promise(function (resolve, reject) {
    var lineReader = require('readline').createInterface({
        input: require('fs').createReadStream('resources/cff_train_position-ic.txt')
    });

    var lines = [];

    lineReader.on('line', function (line) {
        lines.push(JSON.parse(line));
    });

    lineReader.on('close', function () {
        resolve(lines);
    });
    lineReader.on('error', function (e) {
        reject(e);
    });
});

var prmKafkaProducer = new Promise(function (resolve, reject) {
    var client = new Kafka.Client(kafkaHost + ':' + kafkaPort);
    var producer = new Kafka.Producer(client);
    producer.on('ready', function () {
        console.log('PAF is ready')
        producer.createTopics([topic], false, function (err, data) {
            if (err) {
                reject(err);
                return;
            }
            resolve(producer);
        });
    });
});

Promise.all([prmKafkaProducer, prmReadEvent]).then(function (values) {
    var kafkaProducer = values[0];
    var events = values[1];
    console.log('GO for it');




    var iEvent = 0;

    var readNext = function () {
        var event = events[iEvent];
        var wait;

        if (iEvent == events.length - 1) {
            iEvent = 0;
            wait = 0;
        } else {
            wait = events[iEvent + 1].timeStamp - events[iEvent].timeStamp
            iEvent++
        }
        return {
            event: event,
            wait: wait / speedup
        }
    };

    var shootNext = function (action) {
        var x = readNext();
        setTimeout(function () {
            action(x.event);
            shootNext(action);
        }, x.wait);
    };
    shootNext(function (e) {
        kafkaProducer.send([{topic: topic, messages: JSON.stringify(e)}], function (err, data) {
            if (err) {
                console.error(err)
            }
        });
    });
}).catch(function (reason) {
    console.error(reason);
});


