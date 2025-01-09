package kr.co.yahopet.chatservice.repositories;

import kr.co.yahopet.chatservice.entity.MemberChatroomMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberChatroomMappingRepository extends
    JpaRepository<MemberChatroomMapping, Long> {

}
