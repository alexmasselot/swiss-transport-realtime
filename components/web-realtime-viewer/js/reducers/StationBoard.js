'use strict';
import ActionTypes from '../constants/ActionTypes';
import frontendConfig from '../config/FrontendConfig';
import http from 'http';

let defaultState = {
  stats: {},
  details: {status: 'unavailable'},
  timestamp: new Date().getTime()
};

export default function (state = defaultState, action) {
  let _this = this;
  switch (action.type) {
    case ActionTypes.STATION_BOARD_STATS_RECEIVED:
      return {...state, stats: action.data.stats, timestamp: action.timestamp};
    case ActionTypes.STATION_BOARD_DETAILS_RECEIVED:
      return {...state, details: {status: 'success', board: action.board}};
    case ActionTypes.STATION_BOARD_DETAILS_FETCHING:
      return {...state, details: {status: 'fetching', stopName: action.stopName}};
    default:
      return state;
  }

}
