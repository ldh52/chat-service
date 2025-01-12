package kr.co.yahopet.chatservice.dtos;

import java.time.LocalDateTime;
import kr.co.yahopet.chatservice.entities.Chatroom;

public record ChatroomDto(
    Long id,
    String title,
    Boolean hasNewMessage,
    Integer memberCount,
    LocalDateTime createdAt) {

    public static ChatroomDto from(Chatroom chatroom) {
        // chatroom이 null인 경우 처리
        if (chatroom == null) {
            return null; // 또는 적절한 기본값으로 처리
        }

        // getMemberChatroomMappingSet()이 null일 경우를 처리
        int memberCount = (chatroom.getMemberChatroomMappingSet() != null)
            ? chatroom.getMemberChatroomMappingSet().size()
            : 0;

        return new ChatroomDto(chatroom.getId(), chatroom.getTitle(),
            chatroom.getHasNewMessage(),
            memberCount,
            chatroom.getCreatedAt());
    }
}