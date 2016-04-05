import React, {Component} from 'react';
import ReactDOM  from 'react-dom';
import {connect} from 'react-redux';
import Dimensions from 'react-dimensions'

import styles from './StationBoard.css';


class StationBoard extends Component {
  constructor(props) {
    super(props);
  }


  render(newProps) {
    let _this = this;
    let {stop,containerWidth, containerHeight} = _this.props;
    return (
      <div className={styles.station_board} style={{width:containerWidth, height:containerHeight}}>{stop.name}</div>
    );
  }
}

  export default connect(state => state)(Dimensions()(StationBoard));
