picgrid = $ "#picgrid"

current = 0;
range = 24;

# Create and Append each incident as a thumbnail
appendThumbnails = (data) ->
	
	for i in data.incidents
		incidentThumbnail = Window.CreateIncidentThumbnail i
		picgrid.append incidentThumbnail
		current++
		
onFail = (data) ->
	console.log "Rest Call has failed"

Window.RWCall appendThumbnails, onFail, {}, "unapproved_stub", "/start=#{current}/number=#{range}", "GET"
	
win = $ window 
doc = $ document

win.scroll ->
	if win.scrollTop() + win.height() == doc.height()
		console.log "Reached bottom of page"
		Window.RWCall appendThumbnails, onFail, {}, "unapproved_stub", "/start=#{current}/number=#{range}", "GET"
	
