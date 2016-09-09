/**
 * Created by ndriama on 24/06/16.
 */

angular.module('dueApp', ['ngRoute', 'mm.foundation', 'modEtude', 'modSimple', 'modProgress'])
    .config(['$routeProvider', '$locationProvider',
             function ($routeProvider, $locationProvider) {
                 $locationProvider.html5Mode(false);
                 //$locationProvider.hashPrefix('!');
             }]);

var modEtude = angular.module('modEtude', []);

modEtude.controller('PlanListController',
                    ['$scope', '$window', '$http',
                     function ($scope, $window, $http) {

                         "use strict";
                         $scope.planList = [];
                         $scope.isSearching = true;

                         var displayList, baseurl;

                         baseurl = document.getElementsByTagName('base')[0].href;

                         displayList = function () {
                             $http.get(baseurl + 'section/plan',
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
                                     $scope.planList = _.sortBy(data, function (elem) {
                                         return elem.id;
                                     });
                                     $scope.isSearching = false;
                                 }).error(function (data, status, headers,
                                                    config) {
                                 console.log(data);
                                 $scope.isSearching = false;
                             });
                         };

                         $scope.createPlan = function (entity) {
                             $scope.isSearching = true;
                             $http.post(baseurl + 'section/plan',
                                        {},
                                        {
                                            headers: {
                                                'Content-Type': 'application/x-www-form-urlencoded',
                                                'Cache-Control': 'no-cache',
                                                'Expires': 0
                                            },
                                            timeout: 0,
                                            cache: false
                                        })
                                 .success(function (data, status, headers,
                                                    config) {
                                     console.log(data);
                                     $scope.isSearching = false;
                                     $window.location.href =
                                         baseurl + 'etude/edit/plan?envkey=' + data + '&plan_id=0';
                                 }).error(function (data, status, headers,
                                                    config) {
                                 console.log(data);
                                 $scope.isSearching = false;
                             });
                         };

                         $scope.loadPlan = function (planId) {
                             $scope.isSearching = true;
                             $http.get(baseurl + 'section/plan/' + planId,
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

                                     $scope.isSearching = false;
                                     $window.location.href =
                                         baseurl + 'etude/edit/plan?envkey=' + data
                                         + '&plan_id=' + planId;
                                 }).error(function (data, status, headers,
                                                    config) {
                                 console.log(data);
                                 $scope.isSearching = false;
                             });
                         };
                         
                         $scope.loadTreeview = function (planId) {
                             $scope.isSearching = true;
                             $http.get(baseurl + 'section/plan/' + planId,
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

                                     $scope.isSearching = false;
                                     $window.location.href =
                                         baseurl + 'etude/treeview?envkey=' + data
                                         + '&plan_id=' + planId;
                                 }).error(function (data, status, headers,
                                                    config) {
                                 console.log(data);
                                 $scope.isSearching = false;
                             });
                         };

                         displayList();
                     }]);

modEtude.controller('PlanViewController',
                    ['$scope', '$window', '$http', 'instanceManager',
                     function ($scope, $window, $http, instanceManager) {
                         $scope.isSearching = false;
                         $scope.sectionList = [];

                         var listSections, baseurl;

                         //baseurl = document.getElementsByTagName('base')[0].href;
                         baseurl = '/due/';

                         listSections = function (envkey) {
                             $http.get(
                                 baseurl + 'v2/instance/search?envkey=' + $window.envkey
                                     + '&type_id=Ontologie1_Section0',
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
                                     $scope.sectionList = _.sortBy(data, function (elem) {
                                         return elem.label;
                                     });
                                     _.each($scope.sectionList, function(section){
                                         instanceManager.register(
                                             'Section' + section.id, section);
                                     });
                                     $scope.isSearching = false;
                                 }).error(function (data, status, headers,
                                                    config) {
                                 $scope.isSearching = false;
                                 console.log(data);
                             });
                         };

                         $scope.close = function () {
                             $scope.isSearching = true;
                             $http.get(
                                 baseurl + 'v2/environment/close?envkey=' + $window.envkey,
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
                                     $scope.isSearching = false;
                                     $window.location.href = baseurl + 'etude/list/plan';
                                 }).error(function (data, status, headers,
                                                    config) {
                                 $scope.isSearching = false;
                                 console.log(data);
                             });
                         };

                         $scope.save = function () {
                             $scope.isSearching = true;
                             $http.get(
                                 baseurl + 'etude/save/plan?envkey=' + $window.envkey,
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
                                     $scope.isSearching = false;
                                     $window.location.href = baseurl + 'etude/list/plan';
                                 }).error(function (data, status, headers,
                                                    config) {
                                 $scope.isSearching = false;
                                 console.log(data);
                             });
                         };

                         $scope.getProperty = function(instanceId, property){
                             var instance;

                             instance = instanceManager.resolveInstance('Section'+ instanceId);
                             if(instance){
                                 return instance.bo[property];
                             } else {
                                 return undefined;
                             }
                         };

                         $scope.getInstanceName = function(instanceId){
                             return 'Section'+ instanceId;
                         };

                         $scope.reloadSections = function(){
                             $scope.isSearching = true;
                             listSections($window.envkey);
                         };

                         listSections($window.envkey);
                     }]);