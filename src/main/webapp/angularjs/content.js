'use strict';

/* Directives */
var contentModule = angular.module('myApp.content', ['ngRoute', 'ngResource']);

contentModule.run(['ContentService', function(contentService) {
  //contentService.init();
}]);

contentModule.factory('ContentService', ['TemplateService', 'ClipboardService', '$rootScope', '$http', 'Storage', function(templateService, clipboardService, $rootScope, $http, Storage) {
  var service = {};
  
  service.contents = null;
  service.content = null;
  
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
  
  service.nav = '/content';
  service.path = 'content/api/';
  
  service.initPage = function(route) {
    var params = route.current.params;
    if (params.template) {
      service.initTemplate(params.template);
    }
  };
  
  service.initTemplate = function(name) {
    var url = templateService.path + 'get/name/' + name;
    $http.get(url).
      success(function(data, status, headers, config) {
        var template = data;
        service.template = template;
        service.init(template);
        console.log(service.template);
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.initEditTemplate = function(name, id) {
    var url = service.path + 'get/' + name + '/' + id;
    $http.get(url).
      success(function(data, status, headers, config) {
        service.template = data;
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.init = function(template) {
    if (template && template.name) {
      service.initRowCount(template);
    }
  };
  
  service.initContentPage = function(route) {
    var params = route.current.params;
    if (params.id) {
      service.initContent(params.id);
    } else {
      service.initNewContent();
      service.initPage(route);
    }
  };
  
  service.viewContents = function(location, template) {
    var path = service.nav + '/' + template.name;
    location.path(path);
  };
  
  service.addNewContent = function(location, template) {
    var path = service.nav + '/' + template.name + '/add';
    location.path(path);
  };
  
  service.editContent = function(location, template, id) {
    var path = service.nav + '/' + template.name + '/edit/' + id;
    location.path(path);
  };
  
  service.initRowCount = function(template) {
    var url = service.path + template.name + '/countRow';
    $http.get(url).
      success(function(data, status, headers, config) {
        service.rowCount = data.rowCount;
        service.initPageCount();
        service.initContents(template);
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.initContents = function(template) {
    var url = service.path + 'list/' + template.name + '/' + service.first + '/' + service.size;
    $http.get(url).
      success(function(data, status, headers, config) {
        service.contents = data;
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.initNewContent = function() {
    service.content = {};
  };
  
  service.initContent = function(id) {
    var url = service.path + 'get/' + id;
    $http.get(url).
      success(function(data, status, headers, config) {
        service.content = data;
        service.initEditTemplate(service.content.template.name, service.content.id);
        console.log(service.content);
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.removeContent = function(id) {
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
  
  service.removeAllContents = function() {
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
    service.initPagination();
  };
  
  service.nextPage = function() {
    if (service.hasNext()) {
      service.pageIndex++;
      service.updatePage(service.pageIndex);
    }
  };
  
  service.prevPage = function() {
    if (service.hasPrev()) {
      service.pageIndex--;
      service.updatePage(service.pageIndex);
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
    service.initContents(service.template);
  };

  service.pageChanged = function() {
    service.updatePage(service.pagination.current-1);
  };
  
  service.setContents = function(contents) {
    service.contents = contents;
  };
  
  service.updateContent = function(location, template, content) {
    if (template) {
      var url;
      
      if (!content.id) {
        url = service.path + 'add';
        service.updateContentHelper(location, template, content, url, true);
      } else {
        url = service.path + 'update/' + content.id;
        service.updateContentHelper(location, template, content, url, false);
      }
    }
  };
  
  service.updateContentHelper = function(location, template, content, url, redirect) {
    $http.post(url, template).
      success(function(data, status, headers, config) {
        console.log(data);
        if (redirect && data.id) {
          service.editContent(location, template, data.id);
        }
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  service.searchContents = function(keyword) {
    var first = 0;
    var size = service.resultSize;
    var url = service.path + 'search/' + keyword + '/' + first + '/' + size;
    
    console.log(url);
    if (keyword) {
      $http.get(url).
        success(function(data, status, headers, config) {
          service.contents = data;
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
  
  service.copy = function(content) {
    clipboardService.copy(content);
  };
  
  return service;
  
}]);