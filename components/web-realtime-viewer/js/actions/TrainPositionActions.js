import {TRAIN_POSITION_RECEIVED} from '../constants/ActionTypes';

export function updateStationBoarStats(data) {
  return {
    type: TRAIN_POSITION_RECEIVED,
    timestamp:new Date().getTime(),
    data
  }
}
