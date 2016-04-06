import React from 'react';
import {Provider} from 'react-redux';

import data  from '../data/train-1';
import PositionMapTrains from '../../components/PositionMapTrains';
import configureStore from 'redux-mock-store';

const mockStore = configureStore();


export default React.createClass({
  render() {
    let store = mockStore(data);
    return (
      <Provider store={store}>
        <div>
          <div style={{width:200, height:100}}>
            <PositionMapTrains
              bounds={{
              lngMin:10,
              lngMax:10.1,
              latMin:45,
              latMax:45.1
              }}
              width={200}
              height={300}
            />
          </div>
        </div>
      </Provider>
    );
  }
});
