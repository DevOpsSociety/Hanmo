package org.example.hanmo.util;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsCretificationUtil {
    @Value("${spring.coolsms.api-key}")
    private String apiKey;

    @Value("${spring.coolsms.api-secret}")
    private String apiSecret;

    @Value("${spring.coolsms.sender-phone}")
    private String fromNumber;

    DefaultMessageService messageService;

    @PostConstruct
    public void init(){
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");;
    }

    //단일 메시지 발송
    public void sendSMS(String to, String certificationCode){
        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(to); // 수신자 번호 설정
        message.setText("본인확인 인증번호는 " + certificationCode + "입니다."); // 메시지 내용 설정

        this.messageService.sendOne(new SingleMessageSendingRequest(message)); // 메시지 발송 요청
    }
}
