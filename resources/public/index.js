var app = angular.module('predictions', []);

app.directive('timer', function () {
    return {
        template: '<div style="width: 100%; height: 20px"><div style="background: red; width: {{progress}}%; height: 20px"></div></div>',
        scope: { progress: '@progress' },
        replace: true,
        restrict: 'E'
    };
});

app.controller('PredictionsController', function ($scope, $http, $timeout) {

    $scope.progress = 0;
    $scope.predictions = [];

    $scope.loadPredictions = function(latitude, longitude, radius) {
        $http({method: "GET", url: "/api/predictions-for-position", params: {latitude: latitude, longitude: longitude, radius: radius}})
            .success(function(data) {
                $scope.predictions = data.predictions;
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
