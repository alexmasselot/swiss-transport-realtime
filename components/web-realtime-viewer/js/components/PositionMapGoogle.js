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
    const {location, height, width, positions, onLocationChanged} = this.props;
    let center = location.center;
    let zoom = location.zoom;

    console.log(new Date(), 'PositionMapGoogle.render', center, zoom)

    let scaleFactor = 2**zoom;

    let lngMin = center.lng - width / 2 / scaleFactor;
    let lngMax = center.lng + width / 2 / scaleFactor;
    let latMin = center.lat - height / 2 / scaleFactor;
    let latMax = center.lat + height / 2 / scaleFactor;
    let coord2point = {
      x: function (lng) {
        return (lng - center.lng) * scaleFactor;
      },
      y: function (lat) {
        return (center.lat - lat ) * scaleFactor;
      }
    };
    return (
      <div style={{height:height, width:width}}>
        <GoogleMap
          center={center}
          zoom={zoom}
          onBoundsChange={onLocationChanged}
        >
          <PositionMapTrains
            lat={center.lat}
            lng={center.lng}
            coord2point={coord2point}
            positions={positions}
            height={height}
            width={width}
          />
        </GoogleMap></div>
    );
  }
}

export default PositionMapGoogle;
