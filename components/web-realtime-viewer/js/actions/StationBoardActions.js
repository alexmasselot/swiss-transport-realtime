'use strict';
import ActionTypes from '../constants/ActionTypes';

export function updateStationBoardStats(data) {
  return {
    type: ActionTypes.STATION_BOARD_STATS_RECEIVED,
    timestamp: new Date().getTime(),
    data
  }
}

export function updateStationBoardDetails(data) {
  return {
    type: ActionTypes.STATION_BOARD_DETAILS_RECEIVED,
    board: data
  }
}

export function getStationBoardDetails(id) {
  return {
    type: ActionTypes.STATION_BOARD_DETAILS_GET,
    id:id
  }
}
