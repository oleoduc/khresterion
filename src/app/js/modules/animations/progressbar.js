/**
 * Created by Khresterion on 3/6/15.
 * replace previous progress.js
 * Remove unused code
 */

var mprogress = angular.module('modProgress', ['mm.foundation']);

mprogress.factory('progressService', ['$interval', function ($interval) {
    "use strict";

    var newProgressService;

    newProgressService =  {
        promise: {},
        start: function (scope, delay, maxcount, increment, altmessage) {

            scope.pvalue = 0;
            this.promise = $interval(function () {

                if (scope.pvalue  + increment > 100) {
                    scope.pvalue  = 0;
                    scope.firstMessage = altmessage;
                } else {
                    scope.pvalue = scope.pvalue + increment;
                }
            }, delay, maxcount);
        },
        stop: function (scope) {
            $interval.cancel(this.promise);
            scope.pvalue = 100;
        }
    };

    return newProgressService;
}]);

mprogress.controller('CoProgressController', ['$scope','progressService', function ($scope,progressService) {
    "use strict";

    var initFunction,ctrl;

    ctrl = this;

    initFunction = function (initalvalue, delay, maxnbloop, increment) {
        ctrl.pvalue = initalvalue;

        $scope.$on(ctrl.startEvent, function (event, data) {
            progressService.start(ctrl, delay, maxnbloop, increment, ctrl.secondMessage);
        });
        $scope.$on(ctrl.endEvent, function (event, data) {
            progressService.stop(ctrl);
        });
    };

    initFunction(2, 200, 100, 2);
}]);

mprogress.directive('coProgress', [function () {
    "use strict";
    return {
        priority: 0,
        restrict: 'EA',
        replace: true,
        transclude: true,
        template: '<div ng-show="pro.isSaving"><div class="progresslayer"></div><div class="barlayer">'+
        '<span>{{pro.firstMessage}} {{pro.pvalue}}%</span><div><div progressbar value="pro.pvalue" type="pro.type">'+
        '<span class="meter">{{pro.pvalue}}%</span></div></div></div></div>',
        controller: 'CoProgressController',
        controllerAs: 'pro',
        bindToController: true,
        scope: {
            pvalue: "@pvalue",
            type: "@type",
            firstMessage: "@",
            secondMessage: "@",
            startEvent: "@startEvent",
            endEvent: "@endEvent",
            isSaving: "="
        }
    };
}]);
