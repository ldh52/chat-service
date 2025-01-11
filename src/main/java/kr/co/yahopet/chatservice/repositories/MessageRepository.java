package kr.co.yahopet.chatservice.repositories;

import java.util.List;
import kr.co.yahopet.chatservice.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllByChatroomId(Long chatroomId);

}
