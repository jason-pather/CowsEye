picgrid = $ "#picgrid"

for i in [0..24]
	cell =  $ "<li class=\"span3\">
				<div href=\"#\" class=\"thumbnail\" id=\"testThumbnail\">
					<img src=\"http://placehold.it/520x320\" alt=\"\">
					<div class = \"caption\">
						<h5>Picture</h5>
						<p>This is a sample picture to be used as a place holder when testing for content or something and another line for good measure, anlala and some more writing alalala and some more writing alalala and some more writing alalala</p>
					</div>
				</div>
			</li>"	

	
		
	picgrid.append cell
	cell.fadeOut 10
	cell.fadeIn 1000