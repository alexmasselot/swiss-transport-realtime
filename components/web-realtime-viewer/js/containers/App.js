import React from 'react';
import {Provider} from 'react-redux';
import http from 'http';
import configureStore from '../store/configureStore';
import Home from '../components/Home';
import frontendConfig from '../config/FrontendConfig';
import  ActionTypes from '../constants/ActionTypes';

const store = configureStore();

/**
 * setup the pipe between receiving data via websocket and updating the stores
 * @type {WSTrainPosition}
 */


/*
 Launches the regular GET call to refresh the store.
 Train positions and station boards have different refresh rates.
 */
frontendConfig.get().then(function (config) {

  const fGetTrainPosition = function () {
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
  };

  const fGetStationBoardStats = function () {
    http.get(config.url.station_board, function (res) {
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
        console.error(err, config.url.station_board);
      });
  };

  fGetTrainPosition();
  fGetStationBoardStats();

  setInterval(fGetTrainPosition, 2000);
  setInterval(fGetStationBoardStats, 5000);
})
;


export default React.createClass({
  render() {
    return (
      <div>
        <Home store={store}/>
      </div>
    );
  }
});
//        <Provider store={store}>
//...
//        </Provider>
