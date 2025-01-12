package kr.co.yahopet.chatservice.repositories;

import kr.co.yahopet.chatservice.entities.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

}
