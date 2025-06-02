package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsStrikeEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.avenger.AvengerStrikeBranch;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AvengersStrike extends AbstractStrike<AvengersStrike, AvengersStrike.AvengersStrikeStats> implements Damages<AvengersStrike.DamageValues> {

    private final AvengersStrikeStats stats = new AvengersStrikeStats();
    private final DamageValues damageValues = new DamageValues();
    private float energySteal = 10;

    public AvengersStrike() {
        super(AbstractAbilityBuilder.create("avengersStrike").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.energySteal = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("energySteal"), float.class);
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "paladin.paladinstrike.activation", 2, 1);
        randomHitEffect(location, 5, 255, 0, 0);
        EffectUtils.displayParticle(Particle.EFFECT, location, 4, (float) ((Math.random() * 2) - 1), (float) ((Math.random() * 2) - 1), (float) ((Math.random() * 2) - 1), 1);
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull WarlordsEntity nearPlayer) {
        float multiplier = 1;
        float healthDamage = 0;
        if (nearPlayer instanceof WarlordsNPC warlordsNPC) {
            if (pveMasterUpgrade) {
                AbstractMob mob = warlordsNPC.getMob();
                if (mob.getLevel() <= 3) {
                    multiplier += 0.4f;
                } else if (mob.getLevel() >= 4) {
                    healthDamage = nearPlayer.getMaxHealth() * 0.01f;
                }
            } else if (pveMasterUpgrade2) {
                int enemiesNearBy = Math.toIntExact(PlayerFilter.entitiesAround(wp, 10, 10, 10).aliveEnemiesOf(wp).stream().count());
                if (enemiesNearBy >= 7) {
                    multiplier += 0.25f;
                } else {
                    multiplier += 0.5f;
                }
            }
        }
        healthDamage = DamageCheck.clamp(healthDamage);
        nearPlayer.addInstance(InstanceBuilder
                .damage()
                .ability(this)
                .source(wp)
                .min((damageValues.strikeDamage.getMinValue() * multiplier) + (pveMasterUpgrade ? healthDamage : 0))
                .max((damageValues.strikeDamage.getMaxValue() * multiplier) + (pveMasterUpgrade ? healthDamage : 0))
                .crit(damageValues.strikeDamage)
        ).ifPresent(finalEvent -> {
            if (pveMasterUpgrade) {
                for (WarlordsEntity we : PlayerFilter.entitiesAround(nearPlayer, 4, 4, 4).aliveEnemiesOf(wp).closestFirst(nearPlayer).excluding(nearPlayer).limit(2)) {
                    float damage = finalEvent.getValue() * 0.75f;
                    if (we instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob().getLevel() >= 4) {
                        damage = DamageCheck.clamp(we.getMaxHealth());
                    }
                    we.addInstance(InstanceBuilder.damage().ability(this).source(wp).value(damage).showAsCrit(finalEvent.isCrit()).flags(InstanceFlags.TRUE_DAMAGE));
                    Bukkit.getPluginManager().callEvent(new WarlordsStrikeEvent(wp, this, we));
                }
            }
        });
        stats.energyStole += nearPlayer.subtractEnergy(name, energySteal, true);
        return true;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public AvengersStrikeStats getAbilityStats() {
        return stats;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Strike the targeted enemy player, causing")
                                               .damage(damageValues.strikeDamage)
                                               .text("damage and removing ")
                                               .energy(energySteal)
                                               .text(".")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new AvengerStrikeBranch(abilityTree, this);
    }

    public float getEnergySteal() {
        return energySteal;
    }

    public void setEnergySteal(float energySteal) {
        this.energySteal = energySteal;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable strikeDamage = new Value.RangedValueCritable(359, 485, 25, 185);

        private List<Value> values = List.of(strikeDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.strikeDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("strikeDamage"), Value.RangedValueCritable.class);
            this.values = List.of(strikeDamage);
        }

        public Value.RangedValueCritable getStrikeDamage() {
            return strikeDamage;
        }

    }

    public static class AvengersStrikeStats extends AbstractStrikeStats<AvengersStrike, AvengersStrikeStats> {

        @Field("energy_stole")
        private float energyStole = 0;

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Energy Removed", Math.round(energyStole)));
            return statsDisplay;
        }

        @Override
        public AvengersStrikeStats merge(AvengersStrikeStats other, int multiplier) {
            AvengersStrikeStats stats = super.merge(other, multiplier);
            stats.energyStole = this.energyStole + other.energyStole * multiplier;
            return stats;
        }

        @Override
        public Class<AvengersStrikeStats> getClazz() {
            return AvengersStrikeStats.class;
        }

        @Override
        public AvengersStrikeStats create() {
            return new AvengersStrikeStats();
        }

        public float getEnergyStole() {
            return energyStole;
        }

        public void setEnergyStole(float energyStole) {
            this.energyStole = energyStole;
        }

    }

}
