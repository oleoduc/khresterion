/**
 * Created by Khresterion on 3/6/15.
 * instance utilities extracted from corebusiness.js file
 * renamed instanceService to instanceManager
 */
var instMod = angular.module('kengine.instance', []);

/*utility to use instance name for callback function
 * usage: register the instance and call execute
 * */
instMod.factory('instanceManager', [function () {
    'use strict';

    var newInstanceService;

    newInstanceService = {
        map: [],
        update: function (instanceName, instanceObject) {

            var resolved;

            resolved = this.resolveInstance(instanceName);

            if (resolved !== undefined) {
                this.unRegister(instanceName);
                this.register(instanceName, instanceObject);
            } else {
                this.register(instanceName, instanceObject);
            }

            return (resolved !== undefined) ? resolved : undefined;
        },
        register: function (instanceName, instanceObject) {
            var resolved;

            resolved = this.resolveInstance(instanceName);
            if (resolved === undefined) {
                this.map.push({"key": instanceName, "value": instanceObject});
            } else {
                this.update(instanceName, instanceObject);
            }
        },
        unRegister: function (instanceName) {
            this.map = _.reject(this.map, function (obj) {
                return obj.key === instanceName;
            });
        },
        resolveName: function (instanceObject) {

            var resolvedName;
            resolvedName = _.find(this.map, function (obj) {
                return obj.id === instanceObject.id;
            });

            return (resolvedName !== undefined) ? resolvedName.key : undefined;
        },
        resolveInstance: function (instanceName) {
            var resolved;

            resolved = _.find(this.map, function (obj) {
                return obj.key === instanceName;
            });
            return (resolved !== undefined) ? resolved.value : undefined;
        },
        clear: function () {
            this.map = {};
        },
        adjustPropertypath: function (oldPath, typeId) {
            /*replace oldPath origin by typeId, useful for instance summary list*/
            var arrayPath, sep, fullpath, cpt;

            sep = '/';
            arrayPath = oldPath.split(sep);
            fullpath = typeId;

            for (cpt = 1; cpt < arrayPath.length; cpt = cpt + 1) {
                fullpath += sep + arrayPath[cpt];
            }
            return fullpath;
        },
        exportUrl: function (instanceId, templateName, envkey) {

            return (instanceId !== undefined) ?
            'v1/instance/'+  instanceId +'/export?template=' + templateName + '&envkey=' + envkey
                : null;
        }
    };
    return newInstanceService;
}]);
