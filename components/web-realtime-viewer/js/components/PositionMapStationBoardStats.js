import React, {Component} from 'react';
import ReactDOM  from 'react-dom';
import {connect} from 'react-redux';
import d3 from 'd3';
import {bindActionCreators} from 'redux';
import * as TrainPositionActions from '../actions/TrainPositionActions';
import styles from '../../css/app.css';
import classes from './PositionMapCFF.css'
import { Map, Marker, Popup, TileLayer } from 'react-leaflet';
import _ from 'lodash'
import * as StationBoardActions from '../actions/StationBoardActions';
import * as MapLocationActions from '../actions/MapLocationActions';

let i = 0;

class PositionMapStationBoardStats extends Component {
  constructor() {
    super();
    let _this = this;
    this;
  }

  componentDidMount() {
    var _this = this;
    _this._setupD3(ReactDOM.findDOMNode(this));
  }

  shouldComponentUpdate(props) {
    this._renderD3(ReactDOM.findDOMNode(this), props);
    // always skip React's render step
    return false;
  }

  _setupD3(el) {
    let _this = this;

    _this._dim = {
      height: parseInt(_this.props.height),
      width: parseInt(_this.props.width)
    };

    d3.select(el).selectAll().empty();
    _this._elements = {
      svg: d3.select(el)
        .attr({
          //viewBox: '-' + (_this._dim.width / 2) + ' -' + (_this._dim.height / 2) + ' ' + _this._dim.width + ' ' + _this._dim.height,
          //viewBox: '0 0 ' + _this._dim.width + ' ' + _this._dim.height,
          width: _this._dim.width,
          height: _this._dim.height,
          class: classes.train_overlay
        })
      //.style('overflow', 'visible')
    };
    _this._elements.svg.append('rect')
      .attr({
        width: _this._dim.width,// * 3,
        height: _this._dim.height,// * 3,
        x: 0,//-_this._dim.width,
        y: 0,//-_this._dim.height,
        class: classes.masking
      });
    _this._elements.gMainStationBoardStats = _this._elements.svg.append('g')
      .attr({
        class: 'station-board-stats-main'
      });
    _this._renderD3(el, _this.props)
  }

  _updateData(bounds, stationBoardStats) {
    let _this = this;

    let {lngMin, lngMax, latMin, latMax}  = bounds;

    _this._stationBoardStats = _.chain(stationBoardStats)
      .map(function (p) {
        p.x = p.stop.location.lng;
        p.y = p.stop.location.lat;
        return p;
      })
      .filter(function (p) {
        return (p.x >= lngMin) && (p.x <= lngMax) && (p.y >= latMin) && (p.y <= latMax);
      })
      .sortBy(function (p) {
        return -p.total;
      })
      .value();


    return _this;
  }

  _renderD3StationBoardStats(el, zoom, callbacks) {
    let _this = this;

    console.log('_renderD3StationBoardStats')

    let gStats = _this._elements.gMainStationBoardStats
      .selectAll('g.station-board-stats')
      .data(_this._stationBoardStats, function (d) {
        return d.stop.id;
      });
    let gNew = gStats.enter()
      .append('g')
      .attr('class', 'station-board-stats ' + classes['station-board-stats']);
    gNew.attr('transform', function (p) {
      return 'translate(' + _this._scales.x(p.x) + ',' + (_this._scales.y(p.y)) + ')';
    });

    if (callbacks && callbacks.mouseover) {
      gNew.on('mouseover', callbacks.mouseover)
    }

    gNew.append('circle');
    gNew.append('path')
      .attr({
        class: 'delayed ' + classes.stationboard_delayed
      });

    var factor;
    if (zoom <= 8) {
      factor = 0.25;
    } else if (factor >= 11) {
      factor = 1;
    } else {
      factor = ((zoom - 8) + (11 - zoom) * 0.25) / 3;
    }

    let fRadius = function (d) {
      return factor * 5 * (d.total + 1);
    };

    gStats.transition()
      .attr('transform', function (p) {
        return 'translate(' + _this._scales.x(p.x) + ',' + (_this._scales.y(p.y)) + ')';
      });
    gStats.select('circle')
      .attr({
        r: fRadius
      })
    ;
    gStats.selectAll('path.delayed')
      .attr('d', d3.svg.arc()
        .innerRadius(0)
        .outerRadius(fRadius)
        .startAngle(0)
        .endAngle(function (d) {
          return d.total ? (2 * Math.PI * d.delayed / d.total) : 0;
        })
      );

    gStats.exit().remove()
  };

  _renderD3(el, newProps) {
    let _this = this;

    const {dispatch} = newProps;
    const actions = {
      ...bindActionCreators(StationBoardActions, dispatch),
      ...bindActionCreators(MapLocationActions, dispatch)
    };

    let {lngMin, lngMax, latMin, latMax}  = newProps.MapLocation.location.bounds;
    _this._scales = {
      x: d3.scale.linear().range([0, _this._dim.width]).domain([lngMin, lngMax]),
      y: d3.scale.linear().range([0, _this._dim.height]).domain([latMax, latMin])
    };


    _this._updateData(newProps.MapLocation.location.bounds, newProps.StationBoard.stats)
      ._renderD3StationBoardStats(el,
        newProps.zoom,
        {
          mouseover: function (p) {
            actions.getStationBoardDetails(p.id);
          }
        }
      );
  }

  render() {


    return (
      <g/>
    );
  }
}

export default connect(state => state)(PositionMapStationBoardStats);
