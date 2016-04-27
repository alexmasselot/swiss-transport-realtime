import React, {Component} from 'react';
import Dimensions from 'react-dimensions';
import ReactDOM  from 'react-dom';
import '../../node_modules/materialize-css/bin/materialize.css';
import 'materialize-css';

import Introduction from './Introduction';
import Architecture from './Architecture';


class About extends Component {
  componentDidUpdate() {
    console.log('componentDidUpdate');
    $('.scrollspy').scrollSpy();

  }

  componentDidMount() {
    console.log('componentDidMount');
//    $(".button-collapse").sideNav({edge:'right'});
    $('.toc-wrapper .table-of-contents').pushpin({ top: $('.toc-wrapper').offset().top });
    $('.scrollspy').scrollSpy();
  }

  render() {
    //yeaah, that's ugly, but franky, I'm lost with containerHeight and flex css
    const h = d3.select('#main').node().getBoundingClientRect().height;
    return (
      <div className="row" style={{height:h}}>
        <div className="col s10">
          <div id="introduction" className="section scrollspy">
            <Introduction/>
          </div>

          <div id="architecture" className="section scrollspy">
            <Architecture/>
          </div>

          <div id="initialization" className="section scrollspy">
            <p style={{height:500}}>Content </p>
          </div>        </div>
        <div className="col hide-on-small-only s2">
          <div className="toc-wrapper pin-top" style={{top: '0px'}}>
            <div style={{height: '1px'}}>
              <ul className="section table-of-contents">
                <li><a href="#introduction"  className="">Introduction</a></li>
                <li><a href="#architecture" className="">Architecture</a></li>
                <li><a href="#initialization" className="">Intialization</a></li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    );
  }
};

export default Dimensions()(About);
