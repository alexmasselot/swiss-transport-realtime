import React, {Component} from 'react';
import ReactDOM  from 'react-dom';
import {connect} from 'react-redux';
import d3 from 'd3';
import {bindActionCreators} from 'redux';
import * as TrainPositionActions from '../actions/TrainPositionActions';
import styles from '../../css/app.css';
import classes from './PositionMapTrains.css'
import { Map, Marker, Popup, TileLayer } from 'react-leaflet';
import _ from 'lodash'

let i = 0;

class PositionMapTrains extends Component {
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
    //look_minx=5850000&look_maxx=10540000&look_miny=45850000&look_maxy=47800000


    d3.select(el).selectAll().empty();
    _this._elements = {
      svg: d3.select(el).append('svg')
        .attr({
          //viewBox: '-' + (_this._dim.width / 2) + ' -' + (_this._dim.height / 2) + ' ' + _this._dim.width + ' ' + _this._dim.height,
          //viewBox: '0 0 ' + _this._dim.width + ' ' + _this._dim.height,
          width: _this._dim.width,
          height: _this._dim.height
        })
      //.style('overflow', 'visible')
    };
    _this._elements.svg.append('rect')
      .attr({
        width: _this._dim.width * 3,
        height: _this._dim.height * 3,
        x: -_this._dim.width,
        y: -_this._dim.height,
        class: classes.masking
      });
    _this._elements.gMainStationBoardStats = _this._elements.svg.append('g')
      .attr({
        class: 'station-board-stats-main'
      });
    _this._elements.gMainTrainPositions = _this._elements.svg.append('g')
      .attr({
        class: 'train-positions-main'
      });


  }

  _updateData(bounds, trainPositions, stationBoardStats) {
    let _this = this;

    let {lngMin, lngMax, latMin, latMax}  = bounds;

    _this._trainPositions = _.chain(trainPositions)
      .map(function (p) {
        p.x = p.position_lng;
        p.y = p.position_lat;
        return p;
      })
      .filter(function (p) {
        return (p.x >= lngMin) && (p.x <= lngMax) && (p.y >= latMin) && (p.y <= latMax);
      }).value();

    _this._stationBoardStats = _.chain(stationBoardStats)
      .map(function (p) {
        p.x = p.stop.location.lng;
        p.y = p.stop.location.lat;
        return p;
      })
      .filter(function (p) {
        return (p.x >= lngMin) && (p.x <= lngMax) && (p.y >= latMin) && (p.y <= latMax);
      }).value();

    //
    //console.log('delayed?');
    //_.chain(stationBoardStats)
    //  .filter(function (s) {
    //    return s.delayed;
    //  })
    //  .each(function (s) {
    //    console.log(s.stop.name + ': ' + s.delayed + '/' + s.total);
    //  })
    //  .value();

    return _this;
  }

  _renderD3TrainPositions(el) {
    let _this = this;

    let gTrains = _this._elements.gMainTrainPositions
      .selectAll('g.train-position')
      .data(_this._trainPositions, function (d) {
        return d.train_id;
      });

    let gNew = gTrains.enter()
      .append('g')
      .attr({
        transform: function (p) {
          return 'translate(' + _this._scales.x(p.x) + ',' + _this._scales.y(p.y) + ')';
        },
        class: function (p) {
          let s = p.train_name.trim();
          let i = s.indexOf(' ');
          let clazz = 'train-position ';
          clazz = clazz + classes['train-cat_' + p.train_category.toLowerCase()];
          clazz = clazz + ' ' + classes.trainMarker;
          return clazz;
        }
      });
    gNew.on('mouseover', function (p) {
      console.log(p.train_name + '->' + p.train_lastStopName);
    });
    let gSymbol = gNew.append('g')
      .attr({
        class: classes.trainSymbol
      });
    gSymbol.append('circle')
      .attr({
        cx: 0,
        cy: 0,
        r: 2
      });
    //gSymbol.append('text')
    //  .text(function (p) {
    //    let s = p.train.category;
    //
    //    if (p.timedPosition.stop) {
    //      s = s + " - " + p.timedPosition.stop.name;
    //    }
    //    return s;
    //  });

    gNew.append('g')
      .attr({
        class: classes.trainDetails
      }).append('text')
      .attr({
        class: classes.positionText,
        x: 4
      })
      .text(function (p) {
        return p.train_name.trim() + ' (' + p.train_lastStopName + ')';// +_this.props.coord2point.x(p.x);
      });


    //gTrains.classed(classes.stopped, function (p) {
    //  return p.timedPosition.stop;
    //});

    //.transition()
    //.duration(500)
    gTrains
      .attr('transform', function (p) {
        return 'translate(' + _this._scales.x(p.x) + ',' + (_this._scales.y(p.y)) + ')';
      });


    gTrains.exit().transition()
      .duration(300)
      .style('opacity', 1e-6)
      .remove();
    return _this;
  };

  _renderD3StationBoardStats(el) {
    let _this = this;

    let gStats = _this._elements.gMainStationBoardStats
      .selectAll('g.station-board-stats')
      .data(_this._stationBoardStats, function (d) {
        return d.stop.id;
      });
    let gNew = gStats.enter()
      .append('g')
      .attr('class', 'station-board-stats ' + classes['station-board-stats']);

    gNew.append('circle');
    gNew.on('mouseover', function (s) {
      console.log(s.stop.name + ':' + s.delayed + '/' + s.total);
    });


    gStats.transition()
      .attr('transform', function (p) {
        return 'translate(' + _this._scales.x(p.x) + ',' + (_this._scales.y(p.y)) + ')';
      });
    gStats.select('circle')
      .attr({
        r: function (d) {
          return d.total + 1;
        }

      })
      .style({
        'stroke-width': function (d) {
          return 5 * d.delayed;
        }
      });

    gStats.exit().remove()
  };

  _renderD3(el, newProps) {
    let _this = this;


    let {lngMin, lngMax, latMin, latMax}  = newProps.bounds;
    _this._scales = {
      x: d3.scale.linear().range([0, _this._dim.width]).domain([lngMin, lngMax]),
      y: d3.scale.linear().range([0, _this._dim.height]).domain([latMax, latMin])
    };


    _this._updateData(newProps.bounds, newProps.positions, newProps.stats)
      ._renderD3TrainPositions(el)
      ._renderD3StationBoardStats(el);


  }

  render() {
    const {count, positions, dispatch} = this.props;
    return (
      <div></div>
    );
  }
}

export default PositionMapTrains;
