'use strict';

/* Directives */
var workgroupModule = angular.module('myApp.workgroup', []);

workgroupModule.run(['WorkgroupService', function(WorkgroupService) {
  WorkgroupService.init();
}]);

workgroupModule.factory('WorkgroupService', ['$rootScope', '$http', '$location', 'Storage', function($rootScope, $http, $location, Storage) {
  var WorkgroupService = {};
  
  WorkgroupService.workgroups = false;
  WorkgroupService.loaded = false;
  WorkgroupService.dir = 'datasource/';
  
  WorkgroupService.init = function() {
    var url = WorkgroupService.dir + "files.json";
    WorkgroupService.readFiles(url);
  };
  
  WorkgroupService.setWorkgroups = function(data) {
    if (WorkgroupService.workgroups === false) {
      WorkgroupService.workgroups = data;
    } else {
      WorkgroupService.workgroups = _.union(WorkgroupService.workgroups, data);
    }
  };
  
  WorkgroupService.setLoaded = function(loaded) {
    WorkgroupService.loaded = true;
    $rootScope.$broadcast('WorkgroupLoaded');
  };
  
  WorkgroupService.readFiles = function(url) {
    $http.get(url).
      success(function(data, status, headers, config) {
        WorkgroupService.readFilesHelper(data, 0);
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  WorkgroupService.readFilesHelper = function(files, ind) {
    var len = files.length;
    if (ind < len) {
      var file = files[ind];
      var path = WorkgroupService.dir + file.name;
      ind++;
      WorkgroupService.readData(path, files, ind);
    } else {
      WorkgroupService.setLoaded(true);
    }
  };
  
  WorkgroupService.readData = function(url, files, ind) {
    $http.get(url).
      success(function(data, status, headers, config) {
        WorkgroupService.setWorkgroups(data);
        
        WorkgroupService.readFilesHelper(files, ind);
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  return WorkgroupService;
  
}]);