// Generated by CoffeeScript 1.3.3
(function() {
  var data, i, incident, picgrid, _i, _len, _ref;

  picgrid = $("#picgrid");

  data = Window.IncidentList(3, 12);

  _ref = data.Incidents;
  for (_i = 0, _len = _ref.length; _i < _len; _i++) {
    i = _ref[_i];
    incident = Window.CreateIncidentThumbnail(i);
    picgrid.append(incident);
  }

}).call(this);
