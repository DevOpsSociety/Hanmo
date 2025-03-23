package org.example.hanmo.vaildate;

import lombok.RequiredArgsConstructor;
import org.example.hanmo.error.ErrorCode;
import org.example.hanmo.error.exception.NotFoundException;
import org.example.hanmo.redis.RedisTempRepository;
import org.example.hanmo.repository.UserRepository;

@RequiredArgsConstructor
public class PostValidate {
  private final RedisTempRepository redisTempRepository;
  private final UserRepository userRepository;

  public void validateTempToken(String tempToken) {
    String phoneNumber = redisTempRepository.getPhoneNumberByTempToken(tempToken);
    if (phoneNumber == null) {
      throw new NotFoundException("유효하지 않은 토큰입니다.", ErrorCode.INVALID_CODE_EXCEPTION);
    }
  }

}

