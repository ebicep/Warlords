package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.LinkedCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class WoundingStrikeDefender extends AbstractStrikeBase {

    private int wounding = 25;

    public WoundingStrikeDefender() {
        super("Wounding Strike", 415.8f, 556.5f, 0, 100, 20, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Strike the targeted enemy player, causing" + formatRangeDamage(minDamageHeal, maxDamageHeal) +
                "damage and §cwounding §7them for §63 §7seconds. A wounded player receives §c" + wounding +
                "% §7less healing for the duration of the effect.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Players Stuck", "" + timesUsed));

        return info;
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "warrior.mortalstrike.impact", 2, 1);
        randomHitEffect(location, 7, 255, 0, 0);
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull Player player, @Nonnull WarlordsEntity nearPlayer) {
        Optional<WarlordsDamageHealingFinalEvent> finalEvent = nearPlayer.addDamageInstance(
                wp,
                name,
                minDamageHeal,
                maxDamageHeal,
                critChance,
                critMultiplier,
                false
        );

        finalEvent.ifPresent(event -> {
            if (event.isCrit() && pveUpgrade) {
                damageReductionOnCrit(wp, nearPlayer);
            }
        });

        if (!(nearPlayer.getCooldownManager().hasCooldown(WoundingStrikeBerserker.class) || nearPlayer.getCooldownManager()
                .hasCooldown(WoundingStrikeDefender.class))) {
            nearPlayer.sendMessage(ChatColor.GRAY + "You are " + ChatColor.RED + "wounded" + ChatColor.GRAY + ".");
        }
        if (!nearPlayer.getCooldownManager().hasCooldown(WoundingStrikeBerserker.class)) {
            nearPlayer.getCooldownManager().removeCooldown(WoundingStrikeDefender.class, true);
            nearPlayer.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name,
                    "WND",
                    WoundingStrikeDefender.class,
                    new WoundingStrikeDefender(),
                    wp,
                    CooldownTypes.DEBUFF,
                    cooldownManager -> {
                    },
                    cooldownManager -> {
                        if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterNameActionBar("WND").stream().count() == 1) {
                            nearPlayer.sendMessage(ChatColor.GRAY + "You are no longer " + ChatColor.RED + "wounded" + ChatColor.GRAY + ".");
                        }
                    },
                    3 * 20
            ) {
                @Override
                public float doBeforeHealFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                    return currentHealValue * (100 - wounding) / 100f;
                }
            });
        }

        return true;
    }

    private void damageReductionOnCrit(WarlordsEntity we, WarlordsEntity nearPlayer) {
        Set<WarlordsEntity> teammates = PlayerFilter
                .entitiesAround(nearPlayer, 6, 6, 6)
                .aliveTeammatesOfExcludingSelf(we)
                .stream()
                .collect(Collectors.toSet());
        LinkedCooldown<?> linkedCooldown = new LinkedCooldown<>(
                name + " Resistance",
                "STRIKE RES",
                WoundingStrikeDefender.class,
                new WoundingStrikeDefender(),
                we,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                cooldownManager -> {
                },
                5 * 20,
                Collections.emptyList(),
                teammates
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * 0.7f;
            }
        };

        we.getCooldownManager().removeCooldownByName(name + " Resistance");
        we.getCooldownManager().addCooldown(linkedCooldown);
        for (WarlordsEntity teammate : teammates) {
            teammate.getCooldownManager().removeCooldownByName(name + " Resistance");
            teammate.getCooldownManager().addCooldown(linkedCooldown);
        }
    }

    public int getWounding() {
        return wounding;
    }

    public void setWounding(int wounding) {
        this.wounding = wounding;
    }
}