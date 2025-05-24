package org.example.hanmo.util;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Component
public class SmsCertificationUtil {
  @Value("${coolsms.api-key}")
  private String apiKey;

  @Value("${coolsms.api-secret}")
  private String apiSecret;

  @Value("${coolsms.sender-phone}")
  private String fromNumber;

  DefaultMessageService messageService;

  @PostConstruct
  public void init() {
    this.messageService =
        NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
  }

  // 단일 메시지 발송
  public void sendSMS(String receiverPhoneNumber, String certificationCode) {
    Message message = new Message();
    message.setFrom(fromNumber);
    message.setTo(receiverPhoneNumber);
    message.setText("[Hanmo] 본인확인 인증번호는 " + certificationCode + "입니다.");
    this.messageService.sendOne(new SingleMessageSendingRequest(message));
  }

  public void sendMatchingSuccessSms(String receiverPhoneNumber, String userNickname) {
    Message message = new Message();
    message.setFrom(fromNumber);
    message.setTo(receiverPhoneNumber);
    message.setText("[Hanmo] 매칭이 성사되었습니다!\n" + userNickname + "님, 한모에서 매칭 결과를 확인해주세요.");
    this.messageService.sendOne(new SingleMessageSendingRequest(message));
  }
}
