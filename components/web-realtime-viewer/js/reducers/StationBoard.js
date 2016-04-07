'use strict';
import ActionTypes from '../constants/ActionTypes';

let defaultState = {
  stats:{},
  timestamp:new Date().getTime()
};

export default function (state = defaultState, action) {
  switch (action.type) {
    case ActionTypes.STATION_BOARD_STATS_RECEIVED:
      return {...state, stats: action.data.stats, timestamp:action.timestamp};
    case ActionTypes.STATION_BOARD_DETAILS_RECEIVED:
      return {...state, board: action.board};
    default:
      return state;
  }

}
