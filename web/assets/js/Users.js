(function() {
  var adminMenu, getCookie, isAdmin, loginButton, loginMenu, logoutButton, setCookie, setLogInControl;

  adminMenu = $("#adminMenu");

  logoutButton = $("#logoutButton");

  loginMenu = $("#loginMenu");

  loginButton = $("#loginButton");

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
      console.log("Showing admin menu");
      adminMenu.css({
        display: "block"
      });
      return loginMenu.css({
        display: "none"
      });
    } else {
      console.log("Hiding admin menu");
      adminMenu.css({
        display: "none"
      });
      return loginMenu.css({
        display: "block"
      });
    }
  };

  logoutButton.click(function() {
    setCookie("status", "User");
    return setLogInControl();
  });

  loginButton.click(function() {
    setCookie("status", "Admin");
    return setLogInControl();
  });

  setLogInControl();

}).call(this);
