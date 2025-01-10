package kr.co.yahopet.chatservice.dtos;

import java.time.LocalDateTime;
import kr.co.yahopet.chatservice.entity.Chatroom;

public record ChatroomDto(
    Long id,
    String title,
    Integer memberCount,
    LocalDateTime createdAt) {

    public static ChatroomDto from(Chatroom chatroom) {
        return new ChatroomDto(chatroom.getId(), chatroom.getTitle(),
            chatroom.getMemberChatroomMappingSet().size(), chatroom.getCreatedAt());
    }
}
