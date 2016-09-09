/**
 * Created by ndriama on 29/03/16.
 * API V2
 */

var propertyMod = angular.module('kengine.property', ['kengine.instance', 'kengine.reification']);

propertyMod.factory(
    'propertyService',
    ['$http', 'instanceManager', 'reificator', function ($http, instanceManager, reificator) {
        "use strict";
        var newPropertyService;

        newPropertyService = {
            update: function (scope, instanceName, property,
                              changeEventName) {
                var locInstance;

                locInstance =
                    instanceManager.resolveInstance(
                        instanceName);

                return $http.post('v2/property',
                                  $.param({
                                              'instance_id': locInstance.id,
                                              'propertypath': property.propertypath,
                                              'value': reificator.reifyToServer(
                                                  property.value,
                                                  property.valueType),
                                              'envkey': locInstance.envkey
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
                    .success(function (data, status, headers,
                                       config) {
                        instanceManager.update(instanceName,
                                               data);
                        scope.$emit(changeEventName,
                                    instanceName);

                    }).error(function (data, status, headers,
                                       config) {
                        scope.$emit('engineError', null);
                    });
            },
            remove: function (scope, instanceName, property,
                              changeEventName) {
                var locInstance;

                locInstance =
                    instanceManager.resolveInstance(
                        instanceName);

                $http['delete']('v2/property?instance_id=' + locInstance.id + '&envkey=' +
                    locInstance.envkey + '&' +
                    $.param(
                        {'propertypath': property.propertypath}),
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
                        instanceManager.update(instanceName,
                                               data);
                        scope.$emit(changeEventName,
                                    instanceName);
                    }).error(
                    function (data, status, headers, config) {
                        scope.$emit('engineError', null);
                    });
            },
            linkExisting: function(scope, instanceName, property,
                                   changeEventName){
                var locInstance;

                locInstance =
                    instanceManager.resolveInstance(
                        instanceName);

                return $http.post('v2/instance/'+locInstance.id+'/link/existing',
                                  $.param({
                                              'propertypath': property.propertypath,
                                              'linked_id': reificator.reifyToServer(
                                                  property.value,
                                                  property.valueType),
                                              'envkey': locInstance.envkey
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
                    .success(function (data, status, headers,
                                       config) {
                        instanceManager.update(instanceName,
                                               data);
                        scope.$emit(changeEventName,
                                    instanceName);

                    }).error(function (data, status, headers,
                                       config) {
                        scope.$emit('engineError', null);
                    });
            },
            include: function (scope, instanceName, property,
                               changeEventName) {
                var locInstance;

                locInstance =
                    instanceManager.resolveInstance(
                        instanceName);

                $http.post('v2/property/include',
                           $.param({
                                       'instance_id': locInstance.id,
                                       'envkey': locInstance.envkey,
                                       'propertypath': property.propertypath
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
                    .success(function (data, status, headers,
                                       config) {
                        instanceManager.update(instanceName,
                                               data);
                        scope.$emit(changeEventName,
                                    instanceName);
                    }).error(
                    function (data, status, headers, config) {
                        scope.$emit('engineError', null);
                    });

            },
            notinclude: function (scope, instanceName, property,
                               changeEventName) {
                var locInstance;

                locInstance =
                    instanceManager.resolveInstance(
                        instanceName);

                $http.post('v2/property/notinclude',
                           $.param({
                                       'instance_id': locInstance.id,
                                       'envkey': locInstance.envkey,
                                       'propertypath': property.propertypath
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
                    .success(function (data, status, headers,
                                       config) {
                        instanceManager.update(instanceName,
                                               data);
                        scope.$emit(changeEventName,
                                    instanceName);
                    }).error(
                    function (data, status, headers, config) {
                        scope.$emit('engineError', null);
                    });
            },
            exclude: function (scope, instanceName, property,
                             changeEventName) {
            var locInstance;

            locInstance =
                instanceManager.resolveInstance(
                    instanceName);

            $http.post('v2/property/exclude',
                       $.param({
                                   'instance_id': locInstance.id,
                                   'envkey': locInstance.envkey,
                                   'propertypath': property.propertypath
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
                .success(function (data, status, headers,
                                   config) {
                    instanceManager.update(instanceName,
                                           data);
                    scope.$emit(changeEventName,
                                instanceName);
                }).error(
                function (data, status, headers, config) {
                    scope.$emit('engineError', null);
                });

        },
        notexclude: function (scope, instanceName, property,
                           changeEventName) {
            var locInstance;

            locInstance =
                instanceManager.resolveInstance(
                    instanceName);

            $http.post('v2/property/notexclude',
                       $.param({
                                   'instance_id': locInstance.id,
                                   'envkey': locInstance.envkey,
                                   'propertypath': property.propertypath
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
                .success(function (data, status, headers,
                                   config) {
                    instanceManager.update(instanceName,
                                           data);
                    scope.$emit(changeEventName,
                                instanceName);
                }).error(
                function (data, status, headers, config) {
                    scope.$emit('engineError', null);
                });
        }
    };

        return newPropertyService;
    }]);