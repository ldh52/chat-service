package kr.co.yahopet.chatservice.controllers;

import java.security.Principal;
import java.util.Map;
import kr.co.yahopet.chatservice.dtos.ChatMessage;
import kr.co.yahopet.chatservice.entities.Message;
import kr.co.yahopet.chatservice.services.ChatService;
import kr.co.yahopet.chatservice.vos.CustomOauth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class StompChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chats/{chatroomId}")
    @SendTo("/sub/chats/{chatroomId}")
    public ChatMessage handleMessage(Principal principal, @DestinationVariable Long chatroomId,
        @Payload Map<String, String> payload) {
        log.info("{} sent {} in {}", principal.getName(), payload, chatroomId);
        CustomOauth2User user = (CustomOauth2User) ((AbstractAuthenticationToken) principal).getPrincipal();
        Message message = chatService.saveMessage(user.getMember(), chatroomId,
            payload.get("message"));
        messagingTemplate.convertAndSend("/sub/chats/updates", chatService.getChatroom(chatroomId));
        return new ChatMessage(principal.getName(), payload.get("message"));
    }

}
