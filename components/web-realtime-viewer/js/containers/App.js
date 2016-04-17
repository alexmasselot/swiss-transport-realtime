import React, {Component} from 'react';
import {Provider} from 'react-redux';
import Dimensions from 'react-dimensions';
import ReactDOM  from 'react-dom';
import d3 from 'd3';
import http from 'http';
import configureStore from '../store/configureStore';
import Home from '../components/Home';
import frontendConfig from '../config/FrontendConfig';
import  ActionTypes from '../constants/ActionTypes';
import matStyles from 'materialize-css/bin/materialize.css';


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


class App extends Component {

  render() {
    //yeaah, that's ugly, but franky, I'm lost with containerHeight and flex css
    const h = d3.select('#main').node().getBoundingClientRect().height;
    return (
      <div className={matStyles.container} style={{height:h}}>
        <Home store={store}/>
      </div>
    );
  }
}
;

export default Dimensions()(App);

//        <Provider store={store}>
//...
//        </Provider>
