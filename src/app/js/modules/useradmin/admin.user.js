/**
 * Created by Khresterion on 4/1/15.
 */

var modUser = angular.module('admin.user', []);

modUser.factory('memberService', ['$http', function ($http) {
  "use strict";
  return {
    getUser: function (userName) {

      return $http.get('member?user_name=' + userName,
        {
          headers: {'Content-Type': 'application/json', 'Cache-Control': 'no-cache', 'Expires': 0},
          timeout: 0,
          cache: false
        })
        .success(function (data, status, headers, config) {

        }).error(function (data, status, headers, config) {

        });
    },
    listAll: function (page_number, page_size) {

      return $http.get('member/list/all?page_number=' + page_number + '&page_size=' + page_size,
        {
          headers: {'Content-Type': 'application/json', 'Cache-Control': 'no-cache', 'Expires': 0},
          timeout: 0,
          cache: false
        })
        .success(function (data, status, headers, config) {

        }).error(function (data, status, headers, config) {

        });
    },
    findBy: function (lastname, organisation) {

      return $http.get('member/list/by?user_lastname=' + lastname + '&user_orga=' + organisation,
        {
          headers: {'Content-Type': 'application/json', 'Cache-Control': 'no-cache', 'Expires': 0},
          timeout: 0,
          cache: false
        })
        .success(function (data, status, headers, config) {

        }).error(function (data, status, headers, config) {

        });
    },
    createUser: function ( name, key, organisation, firstname, lastname) {
      return $http.post('member', $.param({
          'user_name': name, 'user_key': key, 'user_orga': organisation,
          'user_firstname': firstname, 'user_lastname': lastname
        }),
        {
          headers: {'Content-Type': 'application/x-www-form-urlencoded', 'Cache-Control': 'no-cache', 'Expires': 0},
          timeout: 0,
          cache: false
        })
        .success(function (data, status, headers, config) {
        }).error(function (data, status, headers, config) {
        });
    },
    deleteUser: function (name) {

      return $http['delete']('member?user_name=' + name,
        {
          headers: {'Content-Type': 'application/json', 'Cache-Control': 'no-cache', 'Expires': 0},
          timeout: 0,
          cache: false
        })
        .success(function (data, status, headers, config) {

        }).error(function (data, status, headers, config) {

        });
    },
    updateUSer: function (name, organisation, firstname, lastname) {
      return $http.post('member/update', $.param({
          'user_name': name, 'user_orga': organisation,
          'user_firstname': firstname, 'user_lastname': lastname
        }),
        {
          headers: {'Content-Type': 'application/x-www-form-urlencoded', 'Cache-Control': 'no-cache', 'Expires': 0},
          timeout: 0,
          cache: false
        })
        .success(function (data, status, headers, config) {

        }).error(function (data, status, headers, config) {

        });
    },
    setRole: function (name, role) {
      return $http.post('member/role/add', $.param({'user_name': name, 'role_name': role}),
        {
          headers: {'Content-Type': 'application/x-www-form-urlencoded', 'Cache-Control': 'no-cache', 'Expires': 0},
          timeout: 0,
          cache: false
        })
        .success(function (data, status, headers, config) {
        }).error(function (data, status, headers, config) {
        });
    },
    removeRole: function (name, role) {
      return $http.post('member/role/remove', $.param({'user_name': name, 'role_name': role}),
        {
          headers: {'Content-Type': 'application/x-www-form-urlencoded', 'Cache-Control': 'no-cache', 'Expires': 0},
          timeout: 0,
          cache: false
        })
        .success(function (data, status, headers, config) {
        }).error(function (data, status, headers, config) {
        });
    },
    setPassword: function (name, key) {
      return $http.post('member/password', $.param({'user_name': name, 'user_key': key}),
        {
          headers: {'Content-Type': 'application/x-www-form-urlencoded', 'Cache-Control': 'no-cache', 'Expires': 0},
          timeout: 0,
          cache: false
        })
        .success(function (data, status, headers, config) {
        }).error(function (data, status, headers, config) {
        });
    },
    activate: function (name, isActive) {
      return $http.post('member/activate', $.param({'user_name': name, 'active': isActive}),
        {
          headers: {'Content-Type': 'application/x-www-form-urlencoded', 'Cache-Control': 'no-cache', 'Expires': 0},
          timeout: 0,
          cache: false
        })
        .success(function (data, status, headers, config) {
        }).error(function (data, status, headers, config) {
        });
    }
  };
}]);

modUser.factory('roleService', ['$http', function ($http) {
  "use strict";
  return {
    list: function () {

      return $http.get('role/',
        {
          headers: {'Content-Type': 'application/json', 'Cache-Control': 'no-cache', 'Expires': 0},
          timeout: 0,
          cache: false
        })
        .success(function (data, status, headers, config) {
        }).error(function (data, status, headers, config) {
        });
    }
  };
}]);
