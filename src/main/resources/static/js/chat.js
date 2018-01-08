function createChatList(user) {

    var chatListDiv = '<li class="contact" id="'+user.id+'"> <div class="wrap">';

    if (user.online === true) {
        chatListDiv = chatListDiv + '<span class="contact-status online"/>';
    }
    else {
        chatListDiv = chatListDiv + '<span class="contact-status"/>';
    }
    chatListDiv = chatListDiv + '<div class="meta"><p class="name" id="'+user.id+'">' + user.userName+ '</p> <p class="preview">You just got LITT up, Mike.</p> </div> </div> </li>';
    //chatListDiv = ' <div class="user" id="user' + userId + '">' + userId + '</div>';
    console.log(chatListDiv);
    return chatListDiv;
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

        /* stompClient.subscribe("/topic/chat.message" + "-" + sessionId, function (data) {

         var payload = JSON.parse(data.body);
         console.log("received message: " + payload);
         if ($("#msg_push" + payload.sender).length <= 0) {
         chatBox.append(createChatBox(payload.sender));
         $('.msg_wrap').show();
         $('#msgbox' + payload.sender).show();
         }
         $('#chatbox-container').on('customOnMessageEvent', "#msg_push" + payload.sender, function (event, msg) {

         msg = payload.sender + ": " + msg;
         if (msg != '')
         $('<div class="msg_a">' + msg + '</div>').insertBefore('#msg_push' + payload.sender);
         $('#msg_body' + payload.sender).scrollTop($('#msg_body' + payload.sender)[0].scrollHeight);
         event.stopImmediatePropagation();
         });
         $("#msg_push" + payload.sender).trigger('customOnMessageEvent', [payload.message]);
         });*/

        stompClient.subscribe("/app/chat.participants/" + uuid, function (data) {
            var messageArray = JSON.parse(data.body);
            //chatList.html('');
            for (var i = 0; i < messageArray.length; i++) {
                if (sessionId === messageArray[i].userName)
                    continue;
                chatList.append(createChatList(messageArray[i]));
            }
        });

        stompClient.subscribe("/topic/chat.login"+"-"+sessionId, function (message) {
            if (sessionId !== JSON.parse(message.body).userName)
                //chatList.append(createChatList(JSON.parse(message.body)));
                 var spanDiv = '#'+JSON.parse(message.body).id+" "+'span'
                 $(spanDiv).attr('class','contact-status online')
        });

        stompClient.subscribe("/topic/chat.logout", function (message) {
            var userName = JSON.parse(message.body).userName;
            $("#msgbox" + userName).remove();
            $("#user" + userName).remove();
        });

        /*$('#review-button').click(function () {
         var review = $('textarea#review').val();
         if (review != '') {

         var token = $("meta[name='_csrf']").attr("content");
         var header = $("meta[name='_csrf_header']").attr("content");
         $(document).ajaxSend(function (e, xhr, options) {
         xhr.setRequestHeader(header, token);
         });

         var jsonStr = '{ "user": "' + sessionId + '" , "content": "' + review + ' "}';
         $.ajax({
         type: 'POST',
         url: '/review',
         data: jsonStr,
         contentType: 'application/json; charset=utf-8',
         processData: false,
         cache: false,
         success: function (data, textStatus, xhr) {
         $('textarea#review').val('');
         },
         error: function (data, textStatus, xhr) {
         }
         });
         }
         });*/

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
    });

});