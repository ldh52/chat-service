// STOMP 클라이언트 객체 생성, WebSocket 브로커 URL 설정
const stompClient = new StompJs.Client({
  brokerURL: 'ws://localhost:8080/stomp/chats'
});

// STOMP 연결 성공 시 호출되는 콜백 함수
stompClient.onConnect = (frame) => {
  setConnected(true);
  showChatrooms()
  // stompClient.subscribe('/sub/chats/news',
  stompClient.subtree('/sub/chats/news',
      (chatMessage) => {
        toggleNewMessageIcon(JSON.parse(chatMessage.body), true);
      });
  console.log('Connected: ' + frame);
};

function toggleNewMessageIcon(chatroomId, toggle) {
  if (toggle) {
    $("#new_" + chatroomId.show());
  } else {
    $("#new_" + chatroomId.hide());
  }
}

// WebSocket 연결 에러 발생 시 호출되는 콜백 함수
stompClient.onWebSocketError = (error) => {
  console.error('Error with websocket', error);
};

// STOMP 프로토콜 오류 발생 시 호출되는 콜백 함수
stompClient.onStompError = (frame) => {
  console.error('Broker reported error: ' + frame.headers['message']);
  console.error('Additional details: ' + frame.body);
};

// 연결 상태를 UI에 반영하는 함수
function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  $("#create").prop("disabled", !connected);
}

// STOMP 연결을 활성화하는 함수
function connect() {
  stompClient.activate();
}

// STOMP 연결을 비활성화하는 함수
function disconnect() {
  stompClient.deactivate();
  setConnected(false);
  console.log("Disconnected");
}

// 메시지를 발행하는 함수
function sendMessage() {
  let chatroomId = $("#chatroom-id").val();
  stompClient.publish({
    destination: "/pub/chats/" + chatroomId,
    body: JSON.stringify(
        {'message': $("#message").val()})
  });
  // STOMP 프로토콜 오류 발생 시 호출되는 콜백 함수
  // $("#message").val("")
}

function createChatroom() {
  $.ajax({
    type: 'POST',
    dataType: 'json',
    url: '/chats?title=' + $("#chatroom-title").val(),
    success: function (data) {
      console.log('data: ', data);
      showChatrooms();
      enterChatroom(data.id, true);
    },
    error: function (request, status, error) {
      console.log('request: ' + request);
      console.log('error: ' + error);
    },
  })
}

function showChatrooms() {
  $.ajax({
    type: 'GET',
    dataType: 'json',
    url: '/chats',
    success: function (data) {
      console.log('data: ', data);
      renderChatrooms(data);
    },
    error: function (request, status, error) {
      console.log('request: ' + request);
      console.log('error: ' + error);
    },
  });
}

function renderChatrooms(chatrooms) {
  $("#chatroom-list").html("");
  for (let i = 0; i < chatrooms.length; i++) {
    $("#chatrooms-list").append(
        "<tr onclick='joinChatroom(" + chatrooms[i].id + ")'><td>"
        + chatrooms[i].id + "</td><td>" + chatrooms[i].title
        + "<img src='new.png' id='new_" + chatrooms[i].id + "' style='display: "
        + getDisplayValue(chatrooms[i].hasNewMessage) + "'/></td><td>"
        + chatrooms[i].memberCount + "</td><td>" + chatrooms[i].createdAt
        + "</td></tr>"
    );
  }
}

function getDisplayValue(hasNewMessage) {
  if (hasNewMessage) {
    return "inline";
  }
  return "none";
}

let subscription;

function enterChatroom(chatroomId, newMember) {
  $("chatroom-id").val(chatroomId);
  $("messages").html("");
  showMessages(chatroomId);
  $("conversation").show();
  $("send").prop("disabled", false);
  $("leave").prop("disabled", false);
  toggleNewMessageIcon(chatroomId, false);

  if (subscription !== undefined) {
    subscription.unsubscribe();
  }

  subscription = stompClient.subscribe('/sub/chats/' + chatroomId,
      (chatMessage) => {
        // 수신된 메시지를 화면에 표시
        showMessage(JSON.parse(chatMessage.body));
      });

  if (newMember) {
    // 연결 후 특정 경로("/pub/chats")로 메시지를 발행하여 연결 메시지를 알림
    stompClient.publish({
      destination: "/pub/chats/" + chatroomId,
      body: JSON.stringify(
          {'message': "님이 방에 들어왔습니다."})
    })
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
        showMessages(data[i]);
      }
    },
    error: function (request, status, error) {
      console.log('request: ' + request);
      console.log('error: ' + error);
    },
  })
}

// 수신된 메시지를 대화창에 표시하는 함수
function showMessage(chatMessage) {
  // 메시지를 테이블의 새로운 행으로 추가
  $("#messages").append(
      "<tr><td>" + chatMessage.sender + " : " + chatMessage.message
      + "</td></tr>");
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
      console.log('request: ' + request);
      console.log('error: ' + error);
    },
  })
}

function getRequestParam(currentChatroomId) {
  if (currentChatroomId === "") {
    return "";
  }

  return "?currentChatroomId=" + currentChatroomId;
}

function leaveChatroom() {
  let chatroomId = $("#chatroom-id").val();
  $.ajax({
    type: 'DELETE',
    dataType: 'json',
    url: '/chats/' + chatroomId,
    success: function (data) {
      console.log('data: ', data);
      showChatrooms();
      exitChatroom(chatroomId);
    },
    error: function (request, status, error) {
      console.log('request: ' + request);
      console.log('error: ' + error);
    },
  })
}

function exitChatroom(chatroomId) {
  $("#chatroom-id").val("");
  $("#conversation").hide();
  $("#send").prop("disabled", true);
  $("#leave").prop("disabled", true);
}

// 페이지 로드 시 이벤트 핸들러 등록
$(function () {
  $("form").on('submit', (e) => e.preventDefault());
  $("#connect").click(() => connect());
  $("#disconnect").click(() => disconnect());
  $("#create").click(() => createChatroom());
  $("#leave").click(() => leaveChatroom());
  $("#send").click(() => sendMessage());
});