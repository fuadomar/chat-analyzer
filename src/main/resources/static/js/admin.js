$(document).ready(function () {

    function normalizedDataToneAnalyzer(data) {
        var series = [];
        for (var key in data) {
            if (data.hasOwnProperty(key)) {
                if (data[key] < 0.2)
                    continue
                console.log(key + " -> " + data[key]);
                var arr = [];
                var number = data[key];
                console.log(number);
                arr.push(key);
                arr.push(parseFloat(number.toFixed(2)));
                series.push(arr)
            }
        }
        return series;
    }

    function getRandomInt(min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    function calculateProbableScoreAspect(scoreTag) {

        if (scoreTag === "P+")
            return getRandomInt(0.8, 0.95);
        else if (scoreTag === "P")
            return getRandomInt(0.7, 0.79);
        else if (scoreTag === "N+")
            return getRandomInt(0.1, 0, 2);
        else if (scoreTag === "N")
            return getRandomInt(0.2, 0.3);
        return 0.0;
    }

    function normalizeDataAspectBasedToneAnalyzer(data, aspect) {

        var categories = [];
        var set = new Set();
        for (var i = 0; i < data.length; i++) {
            if (data[i].score_tag === "NONE")
                continue;
            var elementArray = [];
            if (set.has(data[i].type))
                continue;
            set.add(data[i].type);
            if (aspect === "aspect")
                elementArray.push(data[i].form);
            else
                elementArray.push(data[i].type);
            var scoreTag = data[i].score_tag;
            var probableScore = calculateProbableScoreAspect(scoreTag);
            elementArray.push(probableScore);
            categories.push(elementArray);
        }
        return categories;
    }
    function clearGraphDdiv() {
        $("#graph").empty();
        $("#graph-aspect").empty();
    }

    $("#button-analyze-tone-people").click(function () {
        clearGraphDdiv();
        var sender = $("#sender option:selected").text().trim();
        $.get({
            type: 'get',
            url: '/tone-analyzer-people-individual',
            dataType: 'json',
            data: 'sender=' + sender,
            success: function (data) {
                console.log(data);
                var series = normalizedDataToneAnalyzer(data);
                console.log(series);
                drawDonut(series, "graph", "tone analyzer for people")
            }
        });
    });

    $("#button-analyze-tone-places").click(function () {
        clearGraphDdiv();
        var sender = $("#sender option:selected").text().trim();
        $.get({
            type: 'get',
            url: '/tone-analyzer-places-individual',
            dataType: 'json',
            data: 'sender=' + sender,
            success: function (data) {
                console.log(data);
                var series = normalizedDataToneAnalyzer(data);
                console.log(series);
                drawDonut(series, "graph", "tone analyzer for places")
            }
        });
    });

    $("#button-analyze-tone-organizations").click(function () {
        clearGraphDdiv();
        var sender = $("#sender option:selected").text().trim();
        $.get({
            type: 'get',
            url: '/tone-analyzer-organizations-individual',
            dataType: 'json',
            data: 'sender=' + sender,
            success: function (data) {
                console.log(data);
                var series = normalizedDataToneAnalyzer(data);
                console.log(series);
                drawDonut(series, "graph", "tone analyzer for organizations")
            }
        });
    });

    $("#button-analyze-tone").click(function () {
        clearGraphDdiv();
        var sender = $("#sender option:selected").text().trim();
        $.get({
            type: 'get',
            url: '/tone-analyzer-individual',
            dataType: 'json',
            data: 'sender=' + sender,
            success: function (data) {
                console.log(data);
                var series = normalizedDataToneAnalyzer(data);
                console.log(series);
                drawDonut(series, "graph", "tone analyzer")
            }
        });
    });

    $("#button-analyze-texttag").click(function () {
        clearGraphDdiv();
        var sender = $("#sender option:selected").text().trim();
        $.get({
            type: 'get',
            url: '/texttag-analyzer-individual',
            dataType: 'json',
            data: 'sender=' + sender,
            success: function (data) {

                var series = normalizedDataToneAnalyzer(data);
                console.log(series);
                drawDonut(series, "graph", "texttag")
            }
        });
    });

    $("#button-analyze-aspect").click(function () {
        clearGraphDdiv();
        var sender = $("#sender option:selected").text().trim();
        $.get({
            type: 'get',
            url: '/aspect-analyzer-individual',
            dataType: 'json',
            data: 'sender=' + sender,
            success: function (data) {
                var entityList = data.sentimented_entity_list;
                var entityDetails = normalizeDataAspectBasedToneAnalyzer(entityList, "");
                console.log(entityDetails);
                drawDonut(entityDetails, "graph", "aspect-based entity");
                var aspectList = data.sentimented_concept_list;
                var aspectDetails = normalizeDataAspectBasedToneAnalyzer(aspectList, "aspect");
                console.log(aspectDetails);
                drawDonut(aspectDetails, "graph-aspect", "aspect-based aspects");
            }
        });
    });
    function drawDonut(dataPoint, div, title) {

        var divId = "";
        if (typeof div === 'undefined') {
            divId = divId + 'graph';
        }
        else divId = divId + div;

        Highcharts.chart(divId, {
            chart: {
                type: 'pie',
                options3d: {
                    enabled: true,
                    alpha: 45
                }
            },
            title: {
                text: title
            },
            subtitle: {
                text: '3D donut'
            },
            plotOptions: {
                pie: {
                    innerSize: 100,
                    depth: 45
                }
            },
            series: [{
                name: 'Tone',
                data: dataPoint
            }]
        });
    }
});
