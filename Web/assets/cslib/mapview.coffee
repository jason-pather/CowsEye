

makeModal = () ->
	console.log "Creating Modal"
	popup = $ "<div class=\"modal hide fade in\" id=\"myModal\">
		<div class=\"modal-header\">
			<button type=\"button\" class=\"close\" data-dismiss=\"modal\">×</button>
			<h3>Mock Incident</h3>
		</div>
		<div class=\"modal-body\">
			<div href=\"#\" class=\"thumbnail\" id=\"testThumbnail\">
				<img src=\"http://placehold.it/1040x640\" alt=\"\">	
			</div>
			<h2>The Title</h2>
			<p> This is a sample picture to be used as a place holder when testing for content or something and another line for good measure, anlala and some more writing alalala and some more writing alalala and some more writing alalala </p>
			<h2>Comments</h2>
			<p> This is the first comment on this incident </p>
			<p> This is the second comment on this incident, yay another comment</p>
			<p> This is the second comment on this incident, yay another comment</p>
			<p> This is the fourth comment, no wait the second comment was double post</p>
			<textarea class=\"input-xlarge\" id=\"textarea\" rows=\"3\" style=\"margin: 0px; width: 695px; height: 114px; \"></textarea>
			<p></p>
			<a href=\"#\" class=\"btn btn-success\">Leave Comment</a>

		</div>
		<div class=\"modal-footer\">
		<a href=\"#\" class=\"btn btn-danger\" data-dismiss=\"modal\">Close</a>
		
		</div>
		
	</div>"
	
	# container = $ ".container"
	# container.append popup
	
	popup.modal {show:true}

initialize = () ->
	
	# Map set up
	mapCenter = new google.maps.LatLng 40.740, -74.18
	
	mapOptions = { 
		zoom: 13,
		center: mapCenter,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	}

	elem = document.getElementById 'map_canvas'
	
	map = new google.maps.Map elem, mapOptions
			
	# Test Data
	for num in [1..10]
		inc = new incident num, "matt.je.betts@gmail.com", -34.397 + num, 150.644 + num, "http://www.w3schools.com/images/pulpit.jpg", "a"		
		inc.draw(map)
	
	
	# Test image oberlays

	newark = new google.maps.LatLng 40.740, -74.18
	newarkLat = new google.maps.LatLng 40.716216, -74.213393
	newarkLng = new google.maps.LatLng 40.765641, -74.139235

	imageBounds = new google.maps.LatLngBounds newarkLat, newarkLng

	oldmap = new google.maps.GroundOverlay "http://www.lib.utexas.edu/maps/historical/newark_nj_1922.jpg", imageBounds
	
	google.maps.event.addListener oldmap, 'click', () => 
		makeModal()
	oldmap.setMap map


google.maps.event.addDomListener(window, 'load', initialize);


###
Individual incident, located on the map
###
class incident

	constructor: (@id, @email, @lat, @lng, @img, @desc) ->
	
		### 
		Popup content
		the document dom creation needs to be done to reference the dom,
		before wrapping in in jquery,
		so it can be passes to the maps API
		###
		
		popupContentX = document.createElement "div" 
		popupContentX.innerText = "content!"
		popupContent = $ popupContentX 
		
		@status = $ "<p>waiting</p>";
		
		acceptButton = $ "<div> Accept </div>"
		acceptButton.click () => 
			@accept()
			
		# Popup Window
		@info = new google.maps.InfoWindow	
		@info.setContent popupContentX
		
		# Create Popup
		popupContent.append acceptButton 
		popupContent.append @status
		popupContent.append "
			<b> #{ @id }</b>
			<img src=\"#{@img}\" />
			"
		
		# Incident State
		@accepted = false
		
		
	# Add the incident a map
	draw: (map) ->
		
		mrk = new google.maps.Marker
			position: new google.maps.LatLng @lat, @lng
			map:map
		
		google.maps.event.addListener mrk, 'click', () => @info.open map, mrk
		
	# Accept the incident as valid
	accept: () ->
		@accepted = true
		@status.html "Accepted"
		
		
# Fix the height of the map, twitter bootstrap fucks it up, it probbly fucks up some other map things aswell
map = $ "#map_canvas"
map.css {height: "100%" }
