import React, {Component} from 'react';
import ReactDOM  from 'react-dom';
import styles from '../../css/app.css';

import PositionMap from './PositionMap';

class Home extends Component {
  render() {

    return (
      <main>

        <div style={{height:global.window.innerHeight*0.8, width:'80%'}}>
          <PositionMap
          />
        </div>


      </main>
    );
  }
}

export default Home;
