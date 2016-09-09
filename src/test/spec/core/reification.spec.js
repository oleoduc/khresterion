/**
 * Created by Khresterion on 3/6/15.
 * test for kengine.reification
 */

describe('reificationTest', function (){
    "use strict";

    beforeEach(module('kengine.reification'));

    var reificator;

    beforeEach(inject(function(_reificator_){
        reificator = _reificator_;
    }));

    it('should reify from server', function () {
        var a = "5.0";
        expect(reificator.reifyFromServer(a,'Number')).toBe(5.0);
    });

    it('should reify to server', function () {
        var b = 7.5;
        expect(reificator.reifyFromServer(b,'Integer')).toBe(7);
    });
});
