package kr.co.yahopet.chatservice.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import kr.co.yahopet.chatservice.entities.Member;
import kr.co.yahopet.chatservice.enums.Gender;
import kr.co.yahopet.chatservice.repositories.MemberRepository;
import kr.co.yahopet.chatservice.vos.CustomOauth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributeMap = oAuth2User.getAttribute("kakao_account");
        if (attributeMap == null) {
            throw new OAuth2AuthenticationException("Kakao account attributes are missing.");
        }

        String email = (String) attributeMap.get("email");
        Member member = memberRepository.findByEmail(email)
            .orElseGet(() -> registerMember(attributeMap));

        return new CustomOauth2User(member, oAuth2User.getAttributes());
    }

    private Member registerMember(Map<String, Object> attributeMap) {
        Member member = Member.builder()
            .email((String) attributeMap.get("email"))
            .nickName((String) ((Map<?, ?>) attributeMap.get("profile")).get("nickname"))
            .name((String) attributeMap.get("name"))
            .phoneNumber((String) attributeMap.get("phone_number"))
            .gender(getGender(attributeMap))
            .birthday(getBirthDay(attributeMap))
            .role("ROLE_USER")
            .build();

        return memberRepository.save(member);
    }

    private Gender getGender(Map<String, Object> attributeMap) {
        String genderStr = (String) attributeMap.get("gender");
        return genderStr != null ? Gender.valueOf(genderStr.toUpperCase()) : null; // null 처리
    }

    private LocalDate getBirthDay(Map<String, Object> attributeMap) {
        String birthYear = (String) attributeMap.get("birthyear");
        String birthDay = (String) attributeMap.get("birthday");

        if (birthYear != null && birthDay != null) {
            return LocalDate.parse(birthYear + birthDay, DateTimeFormatter.BASIC_ISO_DATE);
        }
        return null; // null 처리
    }
}