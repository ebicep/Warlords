package com.ebicep.warlords.game.maps;

import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.anomaly.EndlessParadoxCurrencyOption;
import com.ebicep.warlords.game.option.pve.anomaly.EndlessParadoxOption;
import com.ebicep.warlords.game.option.pve.anomaly.TimelineAltarMarker;
import com.ebicep.warlords.util.bukkit.LocationFactory;

import java.util.List;

public class EndlessParadox extends AbstractAnomalyMap {

    private static final double[] ALTAR_LOCATION = {0, 90, 0};

    public EndlessParadox() {
        super("Endless Paradox", "EndlessParadox", ALTAR_LOCATION);
    }

    @Override
    protected void addAnomalyOptions(List<Option> options, LocationFactory loc) {
        options.add(TimelineAltarMarker.create(location(loc, ALTAR_LOCATION)).asOption());
        options.add(new EndlessParadoxCurrencyOption());
        options.add(new EndlessParadoxOption());
    }
}
