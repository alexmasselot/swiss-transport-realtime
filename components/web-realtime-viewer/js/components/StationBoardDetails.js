import React, {Component} from 'react';
import ReactDOM  from 'react-dom';
import {connect} from 'react-redux';
import Dimensions from 'react-dimensions'
import dateFormat from 'dateformat';

import styles from './StationBoardDetails.css';
import matStyles from 'materialize-css/bin/materialize.css';


const fBoardTR = function(evt){
  let dep = dateFormat(new Date(evt.departureTimestamp), "HH:MM");
  return (<tr key={evt.train.id}>
    <td>{evt.train.name}</td>
    <td>{dep}</td>
    <td>{evt.train.lastStopName}</td>
  </tr>)
};

const fActualStationBoard = function (board, width, height) {
  return <div className={styles.station_board} style={{width:width, height:height}}>
    <div >{board.stop.name}</div>
    <table className={matStyles.striped}>
      <tbody>
      {_.chain(board.events)
        .values()
        .sortBy(function(e){
          return -e.departureTimestamp;
        })
        .map(function(e){
          return fBoardTR(e);
        })
        .value()
      }
      </tbody>
    </table>
  </div>

}

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
        return fActualStationBoard(details.board, containerWidth, containerHeight);
      default:
      {
        return <span>WTF?</span>
      }
    }
  }
}

export default connect(state => state)(Dimensions()(StationBoard));
