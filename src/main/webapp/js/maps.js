function initialize() {
  var markers = [];
  var map = new google.maps.Map(document.getElementById('map-canvas'), {
    mapTypeId: google.maps.MapTypeId.ROADMAP
  });
  
  var upperLat = 3.2029051282133016;
  var upperLng = 101.38058066368103;
  var lowerLat = 3.002012968172354;
  var lowerLng = 101.89144492149353;

  var defaultBounds = new google.maps.LatLngBounds(
      new google.maps.LatLng(upperLat, upperLng),
      new google.maps.LatLng(lowerLat, lowerLng));
  //map.fitBounds(defaultBounds);
  
  var defaultLat = 3.246095307188791;
  var defaultLng = 101.42040610313416;
  var defaultPos = new google.maps.LatLng(defaultLat, defaultLng);
  var defaultType = google.maps.MapTypeId.HYBRID;
  map.setCenter(defaultPos);
  map.setMapTypeId(defaultType);
  map.setZoom(18);

  // Create the search box and link it to the UI element.
  var input = /** @type {HTMLInputElement} */(
      document.getElementById('pac-input'));
  map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

  var searchBox = new google.maps.places.SearchBox(
    /** @type {HTMLInputElement} */(input));

  // Listen for the event fired when the user selects an item from the
  // pick list. Retrieve the matching places for that item.
  google.maps.event.addListener(searchBox, 'places_changed', function() {
    var places = searchBox.getPlaces();

    if (places.length == 0) {
      return;
    }
    for (var i = 0, marker; marker = markers[i]; i++) {
      marker.setMap(null);
    }

    // For each place, get the icon, place name, and location.
    markers = [];
    var bounds = new google.maps.LatLngBounds();
    for (var i = 0, place; place = places[i]; i++) {
      var image = {
        url: place.icon,
        size: new google.maps.Size(71, 71),
        origin: new google.maps.Point(0, 0),
        anchor: new google.maps.Point(17, 34),
        scaledSize: new google.maps.Size(25, 25)
      };

      // Create a marker for each place.
      var marker = new google.maps.Marker({
        map: map,
        icon: image,
        title: place.name,
        position: place.geometry.location
      });

      markers.push(marker);

      bounds.extend(place.geometry.location);
    }

    map.fitBounds(bounds);
  });

  // Bias the SearchBox results towards places that are within the bounds of the
  // current map's viewport.
  google.maps.event.addListener(map, 'bounds_changed', function() {
    var bounds = map.getBounds();
    searchBox.setBounds(bounds);
  });
  
  var pointedMarker = new google.maps.Marker({
    position: new google.maps.LatLng(-33.8902, 151.1759),
    map: map,
    draggable:true
  });
  
  var infowindow = new google.maps.InfoWindow({
    content: ""
  });
  
  var btn = document.createElement('a');
  btn.innerHTML = 'set this position';
  
  google.maps.event.addListener(map, 'click', function(e) {
    moveMarker(e.latLng, pointedMarker);
  });
  
  google.maps.event.addListener(pointedMarker, 'click', function() {
    infowindow.setContent(btn);
    infowindow.open(pointedMarker.get('map'), pointedMarker);
  });
  
  google.maps.event.addDomListener(btn, 'click', function() {
    var position = pointedMarker.getPosition();
    var lat = position.lat();
    var lng = position.lng();
    assignPosition(lat, lng);
  });
  
  var refreshBtn = document.getElementById('map-refresh');
  
  google.maps.event.addDomListener(refreshBtn, 'click', function() {
    google.maps.event.trigger(map, 'resize');
    map.fitBounds(defaultBounds);
    map.setZoom(18);
    panToSelectedMarker(map);
  });
  
  setTimeout(function() {
    google.maps.event.trigger(map, 'resize');
    map.fitBounds(defaultBounds);
    map.setZoom(18);
    initSelectedMarker(map);
  }, 2000);
}

function moveMarker(position, marker) {
  marker.setPosition(position);
}
function panToSelectedMarker(map) {
  var form = document.getElementById('locationEditForm');
  var latFld = form.latitude;
  var lngFld = form.longitude;
  var lat = latFld.value;
  var lng = lngFld.value;
  if (lat.length > 0 && lng.length > 0) {
    var position = new google.maps.LatLng(lat, lng);
    map.panTo(position);
  }
}
function initSelectedMarker(map) {
  var form = document.getElementById('locationEditForm');
  if (form != undefined) {
    var latFld = form.latitude;
    var lngFld = form.longitude;
    if (latFld != undefined && lngFld != undefined) {
      var lat = latFld.value;
      var lng = lngFld.value;
      if (lat.length > 0 && lng.length > 0) {
        var position = new google.maps.LatLng(lat, lng);
        placePermanentMarker(position, map);
      }
    }
  }
}
function placePermanentMarker(position, map) {
  var marker = new google.maps.Marker({
    icon: {
      path: google.maps.SymbolPath.BACKWARD_CLOSED_ARROW,
      strokeColor: "red",
      scale: 3
    },
    position: position,
    map: map
  });
  map.panTo(position);
}
function placeMarker(position, map) {
  var marker = new google.maps.Marker({
    position: position,
    map: map,
    draggable:true
  });
  map.panTo(position);
}
function assignPosition(lat, lng) {
  var form = document.getElementById('locationEditForm');
  var latFld = form.latitude;
  var lngFld = form.longitude;
  latFld.value = lat;
  lngFld.value = lng;
}

//google.maps.event.addDomListener(window, 'load', initialize);