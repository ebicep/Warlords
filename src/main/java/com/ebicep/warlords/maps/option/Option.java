package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.maps.Game;

public interface Option {
    public void register(Game game);
    public int tick(Game game);
}
