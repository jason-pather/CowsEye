(function() {
  var adminMenu, form_login, getCookie, isAdmin, loginButton, loginMenu, logoutButton, passwordInput, setCookie, setLogInControl, usernameInput;

  adminMenu = $("#adminMenu");

  logoutButton = $("#logoutButton");

  loginMenu = $("#loginMenu");

  loginButton = $("#loginButton");

  usernameInput = $("#usernameInput");

  passwordInput = $("#passwordInput");

  form_login = $("form_login");

  setCookie = function(name, value) {
    var c_name, c_value;
    c_value = escape(value);
    c_name = escape(name);
    document.cookie = "" + c_name + "=" + c_value + ";";
    console.log("Setting cookie to '" + c_name + "=" + value + ";'");
    return console.log("Cookies is '" + document.cookie + "'");
  };

  getCookie = function(name) {
    var c, cookie, cookies, _i, _len;
    cookies = document.cookie.split(";");
    console.log("cookies found " + cookies);
    for (_i = 0, _len = cookies.length; _i < _len; _i++) {
      c = cookies[_i];
      cookie = c.split("=");
      if (name === cookie[0]) return unescape(cookie[1]);
    }
  };

  isAdmin = function() {
    return getCookie("status") === "Admin";
  };

  setLogInControl = function() {
    if (isAdmin()) {
      adminMenu.css({
        display: "block"
      });
      return loginMenu.css({
        display: "none"
      });
    } else {
      adminMenu.css({
        display: "none"
      });
      return loginMenu.css({
        display: "block"
      });
    }
  };

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
