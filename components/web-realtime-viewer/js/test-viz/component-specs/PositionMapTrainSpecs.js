import React from 'react';
import {Provider} from 'react-redux';

import data  from '../data/train-1';
import PositionMapTrains from '../../components/PositionMapTrains';
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
              positions={data}
            />
          </div>
        </SpecsOne>
        <SpecsOne title="zoom=10">
          <div>
            <PositionMapTrains
              bounds={{
              lngMin:10,
              lngMax:10.1,
              latMin:45,
              latMax:45.1
              }}
              zoom={10}
              width={150}
              height={150}
              positions={data}
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
              positions={data}
            />
          </div>
        </SpecsOne>
      </div>
    );
  }
});
