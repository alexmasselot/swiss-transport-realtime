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
      console.log('displayth', {type:ActionTypes.TRAIN_POSITION_RECEIVED, data:event.data});
      console.log(_this.store.getState())
      _this.store.dispatch({type:ActionTypes.TRAIN_POSITION_RECEIVED, data:JSON.parse(event.data)})
    };
  }
}

export default WSTrainPosition;


