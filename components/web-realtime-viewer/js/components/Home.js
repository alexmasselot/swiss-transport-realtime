import React, {Component, Provider} from 'react';
import ReactDOM  from 'react-dom';
import {connect} from 'react-redux';
import Dimensions from 'react-dimensions'
import styles from '../../css/app.css';
import matStyles from 'materialize-css/bin/materialize.css';

import PositionMap from './PositionMap';
import StationBoardDetails from './StationBoardDetails';
import CFFCLock from './CFFCLock';
import Timer from './Timer';

class Home extends Component {
  render() {
    let _this = this;
    return (
      <div className={matStyles.row} style={{height:this.props.containerHeight, marginBottom:'0px'}}>
        <div className={matStyles.col + ' ' + matStyles.s9} style={{height:this.props.containerHeight}}>
          <PositionMap
            store={_this.props.store}
          />
        </div>
        <div className={matStyles.col + ' '+matStyles.s3}>
          <div style={{width:'100%', height:150}}>
            <CFFCLock />
          </div>
          <div style={{'height': global.window.innerHeight * 0.8 - 150 - 10}}>
            <StationBoardDetails store={_this.props.store}/>
          </div>
        </div>
      </div>
    );
  }
}

export default connect(state => state)(Dimensions()(Home));
