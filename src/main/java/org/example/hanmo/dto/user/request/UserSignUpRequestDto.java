package org.example.hanmo.dto.user.request;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.domain.enums.Department;
import org.example.hanmo.domain.enums.Gender;
import org.example.hanmo.domain.enums.Mbti;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpRequestDto {
    @Schema(description = "이름")
    private String name;

    @Schema(description = "전화번호")
    private String phoneNumber;

    @Schema(description = "학번")
    private String studentNumber;

    @Schema(description = "성별", example = "M=1/F=2") // enum
    private Gender gender;

    @Schema(description = "MBTI", example = "INFJ=1") // enum
    private Mbti mbti;

    @Schema(description = "학과", example = "SECULITY=8") // enum
    private Department department;

    @Schema(description = "인스타그램_ID")
    private String instagramId;

    public UserEntity SignUpToUserEntity() {
        return UserEntity.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .studentNumber(studentNumber)
                .gender(gender)
                .mbti(mbti)
                .department(department)
                .instagramId(instagramId)
                .build();
    }
}
