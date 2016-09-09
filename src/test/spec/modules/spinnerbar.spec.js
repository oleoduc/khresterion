/**
 * Created by Khresterion on 3/6/15.
 */

describe('spinner bar', function (){
    "use strict";

    beforeEach(module('ui.showhide'));
    beforeEach(module('modSpinner'));

    var $scope, element;

    beforeEach(inject(function($compile, $rootScope){
        $scope = $rootScope;
        element = $compile("<co-spinner is-loading=\"isLoading\"></co-spinner>")($scope);
    }));

    it('should show', function () {
        $scope.isLoading = true;
        $scope.$digest();
        expect(element.hasClass('ui-show')).toBe(true);
    });

    it('should hide', function () {
        $scope.isLoading = false;
        $scope.$digest();
        expect(element.hasClass('ui-hide')).toBe(true);
    });
});