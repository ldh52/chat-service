package kr.co.yahopet.chatservice.enums;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    USER("ROLE_USER"), CONSULTANT("ROLE_CONSULTANT");

    String code;

    public static Role fromCode(String code) {
        return Arrays.stream(Role.values())
            .filter(r -> r.getCode().equals(code))
            .findFirst()
            .orElseThrow();
    }
}
