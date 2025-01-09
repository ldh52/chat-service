package kr.co.yahopet.chatservice.repositories;

import kr.co.yahopet.chatservice.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatroomRepository extends JpaRepository<ChatRoom, Long> {

}
