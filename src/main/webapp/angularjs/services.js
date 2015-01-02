'use strict';

// In this case it is a simple value service.
var servicesModule = angular.module('myApp.services', []);
servicesModule.value('version', '0.1');
servicesModule.factory('Storage', function() {
    
    var _ = this._; // hold reference to Underscore.js
    
    var S4 = function() {
       return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
    };
    var guid = function() {
       return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
    };
    
    var newServiceInstance = {}; // factory function body that constructs newServiceInstance
    
    newServiceInstance.loadObject = function(key) {

        // variable to hold date found in local storage
        var data = [];

        // retrieve json data from local storage for key
        var json_object = localStorage[key];

        // if data was found in local storage convert to object
        if (json_object) {
          try{
            data = JSON.parse(json_object);
          } catch (err){
            console.log('loadObject -> err -> ');
            console.log(err);
          }
        }
        return data;
    };

    newServiceInstance.clear = function() {
      localStorage.clear();
    };

    newServiceInstance.supported = function() {
      return 'localStorage' in window && window['localStorage'] !== null;
    };

    newServiceInstance.saveObject = function(objectToSave,key) {
        // Save object to local storage under key
        localStorage[key] = JSON.stringify(objectToSave);
    };
    
    newServiceInstance.guid = function() {
      return guid();
    };
    
    return newServiceInstance;
});