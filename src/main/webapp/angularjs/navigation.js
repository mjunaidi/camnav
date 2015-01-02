'use strict';

angular.module('myApp.navigation', ['ngRoute'])
.config(function($routeProvider, $locationProvider) {
  $routeProvider
  .when("/location", {
    templateUrl: "html/location.html"
  })
  .when("/location/add", {
    templateUrl: "html/locationEdit.html"
  })
  .when("/location/edit/:id", {
    templateUrl: "html/locationEdit.html"
  })
  .otherwise({
    redirectTo: "/location"
  });
})
.run(function($route, $http, $templateCache) {
  angular.forEach($route.routes, function(r) {
    if (r.templateUrl) {
      $http.get(r.templateUrl, {cache: $templateCache});
    }
  });
})
.factory('Navigation', ['$location', function($location) {
  return {
    getPath : function(page, contentId) {
      var path = '/';
      if (page != undefined) {
        path += 'nav/' + page._id;
      }
      if (contentId != undefined) {
        path += '/' + contentId;
      }
      return path;
    },
    navigateTo : function(page, contentId) {
      var path = this.getPath(page, contentId);
      $location.path(path);
      console.log(path);
    }
  };
}])
.directive('ngTap', ['$location', function($location) {
  var isTouchDevice = !!("ontouchstart" in window);
  var tap = function(scope, elm, attrs) {
    if (attrs.ngTap.length > 0) {
      scope.$apply(attrs.ngTap);
    } else if (attrs.href.length > 0) {
      // remove hash from href
      var path = attrs.href;
      var ind = path.indexOf('#');
      if (ind >= 0) {
        path = path.substring(ind + 1, path.length);
      }
      console.log(path);
      $location.path(path);
      scope.$apply();
    }
  };
  return function(scope, elm, attrs) {
    if (isTouchDevice) {
      var tapping = false;
      elm.bind('touchstart', function() { tapping = true; });
      elm.bind('touchmove', function() { tapping = false; });
      elm.bind('touchend', function(e) {
        e.preventDefault();
        tapping && tap(scope, elm, attrs);
      });
    } else {
      elm.bind('click', function(e) {
        e.preventDefault();
        tap(scope, elm, attrs);
      });
    }
  };
}]);