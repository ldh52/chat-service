package kr.co.yahopet.chatservice.repositories;

import java.util.Optional;
import kr.co.yahopet.chatservice.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByName(String name);
}