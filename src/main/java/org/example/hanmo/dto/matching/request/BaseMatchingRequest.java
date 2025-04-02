// package org.example.hanmo.dto.matching.request;
//
// import org.example.hanmo.domain.UserEntity;
// import org.example.hanmo.domain.enums.*;
//
// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
//
// @Getter
// @AllArgsConstructor
// @NoArgsConstructor
// public class BaseMatchingRequest {
//
//    public UserEntity toUserEntity(UserEntity user) {
//        return UserEntity.builder()
//                .nickname(user.getNickname())
//                .instagramId(user.getInstagramId())
//                .gender(user.getGender())
//                .department(user.getDepartment())
//                .mbti(user.getMbti())
//                .userStatus(user.getUserStatus())
//                .build();
//    }
// }