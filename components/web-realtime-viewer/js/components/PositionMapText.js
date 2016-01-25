import React, {Component} from 'react';
import ReactDOM  from 'react-dom';
import {connect} from 'react-redux';
import d3 from 'd3';
import {bindActionCreators} from 'redux';
import * as TrainPositionActions from '../actions/TrainPositionActions';
import styles from '../../css/app.css';
import classes from './PositionMapText.css'


class PositionMapText extends Component {

  componentDidMount() {
    var _this = this;
    _this._setupD3(ReactDOM.findDOMNode(this));

  }

  shouldComponentUpdate(props) {
    this._renderD3(ReactDOM.findDOMNode(this));

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

    _this._domain = {
      x:[5850000,10540000],
      y:[45850000,47800000]
    };
    //_this._domain = {
    //  x:[6385532, 6884036],
    //  y:[46441434, 46653942]
    //};
    _this._scales = {
      x: d3.scale.linear().range([0, _this._dim.width]).domain(_this._domain.x),
      y: d3.scale.linear().range([_this._dim.height, 0]).domain(_this._domain.y)
    };

    d3.select(el).selectAll().empty();
    _this._elements = {
      svg: d3.select(el).append('svg')
        .attr({
          width: _this._dim.width,
          height: _this._dim.height
        })
    };
    _this._elements.gMain = _this._elements.svg.append('g')
      .attr({
        class: 'PositionMapText'
      });

  }

  _renderD3(el) {
    let _this = this;
    console.log('renderd3 PositionMapText', _this.props)
    _this._elements.gMain.selectAll('g').remove();
    _this._elements.gMain.selectAll('g').data(_this.props.positions)
      .enter()
      .append('g')
      .attr({
        transform: function (p) {
          return 'translate('+_this._scales.x(parseInt(p.x))+','+ _this._scales.y(parseInt(p.y))+') rotate('+(-10* p.direction)+')';
        }
      })
      .append('text')
      .attr('class', classes.positionText)
      .text(function (p) {
        return p.name.trim()+' ('+ p.lstopname+')';
      })
  }

  render() {
    const {count, positions, dispatch} = this.props;
    console.log('PositionMapText.props', this.props)
    return (
      <div></div>
    );
  }
}

export default PositionMapText;
