import React, {Component} from 'react';
import ReactDOM  from 'react-dom';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import * as TrainPositionActions from '../actions/TrainPositionActions';
import styles from '../../css/app.css';
import classes from './PositionMap.css'

import PositionMapGoogle from './PositionMapGoogle';
import PositionMapTrains from './PositionMapTrains';


class PositionMap extends Component {


  render() {
    let _this = this;
    const {positions, location, dispatch, height, width, onLocationChanged} = this.props;

    return (
      <div>
        <div className={classes.superposeContainer}>
          <div className={classes.superposeItem}>
            <PositionMapGoogle
              positions={positions}
              location={location}
              height={height}
              width={width}
              onLocationChanged={onLocationChanged}
            />
          </div>
          <div className={classes.superposeItem}>
          </div>
        </div>
      </div>
    );
  }
}

export default PositionMap;
