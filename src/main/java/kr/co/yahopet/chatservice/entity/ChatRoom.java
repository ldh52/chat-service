package kr.co.yahopet.chatservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
public class ChatRoom {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    @Id
    Long id;

    String title;

    @OneToMany(mappedBy = "chatRoom")
    Set<MemberChatroomMapping> memberChatroomMappingSet;

    LocalDateTime createdAt;

}
