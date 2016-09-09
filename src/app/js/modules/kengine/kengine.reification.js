/**
 * Created by Khresterion on 3/6/15.
 * extract reifyFromServer, reifyToServer, isPropertyEmpty from coherencepanel.js
 * will allow components to be defined in separate modules
 */

var reifMod = angular.module('kengine.reification', []);

reifMod.factory('reificator', [function () {
    "use strict";
    var newReificator, toUsDate;

    toUsDate = function (obj, sep) {
        if(obj === null || obj === undefined) {
            return null;
        } else {
            var res;
            res = obj.split(sep);
            return res[1] + sep + res[0] + sep +  res[2];
        }
    };

    newReificator = {
        reifyToServer: function (obj, valueType) {
            var reified;
            if (valueType === undefined) {
                reified = obj;
            } else if (valueType === 'Date') {
                reified = new Date(toUsDate(obj, '/')).getTime();
            } else if (valueType === 'Integer') {
                reified = parseInt(obj, 0);
            } else if (valueType === 'Number') {
                reified = parseFloat(obj);
            } else if (valueType === 'Boolean') {
                reified = obj;
            } else {
                reified = obj;
            }
            return reified;
        },
        reifyFromServer: function (obj, valueType) {
            var reified, keys;
            if (valueType === undefined) {
                reified = obj;
            } else if (valueType === 'Date') {
                reified = (obj === null) ? null : new Date(parseInt(obj, 0));
            } else if (valueType === 'Integer') {
                reified = (obj === null) ? null : parseInt(obj, 0);
            } else if (valueType === 'Number') {
                reified = (obj === null) ? null : parseFloat(obj);
            } else if (valueType === 'Boolean') {
                reified = obj;
            } else {
                reified = obj;
            }
            return reified;
        },
        isPropertyEmpty: function (property) {
            if(property.valueType === undefined) {
                return false;
            } else if ((property.valueType === 'String')) {
                return (property.value === null) ? true : property.value.trim() === '';
            } else if ((property.valueType === 'Number') || (property.valueType === 'Integer')) {
                return (property.value === null) || (property.value === undefined) || (property.value === '');
            } else {
                return (property.value === null);
            }
        },
        toUS: function (obj, sep) {
            return toUsDate(obj, sep);
        }
    };

    return newReificator;
}]);
