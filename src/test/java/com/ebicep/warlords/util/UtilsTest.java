package com.ebicep.warlords.util;

import static com.ebicep.warlords.util.Utils.*;
import org.junit.Test;
import static org.junit.Assert.*;

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
    
}
