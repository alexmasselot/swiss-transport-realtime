'use strict';
import ActionTypes from '../constants/ActionTypes';

let defaultState = {
  location: {center:{lat: 46.521, lng: 6.627}, zoom:8},
};

export default function (state = defaultState, action) {
  switch (action.type) {
    case ActionTypes.MAP_LOCATION_CHANGED:
      console.log('ACTION', action);
      return {...state, location: {center:action.center, zoom:action.zoom}};
    default:
      return state;
  }
}
