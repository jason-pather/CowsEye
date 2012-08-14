# Set up the Google map 

SetupMap = () ->
	
	# Map set up
	mapCenter = new google.maps.LatLng -41.288889, 174.777222

	mapOptions = { 
		zoom: 9,
		center: mapCenter,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	}

	elem = document.getElementById 'map_canvas'
	
	new google.maps.Map elem, mapOptions
	
		
googleMap = SetupMap()
google.maps.event.addDomListener(window, "load", googleMap);

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

# Zoom listner
# google.maps.event.addListener googleMap, "zoom_changed", ->
# console.log "Zoom #{googleMap.getZoom()} "
size = 0.05
	

for incident in incidentList.Incidents
		
	newarkLat = new google.maps.LatLng incident.Lat - size, incident.Lng - size * 2
	newarkLng = new google.maps.LatLng incident.Lat + size, incident.Lng + size * 2

	imageBounds = new google.maps.LatLngBounds newarkLat, newarkLng
	oldmap = new google.maps.GroundOverlay "http://placehold.it/480x360", imageBounds

	google.maps.event.addListener oldmap, 'click', () => 
		Window.CreateIncidentModal incident.Incident_ID

	oldmap.setMap googleMap


for incident in incidentList.Incidents

	newarkLat = new google.maps.LatLng incident.Lat - size, incident.Lng - size * 2
	newarkLng = new google.maps.LatLng incident.Lat + size, incident.Lng + size * 2
	
	borderCoordinates = [
		new google.maps.LatLng incident.Lat - size, incident.Lng - size * 2
		new google.maps.LatLng incident.Lat - size, incident.Lng + size * 2
		new google.maps.LatLng incident.Lat + size, incident.Lng + size * 2
		new google.maps.LatLng incident.Lat + size, incident.Lng - size * 2
		new google.maps.LatLng incident.Lat - size, incident.Lng - size * 2
	]
	
	border = new google.maps.Polyline {
			path: borderCoordinates,
			strokeColor: "000000",
			strokeOpacity: 1,
			strokeWeight: 1
	}
		
	border.setMap googleMap

