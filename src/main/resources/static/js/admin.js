$(document).ready(function () {

    $("#button-analyze").click(function () {
        var firstUser = $("#first-user option:selected").text().trim();
        var secondUser = $("#second-user option:selected").text().trim();
        alert(firstUser + " " + secondUser);
        $.get({
            type: 'get',
            url: '/tone-analyzer',
            dataType: 'json',
            data: 'firstUser=' + firstUser + '&secondUser=' + secondUser,
            success: function (data) {
                console.log(data);
                var series = []
                for (var key in data) {
                    if (data.hasOwnProperty(key)) {
                        console.log(key + " -> " + 100 * data[key]);
                        var arr = []
                        arr.push(key);
                        arr.push(100.0 * data[key]);
                        series.push(arr)
                    }
                }
                console.log(series);
                drawGraph(series)
            }
        });
    });


    function drawGraph(dataPoint) {

        Highcharts.chart('graph', {
            chart: {
                type: 'pie',
                options3d: {
                    enabled: true,
                    alpha: 55,
                    beta: 0
                }
            },
            title: {
                text: 'Tone Analyzer'
            },
            tooltip: {
                pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    depth: 45,
                    dataLabels: {
                        enabled: true,
                        format: '{point.name}'
                    }
                }
            },
            series: [{
                type: 'pie',
                name: 'Browser share',
                data: dataPoint
            }]
        });
    }
});
