package org.example.hanmo.smsservice;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.example.hanmo.dto.sms.request.SmsRequestDto;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.SmsSendException;
import org.example.hanmo.redis.RedisSmsRepository;
import org.example.hanmo.repository.user.UserRepository;
import org.example.hanmo.service.impl.SmsServiceImpl;
import org.example.hanmo.util.SmsCertificationUtil;
import org.example.hanmo.vaildate.SmsValidate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SmsServiceTest {

  @Mock private SmsCertificationUtil smsCertificationUtil;

  @Mock private RedisSmsRepository redisSmsRepository; // 추가된 모킹

  @Mock private UserRepository userRepository;

  @InjectMocks private SmsServiceImpl smsServiceImpl;

  @Test
  void SmsSenderTest_인증번호_발송_정상작동_되는지() {
    // Given
    String phoneNumber = "01087206548";
    SmsRequestDto dto = new SmsRequestDto(phoneNumber);

    // When
    smsServiceImpl.sendSms(dto);

    // Then
    verify(smsCertificationUtil, times(1))
        .sendSMS(Mockito.eq(phoneNumber), Mockito.argThat(code -> code.matches("\\d{6}")));
  }

  @Test
  void testVerifyCode_인증이_정상작동_되는지() {
    // Given: 올바른 인증번호와 해당 전화번호가 존재하는 상황
    String certificationCode = "123456";
    String phoneNumber = "01012345678";

    try (MockedStatic<SmsValidate> smsValidateMock = Mockito.mockStatic(SmsValidate.class)) {
      smsValidateMock
          .when(() -> SmsValidate.validateSmsCodeByCode(certificationCode, redisSmsRepository))
          .thenReturn(phoneNumber);
      Mockito.lenient()
          .when(redisSmsRepository.getSmsCertification(certificationCode))
          .thenReturn(phoneNumber);

      // When: verifyCode 메서드를 호출하면
      smsServiceImpl.verifyCode(certificationCode);

      // Then: Redis에서 인증번호 삭제 및 검증 플래그 설정이 호출되어야 함
      then(redisSmsRepository).should().deleteSmsCertification(certificationCode);
      then(redisSmsRepository).should().setVerifiedFlag(phoneNumber);
    }
  }

  @Test
  void testVerifyCode_InvalidCertificationCode_BDD() {
    // Given: 잘못된 인증번호가 전달되어 예외가 발생하는 상황
    String certificationCode = "invalid";

    try (MockedStatic<SmsValidate> smsValidateMock = Mockito.mockStatic(SmsValidate.class)) {
      smsValidateMock
          .when(() -> SmsValidate.validateSmsCodeByCode(certificationCode, redisSmsRepository))
          .thenThrow(
              new SmsSendException(
                  "400_Error, 인증번호가 만료되었습니다.", ErrorCode.SMS_VERIFICATION_FAILED_EXCEPTION));

      // When & Then: 예외가 발생하며 Redis 관련 작업은 호출되지 않아야 함
      assertThrows(SmsSendException.class, () -> smsServiceImpl.verifyCode(certificationCode));
      then(redisSmsRepository).shouldHaveNoInteractions();
    }
  }
  // d
}
