picgrid = $ "#picgrid"

popup = $ "#myModal"
popup.modal {show:false}

for i in [0..11]
	cell =  $ "<li class=\"span3\">
				<div href=\"#myModal\" data-toggle=\"modal\" class=\"thumbnail\" id=\"testThumbnail\">
					<div class = \"caption\">
						<h5>Comment</h5>
						<p>This is a sample picture to be used as a place holder when testing for content or something and another line for good measure, anlala and some more writing alalala and some more writing alalala and some more writing alalala</p>
					</div>
				</div>
			</li>"	

		
	picgrid.append cell
	cell.fadeOut 10
	cell.fadeIn 2000