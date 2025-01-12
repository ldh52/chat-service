package kr.co.yahopet.chatservice.vos;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import kr.co.yahopet.chatservice.entities.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class CustomUserDetails extends CustomOauth2User implements UserDetails {

    // 필드에서 직접 반환
    private final Member member;

    public CustomUserDetails(Member member, Map<String, Object> attributeMap) {
        super(member, attributeMap);
        this.member = member; // member를 필드로 저장
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(member.getRole()));
    }

    @Override
    public String getPassword() {
        if (member == null) {
            throw new IllegalStateException("Member is null");
        }
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        if (member == null) {
            throw new IllegalStateException("Member is null");
        }
        return member.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부
    }

}
