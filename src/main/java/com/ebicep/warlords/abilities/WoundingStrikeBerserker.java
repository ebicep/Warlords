package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractStrike;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.berserker.WoundingStrikeBranchBerserker;
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

public class WoundingStrikeBerserker extends AbstractStrike implements Damages<WoundingStrikeBerserker.DamageValues> {

    private final DamageValues damageValues = new DamageValues();
    private int woundingTickDuration = 60;

    public WoundingStrikeBerserker() {
        super("Wounding Strike", 0, 100);
    }

    public WoundingStrikeBerserker(String name, float cooldown, float energyCost) {
        super(name, cooldown, energyCost);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Strike the targeted enemy player, causing ")
                               .append(Damages.formatDamage(damageValues.strikeDamage))
                               .append(Component.text(" damage and "))
                               .append(Component.text("wounding", NamedTextColor.RED))
                               .append(Component.text(" them for "))
                               .append(Component.text(format(woundingTickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. A wounded player receives "))
                               .append(Component.text("40%", NamedTextColor.RED))
                               .append(Component.text(" less healing for the duration of the effect."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Players Struck", "" + timesUsed));

        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new WoundingStrikeBranchBerserker(abilityTree, this);
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "warrior.mortalstrike.impact", 2, 1);
        randomHitEffect(location, 7, 255, 0, 0);
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull WarlordsEntity nearPlayer) {
        float lustDamageBoost = wp.getCooldownManager().hasCooldown(BloodLust.class) ? pveMasterUpgrade ? 2 : pveMasterUpgrade2 ? 1.3f : 1 : 1;
        nearPlayer.addInstance(InstanceBuilder
                .damage()
                .ability(this)
                .source(wp)
                .min(damageValues.strikeDamage.getMinValue() * lustDamageBoost)
                .max(damageValues.strikeDamage.getMaxValue() * lustDamageBoost)
                .crit(damageValues.strikeDamage)
        ).ifPresent(finalEvent -> onFinalEvent(wp, nearPlayer, finalEvent));

        if (pveMasterUpgrade2) {
            additionalHit(1, wp, nearPlayer, warlordsEntity -> {
                warlordsEntity.addInstance(InstanceBuilder
                        .damage()
                        .ability(this)
                        .source(wp)
                        .min(damageValues.strikeDamage.getMinValue() * lustDamageBoost)
                        .max(damageValues.strikeDamage.getMaxValue() * lustDamageBoost)
                        .crit(damageValues.strikeDamage)
                ).ifPresent(finalEvent -> onFinalEvent(wp, finalEvent.getWarlordsEntity(), finalEvent));
            });
        }

        return true;
    }

    private void onFinalEvent(@Nonnull WarlordsEntity wp, @Nonnull WarlordsEntity nearPlayer, WarlordsDamageHealingFinalEvent finalEvent) {
        if (finalEvent.isDead()) {
            return;
        }
        if (pveMasterUpgrade) {
            bleedOnHit(wp, nearPlayer);
            return;
        }
        if (!(nearPlayer.getCooldownManager().hasCooldownFromName("Wounding Strike"))) {
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
                woundingTickDuration
        ) {
            @Override
            public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                return currentHealValue * .6f;
            }

            @Override
            public PlayerNameData addSuffixFromOther() {
                return new PlayerNameData(Component.text("WND", NamedTextColor.RED),
                        we -> we == wp || (we.isTeammate(nearPlayer) && we.getSpecClass().specType == SpecType.HEALER)
                );
            }
        });
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
                woundingTickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksLeft % 20 == 0) {
                        float healthDamage = hit.getMaxHealth() * 0.005f;
                        healthDamage = DamageCheck.clamp(healthDamage);
                        hit.addInstance(InstanceBuilder
                                .damage()
                                .cause("Bleed")
                                .source(giver)
                                .value(healthDamage)
                                .flags(InstanceFlags.DOT)
                        );
                    }
                })
        ) {
            @Override
            public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                return currentHealValue * .2f;
            }
        });
    }

    public int getWoundingTickDuration() {
        return woundingTickDuration;
    }

    public void setWoundingTickDuration(int woundingTickDuration) {
        this.woundingTickDuration = woundingTickDuration;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable strikeDamage = new Value.RangedValueCritable(497, 632, 20, 175);
        private final List<Value> values = List.of(strikeDamage);

        public Value.RangedValueCritable getStrikeDamage() {
            return strikeDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

}