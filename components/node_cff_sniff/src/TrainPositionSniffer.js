'use strict';
/**
 *
 * Sniff train position and publish to kafka
 */

var rp = require('request-promise');
var _ = require('lodash');
var Promise = require('promise');
var util = require('util');
var encoding = require("encoding");
var utf8 = require('utf8');

/**
 *
 * @param kafkaProducer
 * @param mode
 * @param options:
 * * kafkaTopic ['cff_train_position']
 * * mode ['PROD']
 * * interval [30]
 * @returns {TrainPositionSniffer}
 * @constructor
 */
var TrainPositionSniffer = function (kafkaClient, options) {
    var _this = this;

    options = _.assign({
        kafkaTopic:'cfftrainposition',
        mode: 'PROD',
        interval:30
    }, options);

    _this.kafkaClient = kafkaClient;
    _this.interval=options.interval;
    _this.kafkaTopic = options.kafkaTopic || 'cff_train_position'
    _this.urlFNY = (options.mode === 'DEV') ?
        'http://fahrplan.sbb.ch/bin/query.exe/fny?look_minx=6385532.065906713&look_maxx=6884036.704578587&look_miny=46441434.48378557&look_maxy=46653942.475391746&performLocating=1&performFixedLocating=7&' :
        'http://fahrplan.sbb.ch/bin/query.exe/fny?look_minx=5850000&look_maxx=10540000&look_miny=45850000&look_maxy=47800000&performLocating=1&performFixedLocating=7'

    console.info('TrainPositionSniffer MODE=' + options.mode+' kafkaTopic='+_this.kafkaTopic+' interval (s)='+_this.interval )
    return _this;

};


TrainPositionSniffer.prototype.getFNY = function () {
    var _this = this;
    var tstamp = Date.now();
    return rp({
        uri: _this.urlFNY,
        json: true,
        encoding: 'binary'
    }).then(function (data) {
        return _.map(data.look.trains, function (t) {
            t.timeStamp = tstamp;
            return t;
        });
    });
};

TrainPositionSniffer.prototype.loop = function () {
    var _this = this;

    var doIt = function () {
        _this.getFNY()
            .then(function (messages) {
                return _this.kafkaClient.produce(_this.kafkaTopic, messages);
            })
            .catch(function (err) {
                if(err.statusCode){
                    console.log('TrainPositionSniffer: Error in loadStops: '+err.statusCode+' / '+err.options.uri);
                }else {
                    console.error('TrainPositionSniffer: Error in loadStops', err);
                }
            });
    };
    doIt();
    setInterval(doIt, _this.interval * 1000);
    return _this;
};


module.exports = TrainPositionSniffer;