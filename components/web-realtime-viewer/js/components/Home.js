import React, {Component} from 'react';
import ReactDOM  from 'react-dom';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import * as TrainPositionActions from '../actions/TrainPositionActions';
import styles from '../../css/app.css';

import PositionMapText from './PositionMapText';

class Home extends Component {


  render() {
    const {count, positions, dispatch} = this.props;
    const actions = bindActionCreators(TrainPositionActions, dispatch);
    console.log('Home.props', this.props)
    return (
      <main>
        <h1 className={styles.text}>{count} trains</h1>
        <PositionMapText height="500" width="1000" positions={positions}/>
      </main>
    );
  }
}

export default connect(state => state.TrainPosition)(Home)
