'use strict';

/* Directives */
var userModule = angular.module('myApp.user', []);

userModule.run(['UserService', function(UserService) {
  UserService.init();
}]);

userModule.factory('UserService', ['$rootScope', '$http', '$location', 'Storage', function($rootScope, $http, $location, Storage) {
  var UserService = {};

  UserService.url = "/ajax/login/";
  UserService.loginUrl = "/j_spring_security_check";
  UserService.logoutUrl = "/j_spring_security_logout";
  UserService.login = false;
  
  UserService.init = function() {
    UserService.isLoggedIn();
  };
  
  UserService.loginChanged = function(loaded) {
    $rootScope.$broadcast('LoginChange');
  };
  
  UserService.setLogin = function(login) {
    UserService.login = login;
    UserService.loginChanged();
  };
  
  UserService.isLoggedIn = function() {
    var url = UserService.url + 'isLoggedIn';
    $http.get(url).
      success(function(data, status, headers, config) {
        UserService.setLogin(data);
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  /**
   * form param is a type of jQuery object for example, jQuery('#form').
   */
  UserService.doLogin = function(form) {
    var url = UserService.loginUrl;
    
    var config = {
      headers: {"Content-Type": "application/x-www-form-urlencoded" }
    };
    
    $http.post(url, form.serialize(), config).
      success(function(data, status, headers, config) {
        UserService.setLogin(data);
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  UserService.doLogout = function() {
    var url = UserService.logoutUrl;
    
    $http.get(url).
      success(function(data, status, headers, config) {
        console.log(data);
        
        UserService.setLogin(data);
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  return UserService;
  
}]);