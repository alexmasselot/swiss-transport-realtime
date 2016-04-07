import React from 'react';
import {Provider} from 'react-redux';

import data  from '../data/stationboard-1';
import PositionMapTrains from '../../components/PositionMapCFF';
import SpecsOne from './SpecsOne';


export default React.createClass({
  render() {
    return (
      <div>
        <SpecsOne title="zoom=11">
          <div>
            <PositionMapTrains
              bounds={{
              lngMin:10,
              lngMax:10.1,
              latMin:45,
              latMax:45.1
              }}
              zoom={11}
              width={150}
              height={150}
              stationBoardStats={data}
            />
          </div>
        </SpecsOne>
        <SpecsOne title="zoom=9">
          <div>
            <PositionMapTrains
              bounds={{
              lngMin:10,
              lngMax:10.1,
              latMin:45,
              latMax:45.1
              }}
              zoom={9}
              width={150}
              height={150}
              stationBoardStats={data}
            />
          </div>
        </SpecsOne>
        <SpecsOne title="zoom=7">
          <div>
            <PositionMapTrains
              bounds={{
              lngMin:10,
              lngMax:10.1,
              latMin:45,
              latMax:45.1
              }}
              zoom={7}
              width={150}
              height={150}
              stationBoardStats={data}
            />
          </div>
        </SpecsOne>
      </div>
    );
  }
});
