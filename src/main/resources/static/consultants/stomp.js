const stompClient = new StompJs.Client({
  brokerURL: 'ws://localhost:8080/stomp/chats'
});

stompClient.onConnect = (frame) => {
  setConnected(true);
  showChatrooms(0);
  stompClient.subscribe('/sub/chats/updates', (chatMessage) => {
    try {
      const messageData = JSON.parse(chatMessage.body);
      toggleNewMessageIcon(messageData.id, true);
      updateMemberCount(messageData);
    } catch (error) {
      console.error('Error parsing chat message:', error);
    }
  });
  console.log('Connected: ' + frame);
};

function toggleNewMessageIcon(chatroomId, toggle) {
  if (chatroomId === $("#chatroom-id").val()) {
    return;
  }

  const newMessageIcon = $("#new_" + chatroomId);
  if (toggle) {
    newMessageIcon.show();
  } else {
    newMessageIcon.hide();
  }
}

function updateMemberCount(chatroom) {
  const memberCountElement = $("#memberCount_" + chatroom.id);
  if (memberCountElement.length) {
    memberCountElement.html(chatroom.memberCount);
  }
}

stompClient.onWebSocketError = (error) => {
  console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
  console.error('Broker reported error: ' + frame.headers['message']);
  console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  $("#create").prop("disabled", !connected);
}

function connect() {
  stompClient.activate();
}

function disconnect() {
  stompClient.deactivate();
  setConnected(false);
  console.log("Disconnected");
}

function sendMessage() {
  const chatroomId = $("#chatroom-id").val();
  const message = $("#message").val();
  if (message.trim() === "") {
    alert("Message cannot be empty");
    return;
  }
  stompClient.publish({
    destination: "/pub/chats/" + chatroomId,
    body: JSON.stringify({'message': message})
  });
  $("#message").val("");
}

function createChatroom() {
  const title = $("#chatroom-title").val();
  if (title.trim() === "") {
    alert("Chatroom title cannot be empty");
    return;
  }
  $.ajax({
    type: 'POST',
    dataType: 'json',
    url: '/chats?title=' + encodeURIComponent(title),
    success: function (data) {
      console.log('data: ', data);
      showChatrooms(0);
      enterChatroom(data.id, true);
    },
    error: function (request, status, error) {
      console.error('Error creating chatroom:', error);
      alert('Failed to create chatroom. Please try again.');
    },
  });
}

function showChatrooms(pageNumber) {
  $.ajax({
    type: 'GET',
    dataType: 'json',
    url: '/consultants/chats?sort=id,desc&page=' + pageNumber,
    success: function (data) {
      console.log('data: ', data);
      renderChatrooms(data);
    },
    error: function (request, status, error) {
      console.error('Error fetching chatrooms:', error);
      alert('Failed to load chatrooms. Please try again.');
    },
  });
}

function renderChatrooms(page) {
  const chatrooms = page.content;
  $("#chatroom-list").html("");
  for (let i = 0; i < chatrooms.length; i++) {
    $("#chatroom-list").append(
        "<tr onclick='joinChatroom(" + chatrooms[i].id + ")'><td>"
        + chatrooms[i].id + "</td><td>" + chatrooms[i].title
        + "<img src='new.png' id='new_" + chatrooms[i].id + "' style='display: "
        + getDisplayValue(chatrooms[i].hasNewMessage)
        + "'/></td><td id='memberCount_" + chatrooms[i].id + "'>"
        + chatrooms[i].memberCount + "</td><td>" + chatrooms[i].createdAt
        + "</td></tr>"
    );
  }

  $("#prev").prop("disabled", page.first).off('click').click(() => {
    if (!page.first) {
      showChatrooms(page.number - 1);
    }
  });

  $("#next").prop("disabled", page.last).off('click').click(() => {
    if (!page.last) {
      showChatrooms(page.number + 1);
    }
  });
}

function getDisplayValue(hasNewMessage) {
  return hasNewMessage ? "inline" : "none";
}

let subscription;

function enterChatroom(chatroomId, newMember) {
  $("#chatroom-id").val(chatroomId);
  $("#messages").html("");
  showMessages(chatroomId); // showMessages 함수가 정의되어 있어야 합니다.
  $("#conversation").show();
  $("#send").prop("disabled", false);
  $("#leave").prop("disabled", false);
  toggleNewMessageIcon(chatroomId, false);

  if (subscription) {
    subscription.unsubscribe();
  }

  subscription = stompClient.subscribe('/sub/chats/' + chatroomId,
      (chatMessage) => {
        showMessage(JSON.parse(chatMessage.body));
      });

  if (newMember) {
    stompClient.publish({
      destination: "/pub/chats/" + chatroomId,
      body: JSON.stringify({'message': "님이 방에 들어왔습니다."})
    });
  }
}

function showMessages(chatRoomId) {
  $.ajax({
    type: 'GET',
    dataType: 'json',
    url: '/chats/' + chatRoomId + '/messages',
    success: function (data) {
      console.log('data: ', data);
      for (let i = 0; i < data.length; i++) {
        showMessage(data[i]); // 수정된 부분
      }
    },
    error: function (request, status, error) {
      console.error('Error fetching messages:', error);
      alert('Failed to load messages. Please try again.');
    },
  });
}

function showMessage(chatMessage) {
  $("#messages").append(
      "<tr><td>" + chatMessage.sender + " : " + chatMessage.message
      + "</td></tr>"
  );
}

function joinChatroom(chatroomId) {
  let currentChatroomId = $("#chatroom-id").val();

  $.ajax({
    type: 'POST',
    dataType: 'json',
    url: '/chats/' + chatroomId + getRequestParam(currentChatroomId),
    success: function (data) {
      console.log('data: ', data);
      enterChatroom(chatroomId, data);
    },
    error: function (request, status, error) {
      console.error('Error joining chatroom:', error);
      alert('Failed to join chatroom. Please try again.');
    },
  });
}

function getRequestParam(currentChatroomId) {
  return currentChatroomId ? "?currentChatroomId=" + currentChatroomId : "";
}

function leaveChatroom() {
  let chatroomId = $("#chatroom-id").val();
  $.ajax({
    type: 'DELETE',
    dataType: 'json',
    url: '/chats/' + chatroomId,
    success: function (data) {
      console.log('data: ', data);
      showChatrooms(0);
      exitChatroom(chatroomId);
    },
    error: function (request, status, error) {
      console.error('Error leaving chatroom:', error);
      alert('Failed to leave chatroom. Please try again.');
    },
  });
}

function exitChatroom(chatroomId) {
  $("#chatroom-id").val("");
  $("#conversation").hide();
  $("#send").prop("disabled", true);
  $("#leave").prop("disabled", true);
}

$(function () {
  $("form").on('submit', (e) => e.preventDefault());
  $("#connect").click(() => connect());
  $("#disconnect").click(() => disconnect());
  $("#create").click(() => createChatroom());
  $("#leave").click(() => leaveChatroom());
  $("#send").click(() => {
    const message = $("#message").val();
    if (message.trim() === "") {
      alert("Message cannot be empty");
      return;
    }
    sendMessage();
  });
});