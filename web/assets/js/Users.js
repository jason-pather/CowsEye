(function() {
  var adminMenu, form_login, getCookie, isAdmin, loginButton, loginMenu, logoutButton, passwordInput, setLogInControl, usernameInput;

  adminMenu = $("#adminMenu");

  logoutButton = $("#logoutButton");

  loginMenu = $("#loginMenu");

  loginButton = $("#loginButton");

  usernameInput = $("#usernameInput");

  passwordInput = $("#passwordInput");

  form_login = $("form_login");

  /*
  setCookie = (name, value) ->
  	c_value = escape value
  	c_name = escape name
  	document.cookie = "#{c_name}=#{c_value};"
  	console.log("Setting cookie to '#{c_name}=#{value};'")
  	console.log "Cookies is '#{document.cookie}'"
  */

  getCookie = function(name) {
    var c, cookie, cookies, _i, _len;
    cookies = document.cookie.split(";");
    console.log("cookies found " + cookies);
    for (_i = 0, _len = cookies.length; _i < _len; _i++) {
      c = cookies[_i];
      console.log("Cookie is " + c);
    }
    cookie = c.split("=");
    if (name === cookie[0]) return unescape(cookie[1]);
  };

  isAdmin = function() {
    return getCookie("status") === "Admin";
  };

  setLogInControl = function() {
    return console.log("setLogInControl Called");
  };

  if (isAdmin()) {
    console.log("Is an Admin");
    adminMenu.css({
      display: "block"
    });
    loginMenu.css({
      display: "none"
    });
  } else {
    console.log("Is a user");
    adminMenu.css({
      display: "none"
    });
    loginMenu.css({
      display: "block"
    });
  }

  /* Old Testing controls
  logoutButton.click ->
  	setCookie "status", "User"
  	setLogInControl()
  		
  loginButton.click ->
  	form_login.submit ->
  	    alert "Done"
  	
  	setLogInControl()
  */

  $(function() {
    return setLogInControl();
  });

}).call(this);
