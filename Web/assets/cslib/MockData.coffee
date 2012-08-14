###
This is a class for producing mock data, returns from methods in this class should be formatted as json, as specified by the API
###

# GPS
GPSCoordinatesNZ = [
		{lat: -40.405995, lng: 172.408714},
		{lat: -38.402627, lng: 178.011583},
		{lat: -43.529559, lng: 169.026211},
		{lat: -46.017497, lng: 167.429489},
		{lat: -42.353075, lng: 172.270042},
		{lat: -41.021948, lng: 173.009794},
		{lat: -36.414556, lng: 175.324181},
		{lat: -35.488086, lng: 173.385537},
		{lat: -41.021419, lng: 173.009429},
		{lat: -35.021999, lng: 173.556997},
		{lat: -38.59082, lng: 177.474014},
		{lat: -41.476564, lng: 172.203613},
	]
	
###
Test Stub for retreiving a specific incident
###		
IncidentDetail = ( id ) ->
	
	gps = Math.floor((Math.random()*12));
	
	data = {
		Incident_ID : id
		Full_URL :  "http://placehold.it/960x540"
		Description: "Description for #{id}"
		Tags: [ "Cow", "Poo", "River", "Pond" ]
		Lat: GPSCoordinatesNZ[0].lat
		Lng: GPSCoordinatesNZ[0].lng
	}

###
Test Stub for reteriving multiple incidents
###	
IncidentList = ( start, range ) ->
	
	data = {}
	data[ "start" ] = start
	data[ "range" ] = range
	
	
	
	incidents = []
	
	for i in [0..range - 1]
	
		gps = Math.floor((Math.random()*12));
	
		incidents[i] = {
			Incident_ID : start + i
			Thumbnail_URL :  "http://placehold.it/480x360"
			Short_Description: "Description for #{ start + i}"
			Tags: [ "Cow", "Poo", "River", "Pond" ]
			Lat: GPSCoordinatesNZ[gps].lat
			Lng: GPSCoordinatesNZ[gps].lng
			
			
		}
		
		console.log GPSCoordinatesNZ[gps].lat
	
	data[ "Incidents" ] = incidents
	
	
	
	return data

###
Test Stub for retreiving comments on an incident	
###
CommentsForIncident = (id, start, range) ->

	data = {}
	data[ "Incident_ID" ] = id
	data[ "Start"] = start
	data[ "Given"] = 2

	data[ "Comments"] = [
		{
			Incident_ID: id
			Comment_ID: start
			Comment_Text: "This is a comment"
		}
		{
			Incident_ID: id
			Comment_ID: start+1
			Comment_Text: "This is another comment"
		}
	]
	
	return data
	
# Create GPS Thumbnails

	
# Bind to the window
Window.IncidentList = IncidentList
Window.IncidentDetail = IncidentDetail
Window.CommentsForIncident = CommentsForIncident