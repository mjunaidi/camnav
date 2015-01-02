'use strict';

/* Directives */
var locationModule = angular.module('myApp.location', ['ngRoute', 'ngResource']);

locationModule.run(['LocationService', function(locationService) {
  locationService.init();
}]);

locationModule.factory('LocationService', ['$rootScope', '$http', 'Storage', function($rootScope, $http, Storage) {
  var service = {};
  
  service.locations = null;
  service.location = null;
  
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
  
  service.nav = '/location';
  service.path = 'location/api/';
  
  service.init = function() {
    service.initRowCount();
  };
  
  service.initLocationPage = function(route) {
    var params = route.current.params;
    if (params.id) {
      service.initLocation(params.id);
    } else {
      service.initNewLocation();
    }
  };
  
  service.viewLocations = function($location) {
    $location.path(service.nav);
  };
  
  service.addNewLocation = function($location) {
    var path = service.nav + '/add';
    $location.path(path);
  };
  
  service.editLocation = function($location, id) {
    var path = service.nav + '/edit/' + id;
    $location.path(path);
  };
  
  service.initRowCount = function() {
    var url = service.path + 'countRow';
    $http.get(url).
      success(function(data, status, headers, config) {
        service.rowCount = data.rowCount;
        service.initPageCount();
        service.initLocations();
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.initLocations = function() {
    var url = service.path + 'list/' + service.first + '/' + service.size;
    $http.get(url).
      success(function(data, status, headers, config) {
        service.locations = data;
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.initNewLocation = function() {
    service.location = {};
  };
  
  service.initLocation = function(id) {
    var url = service.path + 'get/' + id;
    $http.get(url).
      success(function(data, status, headers, config) {
        service.location = data;
        console.log(service.location);
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.removeLocation = function(id) {
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
  
  service.removeAllLocations = function() {
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
    service.initLocations();
  };

  service.pageChanged = function() {
    service.updatePage(service.pagination.current-1);
  };
  
  service.setLocations = function(locations) {
    service.locations = locations;
  };
  
  service.assignPosition = function() {
    var form = document.getElementById('locationEditForm');
    var latFld = form.latitude;
    var lngFld = form.longitude;
    service.location.latitude = latFld.value;
    service.location.longitude = lngFld.value;
  };
  
  service.updateLocation = function($location, location) {
    var url;
    // TODO: somehow when assigning position from map, it didn't update ngModel.
    service.assignPosition();
    if (!location.id) {
      url = service.path + 'add';
      service.updateLocationHelper($location, location, url, true);
    } else {
      url = service.path + 'update/' + location.id;
      service.updateLocationHelper($location, location, url, false);
    }
  };
  
  service.updateLocationHelper = function($location, location, url, redirect) {
    $http.post(url, location).
      success(function(data, status, headers, config) {
        console.log(data);
        if (redirect && data.id) {
          service.editLocation($location, data.id);
        }
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.searchLocations = function(keyword) {
    var first = 0;
    var size = service.resultSize;
    var url = service.path + 'search/' + keyword + '/' + first + '/' + size;
    
    if (keyword) {
      $http.get(url).
        success(function(data, status, headers, config) {
          service.locations = data;
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