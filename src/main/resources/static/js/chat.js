function createChatList(user, host, action) {

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

function NotificationList(user, host, action) {

  var chatListDiv = '<li class="notification-item" id="mgs-li-'
      + user.id
      + '"><div class="img-left"><img class="notification-avatar" height="50" width="50" src="/images/default-avatar.png"/></div>';
  chatListDiv = chatListDiv +'<div class="user-content"> <p class="user-info"><span class="name">'+user.userName+'</span> left a comment.</p>';

  chatListDiv = chatListDiv + '<p class="time">1 hour ago</p></div> </li>';

  return chatListDiv;
}

function drawDonut(dataPoint, div, title) {

  var counter = 0;
  $('#canvas').remove();
  var newCanvas = $('<canvas/>', {
    id: 'canvas'
  }).prop({
    width: 500,
    height: 500
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
        formatter: function () {
          return '<b>' + this.point.name + '<br />' + (this.y ) + '%</b>: '
        }
      },
      data: dataPoint
    }]
  });

  canvg(document.getElementById('canvas'), chart.getSVG());
  var canvas = document.getElementById("canvas");
  var img = canvas.toDataURL("image/png");
  //$('#canvas').replaceWith('<img height="500" width="500" src="' + img + '"/>');
  $('#canvas').attr("style", "");
  $('#canvas').css({"height": "400px", "width": "500px"});
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
      //jQuery.event.trigger("ajaxStop");
    },
    error: function (jqXHR, textStatus, errorThrown) {
      console.log(textStatus, errorThrown);
      // jQuery.event.trigger("ajaxStop");
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

$(document).ready(function () {

  var chatList = $("#contacts-uli");
  var chatBox = $("#chatbox-container");
  var host = location.protocol + '//' + location.host;
  console.log("host: " + host);
  var socket = null;
  var stompClient = null;

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

      stompClient.subscribe("/user/queue/receive-message",
          function (data) {
            var payload = JSON.parse(data.body);
            console.log("pay load: " + payload);
            if (typeof userClickedOnWhcihBuddyMessageBox !== 'undefined'
                || userClickedOnWhcihBuddyMessageBox
                !== null || userClickedOnWhcihBuddyMessageBox !== "") {
              if (userClickedOnWhcihBuddyMessageBox) {
                if (userClickedOnWhcihBuddyMessageBox == payload.sender) {
                  receiveMessage(payload.message, payload.sender,
                      profileImageBuddy)
                }
              }
            }
            console.log("received message: " + payload);
          });

      stompClient.subscribe("/app/unseen.messages",
          function (data) {
            var payload = JSON.parse(data.body);
            if (payload !== null && payload.sender !== null) {

              $("#notify-counter").text(payload.sender.length);
              for (var i = 0; i < payload.sender.length; i++) {
                $("#notifications").append(
                    createChatList(payload.sender[i], host, "notification"));
              }
            }
            console.log("pay load: " + payload);
          });

      stompClient.subscribe("/user/queue/unseen-message",
          function (data) {
            var payload = JSON.parse(data.body);
            if (payload !== null && payload.sender !== null) {

              $(".notification-list").html("");
              var isFirstIteration = true;
              for (var i = 0; i < payload.sender.length; i++) {
                if ((typeof userClickedOnWhcihBuddyMessageBox !== 'undefined'
                        || userClickedOnWhcihBuddyMessageBox
                        !== null || userClickedOnWhcihBuddyMessageBox !== ""
                    ) && userClickedOnWhcihBuddyMessageBox
                    == payload.sender[i].userName) {

                  var token = $("meta[name='_csrf']").attr("content");
                  var header = $("meta[name='_csrf_header']").attr("content");
                  $(document).ajaxSend(function (e, xhr, options) {
                    xhr.setRequestHeader(header, token);
                  });

                  $.ajax({
                    type: "POST",
                    url: "/dispose_message_notification_by_user",
                    data: JSON.stringify(payload.sender[i]),
                    contentType: "application/json",
                    dataType: "json",
                    headers: {
                      'X-CSRF-TOKEN': $('meta[name="_csrf"]').attr('content')
                    },
                    success: function (data) {
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                      console.log(textStatus, errorThrown);
                    }
                  });

                  continue;
                }

                if (isFirstIteration) {

                  //$("#notify-counter").show();
                  $("#notify-counter").html(payload.sender.length);
                  isFirstIteration = false;
                }

                 $(".notification-list").append(
                     NotificationList(payload.sender[i], host, "notification"));
              }
            }
            console.log("pay load: " + payload);
          });

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
              chatList.append(createChatList(messageArray[i], host, "message"));
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

      stompClient.subscribe("/user/queue/chat.login",
          function (message) {

            var spanDiv = '#' + "mgs-li-" + JSON.parse(message.body).id + " "
                + 'span'
            if ($(spanDiv).length) {
              $(spanDiv).attr('class', 'contact-status online');
            } else {
              chatList.append(
                  createChatList(JSON.parse(message.body), "login"));
            }
          });

      stompClient.subscribe("/user/queue/chat.logout",
          function (message) {
            console.log(JSON.parse(message.body));
            var spanDiv = '#' + "mgs-li-" + JSON.parse(message.body).id + " "
                + 'span'
            $(spanDiv).attr('class', 'contact-status');
          });

      $(document).ajaxStop(function () {
        console.debug("ajaxStop");
        $("#ajax_loader").hide();
      });
      $(document).ajaxStart(function () {
        console.debug("ajaxStart");
        $("#ajax_loader").show();
      });

      function hideNotificationPanel() {


        $("#notify-counter").text("");
        // TOGGLE (SHOW OR HIDE) NOTIFICATION WINDOW.
        $('#notification-div').fadeToggle('slow', 'linear', function () {
          if ($('#notification-div').is(':hidden')) {
          }
        });
      }

      $('.notification-design').click(function () {
        hideNotificationPanel();
        $.get({
          type: 'get',
          url: '/dispose_all_message_notification',
          success: function (data) {
            jQuery.event.trigger("ajaxStop");
          },
          error: function (jqXHR, textStatus, errorThrown) {
            jQuery.event.trigger("ajaxStop");
            console.log(textStatus, errorThrown);
          }
        });
      });

      $("#conversation-end-ok").click(function () {

        var sender = $('.message-input').attr('id');
        $("#modal-end-conversation").modal('hide');
        jQuery.event.trigger("ajaxStart");

        $.get({
          type: 'get',
          url: '/tone-analyzer-between-users',
          dataType: "json",
          data: 'sender=' + sender + "&recipient=" + userName,
          success: function (data) {
            console.log(data);
            var series = normalizedDataToneAnalyzer(data);
            console.log(series);
            drawDonut(series, "graph", "Your friend's mood is");
            jQuery.event.trigger("ajaxStop");
          },
          error: function (jqXHR, textStatus, errorThrown) {
            jQuery.event.trigger("ajaxStop");
            console.log(textStatus, errorThrown);
          }
        });
      });

      $("#conversation-share-ok").click(function () {
        $("#tone-analyzer-charts").modal('hide');
      })
      $("#send-invitation-modal").click(function () {

        var email = $("#exampleInputEmail1").val();
        var invitedText = $("#email-invitation-text-area").val();
        var invitedUser = $("#name").val();
        console.log("cur usr id " + $("#cur-user-uuid").val());

        $.get({
          type: 'get',
          url: '/invitationEmail',
          dataType: "text",
          data: "email=" + email + "&invitedText=" + invitedText
          + "&invitedUser="
          + invitedUser,
          success: function (data) {
            $('#userInvitationMainModal').modal('hide');
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

      });

      $("#userInvitationTextAreaModal").on("show.bs.modal", function (e) {
        $("#userInvitationMainModal").modal('hide');
        var email = $("#exampleInputEmail1").val();
        var name = $("#name").val();
        var emailTemplate = "Dear " + name + ", \n\n"
            + " I have found a great chatting tool where they will show us some really cool insights \n\nbased on our conversation. I think, it will be a lot of fun!";
        $("#send-invitation-modal").show();
        $("#email-invitation-text-area").val("");
        var text = $("textarea#email-invitation-text-area")
        text.val(emailTemplate);
      });

      $("body").mouseup(function (e) {
        var subject = $("#notifications");

        if (e.target.id != subject.attr('id')) {
          subject.fadeOut();
        }
      });

      $("#generate-chat-link").click(function () {
        $("#userInvitationTextAreaModal").modal('show');
        $.get({
          type: 'get',
          url: '/anonymousChatLink',
          dataType: "text",
          success: function (data) {
            $("#send-invitation-modal").hide();
            $("#email-invitation-text-area").val(data);
          },
          error: function (jqXHR, textStatus, errorThrown) {
            console.log(textStatus, errorThrown);
          }
        });
      });

      $("#notifications").on("click", "li", function (event) {
        $("#contacts-uli" + " #" + $(this).attr('id')).trigger('click');
      });

      $("#contacts-uli").on("click", "li", function (event) {
        console.log('clicked ' + $(this).attr('id'));
        jQuery.event.trigger("ajaxStart");
        $('#ul-messages').html('');
        $('#message-input-div').show();
        var receiverId = $(this).attr('id').replace("mgs-li-", "")
        profileImageLoggedInUser = $('.wrap img').attr('src');
        profileImageBuddy = $("#" + $(this).attr('id') + " .wrap img").attr(
            'src');
        var currentUserToChat = '<img src="' + profileImageBuddy
            + '" alt="" alt="" height="40" width="40"/>';
        currentUserToChat = currentUserToChat + '<p>' + $(".meta " + "#"
            + receiverId).html() + '</p>';
        $("#contact-profile").html(currentUserToChat);
        $('.message-input').attr('id', receiverId);
        var receiver = receiverId;

        userClickedOnWhcihBuddyMessageBox = $(
            "#" + $(this).attr('id') + " .wrap .meta .name").text();
        $(this).css({'background': 'gray'});
        $("#contacts-uli li").each(function () {
          if (receiverId !== $(this).attr('id').replace("mgs-li-", "")) {
            $(this).css({'background': '#e8e8e8'});
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

                  $('<li class="sent"><p>' + item.content
                      + '</p></li>').appendTo(
                      $('.messages ul'));
                  $('.message-input input').val(null);
                  $('.contact.online .preview').html(
                      '<span>You: </span>' + item.content);
                  $(".messages").animate({scrollTop: $(document).height()},
                      "fast");
                }
                else {
                  receiveMessage(item.content, item.sender, profileImageBuddy)
                }
              })
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

      $('#chatbox-container').on('keypress', 'textarea',
          function (e) {
            if (e.keyCode == 13) {
              e.preventDefault();
              var msg = $(this).val();
              var recipientId = $(this).attr('id').substring(3);
              $(this).val('');
              stompClient.send("/app/send.message", {}, JSON.stringify({
                'topic': "message", 'message': msg,
                'recipient': recipientId
              }));

              if (msg != '') {
                $('<div class="msg_b">' + sessionName + ": " + msg
                    + '</div>').insertBefore('#msg_push' + recipientId);
              }
              $('#msg_body' + recipientId).scrollTop(
                  $('#msg_body' + recipientId)[0].scrollHeight);
            }
          });

      function receiveMessage(message, sender, profileImageBuddy) {

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
        $(".messages").animate({scrollTop: $(document).height()}, "fast");
      };

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
        $('<li class="sent"> <p>' + message + '</p></li>').appendTo(
            $('.messages ul'));
        $('.message-input input').val(null);
        $('.contact.online .preview').html('<span>You: </span>' + message);
        $(".messages").animate({scrollTop: $(document).height()}, "fast");
      };

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
    $("#contacts-uli").html("");
    $("#chatbox-container").html("");
    setTimeout(establishConnection, 10000);
  }

  establishConnection();

});