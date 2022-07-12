package com.ebicep.warlords.util;

import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

import static com.ebicep.warlords.util.warlords.Utils.formatTimeLeft;
import static org.junit.Assert.assertEquals;

public class UtilsTest {
    
    @Test
    public void testTimeLeft0() {
        assertEquals("00:00", formatTimeLeft(0));
    }

    @Test
    public void testTimeLeft1() {
        assertEquals("00:01", formatTimeLeft(1));
    }

    @Test
    public void testTimeLeft10() {
        assertEquals("00:10", formatTimeLeft(10));
    }

    @Test
    public void testTimeLeft60() {
        assertEquals("01:00", formatTimeLeft(60));
    }


    @Test
    public void testDateUtil() {
        LocalDateTime time1 = LocalDateTime.now().withHour(10);
        LocalDateTime time2 = LocalDateTime.now().withHour(10).minusMinutes(11);
        assertEquals(ChronoUnit.MINUTES.between(time2, time1), 10);
    }

    @Test
    public void testReset() {
        LocalDateTime reset = LocalDateTime.now().minusDays(7).withHour(10).minusMinutes(11);
        LocalDateTime current = LocalDateTime.now().withHour(10);
        assertEquals(ChronoUnit.MINUTES.between(reset, current), Timing.WEEKLY.minuteDuration + 11);
    }

    @Test
    public void testDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.set(2022, Calendar.JUNE, 11, 10, 0, 0);
        System.out.println(cal.getTime());
        System.out.println(cal.get(Calendar.DAY_OF_WEEK));
    }


}
