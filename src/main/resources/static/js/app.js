function createChatBox(userId) {

    var chatBoxDiv = '<div id="chat-' + userId + '">  <h3>' + userId + ' :</h3> <ol id="messages"></ol>' + ' <div class="panel-footer"><div class="' + '"input-group">'
        + '<input id="+message_' + userId + '" ' + 'class="btn-input" ' + 'type="text" ' + 'class="form-control input-sm chat_input" ' +
        'placeholder="Write your message here..."/>' + ' </div></div></div>';
    console.log(chatBoxDiv);
    return chatBoxDiv;
}
function guid() {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    }

    return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
        s4() + '-' + s4() + s4() + s4();
}

$(document).ready(function () {
    var messageList = $("#container");
    var socket = new SockJS('/stomp');
    var stompClient = Stomp.over(socket);

    stompClient.connect('', function (frame) {

        console.log(socket._transport.url);
        var url = socket._transport.url.split("/");
        var uuid = frame.headers['user-name'];
        console.log("uuid " + uuid);
        var sessionId = uuid;


        stompClient.subscribe("/topic/chat.message" + "-" + sessionId, function (data) {
            var payload = JSON.parse(data.body);

            console.log("received message: " + payload);

            $('#container').on('customOnMessageEvent', "#chat-" + payload.sender, function (event, msg) {
                $("#chat-" + payload.sender)     // create `<div>` with `serialModel` as ID
                    .append("<li>" + msg + "</li>")

                event.stopImmediatePropagation();
            });
            $("#chat-" + payload.sender).trigger('customOnMessageEvent', [payload.message]);
        });

        stompClient.subscribe("/app/chat.participants", function (data) {
            var messageArray = JSON.parse(data.body);
            messageList.html('');
            //console.log(messageArray)
            for (var i = 0; i < messageArray.length; i++) {
                if (sessionId == messageArray[i].userName)
                    continue;
                messageList.append(createChatBox(messageArray[i].userName));
            }
        });

        stompClient.subscribe("/topic/chat.login", function (message) {
            messageList.append(createChatBox(JSON.parse(message.body).userName));
        });

        stompClient.subscribe("/topic/chat.logout", function (message) {
            var userName = JSON.parse(message.body).userName;
            $("#chat-" + userName).remove();
        });

        $('#container').on('keydown', '.btn-input', function (e) {
            if (e.which === 13) {
                var msg = $(this).val();
                var recipientId = $(this).attr('id').split("_")[1];
                console.log("sending message: " + msg)
                stompClient.send("/app/chat-message/message", {}, JSON.stringify({
                    'topic': "message", 'message': msg,
                    'recipient': recipientId, 'sender': sessionId
                }));
                $(this).val('');
            }
        });
    });
});
