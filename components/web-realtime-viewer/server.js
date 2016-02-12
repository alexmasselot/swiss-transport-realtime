'use strict';
require('babel-core/register');

var path = require('path');
var webpack = require('webpack');
var express = require('express');
var webpackConfig = require('./webpack.config');
var config = require('config');

var _ = require('lodash');
var app = express();
var http = require("http");
var KafkaWSTrainPosition = require('./js/server/KafkaWSTrainPosition').default;


var compiler = webpack(webpackConfig);

app.use(require('webpack-dev-middleware')(compiler, {
  noInfo: true,
  publicPath: webpackConfig.output.publicPath,
  historyApiFallback: true
}));

app.use(require('webpack-hot-middleware')(compiler));

app.get('*', function (req, res) {
  res.sendFile(path.join(__dirname, 'index.html'));
});

app.listen(config.get('ports.http'), 'localhost', function (err, result) {
  if (err) {
    console.log(err);
  }

  console.log('Listening at localhost:', config.get('ports.http'));
});

let server = http.createServer(app);
let kafkaWSTrainPosition = new KafkaWSTrainPosition({
  wsPort: config.get('ports.wsTrainPosition'),
  kafkaBroker: '192.168.99.110:2181'
});

