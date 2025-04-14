package org.example.hanmo.util;

import java.util.List;
import java.util.Random;

import org.example.hanmo.domain.enums.Department;
import org.springframework.stereotype.Component;

@Component
public class RandomNicknameUtil {
    private static final List<String> ADJECTIVES =
            List.of(
                    "올 A+받은",
                    "빵 훔치는",
                    "존예",
                    "존잘",
                    "컵라면 뚜껑 핥는",
                    "행운의 주인공",
                    "화장실에서 밥먹는",
                    "연예인 지망생",
                    "도넛 가운데만 먹는",
                    "로또 당첨된",
                    "똥 밟은",
                    "공강에 학교온",
                    "지각한",
                    "밥 10끼먹는",
                    "레전드 존잘",
                    "레전드 존예",
                    "거짓말쟁이",
                    "배고파서 비 먹는",
                    "진격의 거인",
                    "비보잉하는",
                    "노래 개 잘하는",
                    "한달 째 변비인");

    private static final List<String> NOUNS =
            List.of(
                    "흰둥이", "신형만", "봉미선", "채성아", "나미리", "수지", "유리", "짱구", "맹구", "철수", "훈이,", "도라에몽",
                    "둘리", "뽀로로", "피카츄", "스폰지밥", "패트릭", "나루토", "루피", "헬로키티", "미피", "토토로", "손오공",
                    "베지터", "우디", "버즈", "호머", "바트", "스누피", "마징가Z");

    private static final Random RANDOM = new Random();

    public static String generateNickname(Department department) {
        String deptName = department.getDepartmentType();
        String adjective = ADJECTIVES.get(RANDOM.nextInt(ADJECTIVES.size()));
        String noun = NOUNS.get(RANDOM.nextInt(NOUNS.size()));
        return deptName + "의 " + adjective + " " + noun;
    }
}
