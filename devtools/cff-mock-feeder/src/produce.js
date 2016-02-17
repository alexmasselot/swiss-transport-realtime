"use strict";

var Kafka = require('kafka-node');
var _ = require('lodash');
var Promise = require('promise');
var process = require('process');

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
    console.log('waiting for producer to come ready');
    var i = 0;
    var interv = setInterval(function(){
        i++;
        if (i==20){
            reject('stopping after 10 attempt of seeing producer ready');
        }
        console.log('kafka client ready?', client.ready, client.connect);
        client.on('connect', function(){
            console.log('CLIENT HAS CONNECTED')
        })
        if(client.ready){
            console.log('clearing timer and creating a producer');
            clearInterval(interv);
            var producer = new Kafka.HighLevelProducer(client, {partitionerType:0});
            console.log('producer is ready and launching event on', topic);
            producer.createTopics([topic], false, function (err, data) {
                if (err) {
                    console.error('cannot create topic', err);
                    reject(err);
                    return;
                }
                console.log('topic created', data)
                resolve(producer);
            });
        }
    },1000);
});

Promise.all([prmKafkaProducer, prmReadEvent]).then(function (values) {
    var kafkaProducer = values[0];
    var events = values[1];
    console.log('Go for IT');

    var iEvent = 0;
    var iLoop = 0;

    var readNext = function () {
        var event = _.assign({}, events[iEvent], {timeStamp:new Date().getTime()});

        var wait;

        if (iEvent == events.length - 1) {
            iLoop++;
            console.log('loop', iLoop);
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
                console.error('kafka send error', err);
                return;
            }
            console.log(data)
        });
    });
}).catch(function (reason) {
    console.error('caught', reason);
});


