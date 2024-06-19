package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractSeismicWave;
import com.ebicep.warlords.abilities.internal.CanReduceCooldowns;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.defender.SeismicWaveBranchDefender;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class SeismicWaveDefender extends AbstractSeismicWave implements CanReduceCooldowns {

    public SeismicWaveDefender() {
        super(506, 685, 11.74f, 60, 25, 200);
    }

    @Override
    protected void onHit(@Nonnull WarlordsEntity wp, UUID abilityUUID, List<WarlordsEntity> playersHit, int i, WarlordsEntity waveTarget) {
        float multiplier = 1;
        if (pveMasterUpgrade) {
            multiplier = (1.5f / 15f) * Math.min(i + 1, 15) + 1;
        } else if (pveMasterUpgrade2) {
            multiplier = waveTarget.getCooldownManager().hasCooldownFromName("Wounding Strike") ? 1.3f : 1;
        }
        waveTarget.addInstance(InstanceBuilder
                .damage()
                .ability(this)
                .source(wp)
                .min(damageValues.waveDamage.getMinValue() * multiplier)
                .max(damageValues.waveDamage.getMaxValue() * multiplier)
                .crit(damageValues.waveDamage)
                .uuid(abilityUUID)
        ).ifPresent(event -> {
            if (event.isDead() && pveMasterUpgrade2) {
                wp.getAbilitiesMatching(LastStand.class).forEach(lastStand -> lastStand.subtractCurrentCooldown(1f));
            }
        });
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SeismicWaveBranchDefender(abilityTree, this);
    }

    @Override
    public boolean canReduceCooldowns() {
        return pveMasterUpgrade2;
    }

    private final DamageValues damageValues = new DamageValues();

    public DamageValues getDamageValues() {
        return damageValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable waveDamage = new Value.RangedValueCritable(506, 685, 25, 200);
        private final List<Value> values = List.of(waveDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}
