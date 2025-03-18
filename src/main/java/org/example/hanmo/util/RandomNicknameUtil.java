package org.example.hanmo.util;

import org.example.hanmo.domain.enums.Department;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class RandomNicknameUtil {
    private static final List<String> ADJECTIVES = List.of(
            "수줍은", "용감한", "멋진", "귀여운", "활발한",
            "똑똑한", "재치있는", "열정적인", "신비로운", "경쾌한"
    );

    private static final List<String> NOUNS = List.of(
            "하마", "사자", "호랑이", "독수리", "돌고래",
            "토끼", "늑대", "펭귄", "원숭이", "표범"
    );

    private static final Random RANDOM = new Random();

    public static String generateNickname(Department department) {
        String deptName = department.getDepartmentType();
        String adjective = ADJECTIVES.get(RANDOM.nextInt(ADJECTIVES.size()));
        String noun = NOUNS.get(RANDOM.nextInt(NOUNS.size()));
        return deptName + " " + adjective + " " + noun;
    }
}
