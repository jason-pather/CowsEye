
initialize = () ->
	
	# Map set up
	mapCenter = new google.maps.LatLng -34.397, 150.644
	
	mapOptions = { 
		zoom: 8,
		center: mapCenter,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	}

	elem = document.getElementById 'map_canvas'
	
	map = new google.maps.Map elem, mapOptions

google.maps.event.addDomListener(window, 'load', initialize);