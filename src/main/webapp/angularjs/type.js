'use strict';

/* Directives */
var typeModule = angular.module('myApp.type', ['ngRoute', 'ngResource']);

typeModule.run(['TypeService', function(typeService) {
  typeService.init();
}]);

typeModule.factory('TypeService', ['$rootScope', '$http', 'Storage', function($rootScope, $http, Storage) {
  var service = {};
  
  service.types = null;
  service.type = null;
  
  service.initFirst = 0;
  service.initSize = 10;
  service.initResultSize = 500;
  service.first = service.initFirst;
  service.size = service.initSize;
  service.resultSize = service.initResultSize;
  service.rowCount = 0;
  service.pageCount = 0;
  service.pageIndex = 0;
  
  service.pagination = {
      "max": 10,
      "current": 1,
      "perPage": 10,
      "totalItem": 0
  };
  
  service.nav = '/type';
  service.path = 'type/api/';
  
  service.init = function() {
    service.initTypes();
  };
  
  service.initTypePage = function(route) {
    var params = route.current.params;
    if (params.id) {
      service.initType(params.id);
    } else {
      service.initNewType();
    }
  };
  
  service.viewTypes = function(location) {
    location.path(service.nav);
  };
  
  service.addNewType = function(location) {
    var path = service.nav + '/add';
    location.path(path);
  };
  
  service.editType = function(location, id) {
    var path = service.nav + '/edit/' + id;
    location.path(path);
  };
  
  service.initRowCount = function() {
    var url = service.path + 'countRow';
    $http.get(url).
      success(function(data, status, headers, config) {
        service.rowCount = data.rowCount;
        service.initPageCount();
        service.initTypes();
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.initTypes = function() {
    //var url = service.path + 'list/' + service.first + '/' + service.size;
    var url = service.path + 'list';
    $http.get(url).
      success(function(data, status, headers, config) {
        service.types = data;
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.initNewType = function() {
    service.type = {};
  };
  
  service.initType = function(id) {
    var url = service.path + 'get/' + id;
    $http.get(url).
      success(function(data, status, headers, config) {
        service.type = data;
        console.log(service.type);
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.removeType = function(id) {
    if (id) {
      var url = service.path + 'remove/' + id;
      $http.get(url).
        success(function(data, status, headers, config) {
          service.init();
        }).
        error(function(data, status, headers, config) {
          console.log(data);
        });
    } 
  };
  
  service.removeAllTypes = function() {
    var url = service.path + 'removeAll';
    $http.get(url).
      success(function(data, status, headers, config) {
        service.init();
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.initPagination = function() {
    service.pagination.current = service.pageIndex + 1;
    service.pagination.perPage = service.size;
    service.pagination.totalItem = service.rowCount;
  };
  
  service.initPageCount = function() {
    if (!service.pageIndex) service.pageIndex = 0;
    if (!service.first) service.first = service.initFirst;
    if (!service.size) service.size = service.initSize;
    service.pageCount = Math.ceil(service.rowCount/service.size);
    service.initPagination(service);
  };
  
  service.nextPage = function() {
    if (service.hasNext()) {
      service.pageIndex++;
      service.updatePage(service, service.pageIndex);
    }
  };
  
  service.prevPage = function() {
    if (service.hasPrev()) {
      service.pageIndex--;
      service.updatePage(service, service.pageIndex);
    }
  };
  
  service.hasNext = function() {
    return service.pageIndex < service.pageCount-1;
  };
  
  service.hasPrev = function() {
    return service.pageIndex > 0;
  };
  
  service.updatePage = function(pageIndex) {
    if (pageIndex < 0 || pageIndex >= service.pageCount) pageIndex = 0;
    service.pageIndex = pageIndex;
    service.first = pageIndex*service.size;
    service.initTypes();
  };

  service.pageChanged = function() {
    service.updatePage(service.pagination.current-1);
  };
  
  service.setTypes = function(types) {
    service.types = types;
  };
  
  service.updateType = function(type) {
    var url;
    
    if (!type.id) {
      url = service.path + 'add';
    } else {
      url = service.path + 'update/' + type.id;
    }
    
    $http.post(url, type).
      success(function(data, status, headers, config) {
        console.log(data);
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.searchTypes = function(keyword) {
    var first = 0;
    var size = service.resultSize;
    var url = service.path + 'search/' + keyword + '/' + first + '/' + size;
    
    console.log(url);
    if (keyword) {
      $http.get(url).
        success(function(data, status, headers, config) {
          service.types = data;
        }).
        error(function(data, status, headers, config) {
          console.log(data);
        });
    } else {
      service.init();
    }
  };
  
  service.clearSearch = function(form) {
    angular.copy({},form);
    service.init();
  };
  
  service.addNewProperty = function(type) {
    if (type) {
      var properties = type.properties;
      if (properties) {
        var size = properties.length;
        var property = {};
        properties[size] = property;
      } else {
        type.properties = [{}];
      }
    }
  };
  
  service.removeProperty = function(type, property) {
    if (type && property) {
      var properties = type.properties;
      if (type) {
        type.properties = _.without(properties, property);
      }
    }
  };
  
  return service;
  
}]);