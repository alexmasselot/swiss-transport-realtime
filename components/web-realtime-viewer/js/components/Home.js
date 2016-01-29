import React, {Component} from 'react';
import ReactDOM  from 'react-dom';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import * as TrainPositionActions from '../actions/TrainPositionActions';
import * as MapLocationActions from '../actions/MapLocationActions';
import styles from '../../css/app.css';

import PositionMap from './PositionMap';
import Timer from './Timer';

class Home extends Component {
  render() {
    const {MapLocation, TrainPosition, dispatch} = this.props;
    const actions = {
      ...bindActionCreators(TrainPositionActions, dispatch)
      , ...bindActionCreators(MapLocationActions, dispatch)
    };
      let tTrain = _.chain(TrainPosition.positions)
      .map('timeStamp')
      .max()
      .value();

    return (
      <main>
        <h4 className={styles.text}>Positions updated on map <Timer t0={TrainPosition.timestamp}/> ago. Last train updated from CFF <Timer t0={tTrain}/> ago</h4>
        <PositionMap height={400}
                     width={600}
                     positions={TrainPosition.positions}
                     location={MapLocation.location}
                     onLocationChanged={actions.updateLocation}
        />
      </main>
    );
  }
}

export default connect(state => state)(Home)
