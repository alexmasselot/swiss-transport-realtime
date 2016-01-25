jest.dontMock('../TrainPositionCollection');
jest.autoMockOff();

let TrainPositionCollection = require('../TrainPositionCollection').default;
let Set = require('collections/set');


describe('TrainPositionCollection', function () {
  it('constructor', function () {
    expect(new TrainPositionCollection()).not.toBeUndefined();
  });
  it('size = 0', function () {
    expect(new TrainPositionCollection().size()).toBe(0);
  });
  it('update different id', function () {
    let tps = new TrainPositionCollection();
    tps.update({id: 1, x: 'a'});
    tps.update({id: 2, x: 'b'});
    tps.update({id: 3, x: 'c'});
    expect(tps.size()).toBe(3);
  });

  it('update same id', function () {
    let tps = new TrainPositionCollection();
    tps.update({id: 1, x: 'a'});
    tps.update({id: 2, x: 'b'});
    tps.update({id: 1, x: 'c'});
    expect(tps.size()).toBe(2);
  });
  it('update idKey not default', function () {
    let tps = new TrainPositionCollection({
      idKey:'pafId'
    });
    tps.update({pafId: 1, x: 'a'});
    tps.update({pafId: 2, x: 'b'});
    tps.update({pafId: 1, x: 'c'});
    expect(tps.size()).toBe(2);
  });
  it('list', function () {
    let tps = new TrainPositionCollection();
    tps.update({id: 1, x: 'a'});
    tps.update({id: 2, x: 'b'});
    tps.update({id: 1, x: 'c'});
    expect(_.orderBy(tps.list(), 'id')).toEqual(_.orderBy([{id: 1, x: 'c'}, {id: 2, x: 'b'}], 'id'));
  });

  it('clearObsolence', function () {
    let tps = new TrainPositionCollection({obsolescenceMS: 25});
    let t = tps.update({id: 1, x: 'a'});
    t.t -=  50; //push it in the past

    tps.update({id: 2, x: 'b'});
    tps.update({id: 3, x: 'c'});
    tps._clearObsolete();
    expect(_.orderBy(tps.list(), 'id')).toEqual(_.orderBy([{id: 2, x: 'b'}, {id: 3, x: 'c'}], 'id'));

  });

});
