<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<?xml version="1.0" encoding="utf-8" ?>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="en" xmlns:ng="http://angularjs.org" id="ng-app" class="ng-app:myApp" ng-app="myApp" ng-controller="MainCtrl">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="description" content="Analyzing the internet">
  <meta name="author" content="mjunaidi">
  <link rel="shortcut icon" href="assets/ico/favicon.png">

  <title>CamNav</title>

  <!-- Bootstrap core CSS -->
  <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet">
  <link ng-href="bootstrap/css/bootstrap.{{ theme }}.min.css" rel="stylesheet">

  <!-- Custom styles for this template -->
  <link href="css/dashboard.css" rel="stylesheet">
  <link href="css/dashboard-offcanvas.css" rel="stylesheet">
  <link href="css/xeditable.css" rel="stylesheet">
  <link href="css/trendcycle.css" rel="stylesheet">
  <link href="css/override.css" rel="stylesheet">
  <link href="css/animation.css" rel="stylesheet">
  <link href="css/print.css" rel="stylesheet" media="print">

  <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
  <!--[if lt IE 9]>
    <script src="assets/js/html5shiv.js"></script>
    <script src="assets/js/respond.min.js"></script>
  <![endif]-->
</head>

<body ng-init="init()" class="ng-cloak">
  <div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container-fluid">
      <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
          <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="#">CamNav</a>
      </div>
      <div class="collapse navbar-collapse">
        <ul class="nav navbar-nav">
          <li ng-class="getActive('/location')"><a href="#template">Location</a></li>
          <li><a data-toggle="modal" data-target="#aboutModal" href="">About</a></li>
          <li class="dropdown">
            <a href="" class="dropdown-toggle" data-toggle="dropdown">Themes <b class="caret"></b></a>
            <ul class="dropdown-menu">
              <li ng-repeat="t in themes"><a href="" ng-click="setTheme(t)">
                <span ng-if="t === theme" class="glyphicon glyphicon-ok"></span> {{ t }}</a>
              </li>
            </ul>
          </li>
        </ul>
        <!-- right navbar links -->
        <ul class="nav navbar-nav navbar-right" ng-switch on="isLoggedIn">
          <li ng-switch-when="true" class="dropdown">
            <a href="" class="dropdown-toggle" data-toggle="dropdown"><span class="glyphicon glyphicon-user"></span> <b>{{ login.username }}</b> <b class="caret"></b></a>
            <ul class="dropdown-menu">
              <li><a href="#manage">Data Manager</a></li>
              <li ng-switch on="isIe">
                <a ng-switch-when="true" href="<c:url value="/j_spring_security_logout" />">Logout</a>
                <a ng-switch-default href="" ng-click="doLogout()">Logout</a>
              </li>
            </ul>
          </li>
          <li ng-switch-default class="dropdown">
            <a href="" class="dropdown-toggle" data-toggle="dropdown">Login <b class="caret"></b></a>
            <ul class="dropdown-menu">
              <li ng-include="'html/login.html'"></li>
            </ul>
          </li>
          <%-- Conventional JSP login method --%>
          <%--
          <li class="dropdown">
            <c:choose>
              <c:when test="${username != null}">
                <a href="" class="dropdown-toggle" data-toggle="dropdown"><span class="glyphicon glyphicon-user"></span> <b>${username}</b> <b class="caret"></b></a>
                <ul class="dropdown-menu">
                  <li><a href="#manage">Data Manager</a></li>
                  <li><a href="<c:url value="/j_spring_security_logout" />">Logout</a></li>
                </ul>
              </c:when>
              <c:otherwise>
                <a href="" class="dropdown-toggle" data-toggle="dropdown">Login <b class="caret"></b></a>
                <div class="dropdown-menu" ng-include="'${pageContext.request.contextPath}/login'"></div>
              </c:otherwise>
            </c:choose>
          </li>
          --%>
        </ul>
      </div>
      <!-- /.nav-collapse -->
    </div>
    <!-- /.container -->
  </div>
  <!-- /.navbar -->

  <div class="container-fluid">

    <div class="row">
      <div class="col-xs-12 col-sm-12">
        <div class="row">
          <div class="col-12 col-sm-12 col-lg-12" ng-view></div>
          <!--/span-->
        </div>
        <!--/row-->
      </div>
      <!--/span-->
    </div>
    <!--/row-->

  </div>
  <!--/.container-->

  <div ng-include="'html/aboutModal.html'"></div>

  <!-- Bootstrap core JavaScript
    ================================================== -->
  <!-- Placed at the end of the document so the pages load faster -->
  <script src="angularjs/jquery-1.11.2.min.js"></script>
  <script src="bootstrap/js/bootstrap.min.js"></script>
  <script src="js/highcharts.js"></script>

  <!-- Angular JS -->
  <script src="angularjs/underscore-min.js"></script>
  <script src="angularjs/lib/angular.min.js"></script>
  <script src="angularjs/lib/angular-animate.min.js"></script>
  <script src="angularjs/lib/angular-cookies.min.js"></script>
  <script src="angularjs/lib/angular-loader.min.js"></script>
  <script src="angularjs/lib/angular-resource.min.js"></script>
  <script src="angularjs/lib/angular-route.min.js"></script>
  <script src="angularjs/lib/angular-sanitize.min.js"></script>
  <script src="angularjs/lib/angular-touch.min.js"></script>
  
  <!-- extra libraries -->
  <script src="angularjs/ui-bootstrap-tpls-0.11.2.min.js"></script>
  <script src="angularjs/xeditable.min.js"></script>
  
  <!-- core scripts -->
  <script src="angularjs/app.js"></script>
  <script src="angularjs/directives.js"></script>
  <script src="angularjs/navigation.js"></script>
  <script src="angularjs/services.js"></script>
  
  <!-- context services -->
  <script src="angularjs/location.js"></script>
  
  <!-- util services -->
  <script src="angularjs/files.js"></script>
  <script src="angularjs/user.js"></script>
  <script src="angularjs/clipboard.js"></script>
  <script src="angularjs/about.js"></script>
  
  <!-- controllers -->
  <script src="angularjs/MainCtrl.js"></script>
</body>
</html>
