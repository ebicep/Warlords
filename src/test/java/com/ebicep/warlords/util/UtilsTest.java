package com.ebicep.warlords.util;

import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import com.ebicep.warlords.util.java.NumberFormat;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
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

    @Test
    public void testNumberFormat() {
        double value = 3.14;
        double multiply = 1.19;
        System.out.println(value * multiply);
        System.out.println(NumberFormat.formatOptionalHundredths(value * multiply));
    }

    @Test
    public void testStrings() {
        String[] args = {"a", "b", "c"};
        String newName = StringUtils.join(args, " ", 1, args.length);
        System.out.println(newName + "|");

    }

    @Test
    public void testRandom() {
//        double min = 100;
//        double max = 0;
//        for (int i = 0; i < 1000; i++) {
////            int randomInt = ThreadLocalRandom.current().nextInt(10001);
////            double aDouble = randomInt / 100.0;
//            double aDouble = ThreadLocalRandom.current().nextDouble(0, 100);
//            if (aDouble < min) {
//                min = aDouble;
//            } else if (aDouble > max) {
//                max = aDouble;
//            }
//        }
//        System.out.println("Min: " + min);
//        System.out.println("Max: " + max);
    }

    @Test
    public void splitString() {
        System.out.println(Arrays.toString(com.ebicep.warlords.util.java.StringUtils.splitStringNTimes("Kills", 6)));
    }


}
