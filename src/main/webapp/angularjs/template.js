'use strict';

/* Directives */
var templateModule = angular.module('myApp.template', ['ngRoute', 'ngResource']);

templateModule.run(['TemplateService', function(templateService) {
  templateService.init();
}]);

templateModule.factory('TemplateService', ['$rootScope', '$http', 'Storage', function($rootScope, $http, Storage) {
  var service = {};
  
  service.templates = null;
  service.template = null;
  
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
  
  service.nav = '/template';
  service.path = 'template/api/';
  
  service.init = function() {
    service.initRowCount();
  };
  
  service.initTemplatePage = function(route) {
    var params = route.current.params;
    if (params.id) {
      service.initTemplate(params.id);
    } else {
      service.initNewTemplate();
    }
  };
  
  service.viewTemplates = function(location) {
    location.path(service.nav);
  };
  
  service.addNewTemplate = function(location) {
    var path = service.nav + '/add';
    location.path(path);
  };
  
  service.editTemplate = function(location, id) {
    var path = service.nav + '/edit/' + id;
    location.path(path);
  };
  
  service.initRowCount = function() {
    var url = service.path + 'countRow';
    $http.get(url).
      success(function(data, status, headers, config) {
        service.rowCount = data.rowCount;
        service.initPageCount();
        service.initTemplates();
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.initTemplates = function() {
    var url = service.path + 'list/' + service.first + '/' + service.size;
    $http.get(url).
      success(function(data, status, headers, config) {
        service.templates = data;
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.initNewTemplate = function() {
    service.template = {};
  };
  
  service.initTemplate = function(id) {
    var url = service.path + 'get/' + id;
    $http.get(url).
      success(function(data, status, headers, config) {
        service.template = data;
        console.log(service.template);
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.removeTemplate = function(id) {
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
  
  service.removeAllTemplates = function() {
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
    service.initTemplates();
  };

  service.pageChanged = function() {
    service.updatePage(service.pagination.current-1);
  };
  
  service.setTemplates = function(templates) {
    service.templates = templates;
  };
  
  service.updateTemplate = function(location, template) {
    var url;
    
    if (!template.id) {
      url = service.path + 'add';
      service.updateTemplateHelper(location, template, url, true);
    } else {
      url = service.path + 'update/' + template.id;
      service.updateTemplateHelper(location, template, url, false);
    }
  };
  
  service.updateTemplateHelper = function(location, template, url, redirect) {
    $http.post(url, template).
      success(function(data, status, headers, config) {
        console.log(data);
        if (redirect && data.id) {
          service.editTemplate(location, data.id);
        }
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.searchTemplates = function(keyword) {
    var first = 0;
    var size = service.resultSize;
    var url = service.path + 'search/' + keyword + '/' + first + '/' + size;
    
    console.log(url);
    if (keyword) {
      $http.get(url).
        success(function(data, status, headers, config) {
          service.templates = data;
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
  
  service.addNewProperty = function(template) {
    if (template) {
      var properties = template.properties;
      if (properties) {
        var size = properties.length;
        var property = {};
        properties[size] = property;
      } else {
        template.properties = [{}];
      }
    }
  };
  
  service.removeProperty = function(template, property) {
    if (template && property) {
      var properties = template.properties;
      if (template) {
        template.properties = _.without(properties, property);
      }
    }
  };
  
  service.addNewComponent = function(template) {
    if (template) {
      var components = template.components;
      if (components) {
        var size = components.length;
        var component = {};
        components[size] = component;
      } else {
        template.components = [{}];
      }
    }
  };
  
  service.removeComponent = function(template, component) {
    if (template && component) {
      var components = template.components;
      if (template) {
        template.components = _.without(components, component);
      }
    }
  };
  
  return service;
  
}]);