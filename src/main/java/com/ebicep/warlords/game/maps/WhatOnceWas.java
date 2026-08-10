package com.ebicep.warlords.game.maps;

import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.anomaly.AncientRune;
import com.ebicep.warlords.game.option.pve.anomaly.AncientRuneMarker;
import com.ebicep.warlords.game.option.pve.anomaly.AncientVaultMarker;
import com.ebicep.warlords.game.option.pve.anomaly.AnomalySpawnMarker;
import com.ebicep.warlords.game.option.pve.anomaly.WhatOnceWasPuzzleOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;

import java.util.List;

public class WhatOnceWas extends AbstractAnomalyMap {

    private static final double[][] VAULT_LOCATIONS = {
            {0.5, 90, 0.5},
            {0.5, 90, 0.5},
            {0.5, 90, 0.5},
            {0.5, 90, 0.5},
            {0.5, 90, 0.5},
            {0.5, 90, 0.5},
            {0.5, 90, 0.5},
            {0.5, 90, 0.5},
            {0.5, 90, 0.5},
    };
    private static final double[][][] RUNE_LOCATIONS = {
            {{9.5, 91, 9.5}, {13.5, 91, 9.5}, {9.5, 91, 13.5}, {13.5, 91, 13.5}},
            {{-12.5, 91, 9.5}, {-8.5, 91, 9.5}, {-12.5, 91, 13.5}, {-8.5, 91, 13.5}},
            {{-1.5, 91, -14.5}, {2.5, 91, -14.5}, {-1.5, 91, -10.5}, {2.5, 91, -10.5}},
            {{9.5, 91, 9.5}, {13.5, 91, 9.5}, {9.5, 91, 13.5}, {13.5, 91, 13.5}},
            {{-12.5, 91, 9.5}, {-8.5, 91, 9.5}, {-12.5, 91, 13.5}, {-8.5, 91, 13.5}},
            {{-1.5, 91, -14.5}, {2.5, 91, -14.5}, {-1.5, 91, -10.5}, {2.5, 91, -10.5}},
            {{9.5, 91, 9.5}, {13.5, 91, 9.5}, {9.5, 91, 13.5}, {13.5, 91, 13.5}},
            {{-12.5, 91, 9.5}, {-8.5, 91, 9.5}, {-12.5, 91, 13.5}, {-8.5, 91, 13.5}},
            {{-1.5, 91, -14.5}, {2.5, 91, -14.5}, {-1.5, 91, -10.5}, {2.5, 91, -10.5}}
    };
    private static final double[][][] ENEMY_SPAWN_LOCATIONS = {
            {{11.5, 90, 11.5}, {11.5, 90, 12.5}, {11.5, 90, 12.5}},
            {{-11.5, 90, 11.5}, {-11.5, 90, 11.5}, {-11.5, 90, 11.5}},
            {{0.5, 90, -11.5}, {11.5, 90, -13.5}, {-10.5, 90, -12.5}},
            {{11.5, 90, 11.5}, {11.5, 90, 12.5}, {11.5, 90, 12.5}},
            {{-11.5, 90, 11.5}, {-11.5, 90, 11.5}, {-11.5, 90, 11.5}},
            {{0.5, 90, -11.5}, {11.5, 90, -13.5}, {-10.5, 90, -12.5}},
            {{11.5, 90, 11.5}, {11.5, 90, 12.5}, {11.5, 90, 12.5}},
            {{-11.5, 90, 11.5}, {-11.5, 90, 11.5}, {-11.5, 90, 11.5}},
            {{0.5, 90, -11.5}, {11.5, 90, -13.5}, {-10.5, 90, -12.5}},
    };

    public WhatOnceWas() {
        super("What Once Was", "WhatOnceWas", new double[]{0.5, 90, 0.5});
    }

    @Override
    protected void addAnomalyOptions(List<Option> options, LocationFactory loc) {
        for (int vaultIndex = 0; vaultIndex < VAULT_LOCATIONS.length; vaultIndex++) {
            options.add(AncientVaultMarker.create(vaultIndex, location(loc, VAULT_LOCATIONS[vaultIndex])).asOption());
            for (int runeIndex = 0; runeIndex < AncientRune.VALUES.length; runeIndex++) {
                options.add(AncientRuneMarker.create(
                        vaultIndex,
                        AncientRune.VALUES[runeIndex],
                        location(loc, RUNE_LOCATIONS[vaultIndex][runeIndex])
                ).asOption());
            }
            for (double[] spawn : ENEMY_SPAWN_LOCATIONS[vaultIndex]) {
                options.add(AnomalySpawnMarker.create(vaultIndex, location(loc, spawn)).asOption());
            }
        }
        options.add(new WhatOnceWasPuzzleOption());
    }
}
