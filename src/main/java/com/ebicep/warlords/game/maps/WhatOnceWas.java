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
            {16.5, 70, 16.5},
            {-15.5, 70, 16.5},
            {0.5, 70, -19.5}
    };
    private static final double[][][] RUNE_LOCATIONS = {
            {{14.5, 70, 14.5}, {18.5, 70, 14.5}, {14.5, 70, 18.5}, {18.5, 70, 18.5}},
            {{-17.5, 70, 14.5}, {-13.5, 70, 14.5}, {-17.5, 70, 18.5}, {-13.5, 70, 18.5}},
            {{-1.5, 70, -21.5}, {2.5, 70, -21.5}, {-1.5, 70, -17.5}, {2.5, 70, -17.5}}
    };
    private static final double[][][] ENEMY_SPAWN_LOCATIONS = {
            {{31.5, 70, 31.5}, {34.5, 70, 14.5}, {14.5, 70, 34.5}},
            {{-30.5, 70, 31.5}, {-33.5, 70, 14.5}, {-13.5, 70, 34.5}},
            {{0.5, 70, -36.5}, {16.5, 70, -32.5}, {-15.5, 70, -32.5}}
    };

    public WhatOnceWas() {
        super("What Once Was", "WhatOnceWas", new double[]{0.5, 70, 0.5});
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