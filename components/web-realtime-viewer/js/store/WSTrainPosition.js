'use strict'
import  ActionTypes from '../constants/ActionTypes';
import frontendConfig from '../config/FrontendConfig';

class WSTrainPosition {
  constructor({store}) {
    let _this = this;

    _this._init();
    _this.store = store;
    return _this;
  }

  _init() {
    let _this = this;

    frontendConfig.get().then(function (config) {
      let wsUrl = config.url.ws;
      let ws = new WebSocket(wsUrl);
      ws.onmessage = function (event) {
        _this.store.dispatch({
          type: ActionTypes.TRAIN_POSITION_RECEIVED,
          data: JSON.parse(event.data),
          timestamp: new Date().getTime()
        })
      };

    }).catch(function (error) {
      console.error('Error getting config', error);
    });
  }
}

export default WSTrainPosition;
