'use strict';

import Kafka  from 'kafka-node';
import  TrainPositionCollection from './TrainPositionCollection';
var WebSocketServer = require("ws").Server;

var _ = require('lodash');

var defaultOptions = {
  kafkaBroker: 'kafka:9092',
  intervalMS: 5 * 1000
};

class KafkaWSTrainPosition {
  constructor(options) {
    var _this = this;
    options = _.assign(defaultOptions, options);
    _this.kafkaBroker = options.kafkaBroker;
    _this.intervalMS = options.intervalMS;

    _this.wsPort = options.wsPort;

    _this._trainPositions = new TrainPositionCollection({
      idKey: 'trainid'
    });
    _this._initWebsocketServer();
    _this._initKafka();

    _this._interval = setInterval(function () {
      _this.broadcastPositions()
    }, _this.intervalMS);
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
    _this.kafkaClient = new Kafka.Client(_this.kafkaBroker, 'web-realtime-viewer');
    _this.kafkaClient.on('ready', function () {
      var consumer = new Kafka.HighLevelConsumer(_this.kafkaClient,
        [
          {
            topic: 'cff_train_position'
          }
        ],
        {
          groupId: 'web-realtime-viewer'
        }
      );

      consumer.on('message', function (message) {
        let v = JSON.parse(message.value);
        delete v.poly;
        console.log('message', v.trainid)
        _this._trainPositions.update(v);
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
      {
        trainPositions: _this._trainPositions.list(),
        trainCount: _this._trainPositions.size()
      }
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
