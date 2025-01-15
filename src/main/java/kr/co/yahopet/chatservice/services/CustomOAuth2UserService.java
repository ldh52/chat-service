package kr.co.yahopet.chatservice.services;

import kr.co.yahopet.chatservice.entities.Member;
import kr.co.yahopet.chatservice.repositories.MemberRepository;
import kr.co.yahopet.chatservice.vos.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        Member member = memberRepository.findByEmail(email)
            .orElseGet(() -> {
                Member newMember = MemberFactory.create(userRequest, oAuth2User);
                return memberRepository.save(newMember);
            });

        return new CustomOAuth2User(member, oAuth2User.getAttributes());
    }
}