adminMenu = $ "#adminMenu"
logoutButton = $ "#logoutButton"
loginMenu = $ "#loginMenu"
loginButton = $ "#loginButton"

loggedIn = false

setCookie = (name, value) ->
	c_value = escape value
	c_name = escape name
	document.cookie = "#{c_name}=#{c_value};"
	console.log("Setting cookie to '#{c_name}=#{value};'")
	console.log "Cookies is '#{document.cookie}'"
	
getCookie = (name) ->
	cookies = document.cookie.split(";")
	console.log "cookies found #{cookies}"
	for c in cookies
		cookie = c.split "="
		if name == cookie[0]
			return unescape cookie[1]

isAdmin = () ->
	getCookie "status" == "Admin"
	return loggedIn
	
setLogInControl = () ->
	if isAdmin()
		console.log "Showig admin menu"
		adminMenu.show()
		loginMenu.hide()
	else
		console.log "Hiding admin menu"
		adminMenu.hide()
		loginMenu.show()

logoutButton.click ->
	setCookie "status", "User"
	loggedIn = false
	setLogInControl()
	
loginButton.click ->
	setCookie "status", "Admin"
	loggedIn = true
	setLogInControl()
	
setLogInControl()

	
		

		
		