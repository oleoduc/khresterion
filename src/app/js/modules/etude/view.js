/**
 * Created by ndriama on 25/03/16.
 */

angular.module('dueApp',
               ['ngRoute', 'mm.foundation', 'modEtude', 'modSimple', 'modeDate','modProgress'])
    .config(['$routeProvider', '$locationProvider',
             function ($routeProvider, $locationProvider) {
                 $locationProvider.html5Mode(false);
                 //$locationProvider.hashPrefix('!');
             }]);

var modEtude = angular.module('modEtude', []);

modEtude.controller('PageController', [function () {
}]);

modEtude.controller('EtudeController',
                    ['instanceManager', '$scope', '$q', '$timeout', '$window', '$http',
                     '$rootScope',
                     function (instanceManager, $scope, $q, $timeout, $window, $http, $rootScope) {
                         "use strict";

                         $scope.DUE = undefined;
                         $scope.isSearching = false;

                         var displayInstance, baseurl;

                         baseurl = document.getElementsByTagName('base')[0].href;

                         displayInstance =
                             function (instanceid, envkey) {

                                 return $http.get(
                                         baseurl + 'etude/display/' + instanceid + '?envkey=' +
                                         envkey,
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
                                         $scope.DUE = data;
                                         instanceManager.update('DUE', $scope.DUE);
                                     }).error(function (data, status, headers,
                                                        config) {
                                         console.log(data);
                                     });
                             };

                         $scope.closeSession = function () {
                             $rootScope.$broadcast('searchStart', undefined);
                             $http.get(
                                     baseurl + 'etude/close?envkey=' + $window.envkey,
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
                                     $rootScope.$broadcast('searchFinished', undefined);
                                     $window.location.href = baseurl + 'etude/list?page=0&size=50';
                                 }).error(function (data, status, headers,
                                                    config) {
                                 $rootScope.$broadcast('searchFinished', undefined);
                                 console.log(data);
                             });
                         };

                         $scope.saveSession = function () {
                             $rootScope.$broadcast('searchStart', undefined);
                             $http.get(
                                     baseurl + 'etude/save?envkey=' + $window.envkey +
                                     (($window.entity_id.length > 0)?'&entity_id=' + $window.entity_id:''),
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
                                     $rootScope.$broadcast('searchFinished', undefined);
                                     $window.location.href = baseurl + 'etude/list?page=0&size=50';
                                 }).error(function (data, status, headers,
                                                    config) {
                                 $rootScope.$broadcast('searchFinished', undefined);
                                 console.log(data);
                             });
                         };

                         $scope.exportDoc = function () {
                             $window.location.href =
                                 baseurl + 'etude/export?envkey=' + $window.envkey;
                         };

                         $scope.$on('entityChanged', function (event, data) {

                             displayInstance($window.instance_id, $window.envkey);
                         });

                         $scope.$on('searchStart', function (event, data) {

                             $scope.isSearching = true;
                         });

                         $scope.$on('searchFinished', function (event, data) {

                             $scope.isSearching = false;
                         });

                         displayInstance($window.instance_id, $window.envkey);
                     }]);