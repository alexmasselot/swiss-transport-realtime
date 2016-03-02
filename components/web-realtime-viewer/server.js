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
var KafkaWSTrainPosition = require('./js/server/KafkaWSTrainPosition').default;


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
let kafkaWSTrainPosition = new KafkaWSTrainPosition({
  wsPort: config.get('ports.wsTrainPosition'),
  kafkaBroker: config.get('zookeeper.host') + ':' + config.get('zookeeper.port'),
  intervalMS: config.get('kafka.interval'),
  topic: config.get('kafka.topic')
});

