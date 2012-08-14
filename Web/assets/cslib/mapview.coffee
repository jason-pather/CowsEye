# Set up the Google map 

SetupMap = () ->
	
	# Map set up
	mapCenter = new google.maps.LatLng 40.5995, 40.8714

	mapOptions = { 
		zoom: 13,
		center: mapCenter,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	}

	elem = document.getElementById 'map_canvas'
	
	new google.maps.Map elem, mapOptions
	
		
googleMap = SetupMap()
google.maps.event.addDomListener(window, 'load', googleMap);


# Fix the height of the map, twitter bootstrap fucks it up, it probbly fucks up some other map things aswell
mapCanvas = $ "#map_canvas"
mapCanvas.css {height: "100%" }

# Test image oberlays
newark = new google.maps.LatLng 40.740, -74.18
newarkLat = new google.maps.LatLng 40.716216, -74.213393
newarkLng = new google.maps.LatLng 40.765641, -74.139235

imageBounds = new google.maps.LatLngBounds newarkLat, newarkLng

oldmap = new google.maps.GroundOverlay "http://placehold.it/480x360", imageBounds

google.maps.event.addListener oldmap, 'click', () => 
	Window.CreateIncidentModal 2

oldmap.setMap googleMap

# Get test data and populate map
incidentList = Window.IncidentList 0, 12
for incident in incidentList.Incidents
	
	# distance = new google.maps.geometry.spherical.computeDistanceBetween(latlng, locationlatlng);
	size = 0.3
	
	newarkLat = new google.maps.LatLng incident.Lat - size, incident.Lng - size * 2
	newarkLng = new google.maps.LatLng incident.Lat + size, incident.Lng + size * 2

	imageBounds = new google.maps.LatLngBounds newarkLat, newarkLng

	oldmap = new google.maps.GroundOverlay "http://placehold.it/480x360", imageBounds

	google.maps.event.addListener oldmap, 'click', () => 
		Window.CreateIncidentModal 2

	oldmap.setMap googleMap
	
	console.log "created at #{incident.Lat} #{incident.Lng} for #{incident.Incident_ID}"

nz = new google.maps.LatLng -41.288889, 174.777222
googleMap.setCenter nz
