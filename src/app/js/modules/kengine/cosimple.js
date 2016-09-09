/**
 * Created by ndriama on 29/03/16.
 */

var kbsimple = angular.module('modSimple', ['kengine.property', 'ngSanitize']);

kbsimple.controller(
    'StringController',
    ['$scope', 'reificator', 'propertyService', '$sce',
     function ($scope, reificator, propertyService, $sce) {
         "use strict";

         var ctrl;
         ctrl = this;
         ctrl.trustedText = undefined;

         this.updateProperty = function (event) {
             var isValid;

             if (ctrl.property.value === undefined) {

                 isValid = false;
             } else if (reificator.isPropertyEmpty(
                     ctrl.property)) {
                 if (event !== undefined) {
                     isValid =
                         (event.keyCode === 13) || (event.type === 'blur');
                 } else {
                     isValid = false;
                 }
                 if (isValid) {
                     propertyService.remove($scope, ctrl.bo,
                                            ctrl.property,
                                            'entityChanged');
                 }
             } else {

                 if (event !== undefined) {
                     isValid =
                         (event.keyCode === 13) || (event.type === 'blur') ||
                         (event.type === 'click');
                 } else {
                     isValid = false;
                 }
                 if (isValid) {
                     if (ctrl.uppercase === '1') {
                         ctrl.property.value =
                             ctrl.property.value.toUpperCase();
                     }
                     propertyService.update($scope, ctrl.bo,
                                            ctrl.property,
                                            'entityChanged');
                 } else {
                     isValid = false;
                 }
             }
         };
     }]);

kbsimple.directive('coSimple', [function () {
    "use strict";
    return {
        priority: 0,
        restrict: 'EA',
        replace: true,
        transclude: false,
        templateUrl: 'views/partials/tpl/cosimple.html',
        controller: 'StringController',
        controllerAs: 'simple',
        bindToController: true,
        scope: {
            property: "=property",
            bo: "@",
            textclass: "@",
            maxlength: "@",
            uppercase: "@"
        }
    };
}
]);
kbsimple.directive('coString', [function () {
    "use strict";
    return {
        priority: 0,
        restrict: 'EA',
        replace: true,
        transclude: false,
        templateUrl: 'views/partials/tpl/cosimple.html',
        controller: 'StringController',
        controllerAs: 'simple',
        bindToController: true,
        scope: {
            property: "=property",
            bo: "@",
            textclass: "@",
            maxlength: "@",
            uppercase: "@"
        }
    };
}
]);

kbsimple.directive('coClause', [function () {
    "use strict";
    return {
        priority: 0,
        restrict: 'EA',
        replace: true,
        transclude: false,
        templateUrl: 'views/partials/tpl/coclause.html',
        controller: 'StringController',
        controllerAs: 'simple',
        bindToController: true,
        scope: {
            property: "=property",
            bo: "@",
            textclass: "@",
            maxlength: "@",
            uppercase: "@"
        }
    };
}
]);


kbsimple.directive('coNumber', [function () {
    "use strict";
    return {
        priority: 0,
        restrict: 'EA',
        replace: true,
        transclude: false,
        templateUrl: 'views/partials/tpl/conumber.html',
        controller: 'StringController',
        controllerAs: 'simple',
        bindToController: true,
        scope: {
            property: "=property",
            bo: "@",
            textclass: "@",
            maxlength: "@",
            uppercase: "@"
        }
    };
}
]);


kbsimple.directive('coInteger', [function () {
    "use strict";
    return {
        priority: 0,
        restrict: 'EA',
        replace: true,
        transclude: false,
        templateUrl: 'views/partials/tpl/cointeger.html',
        controller: 'StringController',
        controllerAs: 'simple',
        bindToController: true,
        scope: {
            property: "=property",
            bo: "@",
            textclass: "@",
            maxlength: "@",
            uppercase: "@"
        }
    };
}
]);

kbsimple.directive('coMessage', [function () {
    "use strict";
    return {
        priority: 0,
        restrict: 'EA',
        replace: true,
        transclude: false,
        templateUrl: 'views/partials/tpl/comessage.html',
        scope: {
            property: "=property",
            bo: "@"
        }
    };
}
]);

kbsimple.controller('ComboController',
                    ['$scope', 'reificator', 'propertyService', function ($scope, reificator,
                                                                          propertyService) {
                        "use strict";
                        var ctrl;
                        ctrl = this;

                        ctrl.isVisible = false;

                        ctrl.updateProperty = function (event) {
                            var isValid;

                            isValid = true;

                            if (ctrl.property.value === undefined) {

                                isValid = false;
                            } else {

                                if (isValid) {

                                    propertyService.update($scope, ctrl.bo, ctrl.property,
                                                           'entityChanged');
                                } else {
                                    isValid = false;
                                }
                            }
                        };

                        /*update for multiple*/
                        ctrl.applySelected = function (selected, event) {

                            if ((selected !== null) && (selected !== undefined)) {
                                if ((ctrl.property.value === selected.value) &&
                                    (selected.isIncluded)) {
                                    propertyService.remove($scope, ctrl.bo, ctrl.property,
                                                           'entityChanged');
                                } else {
                                    ctrl.property.value = selected.value;
                                    propertyService.update($scope, ctrl.bo, ctrl.property,
                                                           'entityChanged');
                                }
                            } else {
                                propertyService.remove($scope, ctrl.bo, ctrl.property,
                                                       'entityChanged');
                            }
                        };

                    }]);

kbsimple.directive('coCombobox', function () {
    "use strict";
    return {
        priority: 1,
        restrict: 'EA',
        replace: true,
        controller: 'ComboController',
        controllerAs: 'vm',
        bindToController: true,
        transclude: false,
        templateUrl: 'views/partials/tpl/colistbox.html',
        scope: {
            property: "=property",
            bo: "@",
            comboclass: "@"
        },
        link: {
            post: function (scope, iElement, iAttrs) {
                var targetbox, toggle, hide, ctrl;
                targetbox = angular.element(iElement).children('.combobase')
                    .children('.selectbox').children('.comboinput');
                ctrl = scope.vm;

                toggle = function () {
                    if (ctrl.isVisible === false) {
                        ctrl.isVisible = true;

                    } else if (ctrl.isVisible === true) {
                        ctrl.isVisible = false;
                    } else {
                        ctrl.isVisible = false;
                    }
                    scope.$digest();
                };

                hide = function () {
                    ctrl.isVisible = false;
                    scope.$digest();
                };

                targetbox.children('button').first().on('click', toggle);
                targetbox.children('input').first().on('click', toggle);
                angular.element(iElement).on('mouseleave', hide);
            }
        }
    };
});

kbsimple.controller('ComboInstanceController',
                    ['$scope', 'reificator', 'propertyService', function ($scope, reificator,
                                                                          propertyService) {
                        "use strict";
                        var ctrl;
                        ctrl = this;

                        ctrl.isVisible = false;

                        /*update for multiple*/
                        ctrl.applySelected = function (selected, event) {

                            if ((selected !== null) && (selected !== undefined)) {
                                if ((ctrl.property.value === selected.value) &&
                                    (selected.isIncluded)) {
                                    propertyService.remove($scope, ctrl.bo, ctrl.property,
                                                           'entityChanged');
                                } else {
                                    ctrl.property.value = selected.value;
                                    propertyService.linkExisting($scope, ctrl.bo, ctrl.property,
                                                                 'entityChanged');
                                    
                                }
                            } else {
                                propertyService.remove($scope, ctrl.bo, ctrl.property,
                                                       'entityChanged');
                            }
                        };

                    }]);

kbsimple.directive('coInstancecombo', function () {
    "use strict";
    return {
        priority: 1,
        restrict: 'EA',
        replace: true,
        controller: 'ComboInstanceController',
        controllerAs: 'vm',
        bindToController: true,
        transclude: false,
        templateUrl: 'views/partials/tpl/corellistbox.html',
        scope: {
            property: "=property",
            bo: "@",
            comboclass: "@"
        },
        link: {
            post: function (scope, iElement, iAttrs) {
                var targetbox, toggle, hide, ctrl;
                targetbox = angular.element(iElement).children('.combobase')
                    .children('.selectbox').children('.comboinput');
                ctrl = scope.vm;

                toggle = function () {
                    if (ctrl.isVisible === false) {
                        ctrl.isVisible = true;

                    } else if (ctrl.isVisible === true) {
                        ctrl.isVisible = false;
                    } else {
                        ctrl.isVisible = false;
                    }
                    scope.$digest();
                };

                hide = function () {
                    ctrl.isVisible = false;
                    scope.$digest();
                };

                targetbox.children('button').first().on('click', toggle);
                targetbox.children('input').first().on('click', toggle);
                angular.element(iElement).on('mouseleave', hide);
            }
        }
    };
});

kbsimple.controller('EditableComboController',
                    ['$scope', 'reificator', 'propertyService', function ($scope, reificator,
                                                                          propertyService) {
                        "use strict";
                        var ctrl;
                        ctrl = this;

                        ctrl.isVisible = false;

                        ctrl.updateProperty = function (event) {
                            var isValid;

                            if (ctrl.property.value === undefined) {

                                isValid = false;
                            } else if (reificator.isPropertyEmpty(ctrl.property)) {
                                if (event !== undefined) {
                                    isValid = (event.keyCode === 13) || (event.type === 'blur');
                                } else {
                                    isValid = false;
                                }
                                if (isValid) {
                                    propertyService.remove($scope, ctrl.bo, ctrl.property,
                                                           'entityChanged');
                                }
                            } else {

                                if (event !== undefined) {
                                    isValid =
                                        (event.keyCode === 13) || (event.type === 'blur') ||
                                        (event.type === 'click');
                                } else {
                                    isValid = false;
                                }
                                if (isValid) {
                                    propertyService.update($scope, ctrl.bo, ctrl.property,
                                                           'entityChanged');
                                } else {
                                    isValid = false;
                                }
                            }
                        };

                        /*update for multiple*/
                        ctrl.applySelected = function (selected, event) {

                            if ((selected !== null) && (selected !== undefined)) {
                                if ((ctrl.property.value === selected.value) &&
                                    (selected.isIncluded)) {
                                    propertyService.remove($scope, ctrl.bo, ctrl.property,
                                                           'entityChanged');
                                } else {
                                    ctrl.property.value = selected.value;
                                    propertyService.update($scope, ctrl.bo, ctrl.property,
                                                           'entityChanged');
                                }
                            } else {
                                propertyService.remove($scope, ctrl.bo, ctrl.property,
                                                       'entityChanged');
                            }
                        };

                    }]);

kbsimple.directive('coComboboxEditable', function () {
    "use strict";
    return {
        priority: 1,
        restrict: 'EA',
        replace: true,
        controller: 'EditableComboController',
        controllerAs: 'vm',
        bindToController: true,
        transclude: false,
        templateUrl: 'views/partials/tpl/colistbox-editable.html',
        scope: {
            property: "=property",
            bo: "@",
            comboclass: "@"
        },
        link: {
            post: function (scope, iElement, iAttrs) {
                var targetbox, toggle, hide, ctrl;
                targetbox = angular.element(iElement).children('.combobase')
                    .children('.selectbox').children('.comboinput');

                ctrl = scope.vm;
                toggle = function () {
                    if (ctrl.isVisible === false) {
                        ctrl.isVisible = true;

                    } else if (ctrl.isVisible === true) {
                        ctrl.isVisible = false;
                    } else {
                        ctrl.isVisible = false;
                    }
                    scope.$digest();
                };

                hide = function () {
                    ctrl.isVisible = false;
                    scope.$digest();
                };

                targetbox.children('button').first().on('click', toggle);
                targetbox.children('input').first().on('click', toggle);
                angular.element(iElement).on('mouseleave', hide);
            }
        }
    };
});

kbsimple.directive('coTitle', function () {
    "use strict";
    return {
        priority: 0,
        restrict: 'EA',
        replace: true,
        transclude: false,
        template: '<span>{{property.label}}</span>',
        scope: {
            property: "=property",
            bo: "@"
        }
    };

});

kbsimple.directive('coToggle', ['propertyService', '$timeout', function (propertyService,
                                                                         $timeout) {
    "use strict";
    return {
        priority: 0,
        restrict: 'EA',
        replace: true,
        transclude: false,
        templateUrl: 'views/partials/tpl/cotoggle.html',
        scope: {
            property: "=property",
            bo: "@"
        },
        link: {
            post: function (scope, iElement, iAttrs) {

                var toggleInclude, toggleExclude, getCurrent, targetbox;

                toggleInclude = function () {
                    if (scope.state0 === 1) {
                        propertyService.notinclude(scope, scope.bo, scope.property,
                                                   'entityChanged');
                        scope.state0 = 0;
                    } else {
                        propertyService.include(scope, scope.bo, scope.property, 'entityChanged');
                        scope.state0 = 1;
                    }
                };
                toggleExclude = function () {
                    if (scope.state1 === 1) {
                        propertyService.notexclude(scope, scope.bo, scope.property,
                                                   'entityChanged');
                        scope.state1 = 0;
                    } else {
                        propertyService.exclude(scope, scope.bo, scope.property, 'entityChanged');
                        scope.state1 = 1;
                    }
                };

                getCurrent = function (newval) {
                    if ((newval !== null) && (newval !== undefined)) {
                        if ((newval.isIncluded !== null) && (newval.isIncluded !== undefined) &&
                            (newval.isExcluded !== undefined)) {
                            scope.property.isIncluded = newval.isIncluded;
                            scope.property.isExcluded = newval.isExcluded;
                            scope.state0 =
                                (newval.isIncluded)? 1: 0;
                            scope.state1 = (newval.isExcluded)? 1: 0;
                        }

                        scope.$digest();
                    }
                };

                scope.$parent.$watch(iAttrs.property, function (newval, oldval) {

                    $timeout(function () {
                        getCurrent(newval);
                    }, 200);

                }, true);

                targetbox = angular.element(iElement).children('.field-container');
                targetbox.children('input').first().on('click', toggleInclude);
                targetbox.children('input').first().next().on('click', toggleExclude);
            }
        }
    };
}
]);

kbsimple.controller('SectionLinkController', ['$window', '$http', function ($window, $http) {
    "use strict";
    var ctrl, baseurl;

    ctrl = this;
    baseurl = document.getElementsByTagName('base')[0].href;
    
    ctrl.openSection = function () {
        $window.location.href =
            baseurl + "etude/edit/section?section_id=" + ctrl.property.value + '&parent_id=' +
            $window.section_id +  '&envkey=' + $window.envkey + '&path=' + ctrl.property.propertypath +
        '&plan_id=' + $window.plan_id;
    };

    ctrl.removeSection = function () {
        if (ctrl.property.value) {
            $http['delete'](
                baseurl + '/section?section_id=' + ctrl.property.value + '&parent_id=' +
                $window.section_id + '&envkey=' + $window.envkey,
                {
                    headers: {
                        'Content-Type': 'application/json',
                        'Cache-Control': 'no-cache',
                        'Expires': 0
                    },
                    timeout: 0,
                    cache: false
                })
                .success(function (data, status, headers,
                                   config) {
                    console.log(data);
                    ctrl.property.value = null;
                }).error(function (data, status, headers,
                                   config) {
                console.log(data);
            });
        } else {
            console.log('no instance');
        }
    };

}]);

kbsimple.directive('coSectionlink', [function () {
    "use strict";
    return {
        priority: 0,
        restrict: 'EA',
        replace: true,
        controller: 'SectionLinkController',
        controllerAs: 'sect',
        bindToController: true,
        transclude: false,
        templateUrl: 'views/partials/tpl/sectionlink.html',
        scope: {
            property: "=property",
            bo: "@"
        }
    };
}]);


kbsimple.directive('coYesno', [function () {
    "use strict";
    return {
        priority: 0,
        restrict: 'EA',
        replace: true,
        transclude: false,
        templateUrl: 'views/partials/tpl/coyesno.html',
        controller: 'ComboController',
        controllerAs: 'combo',
        bindToController: true,
        scope: {
            property: "=property",
            bo: "@"
        }
    };
}
]);