import React from 'react';
import SpecsGroup from './component-specs/SpecsGroup';
import StationBoardSpecs from './component-specs/StationBoardSpecs';
import PositionMapTrainSpecs from './component-specs/PositionMapTrainSpecs';

export default React.createClass({
  render() {
    return (
      <div>
        <SpecsGroup title="Station boards">
          <StationBoardSpecs />
        </SpecsGroup>
        <SpecsGroup title="Train positions" comment="3 different trains, 2 categories, 'S x' has no bearing information">
          <PositionMapTrainSpecs/>
        </SpecsGroup>
      </div>
    );
  }
});
