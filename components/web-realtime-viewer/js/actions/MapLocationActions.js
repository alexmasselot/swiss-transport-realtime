import actionTypes from '../constants/ActionTypes';

export function updateLocation(center, zoom) {
  console.log('updateLocation', center, zoom, 'firing', actionTypes.MAP_LOCATION_CHANGED)
  return {
    type: actionTypes.MAP_LOCATION_CHANGED,
    center,
    zoom
  }
}
