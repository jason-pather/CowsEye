contentArea = $ "#approveIncidentContent"

make = (i) ->

	incident =	$	"<ul class=\"thumbnails\">
						<li class = \"span3\">
							<div class=\"thumbnail\">
								<img src=\"#{i.thumbnail_url}\" alt=\"\">
							</div>
						</li>
						<li class = \"span9\">
							<div class = \"caption\">
								<h2>Description</h2>
								<p>#{i.description}</p>
							</div>
						</li>
						<li class=\"span12\"> 
							<div class=\"btn-group\" id =\"btns\"> 
							</div>
						</li>
					</ul>
				</div>"
				
	outcome = "reject";
	acceptButton = $ "<a href=\"#\" class=\"btn btn-success btn-long\"><i class=\"icon-ok icon-white\"></i> Accept</a>"
	rejectButton = $ "<a href=\"#\" class=\"btn btn-danger btn-long\"><i class=\"icon-remove icon-white\"></i> Reject</a>"	
	
	btnGroup = incident.find "#btns"
	btnGroup.append acceptButton 
	btnGroup.append rejectButton
	
	contentArea.append incident	
	
	postPath = "assess_incident_stub"
	postCalltype = "POST"
	postDone = (data) ->
		incident.animate {height:0, opacity:0.25}, ->
			incident.remove()
		
	acceptButton.click ->
		Window.RWCall postDone, postDone, {}, postPath, "/id=#{i.id}/status=accepted}", postCalltype
		
	rejectButton.click ->
		Window.RWCall postDone, postDone, {}, postPath, "/id=#{i.id}/status=rejected}", postCalltype

onFail = (data) ->
		console.log "Rest Call has failed"
path = "unapproved_stub"
args = "/start=#{0}/range=24"
calltype = "GET"	

onSuccess = (data) ->
	for i in data[ "incidents" ]
		make i
	
Window.RWCall onSuccess, onFail, {}, path, args, calltype