import React, {Component} from 'react';
import ReactDOM  from 'react-dom';
import {connect} from 'react-redux';
import Dimensions from 'react-dimensions'

import styles from './StationBoardDetails.css';


class StationBoard extends Component {
  constructor(props) {
    super(props);
  }


  render(newProps) {
    let _this = this;
    let {containerWidth, containerHeight} = _this.props;

    let details = _this.props.StationBoard.details;
    switch (details.status) {
      case 'unavailable':
        return <span>-</span>
      case 'error' :
        return <span>error</span>
      case 'success':
        let {stop} = details.board;
        return (
          <div className={styles.station_board} style={{width:containerWidth, height:containerHeight}}>{stop.name}</div>
        );
      default:
      {
        return <span>WTF?</span>
      }
    }
  }
}

export default connect(state => state)(Dimensions()(StationBoard));
