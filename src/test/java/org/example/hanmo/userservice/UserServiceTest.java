package org.example.hanmo.userservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.UUID;

import org.example.hanmo.domain.UserEntity;
import org.example.hanmo.dto.user.request.UserLoginRequestDto;
import org.example.hanmo.dto.user.request.UserSignUpRequestDto;
import org.example.hanmo.dto.user.response.UserProfileResponseDto;
import org.example.hanmo.dto.user.response.UserSignUpResponseDto;
import org.example.hanmo.redis.RedisSmsRepository;
import org.example.hanmo.redis.RedisTempRepository;
import org.example.hanmo.repository.UserRepository;
import org.example.hanmo.service.impl.UserServiceImpl;
import org.example.hanmo.vaildate.AuthValidate;
import org.example.hanmo.vaildate.SmsValidate;
import org.example.hanmo.vaildate.UserValidate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private RedisSmsRepository redisSmsRepository;

  @Mock private RedisTempRepository redisTempRepository;

  @InjectMocks private UserServiceImpl userServiceImpl;

  @Mock private AuthValidate authValidate;

  @Mock private UserValidate userValidate;

  // signUpUser 테스트
  @Test
  void testSignUpUser_회원가입이_정상적으로_되는지() {
    // Given
    String phoneNumber = "01012345678";
    String expectedNickname = "TestNick";
    UserSignUpRequestDto requestDto =
        UserSignUpRequestDto.builder().phoneNumber(phoneNumber).build();

    UserEntity user = new UserEntity();
    setField(user, "phoneNumber", phoneNumber);
    setField(user, "nickname", expectedNickname);

    // DTO의 SignUpToUserEntity() 메서드를 spy하여 user 객체 반환하도록 처리
    UserSignUpRequestDto spyRequestDto = Mockito.spy(requestDto);
    doReturn(user).when(spyRequestDto).SignUpToUserEntity();
    try (MockedStatic<SmsValidate> smsValidateMock = Mockito.mockStatic(SmsValidate.class);
        MockedStatic<UserValidate> userValidateMock = Mockito.mockStatic(UserValidate.class)) {
      smsValidateMock
          .when(() -> SmsValidate.validateSignUp(phoneNumber, redisSmsRepository, userRepository))
          .thenAnswer(invocation -> null);
      userValidateMock
          .when(() -> UserValidate.setUniqueRandomNicknameIfNeeded(user, true, userRepository))
          .thenAnswer(invocation -> null);
      when(userRepository.save(user)).thenReturn(user);

      // When: 회원가입 메서드 호출
      UserSignUpResponseDto responseDto = userServiceImpl.signUpUser(spyRequestDto);

      // Then: 각 static 메서드 및 Redis, Repository 호출 검증
      smsValidateMock.verify(
          () -> SmsValidate.validateSignUp(phoneNumber, redisSmsRepository, userRepository));
      userValidateMock.verify(
          () -> UserValidate.setUniqueRandomNicknameIfNeeded(user, true, userRepository));
      verify(redisSmsRepository, times(1)).deleteVerifiedFlag(phoneNumber);
      verify(redisTempRepository, times(1)).setTempToken(eq(phoneNumber), anyString(), eq(300L));
      verify(userRepository, times(1)).save(user);
      assertEquals(expectedNickname, responseDto.getNickname());
      assertEquals(phoneNumber, responseDto.getPhoneNumber());
    }
  }

  @Test
  void testChangeNickname_닉네임이_정상적으로_변경되는지() {
    // Given
    String tempToken = UUID.randomUUID().toString();
    String phoneNumber = "01012345678";
    String oldNickname = "OldNick";
    String newNickname = "NewNick";

    UserEntity user = new UserEntity();
    setField(user, "phoneNumber", phoneNumber);
    setField(user, "nickname", oldNickname);

    try (MockedStatic<UserValidate> userValidateMock = Mockito.mockStatic(UserValidate.class)) {
      userValidateMock
          .when(() -> UserValidate.validatePhoneNumberByTempToken(tempToken, redisTempRepository))
          .thenReturn(phoneNumber);
      userValidateMock
          .when(() -> UserValidate.getUserByPhoneNumber(phoneNumber, userRepository))
          .thenReturn(user);
      userValidateMock
          .when(() -> UserValidate.setUniqueRandomNicknameIfNeeded(user, true, userRepository))
          .thenAnswer(invocation -> null);
      setField(user, "nickname", newNickname);
      when(userRepository.save(user)).thenReturn(user);

      // When: 닉네임 변경 메서드 호출
      UserSignUpResponseDto responseDto = userServiceImpl.changeNickname(tempToken);

      // Then: 각 static 메서드 및 Redis, Repository 호출 검증
      userValidateMock.verify(
          () -> UserValidate.validatePhoneNumberByTempToken(tempToken, redisTempRepository));
      userValidateMock.verify(() -> UserValidate.getUserByPhoneNumber(phoneNumber, userRepository));
      userValidateMock.verify(
          () -> UserValidate.setUniqueRandomNicknameIfNeeded(user, true, userRepository));
      verify(redisTempRepository, times(1)).deleteTempToken(tempToken);
      verify(userRepository, times(1)).save(user);

      assertEquals(newNickname, responseDto.getNickname());
      assertEquals(phoneNumber, responseDto.getPhoneNumber());
    }
  }

  @Test
  void withdrawUser_shouldDeleteUserAndVerifiedFlag() {
    // given
    String phoneNumber = "01012345678";
    UserEntity user = new UserEntity();
    setField(user, "phoneNumber", phoneNumber);

    try (MockedStatic<UserValidate> mockedStatic = Mockito.mockStatic(UserValidate.class)) {
      mockedStatic
          .when(() -> UserValidate.getUserByPhoneNumber(phoneNumber, userRepository))
          .thenReturn(user);
      // when
      userServiceImpl.withdrawUser(phoneNumber);
      // then: user 삭제 및 인증 완료 플래그 삭제 검증
      then(userRepository).should().delete(user);
      then(redisSmsRepository).should().deleteVerifiedFlag(phoneNumber);
    }
  }

  @Test
  void testLoginUser_shouldReturnTempToken() {
    // Given
    String phoneNumber = "01012345678";
    String studentNumber = "20201234";
    UserLoginRequestDto requestDto = new UserLoginRequestDto(phoneNumber, studentNumber);
    UserEntity user = new UserEntity();
    setField(user, "phoneNumber", phoneNumber);

    String expectedToken = "token123";
    given(userValidate.findByPhoneNumberAndStudentNumber(phoneNumber, studentNumber))
        .willReturn(user);
    given(redisTempRepository.createTempTokenForUser(phoneNumber, true)).willReturn(expectedToken);

    // When
    String actualToken = userServiceImpl.loginUser(requestDto);

    // Then
    assertEquals(expectedToken, actualToken);
    then(userValidate).should().findByPhoneNumberAndStudentNumber(phoneNumber, studentNumber);
    then(redisTempRepository).should().createTempTokenForUser(phoneNumber, true);
  }

  @Test
  void testGetUserProfile_shouldReturnUserProfile() {
    // Given
    String tempToken = "token123";
    UserEntity user = new UserEntity();
    setField(user, "nickname", "닉네임");
    setField(user, "name", "이름");
    setField(user, "instagramId", "인스타아이디");

    given(authValidate.validateTempToken(tempToken)).willReturn(user);

    // When
    UserProfileResponseDto profile = userServiceImpl.getUserProfile(tempToken);

    // Then
    assertThat(profile.getNickname()).isEqualTo("닉네임");
    assertThat(profile.getName()).isEqualTo("이름");
    assertThat(profile.getInstagramId()).isEqualTo("인스타아이디");
    then(authValidate).should().validateTempToken(tempToken);
  }

  // Reflection을 이용해 private 필드 설정 헬퍼 메서드
  private void setField(Object target, String fieldName, Object value) {
    try {
      Field field = target.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(target, value);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      fail("필드 설정 실패: " + e.getMessage());
    }
  }
}
