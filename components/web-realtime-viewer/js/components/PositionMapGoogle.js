import React, {Component} from 'react';
import ReactDOM  from 'react-dom';
import {connect} from 'react-redux';
import d3 from 'd3';
import {bindActionCreators} from 'redux';
import * as TrainPositionActions from '../actions/TrainPositionActions';
import styles from '../../css/app.css';
import classes from './PositionMapGoogle.css'
import shouldPureComponentUpdate from 'react-pure-render/function';
import GoogleMap from 'google-map-react';
import PositionMapTrains from './PositionMapTrains';
import PositionMapStationBoardStats from './PositionMapTrains';


class PositionMapGoogle extends Component {
  componentDidMount() {
    var _this = this;
  }

  shouldComponentUpdate = shouldPureComponentUpdate;

  _onBoundsChange(){
    let _this = this;
  }

  render() {
    let _this = this;
    const {location, height, width, positions, stationBoardStats, onLocationChanged} = _this.props;
    let center = location.center;
    let zoom = location.zoom;


    return (
      <div style={{height:height, width:width}}>
        <GoogleMap
          center={center}
          zoom={zoom}
          onChange={onLocationChanged}
        >
          <PositionMapTrains
            lat={location.bounds.latMax}
            lng={location.bounds.lngMin}
            bounds={location.bounds}
            positions={positions}
            stats={stationBoardStats}
            height={height}
            width={width}
          />
        </GoogleMap></div>
    );
  }
}

export default PositionMapGoogle;
