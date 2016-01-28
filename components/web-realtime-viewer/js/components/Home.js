import React, {Component} from 'react';
import ReactDOM  from 'react-dom';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import * as TrainPositionActions from '../actions/TrainPositionActions';
import * as MapLocationActions from '../actions/MapLocationActions';
import styles from '../../css/app.css';

import PositionMap from './PositionMap';

class Home extends Component {
  render() {
    const {MapLocation, TrainPosition, dispatch} = this.props;
    const actions = {
      ...bindActionCreators(TrainPositionActions, dispatch)
      , ...bindActionCreators(MapLocationActions, dispatch)
    };
    return (
      <main>
        <h1 className={styles.text}>{TrainPosition.count} trains</h1>
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
