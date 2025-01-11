package kr.co.yahopet.chatservice.controllers;

import java.util.List;
import kr.co.yahopet.chatservice.dtos.ChatMessage;
import kr.co.yahopet.chatservice.dtos.ChatroomDto;
import kr.co.yahopet.chatservice.entity.Chatroom;
import kr.co.yahopet.chatservice.entity.Message;
import kr.co.yahopet.chatservice.services.ChatService;
import kr.co.yahopet.chatservice.vos.CustomOauth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chats")
@RestController
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ChatroomDto createChatroom(@AuthenticationPrincipal CustomOauth2User user,
        @RequestParam String title) {
        Chatroom chatroom = chatService.createChatroom(user.getMember(), title);

        return ChatroomDto.from(chatroom);
    }

    @PostMapping("/{chatroomId}")
    public boolean joinChatroom(@AuthenticationPrincipal CustomOauth2User user,
        @PathVariable Long chatroomId, @RequestParam(required = false) Long currrntChatroomId) {
        return chatService.joinChatroom(user.getMember(), chatroomId, currrntChatroomId);
    }

    @DeleteMapping("/{chatroomId}")
    public boolean leaveChatroom(@AuthenticationPrincipal CustomOauth2User user,
        @PathVariable Long chatroomId) {
        return chatService.leaveChatroom(user.getMember(), chatroomId);
    }

    @GetMapping
    public List<ChatroomDto> chatroomList(@AuthenticationPrincipal CustomOauth2User user) {
        List<Chatroom> chatroomList = chatService.getChatroomList(user.getMember());

        return chatroomList.stream()
            .map(ChatroomDto::from)
            .toList();
    }

    @GetMapping("/{chatroomId}/messages")
    public List<ChatMessage> getMessageList(@PathVariable Long chatroomId) {
        List<Message> messageList = chatService.getMessageList(chatroomId);
        return messageList.stream()
            .map(message -> new ChatMessage(message.getMember().getNickname(), message.getText()))
            .toList();
    }
}
