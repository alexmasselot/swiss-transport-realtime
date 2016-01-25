import {createStore, combineReducers} from 'redux';
import * as reducers from '../reducers/index';

const rootReducer = combineReducers(reducers);


export default function configureStore(initialState) {
  return createStore(rootReducer, initialState);
}

