/**
 * Created by ndriama on 24/06/16.
 */

angular.module('dueApp',
               ['ngRoute', 'mm.foundation', 'modEtude', 'modProgress'])
    .config(['$routeProvider', '$locationProvider',
             function ($routeProvider, $locationProvider) {
                 $locationProvider.html5Mode(false);
                 //$locationProvider.hashPrefix('!');
             }]);

var modEtude = angular.module('modEtude', []);

modEtude.controller('SectionListController',
                    ['$scope', '$window', '$http', '$rootScope',
                     function ($scope, $window, $http, $rootScope) {

                         "use strict";
                         $scope.entityList = [];
                         $scope.sectionList = [];
                         $scope.isSearching = false;
                         $scope.param = {"selected": {"id": 0, "typeId": "Ontologie1_00Entete"}};

                         var displayList, baseurl, initSelection;

                         baseurl = document.getElementsByTagName('base')[0].href;

                         displayList = function () {
                             $scope.isSearching = true;
                             $http.get(baseurl + 'section/',
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
                                         return elem.name;
                                     });
                                     $scope.isSearching = false;
                                 }).error(function (data, status, headers,
                                                    config) {
                                 console.log(data);
                                 $scope.isSearching = false;
                             });
                         };

                         initSelection = function () {
                             $http.get(baseurl + 'section/names',
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
                                     var tmpArray;
                                     $scope.entityList = [];
                                     tmpArray = _.sortBy(_.filter(data, function (elem) {
                                         return elem !== 'Ontologie1_Document' &&
                                                elem !== 'Ontologie1_DocumentDUE0' &&
                                                elem !== 'Ontologie1_DUE' &&
                                                elem !== 'Ontologie1_Titre' && elem !== 'Ontologie1_Terme';
                                     }), function (sorted) {
                                         return sorted.toUpperCase();
                                     });
                                     $scope.entityList = _.map(tmpArray, function (element, index) {
                                         return {"id": index, "typeId": element};
                                     });
                                 }).error(function (data, status, headers,
                                                    config) {
                                 console.log(data);
                             });
                         };

                         $scope.createSection = function (entityType) {
                             $scope.isSearching = true;
                             $http.post(baseurl + 'section',
                                        $.param({'type_id': (entityType === undefined) ? null : entityType.typeId}),
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
                                     if (data == false) {
                                         alert('Erreur creation');
                                         $scope.isSearching = false;
                                     } else {
                                         displayList();
                                     }
                                 }).error(function (data, status, headers,
                                                    config) {
                                 console.log(data);
                                 $scope.isSearching = false;
                             });
                         };
                         initSelection();
                         displayList();
                     }]);