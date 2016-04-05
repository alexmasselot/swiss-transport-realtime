import React from 'react';
import {Provider} from 'react-redux';
import http from 'http';
import configureStore from '../store/configureStore';
import Home from '../components/Home';
import WSTrainPosition from '../store/WSTrainPosition';
import frontendConfig from '../config/FrontendConfig';
import  ActionTypes from '../constants/ActionTypes';

const store = configureStore();


/**
 * setup the pipe between receiving data via websocket and updating the stores
 * @type {WSTrainPosition}
 */
let wsStore = new WSTrainPosition({store});


/*
 Launches the regular GET call to refresh the store.
 Train positions and station boards have different refresh rates.
 */

frontendConfig.get().then(function (config) {
  setInterval(function () {
    http.get(config.url.train_position_snapshot, function (res) {
        var tsv = '';
        res.on('data', function (data) {
            tsv += data;
          })
          .on('end', function () {
            store.dispatch({
              type: ActionTypes.TRAIN_POSITION_RECEIVED,
              tsv: tsv,
              timestamp: new Date().getTime()
            })
          });
      })
      .on('error', function (err) {
        console.error(err, config.url.train_position_snapshot);
      });
  }, 2000);
  setInterval(function () {
    http.get(config.url.station_board_stat_snapshot, function (res) {
        var data = '';
        res.on('data', function (chunk) {
            data += chunk;
          })
          .on('end', function () {
            store.dispatch({
              type: ActionTypes.STATION_BOARD_STATS_RECEIVED,
              data: JSON.parse(data || '{}'),
              timestamp: new Date().getTime()
            })
          });
      })
      .on('error', function (err) {
        console.error(err, config.url.train_position_snapshot);
      });
  }, 5000);
})
;


export default React.createClass({
  render() {
    return (
      <div>
        <Provider store={store}>
          <Home />
        </Provider>
      </div>
    );
  }
});
