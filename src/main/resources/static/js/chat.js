function createBuddyList(user, action) {

    var chatListDiv = '<li class="contact" id="mgs-li-' + user.id
        + '">';

    if (typeof user.profileImage === 'undefined' || user.profileImage
        === null || user.profileImage === "") {
        if (action !== "notification") {
            chatListDiv = chatListDiv
                + '<div class="wrap"><img src="/images/default-avatar.png"  alt="" height="40" width="40"/>';
        }
    }
    else {
        if (action !== "notification") {
            chatListDiv = chatListDiv + '<div class="wrap"><img src="'
                + '/profiles/images/'
                + user.profileImage + '" alt="" height="40" width="40"/>';
        }
    }

    if (user.online === true) {
        if (action !== "notification") {
            chatListDiv = chatListDiv + '<span class="contact-status online"/>';
        }
    }
    else {
        if (action !== "notification") {
            chatListDiv = chatListDiv + '<span class="contact-status"/>';
        }
    }
    chatListDiv = chatListDiv + '<div class="meta"><p class="name" id="' + user.id
        + '">' + user.userName
        + '</p> <p class="preview"></p> </div> </div> </li>';
    //chatListDiv = ' <div class="user" id="user' + userId + '">' + userId + '</div>';
    console.log(chatListDiv);
    return chatListDiv;
}

function findTimeDiffFromCurrentDate(date) {

    var timeDiff = "";
    var lastMessageSendingDate = new Date(date);
    var currentDate = new Date();
    var delta = Math.abs(currentDate.getTime() - lastMessageSendingDate.getTime())
        / 1000;
    var days = Math.floor(delta / 86400);
    delta -= days * 86400;

    var hours = Math.floor(delta / 3600) % 24;
    delta -= hours * 3600;
    var minutes = Math.floor(delta / 60) % 60;
    delta -= minutes * 60;
    var seconds = delta;
    if (days >= 1) {
        timeDiff = Math.ceil(days) + " days ago";
    } else if (hours >= 1) {
        timeDiff = Math.ceil(hours) + " hours ago";
    } else if (minutes >= 1) {
        timeDiff = Math.ceil(minutes) + " minutes ago";
    } else if (seconds >= 1) {
        timeDiff = Math.ceil(seconds) + " seconds ago";
    }
    else {
        timeDiff = " a moment ago";
    }
    return timeDiff;
}

function createNotificationList(user) {

    var timeDiff = findTimeDiffFromCurrentDate(user.time);
    var chatListDiv = '<li class="notification-item" id="mgs-li-'
        + user.id
        + '"><div class="img-left"><img class="notification-avatar" height="50" width="50" src="/images/default-avatar.png"/></div>';
    chatListDiv = chatListDiv
        + '<div class="user-content"> <p class="user-info"><span class="name">'
        + user.userName + '</span> left a comment.</p>';
    chatListDiv = chatListDiv + '<p class="time" id="id-time">' + timeDiff
        + '</p></div> </li>';
    return chatListDiv;
}

function paintDonut(dataPoint, title) {

    var counter = 0;
    $('#canvas').remove();
    var newCanvas = $('<canvas/>', {
        id: 'canvas'
    });
    $('#render-div').empty();
    $('#render-div').append(newCanvas);

    var chart = new Highcharts.chart({
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
            text: ''
        },
        plotOptions: {
            pie: {
                innerSize: 100,
                depth: 45
            }
        },
        credits: {
            enabled: false
        },
        series: [{
            name: 'Tone',
            dataLabels: {
                style: {
                    fontSize: '18px'
                },
                formatter: function () {
                    return '<span style="fill: ' + this.color + ';">' + this.point.name
                        + '<br/>' + (this.y) + '%</span>';
                }
            },
            data: dataPoint
        }]
    });

    canvg(document.getElementById('canvas'), chart.getSVG());
    var canvas = document.getElementById("canvas");
    var img = canvas.toDataURL("image/png");
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    $.post({
        type: 'post',
        url: '/tone_analyzer/upload/images',
        data: 'image=' + encodeURIComponent(img),
        success: function (data) {
            console.log(data);
            $("#tone-analyzer-charts").modal('show');
            $('#generate-image-tone-analysis').val(data);
        },
        error: function (jqXHR, textStatus, errorThrown) {
        }
    });

}

function normalizedDataToneAnalyzer(data) {
    var series = [];
    for (var key in data) {
        if (data.hasOwnProperty(key)) {
            if (data[key] < 0.2) {
                continue
            }
            var arr = [];
            var number = data[key] * 100;
            arr.push(key);
            arr.push(parseFloat(number.toFixed(2)));
            console.log(key + " -> " + number.toFixed(2));
            series.push(arr)
        }
    }
    return series;
}

function createChatBox(userId) {

    var chatBox = '<div class="msg_box" style="right:290px"  id="msgbox' + userId
        + '"><div class="msg_head">' + userId +
        ' <div class="close">x</div></div><div class="msg_wrap"><div class="msg_body" id="msg_body'
        + userId + '">' +
        '<div class="msg_push" id="msg_push' + userId + '"></div>' +
        '</div> <div class="msg_footer">' +
        '<textarea class="msg_input" rows="4" id="msg' + userId
        + '"></textarea></div> </div></div>';
    return chatBox;
}

var singleton = {};
$(document).ready(function () {

    var chatList = $("#contacts-uli");
    var chatBox = $("#chatbox-container");
    var host = location.protocol + '//' + location.host;
    console.log("host: " + host);
    var socket = null;
    var stompClient = null;
    singleton.receiverId = "";
    var receiverId = singleton.receiverId;

    function establishConnection() {

        socket = new SockJS("/stomp");
        stompClient = Stomp.over(socket);
        stompClient.connect('', function (frame) {

            $("#failed-msg-connection-error").hide();

            console.log(socket._transport.url);
            var url = socket._transport.url.split("/");
            var userName = frame.headers['user-name'];
            console.log(frame);
            console.log("userName " + userName);
            var sessionName = userName;
            var profileImageBuddy;
            var profileImageLoggedInUser;
            var userClickedOnWhcihBuddyMessageBox = $("#user-is-on-messagebox").val();

            /* if (typeof receiverId !== 'undefined' && receiverId
                 !== null && receiverId !== "") {
               $('.message-input').attr('id', receiverId);
             }
       */
            // subscribe to received message
            stompClient.subscribe("/user/queue/receive-message",
                function (data) {
                    var payload = JSON.parse(data.body);
                    console.log("pay load: " + payload);
                    if (typeof userClickedOnWhcihBuddyMessageBox !== 'undefined'
                        || userClickedOnWhcihBuddyMessageBox
                        !== null || userClickedOnWhcihBuddyMessageBox !== "") {
                        if (userClickedOnWhcihBuddyMessageBox) {
                            if (userClickedOnWhcihBuddyMessageBox == payload.sender) {
                                receiveMessage(escapeHtml(payload.message), profileImageBuddy);
                                $('#ul-messages').animate({
                                        scrollTop: $("#ul-messages li").last().offset().top
                                    },
                                    'slow');
                            }
                        }
                    }
                    console.log("received message: " + payload);
                });

            var dates = []
            //subscribe to unseen message on connect
            stompClient.subscribe("/app/unseen.messages",
                function (data) {
                    var payload = JSON.parse(data.body);
                    if (payload !== null && payload.sender !== null) {

                        $("#notify-counter").text(payload.sender.length);
                        for (var i = 0; i < payload.sender.length; i++) {
                            dates.push(payload.sender[i].time);
                            $(".notification-list").append(
                                createNotificationList(payload.sender[i]));
                        }
                    }
                });

            // hide notification panel if user clicks outside div
            $(document.body).not(".notification-design").click(function () {
                if ($('#notification-div').is(':visible')) {
                    $('#notification-div').hide();
                }
                $("#id-notification-list").html("");
            });

            //subscribe to real time unseen message while chatting online
            // create notification list if users are busy chatting with someone else in a different window
            stompClient.subscribe("/user/queue/unseen-message",
                function (data) {
                    var payload = JSON.parse(data.body);
                    dates = [];
                    $("#id-notification-list").html("");
                    if (payload !== null && payload.sender !== null) {
                        var isFirstIteration = true;
                        for (var i = 0; i < payload.sender.length; i++) {
                            if ((typeof userClickedOnWhcihBuddyMessageBox !== 'undefined'
                                    || userClickedOnWhcihBuddyMessageBox
                                    !== null || userClickedOnWhcihBuddyMessageBox !== ""
                                ) && userClickedOnWhcihBuddyMessageBox
                                == payload.sender[i].userName) {

                                stompClient.send("/app/dispose.ack.message.notification", {},
                                    JSON.stringify(payload.sender[i]));
                                continue;
                            }
                            if (isFirstIteration) {
                                $("#notify-counter").html(payload.sender.length);
                                isFirstIteration = false;
                            }
                            dates.push(payload.sender[i].time);
                            $(".notification-list").append(
                                createNotificationList(payload.sender[i]));
                        }
                    }
                    console.log("pay load: " + payload);
                });

            // subscribe to buddy list on successful connection
            stompClient.subscribe("/app/chat.participants",
                function (data) {

                    var messageArray = JSON.parse(data.body);
                    var queryString = window.location.search.replace("?", '');

                    console.log("query string: " + qStringArray);

                    var sender = null;
                    if (queryString.indexOf("=") != -1) {
                        var qStringArray = queryString.split("=");

                        if (qStringArray.length == 2) {
                            sender = decodeURIComponent(qStringArray[1]);
                        }
                    }
                    console.log("sender: " + sender);
                    for (var i = 0; i < messageArray.length; i++) {

                        console.log("user list: " + messageArray[i].userName)

                        if (sessionName === messageArray[i].userName) {
                            $("#cur-user-uuid").val(messageArray[i].id);
                            console.log("user own uuid: " + messageArray[i].id);
                            continue;
                        } else if (sender == messageArray[i].userName) {
                            sender = messageArray[i].id;
                        }
                        chatList.append(createBuddyList(messageArray[i],
                            "message"));
                    }
                    if ($.trim(sender)) {
                        $("#mgs-li-" + sender).click();
                        var uri = window.location.href.toString();
                        if (uri.indexOf("?") > 0) {
                            var clean_uri = uri.substring(0, uri.indexOf("?"));
                            window.history.replaceState({}, document.title, clean_uri);
                        }
                    }
                });

            // get back to the last user on successful  websocket connection
            //if connection interrupted while chatting
            if (typeof singleton.receiverId !== 'undefined'
                && singleton.receiverId
                !== null && singleton.receiverId !== "") {
                $("#contacts-uli" + " #mgs-li-" + singleton.receiverId).trigger(
                    'click');
            }

            // subscribe to buddy login event
            stompClient.subscribe("/user/queue/chat.login",
                function (message) {

                    var spanDiv = '#' + "mgs-li-" + JSON.parse(message.body).id + " "
                        + 'span'
                    if ($(spanDiv).length) {
                        $(spanDiv).attr('class', 'contact-status online');
                    } else {
                        chatList.append(
                            createBuddyList(JSON.parse(message.body), "login"));
                    }
                });

            //subscribe to buddy log out event
            stompClient.subscribe("/user/queue/chat.logout",
                function (message) {
                    console.log(JSON.parse(message.body));
                    var spanDiv = '#' + "mgs-li-" + JSON.parse(message.body).id + " "
                        + 'span'
                    $(spanDiv).attr('class', 'contact-status');
                });

            $(document).ajaxStop(function () {
                $("#ajax_loader").hide();
            });
            $(document).ajaxStart(function () {
                $("#ajax_loader").show();
            });

            // function hide notification panel
            function hideNotificationPanel() {

                $("#notify-counter").text("");
                $('#notification-div').fadeToggle('slow', 'linear', function () {
                    if ($('#notification-div').is(':hidden')) {
                    }
                });
            }

            //dispose all messages if user clicks the notification panel
            $('.notification-design').click(function (e) {

                $(".notification-list li").each(function (index) {
                    var value = findTimeDiffFromCurrentDate(dates[index]);
                    $("#" + $(this).attr('id') + " .user-content #id-time").text(value);
                });
                dates = [];
                hideNotificationPanel();
                stompClient.send("/app/dispose.all.queued.message.notification", {},
                    '');
                e.stopPropagation();
            });

            // analyze chat tone using watson api if user clicks the conversation end button
            $("#conversation-end-ok").click(function (e) {

                var sender = $('.message-input').attr('id');
                $("#modal-end-conversation").modal('hide');
                jQuery.event.trigger("ajaxStart");

                $.get({
                    type: 'get',
                    url: '/chat-analyzer-between-users',
                    dataType: "json",
                    data: 'sender=' + sender + "&recipient=" + userName,
                    success: function (data) {
                        console.log(data);
                        var series = normalizedDataToneAnalyzer(data);
                        console.log(series);
                        paintDonut(series, "Your friend's mood is");
                        jQuery.event.trigger("ajaxStop");
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        jQuery.event.trigger("ajaxStop");
                        console.log(textStatus, errorThrown);
                    }
                });
                e.stopPropagation();
            });

            // hide model if user wants to share tone chart
            $("#conversation-share-ok").click(function (e) {
                $("#tone-analyzer-charts").modal('hide');
                e.stopPropagation();
            });

            // send invitaion to friends and family
            $("#send-invitation-modal").click(function (e) {

                var email = $("#exampleInputEmail1").val();
                var invitedText = $("#email-invitation-text-area").text();
                var invitedUser = escapeHtml($("#name").val());
                console.log("cur usr id " + $("#cur-user-uuid").val());
                $.get({
                    type: 'get',
                    url: '/emailInvitation',
                    dataType: "text",
                    data: "email=" + escapeHtml(email) + "&invitedText=" + invitedText
                    + "&invitedUser="
                    + escapeHtml(invitedUser),
                    success: function (data) {

                        $('#userInvitationTextAreaModal').modal('hide');
                        $("#success-msg-mail-send").show();

                        var fade_out = function () {
                            $("#success-msg-mail-send").fadeOut().empty();
                        }
                        setTimeout(fade_out, 3000);
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        console.log(textStatus, errorThrown);
                    }
                });
                e.stopPropagation();
            });

            // popup responsible for email template
            $("#userInvitationTextAreaModal").on("show.bs.modal", function (e) {
                $("#userInvitationMainModal").modal('hide');
                var email = $("#exampleInputEmail1").text();
                var name = $("#name").val();
                var emailTemplate = "Dear " + name + ", \n\n"
                    + " I have found a great chatting tool where they will show us some really cool insights \n\nbased on our conversation. I think, it will be a lot of fun!";
                $("#send-invitation-modal").show();
                $("#email-invitation-text-area").text("");
                var text = $("textarea#email-invitation-text-area");
                text.text(emailTemplate);
            });

            $("body").mouseup(function (e) {
                var subject = $("#notifications");

                if (e.target.id != subject.attr('id')) {
                    subject.fadeOut();
                }
            });

            // create anonymous chat link
            $("#generate-chat-link").click(function () {
                $("#userInvitationTextAreaModal").modal('show');
                $.get({
                    type: 'get',
                    url: '/anonymousChatUri',
                    dataType: "text",
                    success: function (data) {
                        $("#send-invitation-modal").hide();
                        $("#email-invitation-text-area").text(data);
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        console.log(textStatus, errorThrown);
                    }
                });
            });

            //show new messages from friends if users click their names in the notification panel
            $("#id-notification-list").on("click", "li", function (event) {
                hideNotificationPanel();
                $("#contacts-uli" + " #" + $(this).attr('id')).trigger('click');
                $("#id-notification-list").html("");
            });

            // show message list from a friend if user clicks his/her name in the buddy list panel
            $("#contacts-uli").on("click", "li", function (event) {
                //hideNotificationPanel();
                console.log('clicked ' + $(this).attr('id'));
                $('#ul-messages').html('');
                $('#message-input-div').show();
                receiverId = $(this).attr('id').replace("mgs-li-", "")
                profileImageLoggedInUser = $('.wrap img').attr('src');
                profileImageBuddy = $("#" + $(this).attr('id') + " .wrap img").attr(
                    'src');
                var currentUserToChat = '<img src="' + profileImageBuddy
                    + '" alt="" alt="" height="40" width="40"/>';
                currentUserToChat = currentUserToChat + '<p>' + $(".meta " + "#"
                    + receiverId).html() + '</p>';
                $("#contact-profile").html(currentUserToChat);
                $('.message-input').show();
                $('.message-input').attr('id', receiverId);
                var receiver = receiverId;
                singleton.receiverId = receiverId;

                userClickedOnWhcihBuddyMessageBox = $(
                    "#" + $(this).attr('id') + " .wrap .meta .name").text();
                $(this).css({'background': '#c9ccd0'});
                $("#contacts-uli li").each(function () {
                    if (receiverId !== $(this).attr('id').replace("mgs-li-", "")) {
                        $(this).css({'background': 'transparent'});
                    }
                });

                $.get({
                    type: 'get',
                    url: '/fetch/messages',
                    dataType: 'json',
                    data: "receiver=" + receiver,
                    success: function (data) {
                        if (!$.trim(data)) {
                        }
                        else {
                            jQuery(data).each(function (i, item) {
                                console.log(item.sender)

                                if (item.sender === userName) {

                                    $('<li class="sent"><p>' + escapeHtml(item.content)
                                        + '</p></li>').appendTo(
                                        $('.messages ul'));
                                    $('.message-input input').val(null);
                                    $('.contact.online .preview').html(
                                        '<span>You: </span>' + escapeHtml(item.content));

                                }
                                else {
                                    receiveMessage(escapeHtml(item.content), profileImageBuddy)
                                }
                            });
                            $('#ul-messages').animate({
                                    scrollTop: $("#ul-messages li").last().offset().top + 50
                                },
                                'fast');
                        }
                    }
                });
            });

            $("#uploadModal").on('shown.bs.modal', function (e) {
                $("#preview").html("");
            })


            $('#upload').click(function () {

                var token = $("meta[name='_csrf']").attr("content");
                var header = $("meta[name='_csrf_header']").attr("content");
                $(document).ajaxSend(function (e, xhr, options) {
                    xhr.setRequestHeader(header, token);
                });

                var fd = new FormData();
                var files = $('#file')[0].files[0];
                fd.append('file', files);
                console.log(files);

                $.ajax({
                    url: '/upload/profile/images',
                    type: 'post',
                    data: fd,
                    contentType: false,
                    processData: false,
                    cache: false,
                    method: 'POST',
                    headers: {
                        'X-CSRF-TOKEN': $('meta[name="_csrf"]').attr('content')
                    },
                    success: function (response) {

                        var image = "data:image/png;base64, " + response
                        $("#preview").html("");
                        $('#preview').append(
                            "<p> Image has been uploaded successfully! </p><img src='"
                            + image
                            + "' width='100' height='100' style='display: inline-block;'>");
                        setTimeout(function () {
                            $('#uploadModal').modal('hide');
                            location.reload();
                        }, 2000);
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        console.log(textStatus, errorThrown);
                    }
                });
            });

            //receive message function
            function receiveMessage(message, profileImageBuddy) {

                if ($.trim(message) == '') {
                    return false;
                }

                if (typeof profileImageBuddy === 'undefined' || profileImageBuddy
                    === null) {
                    $('<li class="replies"><img src="/images/default-avatar.png" alt="" /> <p>'
                        + message + '</p></li>').appendTo(
                        $('.messages ul'));
                }
                else {
                    $('<li class="replies"><img src="' + profileImageBuddy
                        + '" alt="" /> <p>' + message + '</p></li>').appendTo(
                        $('.messages ul'));
                }
                $('.message-input input').val(null);
                $('.contact.online .preview').html('<span>You: </span>' + message);
            };

            // new message function sending message via websocket
            function newMessage() {

                var message = $("#message-box").val();
                var recipientId = $('.message-input').attr('id');
                console.log("id from newMessage: " + recipientId);
                if ($.trim(message) == '') {
                    return false;
                }
                $("#message-box").val("");
                stompClient.send("/app/send.message", {}, JSON.stringify({
                    'topic': "message", 'message': message,
                    'recipient': recipientId
                }));
                $('<li class="sent"> <p>' + escapeHtml(message) + '</p></li>').appendTo(
                    $('.messages ul'));
                $('.message-input input').val(null);
                $('.contact.online .preview').html('<span>You: </span>' + escapeHtml(
                    message));
                $('#messages').animate({
                        scrollTop: $("#ul-messages li").last().offset().top
                    },
                    'fast');
            };

            var entityMap = {
                '&': '&amp;',
                '<': '&lt;',
                '>': '&gt;',
                '"': '&quot;',
                "'": '&#39;',
                '/': '&#x2F;',
                '`': '&#x60;',
                '=': '&#x3D;'
            };

            function escapeHtml(string) {
                return String(string).replace(/[&<>"'`=\/]/g, function (s) {
                    return entityMap[s];
                });
            }

            $('#btn-chat').click(function () {
                newMessage();
            });

            $("textarea").on('keydown', function (e) {
                if (e.which == 13) {
                    newMessage();
                    return false;
                }
            });
        }, stompFailure);
    }

    function stompFailure(error) {

        $("#failed-msg-connection-error").show();
        $('.message-input').hide();
        setTimeout(establishConnection, 10000);
    }

    establishConnection();
});