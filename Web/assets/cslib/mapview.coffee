# Set up the Google map 

SetupMap = () ->
	
	# Map set up
	mapCenter = new google.maps.LatLng -41.288889, 174.777222

	mapOptions = { 
		zoom: 9,
		center: mapCenter,
		mapTypeId: google.maps.MapTypeId.ROADMAP,
		disableDefaultUI: true
	}

	elem = document.getElementById 'map_canvas'
	
	new google.maps.Map elem, mapOptions
	
		
googleMap = SetupMap()
google.maps.event.addDomListener(window, "load", googleMap);

# Fix the height of the map, twitter bootstrap fucks it up, it probbly fucks up some other map things aswell
mapCanvas = $ "#map_canvas"
mapCanvas.css {height: "100%" }
# mapCanvasHeight = mapCanvas.css "height"
# mapCanvas.css {height: "#{mapCanvasHeight - 50}px"}

# Get test data and populate map
incidentList = Window.IncidentList 0, 64

# Zoom listner
# google.maps.event.addListener googleMap, "zoom_changed", ->
# console.log "Zoom #{googleMap.getZoom()} "
size = 0.05
	
createModal = (incident) ->
	lat = new google.maps.LatLng incident.Lat - size, incident.Lng - size * 2
	lng = new google.maps.LatLng incident.Lat + size, incident.Lng + size * 2

	imageBounds = new google.maps.LatLngBounds lat, lng
	overlay = new google.maps.GroundOverlay "#{incident.Thumbnail_URL}", imageBounds

	google.maps.event.addListener overlay, 'click', () => 
		Window.CreateIncidentModal incident.Incident_ID

	overlay.setMap googleMap

for incident in incidentList.Incidents
	createModal incident
		


###
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
###	
