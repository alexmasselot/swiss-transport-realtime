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

class KafkaWSTrainPosition {
  constructor(options) {
    var _this = this;
    options = _.assign(defaultOptions, options);
    _this.kafkaBroker = options.kafkaBroker;
    _this.intervalMS = options.intervalMS;
    _this.topic = options.topic;
    _this.groupId = options.groupId;

    _this.wsPort = options.wsPort;


    _this._initWebsocketServer();
    _this._initKafka();

  }

  _initWebsocketServer() {
    var _this = this;
    var wss = new WebSocketServer({port: _this.wsPort});
    _this._websocketServer = wss;
    wss.on('connection', function connection(ws) {
      ws.send(_this.clientMessage());
    });
  }

  _initKafka() {
    let _this = this;
    console.log('init kafka connection with broker:', _this.kafkaBroker);
    _this.kafkaClient = new Kafka.Client(_this.kafkaBroker, 'web-realtime-viewer');
    _this.kafkaClient.on('ready', function () {
      var consumer = new Kafka.HighLevelConsumer(_this.kafkaClient,
        [
          {
            topic: _this.topic
          }
        ],
        {
          groupId: _this.groupId
        }
      );

      consumer.on('message', function (message) {
        let v = JSON.parse(message.value);
        _this._trainPositions=v;
        _this.broadcastPositions();
      });
      consumer.on('error', function (err) {
        console.error('[ERROR] kafka consumer', err);
      });
      console.info('consumer launched')
    });


  }

  updateTrainPosition(tp) {
    var _this = _this;
    return _this._initWebsocketServer();
  }

  clientMessage() {
    let _this = this;
    return JSON.stringify(
        _this._trainPositions
    );
  }

  broadcastPositions() {
    let _this = this;

    let msg = _this.clientMessage();
    console.log(new Date(), 'broadcast', msg.length, 'bytes');
    _this._websocketServer.clients.forEach(function (client) {
      client.send(msg);
    });
    return _this;
  }

}
;

export default KafkaWSTrainPosition;
