package kr.co.yahopet.chatservice.vos;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import kr.co.yahopet.chatservice.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@AllArgsConstructor
public class CustomOauth2User implements OAuth2User {

    @Getter
    Member member;
    Map<String, Object> attributeMap;

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributeMap;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> this.member.getRole());
    }

    @Override
    public String getName() {
        return this.member.getNickName();
    }
}
