/**
 * Created by ndriama on 23/03/16.
 */


var userScreen = angular.module('userApp', ['ngRoute', 'mm.foundation', 'admin.user'])
    .config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
        "use strict";
        $locationProvider.html5Mode(false);
        //$locationProvider.hashPrefix('!');
    }]);

userScreen.controller('UserController', ['$scope', '$q', '$timeout', '$window', 'memberService', 'roleService',
    function ($scope, $q, $timeout, $window, memberService, roleService) {

        "use strict";
        $scope.errorBox = undefined;
        $scope.roleList = [];
        $scope.userList = [];
        $scope.predicate = undefined;
        $scope.reverse = false;
        $scope.pageinfo = {
            "numberOfElements": 0, "totalElements": 0, "firstPage": false, "lastPage": false, "totalPages": 0,
            "size": 0, "number": 0
        };
        $scope.pswdErrorMessage = null;
        $scope.cgpswdErrorMessage = null;
        $scope.errorBox = null;
        $scope.updrole = null;
        $scope.updusername = null;

        var unsortedList, initPage, listAllUser, oldrole, confirm;

        initPage = function () {
            roleService.list().then(function (res) {
                unsortedList = _.reject(res.data, function (elem, index) {
                    return elem.externalName === null;
                });
                $scope.roleList = _.sortBy(unsortedList, 'externalName');
                listAllUser(0, 25);
            }, function (reason) {
                $scope.errorBox = reason.status;
            });
        };

        listAllUser = function (page_number, page_size) {
            memberService.listAll(page_number, page_size).then(function (result) {

                $scope.userList = _.reject(result.data.content, function (elem, index) {
                    return elem.username === 'root';
                });
                $scope.pageinfo = {
                    "numberOfElements": result.data.numberOfElements - 1, "totalElements": result.data.totalElements - 1,
                    "firstPage": result.data.firstPage, "lastPage": result.data.lastPage, "totalPages": result.data.totalPages,
                    "size": result.data.size, "number": result.data.number
                };
            }, function (reason) {
                $scope.errorBox = reason.status;
            });
        };

        confirm = false;

        $scope.createUser = function () {
            $scope.errorBox = null;
            if (confirm) {
                memberService.createUser($scope.username, $scope.password, $scope.organisation,
                    $scope.firstname, $scope.lastname).then(function (res) {
                    $scope.errorBox = res.status;
                    memberService.setRole($scope.username, $scope.role.name).then(function (res) {
                        listAllUser($scope.pageinfo.number, $scope.pageinfo.size);
                    }, function (result) {
                        $scope.errorBox = 'Création [OK]';
                    });
                }, function (reason) {
                    $scope.errorBox = reason.status;
                });
            } else {
                $scope.errorBox = 'Confirmer mot de passe';
            }
        };
        /*check password*/
        $scope.checkPassword = function () {
            $scope.pswdErrorMessage = null;
            confirm = $scope.password ? $scope.confirmpassword === $scope.password : false;

            if (confirm) {
                $scope.pswdErrorMessage = 'Mot de passe [OK]';
            } else {
                $scope.pswdErrorMessage = 'Le mot de passe ne correspond pas';
            }
        };

        $scope.checkCGPassword = function () {
            $scope.cgpswdErrorMessage = null;
            confirm = $scope.cgpassword ? $scope.confirmcgpassword === $scope.cgpassword : false;

            if (confirm) {
                $scope.cgpswdErrorMessage = 'Mot de passe [OK]';
            } else {
                $scope.cgpswdErrorMessage = 'Le mot de passe ne correspond pas';
            }
        };
        /*add user update*/
        $scope.updateUser = function () {
            $scope.errorBox = null;
            memberService.updateUSer($scope.username, $scope.organisation, $scope.firstname, $scope.lastname).then(function (res) {
                $scope.errorBox = res.status;
                listAllUser($scope.pageinfo.number, $scope.pageinfo.size);
            }, function (reason) {
                $scope.errorBox = reason.status;
            });
        };

        $scope.resetPassword = function () {
            if (confirm) {
                $scope.errorBox = null;
                memberService.setPassword($scope.cgusername, $scope.cgpassword).then(function (res) {
                    $scope.errorBox = 'Mis à jour [OK]';
                }, function (reason) {
                    $scope.errorBox = 'Echec mis à jour';
                });
            } else {
                $scope.errorBox = 'Confirmer mot de passe';
            }
        };

        $scope.removeUser = function (name) {
            $scope.errorBox = '';
            memberService.deleteUser(name).then(function (res) {
                $scope.errorBox = res.status;
                listAllUser($scope.pageinfo.number, $scope.pageinfo.size);
            }, function (reason) {
                $scope.errorBox = reason.status;
            });
        };

        $scope.activateUser = function (name, isActive){
            $scope.errorBox = '';
            memberService.activate(name, isActive).then(function (res) {
                $scope.errorBox = res.status;
                listAllUser($scope.pageinfo.number, $scope.pageinfo.size);
            }, function (reason) {
                $scope.errorBox = reason.status;
            });
        };

        $scope.modifyUser = function (inuser) {
            $scope.cgusername = inuser.username;
            $scope.updusername = inuser.username;

            $scope.username = inuser.username;
            $scope.organisation = inuser.organisation;
            $scope.firstname = inuser.firstName;
            $scope.lastname = inuser.lastName;

            oldrole = (inuser.roleList.length > 0) ? inuser.roleList[0].name : 'ROLE_EXTERNAL';
        };

        oldrole = null;
        $scope.changeRole = function () {
            $scope.errorBox = null;
            if (oldrole === null || !($scope.updrole)) {
                $scope.errorBox = 'Sélectionner utilisateur et un profil';
            } else {
                memberService.removeRole($scope.updusername, oldrole).then(function (res) {
                    memberService.setRole($scope.updusername, $scope.updrole.name).then(function (res) {
                        oldrole = null;
                        $scope.updusername = null;
                        listAllUser($scope.pageinfo.number, $scope.pageinfo.size);
                    }, function (reason) {
                        $scope.errorBox = reason.status;
                    });
                }, function (reason) {
                    $scope.errorBox = reason.status;
                });
            }
        };

        $scope.getNextTablePage = function () {
            var nextPage;

            nextPage = $scope.pageinfo.number + 1;
            if (nextPage >= $scope.pageinfo.totalPages) {
                nextPage = $scope.pageinfo.totalPages - 1;
            }
            listAllUser(nextPage, $scope.pageinfo.size);
        };

        $scope.getPreviousTablePage = function () {
            var previousPage;

            previousPage = $scope.pageinfo.number - 1;
            if (previousPage < 0) {
                previousPage = 0;
            }

            listAllUser(previousPage, $scope.pageinfo.size);
        };

        $scope.getFirstTablePage = function () {

            listAllUser(0, $scope.pageinfo.size);
        };

        $scope.getLastTablePage = function () {
            listAllUser($scope.pageinfo.totalPages - 1, $scope.pageinfo.size);
        };

        initPage();
    }]);