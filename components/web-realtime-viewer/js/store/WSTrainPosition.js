'use strict'

class WSTrainPosition {
  constructor({store}) {
    let _this = this;

    _this.store = store;
    return _this;
  }

  /**
   * read data on a websocket and dispatch an action to the store
   * @param wsUrl
   * @param dispatchAtionBuilder : function(event), where the event contains at leas a data field.
   * The function returns an object, such as
   * {
   *   type: ActionTypes.TRAIN_POSITION_RECEIVED,
   *   data: JSON.parse(event.data),
   *   timestamp: new Date().getTime()
   *  }
   */
  pipe(wsUrl, dispatchActionBuilder) {
    let _this = this;
    let ws = new WebSocket(wsUrl);
    ws.onmessage = function (event) {
      _this.store.dispatch(
        dispatchActionBuilder(event)
      )
    };
  };
}

export default WSTrainPosition;
