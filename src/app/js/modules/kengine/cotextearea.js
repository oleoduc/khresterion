/**
 * Created by ndriama on 31/03/16.
 */

var kbarea = angular.module('modTextarea', ['kengine.property','ngSanitize','ui.tinymce']);

kbarea.controller(
    'TextAreaController',
    ['$scope', 'reificator', 'propertyService','$sce', function ($scope, reificator, propertyService,$sce) {
        "use strict";

        var ctrl;
        ctrl = this;
        ctrl.trustedText = undefined;
        ctrl.tinymceOptions = {selector: 'textarea',
            skin: 'customgrad',
            height: 450,
            plugins: [
                'advlist autolink lists link image charmap print preview anchor',
                'searchreplace visualblocks wordcount spellchecker fullscreen',
                'insertdatetime media table contextmenu paste code',
                'colorpicker textcolor'
            ],
            toolbar: 'insertfile undo redo | bold italic underline | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image'};

        this.preview = function (event) {
            var isValid;

            if (ctrl.property.value === undefined) {

                isValid = false;
            } else {

                if (event !== undefined) {
                    isValid =
                        (event.keyCode === 13) || (event.type === 'blur') ||
                        (event.type === 'click');
                } else {
                    isValid = false;
                }
                if (isValid) {

                    ctrl.trustedText = $sce.trustAsHtml(ctrl.property.value);
                } else {
                    isValid = false;
                }
            }
        };
    }]);

kbarea.directive('coTextarea', [function () {
    "use strict";
    return {
        priority: 0,
        restrict: 'EA',
        replace: true,
        transclude: false,
        controller: 'TextAreaController',
        controllerAs: 'com',
        bindToController: true,
        templateUrl: 'views/partials/tpl/cocomment.html',
        scope: {
            property: "=property",
            bo: "@"
        }
    };
}
]);
