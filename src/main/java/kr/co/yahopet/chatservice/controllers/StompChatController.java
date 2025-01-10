package kr.co.yahopet.chatservice.controllers;

import java.security.Principal;
import java.util.Map;
import kr.co.yahopet.chatservice.dtos.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class StompChatController {

    @MessageMapping("/chats/{chatroomId}")
    @SendTo("/sub/chats/{chatroomId}")
    public ChatMessage handleMessage(@AuthenticationPrincipal Principal principal,
        @DestinationVariable Long chatroomId,
        @Payload Map<String, String> payload) {
        log.info("{} sent {} in {}", principal.getName(), payload, chatroomId);

        return new ChatMessage(principal.getName(), payload.get("message"));
    }

}
