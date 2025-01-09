package kr.co.yahopet.chatservice.repositories;

import java.util.List;
import kr.co.yahopet.chatservice.entity.MemberChatroomMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberChatroomMappingRepository extends
    JpaRepository<MemberChatroomMapping, Long> {

    boolean existsByMemberIdAndChatroomId(Long memberId, Long chatroomId);

    void deleteByMemberIdAndChatroomId(Long memberId, Long chatroomId);

    List<MemberChatroomMapping> findByMemberId(Long memberId);

}
