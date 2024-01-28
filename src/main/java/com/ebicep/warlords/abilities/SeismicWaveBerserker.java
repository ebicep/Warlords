package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractSeismicWave;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.berserker.SeismicWaveBranchBerserker;
import com.ebicep.warlords.util.warlords.GameRunnable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class SeismicWaveBerserker extends AbstractSeismicWave {

    public SeismicWaveBerserker() {
        super(557, 753, 11.74f, 60, 25, 200);
    }

    @Override
    protected void onHit(@Nonnull WarlordsEntity wp, UUID abilityUUID, List<WarlordsEntity> playersHit, int i, WarlordsEntity waveTarget) {
        float multiplier = 1;
        if (pveMasterUpgrade) {
            multiplier = (1.5f / 15f) * Math.min(i + 1, 15) + 1;
        } else if (pveMasterUpgrade2) {
            multiplier = waveTarget.getCooldownManager().hasCooldownFromName("Wounding Strike") ? 1.3f : 1;
            if (waveTarget instanceof WarlordsNPC warlordsNPC) {
                new GameRunnable(wp.getGame()) {
                    @Override
                    public void run() {
                        if (warlordsNPC.getEntity().isOnGround()) {
                            warlordsNPC.setStunTicks(20);
                            this.cancel();
                        }
                    }
                }.runTaskTimer(5, 0);
            }
        }
        waveTarget.addDamageInstance(wp, name, minDamageHeal * multiplier, maxDamageHeal * multiplier, critChance, critMultiplier, abilityUUID);
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SeismicWaveBranchBerserker(abilityTree, this);
    }

}
