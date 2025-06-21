package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.defender.SeismicWaveBranchDefender;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class SeismicWaveDefender extends AbstractSeismicWave implements CanReduceCooldowns, Damages<SeismicWaveDefender.DamageValues> {

    private final DamageValues damageValues = new DamageValues();

    public SeismicWaveDefender() {
        super(AbstractAbilityBuilder.create("seismicWaveDefender").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
    }

    @Override
    protected void onHit(@Nonnull WarlordsEntity wp, UUID abilityUUID, int i, WarlordsEntity waveTarget) {
        float multiplier = 1;
        if (pveMasterUpgrade) {
            multiplier = (1.5f / 15f) * Math.min(i + 1, 15) + 1;
        } else if (pveMasterUpgrade2) {
            multiplier = waveTarget.getCooldownManager().hasCooldown(WoundingCooldown.WoundingData.class) ? 1.3f : 1;
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
            onHitFinalEvent(wp, waveTarget);
            if (event.isDead() && pveMasterUpgrade2) {
                wp.getAbilitiesMatching(LastStand.class).forEach(lastStand -> lastStand.subtractCurrentCooldown(1f));
            }
        });
    }

    @Override
    public Value.RangedValueCritable getWaveDamage() {
        return damageValues.waveDamage;
    }

    @Override
    public boolean canReduceCooldowns() {
        return pveMasterUpgrade2;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SeismicWaveBranchDefender(abilityTree, this);
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable waveDamage = new Value.RangedValueCritable(455, 616, 25, 200);

        private List<Value> values = List.of(waveDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.waveDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("waveDamage"), Value.RangedValueCritable.class);
            this.values = List.of(waveDamage);
        }

    }

}
