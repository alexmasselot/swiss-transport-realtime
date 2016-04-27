import React, {Component} from 'react';
import Dimensions from 'react-dimensions';
import '../../node_modules/materialize-css/bin/materialize.css';

class Architecture extends Component {
  render() {
    return (
      <div style={{height:500}}>
        <h3>Architecture </h3>
        <ul>
          <li>feeder</li>
          <li>message broker</li>
          <li>stream processing</li>
          <li>web display</li>
        </ul>
      </div>
    );
  };
};

export default Dimensions()(Architecture);
