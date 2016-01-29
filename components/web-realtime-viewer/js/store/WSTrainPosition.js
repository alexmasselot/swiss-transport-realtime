'use strict'
import  ActionTypes from '../constants/ActionTypes';

class WSTrainPosition {
  constructor({store}) {
    let _this = this;

    _this._init();
    _this.store = store;
    return _this;
  }

  _init() {
    let _this = this;
    let ws = new WebSocket('ws://localhost' + ':3001');
    ws.onmessage = function (event) {
      _this.store.dispatch({type:ActionTypes.TRAIN_POSITION_RECEIVED, data:JSON.parse(event.data), timestamp:new Date().getTime()})
    };
  }
}

export default WSTrainPosition;
