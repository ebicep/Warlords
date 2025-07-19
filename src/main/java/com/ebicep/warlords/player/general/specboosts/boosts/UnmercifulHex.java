package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.MercifulHex;
import com.ebicep.warlords.abilities.RayOfLight;
import com.ebicep.warlords.abilities.SanctifiedBeacon;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class UnmercifulHex implements SpecBoostManager.SpecBoost<UnmercifulHex> {

    @Override
    public void init() {
    }

    @Override
    public String getConfigFieldName() {
        return "unmercifulHex";
    }

    @Override
    public List<Object> getVariables() {
        return List.of();
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public UnmercifulHex get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(MercifulHex.class).forEach(mercifulHex -> {
                mercifulHex.setMaxEnemiesHit(Integer.MAX_VALUE);
                mercifulHex.setHexStacksPerHitAfter(0);
            });
            warlordsPlayer.getAbilitiesMatching(RayOfLight.class).forEach(rayOfLight -> {
                rayOfLight.setRemoveDebuffs(false);
            });
            warlordsPlayer.getAbilitiesMatching(SanctifiedBeacon.class).forEach(sanctifiedBeacon -> {
                sanctifiedBeacon.setStacksGranted(0);
            });
        }

    }

}
