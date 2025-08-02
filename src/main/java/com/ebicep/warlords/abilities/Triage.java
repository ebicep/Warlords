package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.events.game.WarlordsFlagUpdatedEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.flags.PlayerFlagLocation;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Triage extends AbstractAbility implements PurpleAbilityIcon, Listener, AbilityStats<Triage, Triage.TriageStats> {

    private final TriageStats stats = new TriageStats();
    private int speedBuffRange;
    private int speedBuff;
    private int speedBuffDurationTicks;
    private int castEnergyCost;
    private float targetBonusHealing;
    private int bonusHealingDurationTicks;
    private final HashMap<Team, WarlordsEntity> lastFlagCarriers = new HashMap<>();

    public Triage() {
        super(AbstractAbilityBuilder.create("triage").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.speedBuffRange = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("speedBuffRange"), int.class);
        this.speedBuff = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("speedBuff"), int.class);
        this.speedBuffDurationTicks = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("speedBuffDurationTicks"), int.class);
        this.castEnergyCost = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("castEnergyCost"), int.class);
        this.targetBonusHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("targetBonusHealing"), float.class);
        this.bonusHealingDurationTicks = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("bonusHealingDurationTicks"), int.class);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Teleport to your most recent flag carrier, consuming energy equal to the blocks travelled. If they are dead or within ")
                .blocks(speedBuffRange)
                .text(", instead gain ")
                .percent(speedBuff, NamedTextColor.WHITE)
                .text(" speed for ")
                .durationTicks(speedBuffDurationTicks)
                .text(" and consume ")
                .energy(castEnergyCost)
                .text(". Increase your healing on them by ")
                .percent(targetBonusHealing, NamedTextColor.GREEN)
                .text(" for ")
                .durationTicks(bonusHealingDurationTicks)
                .text(".")
                .build();
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        WarlordsEntity lastFlagCarrier = lastFlagCarriers.computeIfAbsent(wp.getTeam(), k -> wp);
        boolean teleport = false;
        float distance = 0;
        castEnergyCost = (int) getEnergyCostValue();
        if (!lastFlagCarrier.isDead()) {
            distance = (float) wp.getLocation().distance(lastFlagCarrier.getLocation());
            if (distance > speedBuffRange) {
                teleport = true;
                castEnergyCost = (int) Math.ceil(distance);
            }
        }
        if (wp.getCurrentEnergy() < castEnergyCost) {
            wp.playSound(wp.getLocation(), "notreadyalert", 1, 1);
            wp.sendMessage(Component.text("You do not have enough energy!", NamedTextColor.RED));
            return false;
        }
        if (teleport) {
            Utils.playGlobalSound(wp.getLocation(), "mage.timewarp.activation", 3, 1);
            wp.getLocation().getWorld().spawnParticle(Particle.WITCH, wp.getLocation(), 4, 0.1, 0, 0.1, 0.001, null, true);
            wp.subtractEnergy(name, castEnergyCost, false);
            wp.getEntity().teleport(lastFlagCarrier.getLocation());
            stats.distanceTeleported += distance;
        } else {
            wp.subtractEnergy(name, castEnergyCost, false);
            wp.addSpeedModifier(wp, name, speedBuff, speedBuffDurationTicks);
        }
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "TRIAGE",
                Triage.class,
                null,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                },
                bonusHealingDurationTicks
        ) {
            @Override
            public float modifyHealingFromAttacker(WarlordsDamageHealingEvent event, float currentHealValue) {
                if (event.getWarlordsEntity() == lastFlagCarrier) {
                    stats.healingIncreased += currentHealValue * targetBonusHealing / 100f;
                    return currentHealValue * convertToMultiplicationDecimal(targetBonusHealing);
                }
                return currentHealValue;
            }
        });
        return true;
    }

    @EventHandler
    public void onWarlordsFlagUpdated(WarlordsFlagUpdatedEvent event) {
        if (event.getNew() instanceof PlayerFlagLocation playerFlagLocation) {
            WarlordsEntity player = playerFlagLocation.getPlayer();
            lastFlagCarriers.put(player.getTeam(), player);
        }
    }

    @Override
    public TriageStats getAbilityStats() {
        return stats;
    }

    public static class TriageStats extends AbstractAbilityStats<Triage, TriageStats> {

        @Field("distance_teleported")
        private float distanceTeleported = 0;

        @Field("healing_increased")
        private float healingIncreased = 0;

        @Override
        public Class<TriageStats> getClazz() {
            return TriageStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Distance Teleported", distanceTeleported));
            statsDisplay.add(new AbilityStatDisplay("Healing Increased", healingIncreased));
            return statsDisplay;
        }

        @Override
        public TriageStats merge(TriageStats other, int multiplier) {
            TriageStats stats = super.merge(other, multiplier);
            stats.distanceTeleported = this.distanceTeleported + other.distanceTeleported * multiplier;
            stats.healingIncreased = this.healingIncreased + other.healingIncreased * multiplier;
            return stats;
        }

        @Override
        public TriageStats create() {
            return new TriageStats();
        }

    }
}
