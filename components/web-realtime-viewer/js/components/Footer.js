import React, {Component, Provider} from 'react';
import ReactDOM  from 'react-dom';
import {connect} from 'react-redux';
import Dimensions from 'react-dimensions'
import styles from './Footer.css';
import matStyles from 'materialize-css/bin/materialize.css';

class Home extends Component {
  render() {
    let _this = this;
    return (
      <div className="container" style={{height:'100%'}}>
        <div className="row noBottomMargin" style={{height:'100%'}}>
          <div className="col s2">&nbsp;</div>
          <div className="col s8">made with fun by OCTO Technology Suisse - <a href="www.octo.ch">www.octo.ch</a></div>
          <div className="col s2">x</div>
        </div>
      </div>
    );
  }
}


export default Dimensions()(Home);
