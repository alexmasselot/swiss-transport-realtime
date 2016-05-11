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

//send back the configuration object (the "frontend" subtree of the global config)
app.get('/config.json', function (req, res) {
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

