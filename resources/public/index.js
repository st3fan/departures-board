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

    $scope.progress = 0;
    $scope.lines = [];

    $scope.loadPredictions = function(latitude, longitude, radius) {
        $http({method: "GET", url: "/api/predictions-for-stops", params: {stops: "7060,1653,436,5275"}})
            .success(function(data) {
                // TODO Ideally all this moves to the server side so that we only have to fetch data here
                var routesOfInterest = [
                    {route: "501", stop: "7060", stopName: "Queen & Peter"}, // 501 East: Queen & Peter St
                    {route: "501", stop: "1653", stopName: "Queen & Spadina"}, // 501 West: Queen & Spadina Ave
                    {route: "504", stop: "436",  stopName: "King & Spadina"}, // 504 West: King St West & Spadina Ave
                    {route: "508", stop: "436",  stopName: "King & Spadina"}, // 508 West: King St West & Spadina Ave
                    {route: "510", stop: "5275", stopName: "King & Spadina"}  // 510 North: Spadina Ave & King St West North Side
                ];

                var isPredictionOfInterest = function(prediction) {
                    for (var i = 0; i < routesOfInterest.length; i++) {
                        if (routesOfInterest[i].route === prediction.route.tag && routesOfInterest[i].stop === prediction.stop.tag) {
                            return true;
                        }
                    }
                    return false;
                };

                var simplifyDirectionName = function(name) {
                    var i = name.indexOf("towards");
                    if (i != -1) {
                        return "T" + name.substring(i+1);
                    } else {
                        return name;
                    }
                };

                var getStopName = function(tag) {
                    for (var i = 0; i < routesOfInterest.length; i++) {
                      if (tag === routesOfInterest[i].stop) {
                          return routesOfInterest[i].stopName;
                      }
                    }
                    return "";
                }

                var lines = [];
                _.each(data.predictions, function (prediction) {
                    if (isPredictionOfInterest(prediction)) {
                        _.each(prediction.directions, function (direction) {
                            var line = {route: prediction.route.tag, direction: direction.title[0],
                                        name: simplifyDirectionName(direction.title), times: [], stopName: getStopName(prediction.stop.tag) };
                            _.each(direction.predictions, function(prediction) {
                                if (prediction.minutes > 1) {
                                    line.times.push(prediction.minutes);
                                }
                            });
                            lines.push(line);
                            console.log(line);
                        });
                    }
                });

                $scope.lines = lines;
            })
            .error(function(/*data, status, headers, config*/){
            });
    };

    var seconds = 0;

    $scope.onTimeout = function() {
        console.log("Reloading");
        seconds++;
        $scope.progress = (seconds / 30) * 100;
        if (seconds == 30) {
            seconds = 0;
            $scope.loadPredictions(43.647294, -79.394374, 0.25);
        }
        refreshTimeout = $timeout($scope.onTimeout, 1000);
    };

    $scope.loadPredictions(43.647294, -79.394374, 0.25);
    var refreshTimeout = $timeout($scope.onTimeout, 1000);
});
