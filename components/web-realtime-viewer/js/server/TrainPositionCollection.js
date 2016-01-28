import _ from 'lodash'

let defaultOptions = {
  obsolescenceMS: 60 * 1000,
  idKey:'id'
};

class TrainPositionCollection {
  constructor(options) {
    var _this = this;

    let opt = _.clone(defaultOptions)
    options = _.assign(opt, options);
    _this.obsolescenceMS = options.obsolescenceMS;
    _this.idKey = options.idKey;

    _this._positions = {};

    _this._obsoletInterval = setInterval(function () {
      _this._clearObsolete()
    }, _this.obsolescenceMS);
  }

  size() {
    return _.size(this._positions);
  }

  list() {
    return _.map(this._positions, 'obj');
  }

  update(tp) {
    var _this = this;

    let t = new Date().getTime();

    tp.x=parseFloat(tp.x)/1000000;
    tp.y=parseFloat(tp.y)/1000000;
    _this._positions[tp[_this.idKey]] = {
      t: t,
      obj: tp
    };
    return _this._positions[tp[_this.idKey]];
  }

  _clearObsolete() {
    let _this = this;

    let tDrop = new Date().getTime() - _this.obsolescenceMS;
    let rmIds = _.chain(_this._positions)
      .filter(function (p) {
        return p.t < tDrop;
      })
      .map('obj.'+_this.idKey)
      .value();
    _.each(rmIds, function (id) {
      delete _this._positions[id];
    });
    return _this;
  }

}

export default TrainPositionCollection;
