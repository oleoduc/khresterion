/**
 * Created by ndriama on 05/04/16.
 */

angular.module('dueApp',
               ['ngRoute', 'mm.foundation', 'modEtude', 'modSimple', 'modeDate', 'modTextarea',
                'modProgress'])
    .config(['$routeProvider', '$locationProvider',
             function ($routeProvider, $locationProvider) {
                 $locationProvider.html5Mode(false);
                 //$locationProvider.hashPrefix('!');
             }]);

var modEtude = angular.module('modEtude', []);

modEtude.factory('variableService', ['$http', function ($http) {
    "use strict";
    return {
        findAll: function (baseurl) {
            return $http.get(baseurl + 'variable/find_all',
                             {
                                 headers: {
                                     'Content-Type': 'application/json',
                                     'Cache-Control': 'no-cache',
                                     'Expires': 0
                                 },
                                 timeout: 0,
                                 cache: false
                             })
                .success(function (data, status, headers, config) {

                }).error(function (data, status, headers, config) {

                });
        },
        createVariable: function (baseurl, key, value) {
            return $http.post(baseurl + 'variable/',
                              $.param({
                                          'key': key, 'value': value
                                      }),
                              {
                                  headers: {
                                      'Content-Type': 'application/x-www-form-urlencoded',
                                      'Cache-Control': 'no-cache',
                                      'Expires': 0
                                  },
                                  timeout: 0,
                                  cache: false
                              })
                .success(function (data, status, headers, config) {

                }).error(function (data, status, headers, config) {

                });
        },
        updateVariable: function (baseurl, key, value) {
            return $http.post(baseurl + 'variable/update',
                              $.param({
                                          'key': key, 'value': value
                                      }),
                              {
                                  headers: {
                                      'Content-Type': 'application/x-www-form-urlencoded',
                                      'Cache-Control': 'no-cache',
                                      'Expires': 0
                                  },
                                  timeout: 0,
                                  cache: false
                              })
                .success(function (data, status, headers, config) {

                }).error(function (data, status, headers, config) {

                });
        }
    };

}]);

modEtude.controller('SectionController',
                    ['$scope', '$q', '$timeout', '$window', '$http',
                     '$rootScope',
                     function ($scope, $q, $timeout, $window, $http, $rootScope) {

                         "use strict";
                         $scope.Section0 = undefined;
                         $scope.isSearching = false;

                         var displayInstance, loadInstance, baseurl;

                         baseurl = document.getElementsByTagName('base')[0].href;

                         displayInstance = function (dbid, envkey) {

                             $http.get(
                                 baseurl + 'section/' + dbid,
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
                                     $scope.Section0 = data;
                                 }).error(function (data, status, headers,
                                                    config) {
                                 console.log(data);
                             });
                         };

                         loadInstance = function (dbid, type_id) {

                             $http.get(baseurl + 'section/' + dbid + '/load?type_id=' + type_id,
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
                                     $scope.Section0 = data;
                                 }).error(function (data, status, headers,
                                                    config) {
                                 console.log(data);
                             });
                         };

                         $scope.close = function () {
                             $window.location.href = baseurl + 'etude/list/section';
                         };

                         $scope.saveAndClose = function () {
                             $rootScope.$broadcast('searchStart', undefined);
                             $http.post(baseurl + 'section/' + $window.section_id + '/update',
                                        $.param({'text': $scope.Section0.Texte.value}), {
                                            headers: {
                                                'Content-Type': 'application/x-www-form-urlencoded',
                                                'Cache-Control': 'no-cache',
                                                'Expires': 0
                                            },
                                            timeout: 0,
                                            cache: false
                                        }).success(function (data, status, headers,
                                                             config) {
                                 $rootScope.$broadcast('searchFinished', undefined);
                                 $window.location.href = baseurl + 'etude/list/section';
                             }).error(function (data, status, headers,
                                                config) {
                                 $rootScope.$broadcast('searchFinished', undefined);
                                 console.log(data);
                             });
                         };
                         

                         $scope.save = function () {
                             $rootScope.$broadcast('searchStart', undefined);
                             $http.post(baseurl + 'section/' + $window.section_id + '/update',
                                        $.param({'text': $scope.Section0.Texte.value}), {
                                            headers: {
                                                'Content-Type': 'application/x-www-form-urlencoded',
                                                'Cache-Control': 'no-cache',
                                                'Expires': 0
                                            },
                                            timeout: 0,
                                            cache: false
                                        }).success(function (data, status, headers,
                                                             config) {
                                 $rootScope.$broadcast('searchFinished', undefined);
                             }).error(function (data, status, headers,
                                                config) {
                                 $rootScope.$broadcast('searchFinished', undefined);
                                 console.log(data);
                             });
                         };

                         $scope.$on('searchStart', function (event, data) {

                             $scope.isSearching = true;
                         });

                         $scope.$on('searchFinished', function (event, data) {

                             $scope.isSearching = false;
                             displayInstance($scope.Section0.id, $scope.Section0.typeId);
                         });

                         loadInstance($window.section_id, $window.type_id);
                     }]);

modEtude.controller('VariableController',
                    ['$scope', '$location', '$window', '$modal', 'variableService',
                     function ($scope, $location, $window, $modal, variableService) {
                         $scope.variableList = [];

                         var initVariables, baseurl;

                         baseurl = document.getElementsByTagName('base')[0].href;

                         initVariables = function () {
                             variableService.findAll(baseurl).then(function (res) {
                                 $scope.variableList = [];
                                 _.each(res.data, function (elem, index) {
                                     $scope.variableList.push(elem);
                                 });
                             }, function (reason) {
                                 $scope.variableList = [];
                             });
                         };

                         $scope.openModal = function (key, value) {

                             var modalInstance = $modal.open({
                                                                 templateUrl: 'views/partials/variable.html',
                                                                 controller: 'ModalVariableController',
                                                                 resolve: {
                                                                     baseurl: function () {
                                                                         return baseurl;
                                                                     },
                                                                     editkey: function () {
                                                                         return key ? key : null;
                                                                     },
                                                                     editvalue: function () {
                                                                         return value ? value : null;
                                                                     }
                                                                 }
                                                             });

                             modalInstance.result.then(function (data) {
                                 initVariables();
                             }, function () {

                             });
                         };

                         initVariables();
                     }]);

modEtude.controller('ModalVariableController',
                    ['$scope', 'variableService', '$modalInstance', 'baseurl', 'editkey',
                     'editvalue',
                     function ($scope, variableService, $modalInstance, baseurl, editkey,
                               editvalue) {

                         var saveVariable, initPage;

                         initPage = function () {
                             $scope.mvariable = {"key": editkey, "value": editvalue};
                             $scope.editmode = (editkey !== null) && (editkey !== undefined);
                         };

                         saveVariable = function () {
                             if ($scope.mvariable.key !== null &&
                                 $scope.mvariable.key !== undefined) {
                                 $scope.mvariable.key = $scope.mvariable.key.trim();
                                 if ($scope.mvariable.key === '') {
                                     $scope.mvariable.key =
                                         Math.random().toString(36).substring(10);
                                 }
                             } else {
                                 $scope.mvariable.key = Math.random().toString(36).substring(10);
                             }
                             if ($scope.mvariable.value !== null &&
                                 $scope.mvariable.value !== undefined) {
                                 $scope.mvariable.value = $scope.mvariable.value.trim();
                             } else {
                                 $scope.mvariable.value = null;
                             }
                             if (editkey) {
                                 return variableService.updateVariable(baseurl,
                                                                       $scope.mvariable.key,
                                                                       $scope.mvariable.value);
                             } else {
                                 return variableService.createVariable(baseurl,
                                                                       $scope.mvariable.key,
                                                                       $scope.mvariable.value);
                             }
                         };

                         $scope.confirm = function () {
                             saveVariable().then(function (result) {
                                 $modalInstance.close('confirm');
                             }, function (reason) {
                                 $scope.errorMessage =
                                     'Erreur ' + reason.status + ' ' + reason.data;
                             });
                         };

                         $scope.cancel = function () {
                             $modalInstance.dismiss('cancel');
                         };

                         initPage();
                     }]);