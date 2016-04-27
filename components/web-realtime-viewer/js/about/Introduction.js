import React, {Component} from 'react';
import Dimensions from 'react-dimensions';
import '../../node_modules/materialize-css/bin/materialize.css';

class Introduction extends Component {
  render() {
    return (
      <div style={{height:500}}>
        <h3>Introduction </h3>
        blablabla blablabla
      </div>
    );
  };
};

export default Dimensions()(Introduction);
