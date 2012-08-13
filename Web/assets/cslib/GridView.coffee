picgrid = $ "#picgrid"

# Get Incidents
data = Window.IncidentList 3, 12 

# Create and Append each incident as a thumbnail
for i in data.Incidents
	incident = Window.CreateIncidentThumbnail i
	picgrid.append incident
	
