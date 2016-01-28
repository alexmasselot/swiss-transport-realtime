import {createStore, combineReducers} from 'redux';
import TrainPosition from '../reducers/TrainPosition';
import MapLocation from '../reducers/MapLocation';

const rootReducer = combineReducers({TrainPosition, MapLocation});


export default function configureStore(initialState) {
  return createStore(rootReducer, initialState);
}

