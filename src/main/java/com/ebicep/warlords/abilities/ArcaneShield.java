package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.ArcaneShieldBranch;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArcaneShield extends AbstractAbility implements BlueAbilityIcon, Duration, AbilityStats<ArcaneShield, ArcaneShield.ArcaneShieldStats> {

    private final ArcaneShieldStats stats = new ArcaneShieldStats();
    private int maxShieldHealth;
    private int shieldPercentage = 50;
    private int tickDuration = 120;

    public ArcaneShield() {
        super(AbstractAbilityBuilder.create("Arcane Shield").pvp());
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Surround yourself with arcane energy, creating a shield that will absorb up to ")
                .percent(shieldPercentage, AbilityDescriptionBuilder.COLOR_BROWN)
                .text(" of your maximum health. Lasts ")
                .durationTicks(tickDuration)
                .text(".")
                .build();
    }

    @Override
    public ArcaneShieldStats getAbilityStats() {
        return stats;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        Utils.playGlobalSound(wp.getLocation(), "mage.arcaneshield.activation", 2, 1);

        Shield shield = new Shield(name, maxShieldHealth);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "ARCA",
                Shield.class,
                shield,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (pveMasterUpgrade) {
                        Utils.playGlobalSound(wp.getLocation(), "mage.arcaneshield.activation", 2, 0.5f);
                        EffectUtils.strikeLightning(wp.getLocation(), false);
                        for (WarlordsNPC we : PlayerFilterGeneric
                                .entitiesAround(wp, 6, 6, 6)
                                .aliveEnemiesOf(wp)
                                .closestFirst(wp)
                                .warlordsNPCs()
                        ) {
                            we.setStunTicks(6 * 20);
                        }
                    } else if (pveMasterUpgrade2) {
                        List<AbstractAbility> abilities = wp.getAbilities();
                        if (abilities.isEmpty()) {
                            return;
                        }
                        AbstractAbility rightClick = abilities.get(0);
                        FloatModifiable.FloatModifier modifier = rightClick.getEnergyCost().addMultiplicativeModifierAdd("Arcane Energy", -.25f);
                        wp.updateItem(rightClick);
                        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Arcane Energy",
                                "ARC",
                                ArcaneShield.class,
                                new ArcaneShield(),
                                wp,
                                CooldownTypes.ABILITY,
                                cooldownManager2 -> {
                                    modifier.forceEnd();
                                    wp.updateItem(rightClick);
                                },
                                100,
                                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                    if (ticksElapsed % 3 == 0) {
                                        EffectUtils.displayParticle(
                                                Particle.ELECTRIC_SPARK,
                                                wp.getLocation().add(0, 1, 0),
                                                10,
                                                .4,
                                                .4,
                                                .4,
                                                0
                                        );
                                    }
                                })
                        ));
                    }
                },
                cooldownManager -> {
                    if (shield.isBroken()) {
                        stats.timesBroken++;
                    }
                    stats.totalAbsorbed += shield.getMaxShieldHealth() - Math.max(0, shield.getShieldHealth());
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        Location location = wp.getLocation();
                        location.add(0, 1.5, 0);
                        EffectUtils.displayParticle(Particle.CLOUD, location, 2, 0.15, 0.3, 0.15, 0.01);
                        EffectUtils.displayParticle(Particle.FIREWORK, location, 1, 0.3, 0.3, 0.3, 0.0001);
                        EffectUtils.displayParticle(Particle.WITCH, location, 1, 0.3, 0.3, 0.3, 0);
                    }
                })
        ) {
            @Override
            public void onShieldFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                event.getWarlordsEntity().getCooldownManager().queueUpdatePlayerNames();
            }

            @Override
            public PlayerNameData addPrefixFromOther() {
                return new PlayerNameData(
                        Component.text((int) (shield.getShieldHealth()), NamedTextColor.YELLOW),
                        we -> we.isTeammate(wp)
                );
            }
        });

        return true;
    }

    public static class ArcaneShieldStats extends AbstractAbilityStats<ArcaneShield, ArcaneShieldStats> {

        @Field("times_broken")
        private int timesBroken = 0;
        @Field("total_absorbed")
        private float totalAbsorbed = 0;

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Times Broken", timesBroken));
            statsDisplay.add(new AbilityStatDisplay("Total Absorbed", totalAbsorbed));
            return statsDisplay;
        }

        @Override
        public ArcaneShieldStats merge(ArcaneShieldStats other, int multiplier) {
            ArcaneShieldStats stats = super.merge(other, multiplier);
            stats.timesBroken = this.timesBroken + other.timesBroken * multiplier;
            stats.totalAbsorbed = this.totalAbsorbed + other.totalAbsorbed * multiplier;
            return stats;
        }

        @Override
        public Class<ArcaneShieldStats> getClazz() {
            return ArcaneShieldStats.class;
        }

        @Override
        public ArcaneShieldStats create() {
            return new ArcaneShieldStats();
        }
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new ArcaneShieldBranch(abilityTree, this);
    }

    @Override
    public void updateCustomStats(WarlordsEntity warlordsEntity) {
        super.updateCustomStats(warlordsEntity);
        if (warlordsEntity != null) {
            setMaxShieldHealth((int) (warlordsEntity.getMaxHealth() * (getShieldPercentage() / 100f)));
            updateDescription(null);
        }
    }

    public void setMaxShieldHealth(int maxShieldHealth) {
        this.maxShieldHealth = maxShieldHealth;
    }

    public int getShieldPercentage() {
        return shieldPercentage;
    }

    public void setShieldPercentage(int shieldPercentage) {
        this.shieldPercentage = shieldPercentage;
    }


}
