'use strict';
require('babel-core/register');

var path = require('path');
var webpack = require('webpack');
var express = require('express');
var webpackConfig = require('./webpack.config');
var config = require('config');

var _ = require('lodash');
const app = global.server = express();
var http = require("http");
var KafkaWSPipe = require('./js/server/KafkaWSPipe').default;


var compiler = webpack(webpackConfig);

app.use(require('webpack-dev-middleware')(compiler, {
  noInfo: true,
  publicPath: webpackConfig.output.publicPath,
  historyApiFallback: true
}));

app.use(require('webpack-hot-middleware')(compiler));

app.get('/', function (req, res) {
  res.sendFile(path.join(__dirname, 'index.html'));
});

app.get('/config', function (req, res) {
  res.send(config.frontend);
});

app.listen(config.get('ports.http'), 'localhost', function (err, result) {
  if (err) {
    console.log(err);
  }

  console.log('Listening at localhost:', config.get('ports.http'));
});

console.log(config);

let server = http.createServer(app);
//let kwPipe = new KafkaWSPipe({
//  kafkaBroker: config.get('zookeeper.host') + ':' + config.get('zookeeper.port'),
//  intervalMS: config.get('kafka.interval'),
//  channels:[
//    {wsPort:config.get('ports.ws.train_position'),topic: config.get('kafka.topic.train_position')},
//    //{wsPort:config.get('ports.ws.station_board_stats'),topic: config.get('kafka.topic.station_board_stats')}
//  ],
//  groupId: 'web-realtime-viewer-'+Math.round(Math.random()*10000000)
//});

