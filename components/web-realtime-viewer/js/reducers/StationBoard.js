'use strict';
import ActionTypes from '../constants/ActionTypes';
import frontendConfig from '../config/FrontendConfig';
import http from 'http';

let defaultState = {
  stats:{},
  timestamp:new Date().getTime()
};

export default function (state = defaultState, action) {
  let _this = this;
  switch (action.type) {
    case ActionTypes.STATION_BOARD_STATS_RECEIVED:
      return {...state, stats: action.data.stats, timestamp:action.timestamp};
    case ActionTypes.STATION_BOARD_DETAILS_RECEIVED:
      console.log(ActionTypes.STATION_BOARD_DETAILS_RECEIVED, action.data);
      console.log('YALLA  RECEVIVED');
      return {...state, board: action.board};
    //case ActionTypes.STATION_BOARD_DETAILS_GET:
    //  frontendConfig.get().then(function (config) {
    //    let url = config.url.station_board + '/' + action.id;
    //    http.get(url, function (res) {
    //        var data = '';
    //        res.on('data', function (chunk) {
    //            data += chunk;
    //          })
    //          .on('end', function () {
    //            _this.dispatch({
    //              type: ActionTypes.STATION_BOARD_DETAILS_RECEIVED,
    //              data: JSON.parse(data || '{}'),
    //              timestamp: new Date().getTime()
    //            })
    //          });
    //      })
    //      .on('error', function (err) {
    //        console.error(err, url);
    //      });
    //  });
    //  return {...state};
    default:
      return state;
  }

}
