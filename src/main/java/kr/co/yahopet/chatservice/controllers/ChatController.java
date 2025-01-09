package kr.co.yahopet.chatservice.controllers;

import java.util.List;
import kr.co.yahopet.chatservice.entity.Chatroom;
import kr.co.yahopet.chatservice.services.ChatService;
import kr.co.yahopet.chatservice.vos.CustomOauth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    public Chatroom createChatroom(@AuthenticationPrincipal CustomOauth2User user,
        @RequestParam String title) {
        return chatService.createChatroom(user.getMember(), title);
    }

    @PostMapping("/{chatroomId}")
    public boolean joinChatroom(@AuthenticationPrincipal CustomOauth2User user,
        @RequestParam Long chatroomId) {
        return chatService.joinChatroom(user.getMember(), chatroomId);
    }

    @DeleteMapping("/{chatroomId}")
    public boolean leaveChatroom(@AuthenticationPrincipal CustomOauth2User user,
        @RequestParam Long chatroomId) {
        return chatService.leaveChatroom(user.getMember(), chatroomId);
    }

    @GetMapping
    public List<Chatroom> chatroomList(@AuthenticationPrincipal CustomOauth2User user) {
        return chatService.chatroomList(user.getMember());
    }
}
