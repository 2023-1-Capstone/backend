package com.capstone.carbonlive.dto.request;

import com.capstone.carbonlive.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinRequest {

    @NotBlank(message = "학번을 입력해주세요.")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "[a-zA-Z0-9]{8,20}",
            message = "비밀번호는 영어와 숫자 포함해서 8~20자리 이내로 입력해주세요.")
    private String password;

    @NotBlank(message = "학교 이메일을 입력해주세요.")
    @Email
    private String email;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    public User toEntity() {
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .name(name)
                .build();
    }
}
