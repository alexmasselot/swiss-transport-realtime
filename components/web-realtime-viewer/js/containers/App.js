import React from 'react';
import {Provider} from 'react-redux';
import configureStore from '../store/configureStore';
import Home from '../components/Home';
import WSTrainPosition from '../store/WSTrainPosition';
import frontendConfig from '../config/FrontendConfig';
import  ActionTypes from '../constants/ActionTypes';

const store = configureStore();


/**
 * setup the pipe between receiving data via websocket and updating the stores
 * @type {WSTrainPosition}
 */
let wsStore = new WSTrainPosition({store});
frontendConfig.get().then(function (config) {
  wsStore.pipe(config.url.ws.train_position, function (event) {
    return {
      type: ActionTypes.TRAIN_POSITION_RECEIVED,
      tsv: event.data,
      timestamp: new Date().getTime()
    };
  });
    wsStore.pipe(config.url.ws.station_board_stats, function (event) {
      return {
        type: ActionTypes.STATION_BOARD_STATS_RECEIVED,
        data: JSON.parse(event.data || '{}'),
        timestamp: new Date().getTime()
      };
  });
}).catch(function (error) {
  console.error('Error getting frontendConfig', error);
});

export default React.createClass({
  render() {
    return (
      <div>
        <Provider store={store}>
          <Home />
        </Provider>
      </div>
    );
  }
});
