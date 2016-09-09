/**
 * Created by ndriama on 30/03/16.
 */

var kbdates = angular.module('modeDate', ['kengine.property']);

kbdates.controller('DateCoherenceController',
                   ['$scope', 'reificator', 'propertyService',
                    function ($scope, reificator, propertyService) {
                        "use strict";

                        var ctrl, notDate;

                        ctrl = this;

                        ctrl.dateOptions = {
                            changeYear: true,
                            changeMonth: true,
                            maxDate: '+50Y',
                            minDate: '-100Y'
                        };
                        ctrl.isValid = true;


                        notDate = function (value) {
                            var invalidDate;

                            invalidDate = false;

                            if ((value === null || value === undefined)) {
                                invalidDate = true;
                            } else if (value.length < 10) {
                                invalidDate = true;
                            } else {
                                invalidDate =
                                    isNaN(new Date(reificator.toUS(value, '/')).getTime());
                            }
                            return invalidDate;
                        };
                        ctrl.updateProperty = function (event, tmp) {

                            var validDate;

                            validDate = true;
                            if ((tmp === undefined) || (tmp === null)) {

                                validDate = true;
                                ctrl.removeProperty();
                            } else if (tmp === '') {
                                validDate = true;
                                ctrl.removeProperty();
                            } else if (notDate(tmp)) {
                                validDate = false;
                            } else {

                                validDate =
                                    (event === undefined)? false: (event.keyCode === 13) ||
                                                                  (event.type === 'blur');
                                if (validDate) {
                                    ctrl.isValid = validDate;
                                    ctrl.property.value = tmp;
                                    propertyService.update($scope, ctrl.bo, ctrl.property,
                                                           'entityChanged');
                                }
                            }
                            ctrl.isValid = validDate;
                            return validDate;
                        };

                        ctrl.removeProperty = function () {

                            if (ctrl.property.valueType === "Entity") {
                                propertyService.remove($scope, ctrl.bo, ctrl.property,
                                                       'entityChanged');
                            } else {
                                propertyService.remove($scope, ctrl.bo, ctrl.property,
                                                       'entityChanged');
                            }
                        };
                    }]);

kbdates.directive('coDate', ['$timeout', 'reificator',
                             function ($timeout, reificator) {
                                 "use strict";
                                 return {
                                     priority: 0,
                                     restrict: 'EA',
                                     replace: true,
                                     transclude: false,
                                     templateUrl: 'views/partials/tpl/codate.html',
                                     controller: 'DateCoherenceController',
                                     controllerAs: 'dtc',
                                     bindToController: true,
                                     scope: {
                                         property: "=property",
                                         bo: "@"
                                     },
                                     link: {
                                         post: function (scope, iElement, iAttrs) {
                                             var updateDataTime, ctrl;

                                             ctrl = scope.dtc;

                                             updateDataTime = function () {
                                                 if ((ctrl.property !== null) &&
                                                     (ctrl.property !== undefined)) {
                                                     var rawdate, rawmonth;

                                                     rawdate =
                                                         reificator.reifyFromServer(
                                                             ctrl.property.value, 'Date');
                                                     if (rawdate !== undefined &&
                                                         rawdate !== null) {
                                                         rawmonth = rawdate.getMonth() + 1;
                                                         scope.dtime =
                                                             ((rawdate.getDate() >= 10)
                                                                 ? rawdate.getDate(): '0' +
                                                             rawdate.getDate()) +
                                                             '/' +
                                                             ((rawmonth >= 10)? rawmonth: '0' +
                                                             rawmonth) + '/' +
                                                             rawdate.getFullYear();
                                                     } else {
                                                         ctrl.dtime = null;
                                                     }
                                                 } else {

                                                     ctrl.dtime = null;

                                                 }
                                                 scope.$digest();
                                             };

                                             scope.$parent.$watch(iAttrs.property,
                                                                  function (newval, oldval) {

                                                                      $timeout(function () {
                                                                          updateDataTime();
                                                                      }, 500);

                                                                  }, true);
                                         }
                                     }
                                 };
                             }
]);
