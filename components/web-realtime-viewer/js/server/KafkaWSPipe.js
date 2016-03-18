'use strict';

import Kafka  from 'kafka-node';
var WebSocketServer = require("ws").Server;

var _ = require('lodash');

var defaultOptions = {
  kafkaBroker: 'kafka:9092',
  intervalMS: 5 * 1000,
  topic: 'default_topic',
  groupId: 'web-realtime-viewer'
};

class KafkaWSPipe {
  constructor(options) {
    var _this = this;
    options = _.assign(defaultOptions, options);
    _this.kafkaBroker = options.kafkaBroker;
    _this.intervalMS = options.intervalMS;
    _this.groupId = options.groupId;


    _this.channels = {};
    _.each(options.channels, function (c) {
      _this.channels[c.topic] = {topic: c.topic, wsPort: c.wsPort}
      ;
    });
    _this._lastMessages = {};
    _this._wsServers = {};

    console.log('initilizing KafkaWSPipe with ', _this.channels);

    _this._initWebsocketServer();
    _this._initKafka();

  }

  _initWebsocketServer() {
    var _this = this;

    _.each(_this.channels, function (channel, topic) {
      var wss = new WebSocketServer({port: channel.wsPort});
      wss.on('connection', function connection(ws) {
        ws.send(_this._lastMessages[topic]);
      });
      _this._wsServers[topic] = wss;
    })
  }

  _initKafka() {
    let _this = this;
    console.log('init kafka connection with broker:', _this.kafkaBroker);
    _this.kafkaClient = new Kafka.Client(_this.kafkaBroker, 'web-realtime-viewer');
    _this.kafkaClient.on('ready', function () {
      console.log('Kafka client is ready');
      let offset = new Kafka.Offset(_this.kafkaClient)

      offset.fetch(
        _.map(_this.channels, function (channel) {
          return {topic: channel.topic};
        })
        , function (err, data) {
          if (err) {
            console.error('ERROR fetching offset', err);
            return;
          }
          _.each(data, function (offsets, topic) {
            let offset = _.min(_.flatten(_.values(offsets)));
            _this.channels[topic].offset = offset;
          });
          let topoff = _.map(_this.channels, function (channel) {
            return {topic: channel.topic, offset: channel.offset, partition: 0};
          });
          console.log('topic/offset', topoff);

          var consumer = new Kafka.Consumer(_this.kafkaClient,
            topoff,
            {
              groupId: _this.groupId,
              fromOffset: true
            });
          //_.each(_this.channels, function (channel, topic) {
          //  console.log('adding '+channel.topic+'/'+channel.offset);
          //  consumer.addTopics([{topic:channel.topic, offset:channel.offset}], function (err, data) {
          //  }, true);
          //});

          consumer.on('message', function (message) {
            let topic = message.topic;
            if (message.value.trim().substr(0, 1) != '{') {
              console.error('not  a json message ' + message.value);
              return;
            }
            let v = JSON.parse(message.value);
            _this.broadcastPositions(topic, v);
          });
          consumer.on('error', function (err) {
            console.error('[ERROR] kafka consumer', err);
          });
          console.info('consumer launched', _.keys(_this.channels));

        });
    });
  }

  updateTrainPosition(tp) {
    var _this = _this;
    return _this._initWebsocketServer();
  }

  broadcastPositions(topic, data) {
    let _this = this;

    let msg = JSON.stringify(data);
    _this._lastMessage = msg;
    console.log(new Date(), 'broadcast ', topic, msg.length, 'bytes');
    console.log(msg.substring(0, 80), '...');
    _this._wsServers[topic].clients.forEach(function (client) {
      client.send(msg);
    });
    return _this;
  }

}
;

export
default
KafkaWSPipe;
