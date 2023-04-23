package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.abilties.internal.DamageCheck;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WoundingStrikeBerserker extends AbstractStrikeBase {

    private int woundingDuration = 3;

    public WoundingStrikeBerserker() {
        super("Wounding Strike", 497, 632, 0, 100, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Strike the targeted enemy player, causing" + formatRangeDamage(minDamageHeal, maxDamageHeal) +
                "damage and §cwounding §7them for §6" + woundingDuration +
                " §7seconds. A wounded player receives §c40% §7less healing for the duration of the effect.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Players Struck", "" + timesUsed));

        return info;
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "warrior.mortalstrike.impact", 2, 1);
        randomHitEffect(location, 7, 255, 0, 0);
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull Player player, @Nonnull WarlordsEntity nearPlayer) {
        boolean lustDamageBoost = pveUpgrade && wp.getCooldownManager().hasCooldown(BloodLust.class);
        nearPlayer.addDamageInstance(
                wp,
                name,
                minDamageHeal * (lustDamageBoost ? 1.8f : 1),
                maxDamageHeal * (lustDamageBoost ? 1.8f : 1),
                critChance,
                critMultiplier,
                false
        );
//        if(finalEvent.isPresent()) {
        if (pveUpgrade) {
            bleedOnHit(wp, nearPlayer);
        } else {
            if (!(nearPlayer.getCooldownManager().hasCooldown(WoundingStrikeBerserker.class) || nearPlayer.getCooldownManager()
                                                                                                          .hasCooldown(WoundingStrikeDefender.class))) {
                nearPlayer.sendMessage(
                        Component.text("You are ", NamedTextColor.GRAY)
                                 .append(Component.text("wounded", NamedTextColor.RED))
                                 .append(Component.text(".", NamedTextColor.GRAY))
                );
            }
            nearPlayer.getCooldownManager().removePreviousWounding();
            nearPlayer.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name,
                    "WND",
                    WoundingStrikeBerserker.class,
                    new WoundingStrikeBerserker(),
                    wp,
                    CooldownTypes.DEBUFF,
                    cooldownManager -> {
                    },
                    cooldownManager -> {
                        if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterNameActionBar("WND").stream().count() == 1) {
                            nearPlayer.sendMessage(
                                    Component.text("You are no longer ", NamedTextColor.GRAY)
                                             .append(Component.text("wounded", NamedTextColor.RED))
                                             .append(Component.text(".", NamedTextColor.GRAY))
                            );
                        }
                    },
                    woundingDuration * 20
            ) {
                @Override
                public float doBeforeHealFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                    return currentHealValue * .6f;
                }
            });
        }
        return true;
    }

    private void bleedOnHit(WarlordsEntity giver, WarlordsEntity hit) {
        hit.getCooldownManager().removePreviousWounding();
        hit.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Bleed",
                "BLEED",
                WoundingStrikeBerserker.class,
                new WoundingStrikeBerserker(),
                giver,
                CooldownTypes.DEBUFF,
                cooldownManager -> {
                },
                woundingDuration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksLeft % 20 == 0) {
                        float healthDamage = hit.getMaxHealth() * 0.005f;
                        if (healthDamage < DamageCheck.MINIMUM_DAMAGE) {
                            healthDamage = DamageCheck.MINIMUM_DAMAGE;
                        }
                        if (healthDamage > DamageCheck.MAXIMUM_DAMAGE) {
                            healthDamage = DamageCheck.MAXIMUM_DAMAGE;
                        }
                        hit.addDamageInstance(
                                giver,
                                "Bleed",
                                healthDamage,
                                healthDamage,
                                0,
                                100,
                                false
                        );
                    }
                })
        ) {
            @Override
            public float doBeforeHealFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                return currentHealValue * .2f;
            }
        });
    }

    public int getWoundingDuration() {
        return woundingDuration;
    }

    public void setWoundingDuration(int woundingDuration) {
        this.woundingDuration = woundingDuration;
    }


}