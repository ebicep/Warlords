package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.abilities.internal.AbstractSeismicWave;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.berserker.SeismicWaveBranchBerserker;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SeismicWaveBerserker extends AbstractSeismicWave implements Damages<SeismicWaveBerserker.DamageValues> {

    private final DamageValues damageValues = new DamageValues();

    public SeismicWaveBerserker() {
        super(AbstractAbilityBuilder.create("seismicWaveBerserker").pvp());
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    protected void doWaveDamage(@Nonnull WarlordsEntity wp, List<List<Location>> fallingBlockLocations, UUID abilityUUID) {
        super.doWaveDamage(wp, fallingBlockLocations, abilityUUID);
        if (!pveMasterUpgrade2) {
            return;
        }
        ArrayList<List<Location>> locations = new ArrayList<>(fallingBlockLocations);
        new GameRunnable(wp.getGame()) {

            int secondsElapsed = 0;

            @Override
            public void run() {
                UUID uuid = UUID.randomUUID();
                List<WarlordsEntity> playersHit = new ArrayList<>();
                for (List<Location> fallingBlockLocation : locations) {
                    for (Location loc : fallingBlockLocation) {
                        for (WarlordsEntity waveTarget : PlayerFilter.entitiesAroundRectangle(loc, .6, 4, .6).aliveEnemiesOf(wp).excluding(playersHit).closestFirst(wp)) {
                            playersHit.add(waveTarget);
                            waveTarget.addInstance(InstanceBuilder.damage()
                                                                  .ability(SeismicWaveBerserker.this)
                                                                  .source(wp)
                                                                  .min(723)
                                                                  .max(906)
                                                                  .crit(damageValues.waveDamage)
                                                                  .uuid(uuid));
                        }
                    }
                }
                secondsElapsed++;
                if (secondsElapsed >= 4) {
                    this.cancel();
                }
            }
        }.runTaskTimer(20, 20);
    }

    @Override
    protected void onHit(@Nonnull WarlordsEntity wp, UUID abilityUUID, int i, WarlordsEntity waveTarget) {
        float multiplier = 1;
        if (pveMasterUpgrade) {
            multiplier = (1.5f / 15f) * Math.min(i + 1, 15) + 1;
        }
        waveTarget.addInstance(InstanceBuilder.damage()
                                              .ability(this)
                                              .source(wp)
                                              .min(damageValues.waveDamage.getMinValue() * multiplier)
                                              .max(damageValues.waveDamage.getMaxValue() * multiplier)
                                              .crit(damageValues.waveDamage)
                                              .uuid(abilityUUID));
    }

    @Override
    public Value.RangedValueCritable getWaveDamage() {
        return damageValues.waveDamage;
    }

    @Override
    protected void init(AbstractAbilityBuilder builder) {
        super.init(builder);
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SeismicWaveBranchBerserker(abilityTree, this);
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable waveDamage = new Value.RangedValueCritable(557, 753, 25, 200);

        private final List<Value> values = List.of(waveDamage);

        public Value.RangedValueCritable getWaveDamage() {
            return waveDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.waveDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("waveDamage"), Value.RangedValueCritable.class);
        }

    }

}
