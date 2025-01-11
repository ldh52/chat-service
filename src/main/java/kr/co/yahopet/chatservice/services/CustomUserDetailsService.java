package kr.co.yahopet.chatservice.services;

import kr.co.yahopet.chatservice.dtos.MemberDto;
import kr.co.yahopet.chatservice.entity.Member;
import kr.co.yahopet.chatservice.enums.Role;
import kr.co.yahopet.chatservice.repositories.MemberRepository;
import kr.co.yahopet.chatservice.vos.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByName(username).get();

        if (Role.fromCode(member.getRole()) != Role.CONSULTANT) {
            throw new AccessDeniedException("상담사가 아닙니다.");
        }
        return new CustomUserDetails(member);
    }

    public MemberDto saveMember(MemberDto memberDto) {
        Member member = MemberDto.to(memberDto);
        member.updatePassword(memberDto.password(), memberDto.confirmedPassword(), passwordEncoder);

        member = memberRepository.save(member);

        return MemberDto.from(member);
    }
}
