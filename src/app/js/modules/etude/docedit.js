/**
 * Created by ndriama on 31/03/16.
 */

angular.module('dueApp',
               ['ngRoute', 'mm.foundation', 'modEtude', 'modSimple', 'modeDate',
                'modProgress', 'ui.tree'])
    .config(['$routeProvider', '$locationProvider',
             function ($routeProvider, $locationProvider) {
                 $locationProvider.html5Mode(false);
                 //$locationProvider.hashPrefix('!');
             }]);

var modEtude = angular.module('modEtude', []);

modEtude.controller('PageController', [function () {
}]);

modEtude.controller('DocDUEController',
                    ['instanceManager', '$scope', '$q', '$timeout', '$window', '$http', '$sce',
                     '$rootScope',
                     function (instanceManager, $scope, $q, $timeout, $window, $http, $sce,
                               $rootScope) {

                         "use strict";
                         console.log("sdqdqsd");
                         var displayInstance, baseurl, removeSpecificSection;

                         baseurl = document.getElementsByTagName('base')[0].href;

                         $scope.plantree = {"list": []};

                         $scope.Document = undefined;
                         $scope.Section0 = undefined;
                         $scope.prevRedaction = undefined;
                         $scope.isSearching = false;

                         displayInstance =
                             function (docid, envkey) {

                                 var qi;

                                 qi = [];

                                 $timeout(function () {
                                     qi.push($http.get(
                                             baseurl + 'treeview/instance/' + docid + '?envkey=' +
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
                                                     $scope.Document = data;
                                                     instanceManager.update('Document',
                                                                            $scope.Document);
                                                     $scope.prevRedaction =
                                                         $sce.trustAsHtml(
                                                             $scope.Document.bo.estRepresenteParRedaction.value);
                                                 }).error(function (data, status, headers,
                                                                    config) {
                                             console.log(data);
                                         })
                                     );

                                     qi.push($http.get(
                                             baseurl + 'treeview?envkey=' + envkey,
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
                                                     $scope.plantree = data;
                                                 }).error(function (data, status, headers,
                                                                    config) {
                                             console.log(data);
                                         })
                                     );
                                 });

                                 $q.all(qi).then(function (resa) {
                                     console.log('all promises resolved');
                                 }, function (reason) {
                                     console.log('some error with promises');
                                 });

                             };

                         removeSpecificSection = function(sectionId, envkey, planId){
                             $http['delete'](
                                     baseurl + 'section/'+sectionId+'?envkey=' + envkey + '&parent_id=' + planId,
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
                                     displayInstance($window.plan_id, $window.envkey);
                                 }).error(function (data, status, headers,
                                                    config) {
                                 console.log(data);
                             })
                         };

                         $scope.closeSession = function () {
                             $rootScope.$broadcast('searchStart', undefined);
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
                                 baseurl + 'etude/export?envkey=' + $window.envkey +
                                 '&instance_id=' + $window.plan_id;
                         };

                         $scope.addMainSection = function (nodeId) {

                             $window.location.href = baseurl + 'etude/edit/section?envkey='+$window.envkey +
                                                     '&parent_id=' + $window.parent_id + '&plan_id=' + $scope.Document.id +
                                                     '&owner_id=' + nodeId + '&path=Ontologie1_Section0/Ontologie1_estSuiviDe/Ontologie1_Section0';
                         };

                         $scope.addSubSection = function (nodeId) {
                             $window.location.href = baseurl + 'etude/edit/section?envkey='+$window.envkey +
                                                     '&parent_id=' + $window.parent_id + '&plan_id=' + $scope.Document.id +
                                                     '&owner_id=' + nodeId + '&path=Ontologie1_Section0/Ontologie1_aPourComposant/Ontologie1_Section0';
                         };

                         $scope.removeSection = function(nodeId){
                             if(nodeId !== $window.section_id){
                                 removeSpecificSection(nodeId, $window.envkey, $window.plan_id);
                             } else {
                                 console.log('remove not authorized');
                             }
                         };

                         $scope.addAltSection = function (nodeId) {
                             $window.location.href = baseurl + 'etude/edit/section?envkey='+$window.envkey +
                                                     '&parent_id=' + $window.parent_id + '&plan_id=' + $scope.Document.id +
                                                     '&owner_id=' + nodeId + '&path=Ontologie1_Section0/Ontologie1_aPourAlternative/Ontologie1_Section0';
                         };

                         $scope.$on('entityChanged', function (event, data) {

                             displayInstance($window.plan_id, $window.envkey);
                         });
                         $scope.$on('searchStart', function (event, data) {

                             $scope.isSearching = true;
                         });

                         $scope.$on('searchFinished', function (event, data) {

                             $scope.isSearching = false;
                         });

                         displayInstance($window.plan_id, $window.envkey);
                     }]);