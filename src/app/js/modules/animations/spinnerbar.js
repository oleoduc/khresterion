/**
 * Created by Khresterion on 3/6/15.
 * replace old spinner bar by a directive. Depends on angular-ui-utils
 * <div class="spinner spinner-wheel" ui-show="isLoading"></div>
 */
var modSpinner = angular.module('modSpinner',[]);

modSpinner.directive('coSpinner', [function(){
    "use strict";
    return {
        priority: 0,
        restrict: 'EA',
        replace: true,
        transclude: false,
        template: '<div ng-show="isLoading"><div class="spinner spinner-bar"></div></div>',
        scope: {
            isLoading: "="
        }
    };
}]);
