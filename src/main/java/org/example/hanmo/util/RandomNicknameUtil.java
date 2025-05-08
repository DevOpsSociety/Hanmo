package org.example.hanmo.util;

import java.util.List;
import java.util.Random;

import org.example.hanmo.domain.enums.Department;
import org.springframework.stereotype.Component;

@Component
public class RandomNicknameUtil {
  private static final List<String> ADJECTIVES =
          List.of(
                  "행복한", "귀여운", "즐거운", "우아한", "상큼한", "달콤한", "쾌활한", "신나는",
                  "화려한", "든든한", "시원한", "포근한", "고요한", "건강한", "깔끔한", "풍부한",
                  "빛나는", "진지한", "활기찬", "다정한", "빵빵한", "유쾌한", "시크한", "따뜻한",
                  "차분한", "꼼꼼한", "넉넉한", "훌륭한", "명랑한", "신비한", "짜릿한", "진솔한",
                  "탁월한", "순수한", "말랑한", "대담한", "산뜻한", "상쾌한", "낭만한", "달달한"
          );

  private static final List<String> NOUNS =
          List.of(
                  "뽀로로", "피카츄", "나루토", "손오공", "베지터", "가필드", "토토로", "스누피", "캐스퍼", "가가멜",
                  "슈퍼맨", "배트맨", "라푼젤", "이치고", "사스케", "카카시", "도라미", "번개맨", "꼬부기", "파이리",
                  "야도란", "피존투", "디그다", "잠만보", "리자몽", "버터풀", "라이츄", "또가스", "독침붕", "랄토스",
                  "프리저", "피콜로", "아구몬", "메타몽", "망나뇽", "부르마", "코코몽", "피터팬", "피글렛", "토마스"
          );


private static final Random RANDOM = new Random();

  public static String generateNickname(Department department) {
    String deptName = department.getDepartmentType();
    String adjective = ADJECTIVES.get(RANDOM.nextInt(ADJECTIVES.size()));
    String noun = NOUNS.get(RANDOM.nextInt(NOUNS.size()));
    return deptName + "의 " + adjective + " " + noun;
  }
}
