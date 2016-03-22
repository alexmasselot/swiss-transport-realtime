import {TRAIN_POSITION_RECEIVED} from '../constants/ActionTypes';

export function updatePositions(data) {
  return {
    type: STATION_BOARD_STATS_RECEIVED,
    timestamp:new Date().getTime(),
    data
  }
}
