import React, {Component} from 'react';
import ReactDOM  from 'react-dom';
import {connect} from 'react-redux';
import d3 from 'd3';
import {bindActionCreators} from 'redux';
import * as MapLocationActions from '../actions/MapLocationActions';
import styles from '../../css/app.css';
import classes from './PositionMapGoogle.css'
import shouldPureComponentUpdate from 'react-pure-render/function';
import GoogleMap from 'google-map-react';
import PositionMapGoogleOverlayData from './PositionMapGoogleOverlayData';


class PositionMapGoogle extends Component {
  componentDidMount() {
    var _this = this;
  }

  shouldComponentUpdate = shouldPureComponentUpdate;

  _onBoundsChange() {
    let _this = this;
  }

  render() {
    let _this = this;


    const {height, width, dispatch, store} = _this.props;

    const actions = {
      ...bindActionCreators(MapLocationActions, dispatch)
    };

    let {center, zoom, bounds} = _this.props.MapLocation.location;

    return (
      <div style={{height:height, width:width}}>
        <GoogleMap
          center={center}
          zoom={zoom}
          onChange={actions.updateLocation}
        >

          <PositionMapGoogleOverlayData lat={bounds.latMax}
                                        lng={bounds.lngMin}
                                        height={height}
                                        width={width}
                                        zoom={zoom}
                                        store={store}

          />

        </GoogleMap></div>
    );
  }
}

export default connect(state => state)(PositionMapGoogle);
