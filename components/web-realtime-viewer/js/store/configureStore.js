import {createStore, combineReducers} from 'redux';
import TrainPosition from '../reducers/TrainPosition';
import StationBoard from '../reducers/StationBoard';
import MapLocation from '../reducers/MapLocation';

const rootReducer = combineReducers({TrainPosition, MapLocation, StationBoard});

export default function configureStore(initialState) {
  return createStore(rootReducer, initialState);
}

