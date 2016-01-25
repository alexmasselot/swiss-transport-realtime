import React from 'react';
import {Provider} from 'react-redux';
import configureStore from '../store/configureStore';
import Home from '../components/Home';
import WSTrainPosition from '../store/WSTrainPosition';

const store = configureStore();

console.log('store=', store)
console.log('haha')
new WSTrainPosition({store});
console.log('haha')
export default React.createClass({
  render() {
    return (
      <div>
        <Provider store={store}>
          <Home />
        </Provider>
      </div>
    );
  }
});
