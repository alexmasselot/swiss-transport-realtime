import React from 'react';
import {Provider} from 'react-redux';
import StationBoardSpecs from './component-specs/StationBoardSpecs';

export default React.createClass({
  render() {
    return (
      <div>
        <StationBoardSpecs />
      </div>
    );
  }
});
