import React, {Component} from 'react';
import ReactDOM  from 'react-dom';
import {connect} from 'react-redux';
import Dimensions from 'react-dimensions'

import {bindActionCreators} from 'redux';
import * as TrainPositionActions from '../actions/TrainPositionActions';
import styles from '../../css/app.css';
import classes from './PositionMap.css'

import PositionMapGoogle from './PositionMapGoogle';


class PositionMap extends Component {


  render() {
    let _this = this;
    const {positions, stationBoardStats, location, dispatch, containerHeight, containerWidth, onLocationChanged} = this.props;

    return (
      <div>
        <div className={classes.superposeContainer}>
          <div className={classes.superposeItem}>
            <PositionMapGoogle
//              positions={positions}
//              stationBoardStats={stationBoardStats}
//              location={location}
              height={containerHeight}
              width={containerWidth}
//              onLocationChanged={onLocationChanged}
            />
          </div>
          <div className={classes.superposeItem}>
          </div>
        </div>
      </div>
    );
  }
}

export default Dimensions()(PositionMap);
