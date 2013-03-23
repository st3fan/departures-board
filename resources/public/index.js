var app = angular.module('predictions', []);

app.controller('PredictionsController', function ($scope, $http, $timeout) {
    $scope.recipes = [];

    $scope.loadPredictions = function(latitude, longitude, radius) {
        $http({method: "GET", url: "/api/predictions", params: {latitude: latitude, longitude: longitude, radius: radius}})
            .success(function(data) {
                $scope.predictions = data.predictions;
            })
            .error(function(/*data, status, headers, config*/){
            });
    };

    // Mozilla Toronto Office

    
    $scope.onTimeout = function() {
        console.log("Reloading");
        $scope.loadPredictions(43.647294, -79.394374, 0.25);
        refreshTimeout = $timeout($scope.onTimeout, 30000);
    };

    $scope.loadPredictions(43.647294, -79.394374, 0.25);
    var refreshTimeout = $timeout($scope.onTimeout, 30000);
});
