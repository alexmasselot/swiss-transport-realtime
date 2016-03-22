import {createStore, combineReducers} from 'redux';
import TrainPosition from '../reducers/TrainPosition';
import StationBoardStats from '../reducers/StationBoardStats';
import MapLocation from '../reducers/MapLocation';

const rootReducer = combineReducers({TrainPosition, MapLocation, StationBoardStats});


export default function configureStore(initialState) {
  return createStore(rootReducer, initialState);
}

