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
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Triage extends AbstractAbility implements PurpleAbilityIcon, Listener, AbilityStats<Triage, Triage.TriageStats> {

    private final TriageStats stats = new TriageStats();
    private int speedBuff;
    private int speedBuffDurationTicks;
    private float targetBonusHealing;
    private int bonusHealingDurationTicks;
    private final HashMap<Team, WarlordsEntity> lastFlagCarriers = new HashMap<>();

    public Triage() {
        super(AbstractAbilityBuilder.create("triage").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.speedBuff = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("speedBuff"), int.class);
        this.speedBuffDurationTicks = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("speedBuffDurationTicks"), int.class);
        this.targetBonusHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("targetBonusHealing"), float.class);
        this.bonusHealingDurationTicks = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("bonusHealingDurationTicks"), int.class);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Teleport to your most recent flag carrier, if it is yourself or they are dead, instead gain ")
                .percent(speedBuff, NamedTextColor.WHITE)
                .text(" speed for ")
                .durationTicks(speedBuffDurationTicks)
                .text(". Your most recent flag carrier receives ")
                .percent(targetBonusHealing, NamedTextColor.GREEN)
                .text(" bonus healing from you for ")
                .durationTicks(bonusHealingDurationTicks)
                .text(".")
                .build();
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        WarlordsEntity lastFlagCarrier = lastFlagCarriers.computeIfAbsent(wp.getTeam(), k -> wp);
        if (lastFlagCarrier.isDead() || lastFlagCarrier == wp) {
            wp.addSpeedModifier(wp, name, speedBuff, speedBuffDurationTicks);
        } else {
            Utils.playGlobalSound(wp.getLocation(), "mage.timewarp.activation", 3, 1);
            wp.getLocation().getWorld().spawnParticle(Particle.WITCH, wp.getLocation(), 4, 0.1, 0, 0.1, 0.001, null, true);
            wp.getEntity().teleport(lastFlagCarrier.getLocation());
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

        @Override
        public Class<TriageStats> getClazz() {
            return TriageStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public TriageStats merge(TriageStats other, int multiplier) {
            TriageStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public TriageStats create() {
            return new TriageStats();
        }

    }
}
