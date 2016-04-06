import React from 'react';
import {Provider} from 'react-redux';
import StationBoardSpecs from './component-specs/StationBoardSpecs';
import PositionMapTrainSpecs from './component-specs/PositionMapTrainSpecs';

export default React.createClass({
  render() {
    return (
      <div>
        <StationBoardSpecs />
        <PositionMapTrainSpecs/>
      </div>
    );
  }
});
