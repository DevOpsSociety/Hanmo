package org.example.hanmo.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeUtil {

    private DateTimeUtil() { }

    public static LocalDateTime startOfToday(ZoneId zone) {
        return LocalDate.now(zone).atStartOfDay();
    }

    public static LocalDateTime startOfTomorrow(ZoneId zone) {
        return LocalDate.now(zone).plusDays(1).atStartOfDay();
    }
}
