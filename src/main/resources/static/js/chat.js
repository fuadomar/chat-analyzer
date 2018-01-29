function createChatList(user, host) {

  var chatListDiv = '<li class="contact" id="mgs-li-' + user.id
      + '">';

  if (typeof user.profileImage === 'undefined' || user.profileImage
      === null || user.profileImage === "") {
    chatListDiv = chatListDiv
        + '<div class="wrap"><img src="/images/default-avatar.png"  alt="" height="40" width="40"/>';
    //alert(user.profileImage + " " + user.userName);

  }
  else {
    chatListDiv = chatListDiv + '<div class="wrap"><img src="'
        + '/profiles/images/'
        + user.profileImage + '" alt="" height="40" width="40"/>';
  }

  if (user.online === true) {
    chatListDiv = chatListDiv + '<span class="contact-status online"/>';
  }
  else {
    chatListDiv = chatListDiv + '<span class="contact-status"/>';
  }
  chatListDiv = chatListDiv + '<div class="meta"><p class="name" id="' + user.id
      + '">' + user.userName
      + '</p> <p class="preview">You just got LITT up</p> </div> </div> </li>';
  //chatListDiv = ' <div class="user" id="user' + userId + '">' + userId + '</div>';
  console.log(chatListDiv);
  return chatListDiv;
}

function clearGraphDdiv() {
  //$("#canvas").empty();
  //var canvas = $('#canvas').remove(); // or document.getElementById('canvas');
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
  var divId = "";
  if (typeof div === 'undefined') {
    divId = divId + 'graph';
  }
  else {
    divId = divId + div;
  }

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
      text: '3D donut'
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

  canvg(document.getElementById('canvas'), chart.getSVG())
  var canvas = document.getElementById("canvas");
  var img = canvas.toDataURL("image/png");
  $('#canvas').replaceWith('<img height="500" width="500" src="' + img + '"/>');

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
      jQuery.event.trigger("ajaxStop");
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

  var socket = new SockJS(host + "/stomp");
  var stompClient = Stomp.over(socket);

  stompClient.connect('', function (frame) {

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
          for (var i = 0; i < payload.sender.length; i++) {
            var sender = payload.sender[i];
            console.log(sender);
          }
          alert(payload.sender);
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
            chatList.append(createChatList(messageArray[i], host));
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
            chatList.append(createChatList(JSON.parse(message.body)));
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
          clearGraphDdiv();
          drawDonut(series, "graph", "tone analyzer for people");
          jQuery.event.trigger("ajaxStop");
        }
      });
    });

    $("#send-invitation-modal").click(function () {

      var email = $("#inputEmail").val();
      var name = $("#text").val();
      console.log("cur usr id " + $("#cur-user-uuid").val());

      $.get({
        type: 'get',
        url: '/invitation-email',
        dataType: "text",
        data: "email=" + email,
        success: function (data) {
          $('#myModalHorizontal').modal('hide');
          $("#success-msg-mail-send").show();

          var fade_out = function () {
            $("#success-msg-mail-send").fadeOut().empty();
          }
          setTimeout(fade_out, 3000);
        }
      });
    });

    $("#myModal").on("show.bs.modal", function (e) {
      var link = $(e.relatedTarget);
      $(this).find(".modal-body").load(link.attr("href"));
    });

    $("#contacts-uli").on("click", "li", function (event) {
      console.log('clicked ' + $(this).attr('id'));
      $('#ul-messages').html('');
      profileImageLoggedInUser = $('.wrap img').attr('src');
      profileImageBuddy = $("#" + $(this).attr('id') + " .wrap img").attr(
          'src');
      var receiverId = $(this).attr('id').replace("mgs-li-", "")
      $('.message-input').attr('id', receiverId);
      var receiver = receiverId;
      //$("#user-is-on-messagebox").val(re);
      userClickedOnWhcihBuddyMessageBox = $(
          "#" + $(this).attr('id') + " .wrap .meta .name").text();
      //$("#user-is-on-messagebox").val( $("#"+(this).attr('id') + " .wrap .meta .name").text());
      $(this).css({'background': 'gray'});
      $("#contacts-uli li").each(function () {
        if (receiverId !== $(this).attr('id').replace("mgs-li-", "")) {
          $(this).css({'background': '#2c3e50'});
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

                $('<li class="sent"><img src="' + profileImageLoggedInUser
                    + '" alt="" /> <p>' + item.content + '</p></li>').appendTo(
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
              "<p> Image has been uploaded successfully! </p><img src='" + image
              + "' width='100' height='100' style='display: inline-block;'>");
          setTimeout(function () {
            $('#uploadModal').modal('hide');
            location.reload();
          }, 2000);
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

      var message = $(".message-input input").val();
      var recipientId = $('.message-input').attr('id');
      console.log("id from newMessage: " + recipientId);
      if ($.trim(message) == '') {
        return false;
      }
      stompClient.send("/app/send.message", {}, JSON.stringify({
        'topic': "message", 'message': message,
        'recipient': recipientId
      }));
      /* if (typeof profileImageLoggedInUser === 'undefined'
       || profileImageLoggedInUser === null) {
       $('<li class="sent"><p>' + message + '</p></li>').appendTo(
       $('.messages ul'))
       }*/
      /* else {*/
      $('<li class="sent"> <img src="' + profileImageLoggedInUser
          + '" alt="" /> <p>' + message + '</p></li>').appendTo(
          $('.messages ul'));
      /*}*/
      $('.message-input input').val(null);
      $('.contact.online .preview').html('<span>You: </span>' + message);
      $(".messages").animate({scrollTop: $(document).height()}, "fast");
    };

    $('#btn-chat').click(function () {
      newMessage();
    });

    $(window).on('keydown', function (e) {
      if (e.which == 13) {
        newMessage();
        return false;
      }
    });
  });
});