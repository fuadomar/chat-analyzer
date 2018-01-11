function createChatList(user) {

    var chatListDiv = '<li class="contact" id="' + user.id + '"> <div class="wrap">';

    if (user.online === true) {
        chatListDiv = chatListDiv + '<span class="contact-status online"/>';
    }
    else {
        chatListDiv = chatListDiv + '<span class="contact-status"/>';
    }
    chatListDiv = chatListDiv + '<div class="meta"><p class="name" id="' + user.id + '">' + user.userName + '</p> <p class="preview">You just got LITT up, Mike.</p> </div> </div> </li>';
    //chatListDiv = ' <div class="user" id="user' + userId + '">' + userId + '</div>';
    console.log(chatListDiv);
    return chatListDiv;
}

function drawChart() {

    chart = new Highcharts.chart({

        chart: {
            renderTo: 'canvas'
        },

        title: {
            text: ''
        },

        xAxis: {
            categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
        },

        series: [{
            data: [29.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4]
        }],

        navigation: {
            buttonOptions: {
                align: 'center'
            }
        }
    });

    canvg(document.getElementById('canvas'), chart.getSVG())
    var canvas = document.getElementById("canvas");
    var img = canvas.toDataURL("image/png");
    $('#canvas').replaceWith('<img height="400" width="400" src="' + img + '"/>');
    //$('#graph').prepend($('<img>', {id: 'theImg', src: img}))
    //document.write('<img src="'+img+'"/>');
}


function drawDonut(dataPoint, div, title) {

    var divId = "";
    if (typeof div === 'undefined') {
        divId = divId + 'graph';
    }
    else divId = divId + div;

    chart = new Highcharts.chart({
        chart: {
            type: 'pie',
            renderTo: 'canvas',
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

    canvg(document.getElementById('canvas'), chart.getSVG())
    var canvas = document.getElementById("canvas");
    var img = canvas.toDataURL("image/png");
    $('#canvas').replaceWith('<img height="400" width="400" src="' + img + '"/>');

    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });


    /* $.ajax({
     type: 'POST',
     url: 'http://data-uri-to-img-url.herokuapp.com/images.json',
     headers: {  'Access-Control-Allow-Origin': 'http://data-uri-to-img-url.herokuapp.com' },
     data: image,
     cache: false,
     success: function (data, textStatus, xhr) {
     console.log(data);
     $('#generate-image-tone-analysis').attr({
     type: 'hidden',
     id: 'generate-hidden-uri',
     name: 'generate-hidden-uri'
     });
     },
     error: function (data, textStatus, xhr) {
     }
     });*/

    $.post({
        type: 'post',
        url: '/upload/images',
        data: 'image=' + img,
        success: function (data) {
            console.log(data);
            alert(data);
            var urlJson = JSON.parse(data);
            $('#generate-image-tone-analysis').val(urlJson.url);
        }
    });

}
function normalizedDataToneAnalyzer(data) {
    var series = [];
    for (var key in data) {
        if (data.hasOwnProperty(key)) {
            if (data[key] < 0.2)
                continue
            console.log(key + " -> " + data[key]);
            var arr = [];
            var number = data[key];
            arr.push(key);
            arr.push(parseFloat(number.toFixed(2)));
            series.push(arr)
        }
    }
    return series;
}


function createChatBox(userId) {

    var chatBox = '<div class="msg_box" style="right:290px"  id="msgbox' + userId + '"><div class="msg_head">' + userId +
        ' <div class="close">x</div></div><div class="msg_wrap"><div class="msg_body" id="msg_body' + userId + '">' +
        '<div class="msg_push" id="msg_push' + userId + '"></div>' +
        '</div> <div class="msg_footer">' +
        '<textarea class="msg_input" rows="4" id="msg' + userId + '"></textarea></div> </div></div>';
    return chatBox;
}


$(document).ready(function () {

    var chatList = $("#contacts-uli");
    var chatBox = $("#chatbox-container");
    var host = location.protocol + '//' + location.host;
    console.log("host: " + host);

    var socket = new SockJS(host + "/stomp");
    var stompClient = Stomp.over(socket);

    stompClient.connect('', function (frame) {

        console.log(socket._transport.url);
        var url = socket._transport.url.split("/");
        var uuid = frame.headers['user-name'];
        console.log(frame);
        console.log("uuid " + uuid);
        var sessionId = uuid;

        stompClient.subscribe("/topic/chat.message" + "-" + sessionId, function (data) {

            var payload = JSON.parse(data.body);
            receiveMessage(payload.message, payload.sender)
            console.log("received message: " + payload);

        });

        stompClient.subscribe("/app/chat.participants/" + uuid, function (data) {
            var messageArray = JSON.parse(data.body);
            //chatList.html('');
            for (var i = 0; i < messageArray.length; i++) {
                if (sessionId === messageArray[i].userName)
                    continue;
                chatList.append(createChatList(messageArray[i]));
            }
        });

        stompClient.subscribe("/topic/chat.login" + "-" + sessionId, function (message) {
            var spanDiv = '#' + JSON.parse(message.body).id + " " + 'span'
            if ($('#selector').length) {
                $(spanDiv).attr('class', 'contact-status online');
            }
            else {
                chatList.append(createChatList(JSON.parse(message.body)));
            }
        });

        stompClient.subscribe("/topic/chat.logout" + "-" + sessionId, function (message) {
            console.log(JSON.parse(message.body));
            var spanDiv = '#' + JSON.parse(message.body).id + " " + 'span'
            $(spanDiv).attr('class', 'contact-status');
        });

        $("#conversation-end-ok").click(function () {

            /*  $("#modal-end-conversation").modal('hide');
             $("#tone-analyzer-charts").modal('show');*/

            var sender = $('.message-input').attr('id');
            $.get({
                type: 'get',
                url: '/tone-analyzer-between-users',
                dataType: "json",
                data: 'sender=' + sender + "&recipient=" + uuid,
                success: function (data) {
                    $("#modal-end-conversation").modal('hide');

                    console.log(data);
                    var series = normalizedDataToneAnalyzer(data);
                    console.log(series);
                    $("#tone-analyzer-charts").modal('show');
                    drawDonut(series, "graph", "tone analyzer for people")
                }
            });

            //drawChart();
        });


        $("#send-invitation-modal").click(function () {
            var email = $("#inputEmail3").val();
            var name = $("#text3").val();
            console.log(email + " " + name);
            $.get({
                type: 'get',
                url: '/invitation-email',
                dataType: "text",
                data: 'sender=' + name + "&email=" + email,
                success: function (data) {
                    $('#myModalHorizontal').modal('hide');
                }
            });
        });


        $('#share_button').click(function (e) {
            e.preventDefault();
            $("#tone-analyzer-charts").modal('hide');
            var hidUrl = $("#generate-image-tone-analysis").val();
            console.log("hidden url: " + hidUrl);
            FB.ui(
                {
                    method: 'feed',
                    name: 'This is the tone analyzer',
                    link: 'http://localhost:8080',
                    picture: hidUrl,
                    caption: 'your friends tone is: ',
                    description: "learn more about machine learning with tone analyzer",
                    message: "Hello machine learning"
                });
        });


        $("#myModal").on("show.bs.modal", function (e) {
            var link = $(e.relatedTarget);
            alert(link);
            $(this).find(".modal-body").load(link.attr("href"));
        });


        $('.chat_box').on('click', '.chat_head', function (e) {
            e.stopPropagation();
            $(this).siblings('.chat_body').slideToggle('slow');
        });

        $('#chatbox-container').on('click', '.msg_head', function (e) {
            e.stopPropagation();
            $(this).siblings('.msg_wrap').slideToggle('slow');
        });

        $('#chatbox-container').on('click', '.close', function (e) {
            var divId = $(this).parent().parent().attr('id');
            e.stopPropagation();
            $('#' + divId).hide();
        });

        $('.chat_body').on('click', '.user', function (e) {
            var divId = $(this).attr('id').substring(4);
            e.stopPropagation();
            if ($("#msgbox" + divId).length <= 0) {
                chatBox.append(createChatBox(divId));
            }
            else {
                $('#msgbox' + divId).show();
            }
        });

        $("#contacts-uli").on("click", "li", function (event) {
            // do your code
            console.log('clicked ' + $(this).attr('id'));
            $('#ul-messages').html('');
            $('.message-input').attr('id', $(this).attr('id'));
            var sender = uuid;
            var receiver = $(this).attr('id');

            $.get({
                type: 'get',
                url: '/fetch/messages',
                dataType: 'json',
                data: 'sender=' + sender + "&receiver=" + receiver,
                success: function (data) {

                    if (!$.trim(data)) {
                    }
                    else {
                        jQuery(data).each(function (i, item) {
                            console.log(item.sender)

                            if (item.sender === uuid) {

                                $('<li class="sent"><p>' + item.content + '</p></li>').appendTo($('.messages ul'));
                                $('.message-input input').val(null);
                                $('.contact.online .preview').html('<span>You: </span>' + item.content);
                                $(".messages").animate({scrollTop: $(document).height()}, "fast");
                            }
                            else {
                                receiveMessage(item.content, item.sender)
                            }
                        })
                    }
                }
            });
        });

        $('#chatbox-container').on('keypress', 'textarea',
            function (e) {
                if (e.keyCode == 13) {
                    e.preventDefault();
                    var msg = $(this).val();
                    var recipientId = $(this).attr('id').substring(3);
                    $(this).val('');
                    stompClient.send("/app/chat-message/message", {}, JSON.stringify({
                        'topic': "message", 'message': msg,
                        'recipient': recipientId, 'sender': sessionId
                    }));

                    if (msg != '')
                        $('<div class="msg_b">' + sessionId + ": " + msg + '</div>').insertBefore('#msg_push' + recipientId);
                    $('#msg_body' + recipientId).scrollTop($('#msg_body' + recipientId)[0].scrollHeight);
                }
            });


        function receiveMessage(message, sender) {

            if ($.trim(message) == '') {
                return false;
            }

            $('<li class="replies"><p>' + message + '</p></li>').appendTo($('.messages ul'));
            $('.message-input input').val(null);
            $('.contact.online .preview').html('<span>You: </span>' + message);
            $(".messages").animate({scrollTop: $(document).height()}, "fast");
        };

        function newMessage() {
            var message = $(".message-input input").val();
            var recipientId = $('.message-input').attr('id');
            console.log("id from newMessage: " + recipientId);
            if ($.trim(message) == '') {
                return false;
            }
            stompClient.send("/app/chat-message/message", {}, JSON.stringify({
                'topic': "message", 'message': message,
                'recipient': recipientId, 'sender': sessionId
            }));
            $('<li class="sent"><p>' + message + '</p></li>').appendTo($('.messages ul'));
            $('.message-input input').val(null);
            $('.contact.online .preview').html('<span>You: </span>' + message);
            $(".messages").animate({scrollTop: $(document).height()}, "fast");
        };

        $('.submit').click(function () {
            newMessage();
        });

        $(window).on('keydown', function (e) {
            if (e.which == 13) {
                newMessage();
                return false;
            }
        });


    });

})
;