'use strict';
import ActionTypes from '../constants/ActionTypes';

let defaultState = {
  positions: [],
  count:0,
  timestamp:new Date().getTime()
};

export default function (state = defaultState, action) {
  switch (action.type) {
    case ActionTypes.TRAIN_POSITION_RECEIVED:
      console.log(ActionTypes.TRAIN_POSITION_RECEIVED, action.data.positions.length);
      return {...state, positions: action.data.positions, timestamp:action.timestamp};
    default:
      return state;
  }
}
