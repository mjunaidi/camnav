'use strict';

/* Directives */
var websiteModule = angular.module('myApp.website', ['ngRoute', 'ngResource']);

websiteModule.run(['WebsiteService', function(websiteService) {
  websiteService.init();
}]);

websiteModule.factory('WebsiteService', ['$rootScope', '$http', 'Storage', function($rootScope, $http, Storage) {
  var service = {};
  
  service.websites = null;
  service.website = null;
  
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
  
  service.nav = '/website';
  service.path = 'website/api/';
  
  service.init = function() {
    service.initRowCount();
  };
  
  service.initWebsitePage = function(route) {
    var params = route.current.params;
    if (params.id) {
      service.initWebsite(params.id);
    } else {
      service.initNewWebsite();
    }
  };
  
  service.viewWebsites = function(location) {
    location.path(service.nav);
  };
  
  service.addNewWebsite = function(location) {
    location.path(service.nav + '/add');
  };
  
  service.editWebsite = function(location, id) {
    var path = service.nav + '/edit/' + id;
    location.path(path);
  };
  
  service.initRowCount = function() {
    var url = service.path + 'rowCount';
    $http.get(url).
      success(function(data, status, headers, config) {
        service.rowCount = data.rowCount;
        service.initPageCount();
        service.initWebsites();
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.initWebsites = function() {
    var url = service.path + 'list/' + service.first + '/' + service.size;
    $http.get(url).
      success(function(data, status, headers, config) {
        service.websites = data;
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.initNewWebsite = function() {
    service.website = {};
  };
  
  service.initWebsite = function(id) {
    var url = service.path + 'get/' + id;
    $http.get(url).
      success(function(data, status, headers, config) {
        service.website = data;
        console.log(service.website);
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.removeWebsite = function(id) {
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
  
  service.removeAllWebsites = function() {
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
    service.initWebsites();
  };

  service.pageChanged = function() {
    service.updatePage(service.pagination.current-1);
  };
  
  service.setWebsites = function(websites) {
    service.websites = websites;
  };
  
  service.updateWebsite = function(website) {
    var url;
    if (!website.id) {
      url = service.path + 'add';
    } else {
      url = service.path + 'update/' + website.id;
    }
    
    $http.post(url, website).
      success(function(data, status, headers, config) {
        console.log(data);
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.searchWebsites = function(keyword) {
    var first = 0;
    var size = service.resultSize;
    var keys = 'name,slug,url,';
    var url = service.path + 'search/' + keys + '/' + keyword + '/' + first + '/' + size;
    
    console.log(url);
    if (keyword) {
      $http.get(url).
        success(function(data, status, headers, config) {
          service.websites = data;
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
  
  return service;
  
}]);