picgrid = $ "#picgrid"

current = 0;

# Get First 12 Incidents
data = Window.IncidentList 0, 12 

# Create and Append each incident as a thumbnail
for i in data.Incidents
	incident = Window.CreateIncidentThumbnail i
	picgrid.append incident
	current++
	
win = $ window 
doc = $ document

win.scroll ->
	if win.scrollTop() + win.height() == doc.height()
		console.log "Reached bottom of page"
	   
		data = Window.IncidentList current, 12 
	   
		for i in data.Incidents
			incident = Window.CreateIncidentThumbnail i
			picgrid.append incident
			current++
	
