
// Configuration: predefined locations. You can specify these in the
// url like http://127.0.0.1:8080/#hacklabto If no location is
// specified then the app defaults to your current geo location and
// shows all stops near you within a 200 meter radius

var locations = {
    "hacklabto": {
        title: "Hacklab.TO Transit Board",
        agency: "ttc",
        routes: [
            {route: "510", stop: "3159", stopName: "Spadina & Nassau"}, // 510 South: Spadina Ave At Nassau St South Side
            {route: "510", stop: "6577", stopName: "Spadina & Nassau"}  // 510 North: Spadina Ave at Nassau St
        ]
    },
    "mozilla-toronto": {
        title: "Mozilla Toronto Transit Board",
        agency: "ttc",
        routes: [
            {route: "501", stop: "7060", stopName: "Queen & Peter"}, // 501 East: Queen & Peter St
            {route: "501", stop: "1653", stopName: "Queen & Spadina"}, // 501 West: Queen & Spadina Ave
            {route: "504", stop: "436",  stopName: "King & Spadina"}, // 504 West: King St West & Spadina Ave
            {route: "508", stop: "436",  stopName: "King & Spadina"}, // 508 West: King St West & Spadina Ave
            {route: "510", stop: "5275", stopName: "King & Spadina"}  // 510 North: Spadina Ave & King St West North Side
        ]
    }
};

// Below here is all app code.

var app = angular.module('predictions', []);

app.directive('timer', function () {
    return {
        template: '<div style="width: 100%; height: 20px"><div style="background: #BF1616; width: {{progress}}%; height: 20px"></div></div>',
        scope: { progress: '@progress' },
        replace: true,
        restrict: 'E'
    };
});

app.controller('PredictionsController', function ($scope, $http, $timeout) {

    $scope.seconds = 0;
    $scope.progress = 0;
    $scope.lines = [];
    $scope.focus = true;
    $scope.title = "";

    $scope.loadPredictions = function(location) {

        var stopsForLocation = function(name) {
            var stops = [];
            for (var i = 0; i < locations[name].routes.length; i++) {
                stops.push(locations[name].routes[i].stop);
            }
            return stops;
        };

        var simplifyDirectionName = function(name) {
            name = name.replace("Station", "stn");
            var i = name.indexOf("towards");
            if (i != -1) {
                return "T" + name.substring(i+1);
            } else {
                return name;
            }
        };

        var request = {method: "GET"};
        if (location.position) {
            request.url = "/api/predictions-for-position";
            request.params = {latitude: location.position.latitude, longitude: location.position.longitude, radius: 0.25};
        } else if (location.name) {
            request.url = "/api/predictions-for-stops";
            request.params = {stops: stopsForLocation(location.name).join(",")};
        }

        $http(request)
            .success(function(data) {

                var lines = [];

                if (location.name)
                {
                    var isPredictionOfInterest = function(name, prediction) {
                        for (var i = 0; i < locations[name].routes.length; i++) {
                            if (locations[name].routes[i].route === prediction.route.tag && locations[name].routes[i].stop === prediction.stop.tag) {
                                return true;
                            }
                        }
                        return false;
                    };

                    var getStopName = function(name, tag) {
                        for (var i = 0; i < locations[name].routes.length; i++) {
                            if (tag === locations[name].routes[i].stop) {
                                return locations[name].routes[i].stopName;
                            }
                        }
                        return "";
                    }

                    _.each(data.predictions, function (prediction) {
                        if (isPredictionOfInterest(location.name, prediction)) {
                            _.each(prediction.directions, function (direction) {
                                var line = {route: prediction.route.tag,
                                            direction: direction.title[0],
                                            stopName: simplifyDirectionName(direction.title),
                                            times: [],
                                            name: getStopName(location.name, prediction.stop.tag) };
                                _.each(direction.predictions, function(prediction) {
                                    if (prediction.minutes > 1) {
                                        line.times.push(prediction.minutes);
                                    }
                                });
                                lines.push(line);
                            });
                        }
                    });
                }

                else if (location.position)
                {
                    _.each(data.predictions, function (prediction) {
                        _.each(prediction.directions, function (direction) {
                            var line = {route: prediction.route.tag,
                                        direction: direction.title[0],
                                        stopName: simplifyDirectionName(direction.title),
                                        times: [],
                                        name: prediction.stop.title };
                            _.each(direction.predictions, function(prediction) {
                                if (prediction.minutes > 0) {
                                    line.times.push(prediction.minutes);
                                }
                            });
                            lines.push(line);
                        });                        
                    });
                }

                // Sort by direction, then line
                var directions = ['E', 'W', 'N', 'S'];
                $scope.lines = _.sortBy(lines, function(line) { return "" + directions.indexOf(line.direction) + line.route; });
            })
            .error(function(/*data, status, headers, config*/){
            });
    };

    // If we are called with a location name (in the hash) then we load those stops. Otherwise
    // we figure out where we are and just show stops near us.

    var location = {};

    $scope.onTimeout = function() {
        if ($scope.focus) {
            if ($scope.seconds == 0) {
                $scope.loadPredictions(location);
            }

            $scope.seconds++;
            $scope.progress = ($scope.seconds / 30) * 100;
            
            if ($scope.seconds == 30) {
                $scope.seconds = 0;
            }
        }        
        refreshTimeout = $timeout($scope.onTimeout, 1000);
    };

    if (window.location.hash === "") {
        $scope.title = "Departures"
        navigator.geolocation.getCurrentPosition(function (position) {
            location = {position: {latitude: position.coords.latitude, longitude: position.coords.longitude}};
            $scope.loadPredictions(location);
            var refreshTimeout = $timeout($scope.onTimeout, 1000);
        });
    } else {
        var name = window.location.hash.substr(1);
        $scope.title = locations[name].title;
        location = {name: name};
        $scope.loadPredictions(location);
        var refreshTimeout = $timeout($scope.onTimeout, 1000);
    }
});
