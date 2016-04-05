import React from 'react';
import {Provider} from 'react-redux';

import data  from '../data/stationboard-8501120';
import StationBoard from '../../components/StationBoard';
import configureStore from 'redux-mock-store';

const mockStore = configureStore();


export default React.createClass({
  render() {
    let store = mockStore(data);
    return (
      <Provider store={store}>
        <div>
          <div style={{width:200, height:100}}>
            <StationBoard/>
          </div>
          <div style={{width:300, height:200}}>
            <StationBoard/>
          </div>
        </div>
      </Provider>
    );
  }
});
