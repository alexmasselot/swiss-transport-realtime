import {TRAIN_POSITION_RECEIVED} from '../constants/ActionTypes';

export function updateStationBoardStats(data) {
  return {
    type: STATION_BOARD_STATS_RECEIVED,
    timestamp:new Date().getTime(),
    data
  }
}
export function updateStationBoardDetails(data) {
  return {
    type: STATION_BOARD_DETAILS_RECEIVED,
    board:data
  }
}
