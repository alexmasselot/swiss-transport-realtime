"use strict";

var Kafka = require('kafka-node');
var _ = require('lodash');
var Promise = require('promise');
var process = require('process');
var fs = require('fs');
var readline= require('readline');
var zlib = require('zlib');

var speedup = 10;
var kafkaHost = 'kafka';
var kafkaPort = 2181;
var topicTrainPositions = 'cff_train_position';
var topicStops = 'cff_station_board';

var isMockKafka = process.env.MOCK_KAFKA;

var prmReadEvent = function (file) {
    return new Promise(function (resolve, reject) {
        var gunzip = zlib.createGunzip()

        var lineReader = readline.createInterface({
            input: fs.createReadStream(file).pipe(gunzip)
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
};

var prmKafkaProducer = new Promise(function (resolve, reject) {
    if (isMockKafka) {
        resolve();
        return;
    }

    var client = new Kafka.Client(kafkaHost + ':' + kafkaPort);
    console.log('waiting for producer to come ready');
    var i = 0;
    var interv = setInterval(function () {
        i++;
        if (i == 20) {
            reject('stopping after 20 attempt of seeing producer ready');
        }
        console.log('kafka client ready?', client.ready, (client.ready ? 'OK' : 'WAIT'));

        if (client.ready) {
            console.log('clearing timer and creating a producer');
            clearInterval(interv);
            var producer = new Kafka.HighLevelProducer(client, {partitionerType: 0});
            console.log('producer is ready and launching event on');
            setTimeout(function () {
                resolve(producer);
            }, 2000);

            //producer.createTopics([topicTrainPositions], false, function (err, data) {
            //    if (err) {
            //        console.error('cannot create topicTrainPositions', err);
            //        reject(err);
            //        return;
            //    }
            //    console.log('topicTrainPositions created', data)
            //});
        }
    }, 1000);
});

var shootCycle = function (events, kafkaProducer, topic) {
    var iEvent = 0;
    var iLoop = 0;

    var readNext = function () {
        var event = _.assign({}, events[iEvent]);//, {timeStamp: new Date().getTime()});

        var wait;

        if (iEvent == events.length - 1) {
            iLoop++;
            console.log('loop', topic, iLoop);
            iEvent = 0;
            wait = 0;
        } else {
            wait = events[iEvent + 1].timeStamp - events[iEvent].timeStamp
            iEvent++
        }
        let evt = _.cloneDeep(event);
        let t0 = event.timeStamp;
        let t1 = new Date().getTime();
        evt.timeStamp=t1;
        if(event.stop !== undefined){
            evt.stop.departureTimestamp = Math.round(event.stop.departureTimestamp + (t1-t0)/1000);
            evt.stop.arrivalTimestamp = Math.round(event.stop.arrivalTimestamp + (t1-t0)/1000);
        }
        return {
            event: evt,
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
        var message = JSON.stringify(e);
        if (isMockKafka) {
            console.log(message);
            return;
        }
        kafkaProducer.send([{topic: topic, messages: message}], function (err, data) {
            if (err) {
                console.error('kafka send error', err);
                return;
            }
            //console.log(data)
        });
    });
};

Promise.all([prmKafkaProducer,
    prmReadEvent('resources/cff_train_position-2016-02-29__.jsonl.gz'),
    prmReadEvent('resources/cff-stop-2016-02-29__.jsonl.gz')
    //prmReadEvent('resources/cff_train_position-ic.txt'),
    //prmReadEvent('resources/cff-stop-2016-02-14_17.txt')
]).then(function (values) {
    var kafkaProducer = values[0];
    var eventsTrainPositions = values[1];
    var eventsStops = values[2];
    console.log('Go for IT');


    shootCycle(eventsTrainPositions, kafkaProducer, topicTrainPositions);
    shootCycle(eventsStops, kafkaProducer, topicStops);


}).catch(function (reason) {
    console.error('caught', reason);
});


