package com.ebicep.warlords.game.maps;

import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.anomaly.DunestarEscortOption;
import com.ebicep.warlords.game.option.pve.anomaly.DunestarRouteMarker;
import com.ebicep.warlords.util.bukkit.LocationFactory;

import java.util.List;

public class PlainsOfDunestar extends AbstractAnomalyMap {

    private static final double[][] ROUTE_LOCATIONS = {
            {0.5, 80, 0.5},
            {30.5, 80, 5.5},
            {60.5, 80, -8.5},
            {90.5, 80, 0.5}
    };

    public PlainsOfDunestar() {
        super("Plains of Dunestar", "PlainsOfDunestar", ROUTE_LOCATIONS[0]);
    }

    @Override
    protected void addAnomalyOptions(List<Option> options, LocationFactory loc) {
        for (int routeIndex = 0; routeIndex < ROUTE_LOCATIONS.length; routeIndex++) {
            options.add(DunestarRouteMarker.create(routeIndex, location(loc, ROUTE_LOCATIONS[routeIndex])).asOption());
        }
        options.add(new DunestarEscortOption());
    }
}