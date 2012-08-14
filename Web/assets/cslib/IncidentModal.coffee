###
Retreive, and create a incident modal dialog box
###

Make = (id) ->

	# Change somthing to the loading icon...
		
	# Reterive Infromation
	data = Window.IncidentDetail id 
	
	# Populate modal with correct information
	
	incidentModal = $ "<div class=\"modal hide fade in\" id=\"myModal\">
		<div class=\"modal-header\">
			<button type=\"button\" class=\"close\" data-dismiss=\"modal\">×</button>
			<h3>Mock Incident</h3>
		</div>
		<div class=\"modal-body\">
			<div href=\"#\" class=\"thumbnail\" id=\"testThumbnail\">
				<img src=\"#{data.Full_URL}\" alt=\"\">	
			</div>
			<h2>The Title for #{data.Incident_ID}</h2>
			<p> This is a sample picture to be used as a place holder when testing for content or something and another line for good measure, anlala and some more writing alalala and some more writing alalala and some more writing alalala </p>
			<h2>Comments</h2>
			<div id = \"comments\"></div>
			<textarea class=\"input-xlarge\" id=\"textarea\" rows=\"3\" style=\"margin: 0px; width: 695px; height: 114px; \"></textarea>
			<p></p>
			<a href=\"#\" class=\"btn btn-success\">Leave Comment</a>
		</div>
		<div class=\"modal-footer\">
		<a href=\"#\" class=\"btn btn-danger\" data-dismiss=\"modal\">Close</a>
		
		</div>
		
	</div>"
	
	incidentModal.modal {show: true }
	
	
	# Get comments
	comments = Window.CommentsForIncident id
	commentsSection = incidentModal.find "#comments"
	
	console.log "Found comments section"
	
	for c in comments["Comments"]
		console.log "adding coment #{c}"
		commentsSection.append $ "<p>#{c.Comment_Text}</p>"
		
Window.CreateIncidentModal = Make