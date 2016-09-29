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


app.use(function(err, req, res, next) {
  console.log('app.use');
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});

app.use('/static', express.static('static'));

app.get('/', function (req, res) {
  res.sendFile(path.join(__dirname, 'index.html'));
});

//send back the configuration object (the "frontend" subtree of the global config)
app.get('/config.json', function (req, res) {
  res.send(config.frontend);
});

app.listen(config.get('ports.http'), function (err, result) {
  if (err) {
    console.log(err);
  }

  console.log('Listening on', config.get('ports.http'));
});

console.log(config);

let server = http.createServer(app);

