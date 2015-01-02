'use strict';

/* Directives */
var aboutModule = angular.module('myApp.about', []);

aboutModule.run(['AboutService', function(AboutService) {
  AboutService.init();
}]);

aboutModule.factory('AboutService', ['$rootScope', '$http', '$location', 'Storage', function($rootScope, $http, $location, Storage) {
  var AboutService = {};

  AboutService.url = "/contentdata/about.json";
  AboutService.about = false;
  
  AboutService.init = function() {
    AboutService.loadAbout();
  };
  
  AboutService.aboutLoaded = function(loaded) {
    $rootScope.$broadcast('AboutLoaded');
  };
  
  AboutService.activateLinks = function(str, links) {
    if (str == undefined || links == undefined) return str;
    
    var template = '<a href="{url}" target="_blank">{label}</a>';
    for (var i in links) {
      var link = links[i];
      var a = template.replace('{url}', link.url).replace('{label}', link.name);
      str = str.replace(link.name, a);
    }
      
    return str;
  };
  
  AboutService.setAbout = function(about) {
    about.credits = AboutService.activateLinks(about.credits, about.links);
    about.themes = AboutService.activateLinks(about.themes, about.links);
    
    AboutService.about = about;
    AboutService.aboutLoaded();
  };
  
  AboutService.loadAbout = function() {
    var url = AboutService.url
    $http.get(url).
      success(function(data, status, headers, config) {        
        AboutService.setAbout(data);
      }).
      error(function(data, status, headers, config) {
        console.log(data);
      });
  };
  
  return AboutService;
  
}]);