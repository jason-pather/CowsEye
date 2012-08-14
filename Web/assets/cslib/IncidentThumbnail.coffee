###
Create a Thumbnail For an Incident
###

Make = ( incident ) ->	
	
	# Create Cell
	cell =  $ "<li class=\"span3\">
				<div href=\"\" data-toggle=\"modal\" class=\"thumbnail\" id=\"testThumbnail\">
					<img src=\"#{incident.Thumbnail_URL}\" alt=\"\">
					<div class = \"caption\">
						<h5>Incident #{incident.Incident_ID}</h5>
						<p>#{incident.Short_Description}</p>
					</div>
				</div>
			</li>"	
		
	# Show Modal on Click
	cell.click ->
		Window.CreateIncidentModal incident.Incident_ID
	
	# Return Completed JQuery Element
	return cell
		
# Bind the method to the window so its global
Window.CreateIncidentThumbnail = Make