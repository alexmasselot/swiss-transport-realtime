'use strict';
import ActionTypes from '../constants/ActionTypes';

let defaultState = {
  positions: [],
  count:0
};

export default function (state = defaultState, action) {
  switch (action.type) {
    case ActionTypes.TRAIN_POSITION_RECEIVED:
      return {...state, positions: action.data.trainPositions};
    default:
      return state;
  }
}
