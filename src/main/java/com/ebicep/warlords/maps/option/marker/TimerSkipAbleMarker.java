package com.ebicep.warlords.maps.option.marker;

public interface TimerSkipAbleMarker extends GameMarker {

    public int getDelay();

    public void skipTimer(int delay);
}
