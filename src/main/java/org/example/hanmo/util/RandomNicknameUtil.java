package org.example.hanmo.util;

import org.example.hanmo.domain.enums.Department;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class RandomNicknameUtil {
    private static final List<String> ADJECTIVES = List.of(
            "올 A+받은", "빵 훔치는", "존예", "존잘", "컵라면 뚜껑 핥는",
            "행운의 주인공", "화장실에서 밥먹는", "연예인 지망생", "도넛 가운데만 먹는", "로또 당첨된"
    );

    private static final List<String> NOUNS = List.of(
            "흰둥이", "신형만", "봉미선", "채성아", "나미리",
            "수지", "유리", "짱구", "맹구", "철수"
    );

    private static final Random RANDOM = new Random();

    public static String generateNickname(Department department) {
        String deptName = department.getDepartmentType();
        String adjective = ADJECTIVES.get(RANDOM.nextInt(ADJECTIVES.size()));
        String noun = NOUNS.get(RANDOM.nextInt(NOUNS.size()));
        return deptName + "의 " + adjective + " " + noun;
    }
}
