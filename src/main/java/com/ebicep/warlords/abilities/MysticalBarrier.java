package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.sentinel.MysticalBarrierBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MysticalBarrier extends AbstractAbility implements BlueAbilityIcon, Duration {

    private float runeTimerIncrease = 0.25f;
    private int tickDuration = 100;
    private float meleeDamageReduction = 50;
    private int shieldBase = 400;
    private int shieldIncrease = 80;
    private int shieldMaxHealth = 1200;
    private int reactivateTickDuration = 100;

    public MysticalBarrier() {
        super("Mystical Barrier", 0, 0, 28, 20, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Surround yourself with magical spirits that reduce all melee damage you take by")
                               .append(Component.text(format(meleeDamageReduction) + "%", NamedTextColor.YELLOW))
                               .append(Component.text("and increase the attacker’s cooldowns by "))
                               .append(Component.text(formatHundredths(runeTimerIncrease), NamedTextColor.GOLD))
                               .append(Component.text(" seconds for every instance of damage they deal to you for "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds.\n\nReactivate the ability to grant the nearest ally a shield equal to"))
                               .append(Component.text(shieldBase, NamedTextColor.YELLOW))
                               .append(Component.text(" + "))
                               .append(Component.text(shieldIncrease, NamedTextColor.YELLOW))
                               .append(Component.text(" for each instance of damage you took, up to a maximum of "))
                               .append(Component.text(shieldMaxHealth, NamedTextColor.YELLOW))
                               .append(Component.text(" health, that lasts "))
                               .append(Component.text(format(reactivateTickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds, as well as "))
                               .append(Component.text("3", NamedTextColor.BLUE)
                                                .append(Component.text(" stacks of Fortifying Hex."))
                                                .append(Component.text("\n\nNot reactivating the ability will instead grant you the shield for the same duration.")));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        AtomicInteger damageInstances = new AtomicInteger();
        Utils.playGlobalSound(wp.getLocation(), Sound.ITEM_ARMOR_EQUIP_DIAMOND, 2, 0.4f);
        Utils.playGlobalSound(wp.getLocation(), "arcanist.mysticalbarrier.activation", 2, 1);
        RegularCooldown<MysticalBarrier> mysticalBarrierCooldown = new RegularCooldown<>(
                name,
                "MYSTIC",
                MysticalBarrier.class,
                new MysticalBarrier(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (!wp.isAlive()) {
                        return;
                    }
                    Utils.playGlobalSound(wp.getLocation(), "arcanist.mysticalbarrier.giveshield", 2, 1.75f);
                    int shieldHealth = Math.min(shieldMaxHealth, shieldBase + shieldIncrease * damageInstances.get());
                    giveShield(wp, wp, shieldHealth);
                    for (int i = 0; i < 3; i++) {
                        FortifyingHex.giveFortifyingHex(wp, wp);
                    }
                    if (pveMasterUpgrade2) {
                        subtractCurrentCooldown(cooldown.getBaseValue() * .35f);
                    }
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    EffectUtils.playCircularEffectAround(
                            wp.getGame(),
                            wp.getLocation(),
                            Particle.TOTEM,
                            3,
                            1,
                            0.15,
                            2.2,
                            8,
                            1,
                            4,
                            ticksElapsed
                    );
                })
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return event.getAbility().isEmpty() ? currentDamageValue * convertToDivisionDecimal(meleeDamageReduction) : currentDamageValue;
            }

            @Override
            public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                event.getAttacker().getSpec().increaseAllCooldownTimersBy(runeTimerIncrease);
                damageInstances.getAndIncrement();
            }
        };
        wp.getCooldownManager().addCooldown(mysticalBarrierCooldown);

        addSecondaryAbility(
                5,
                () -> {
                    wp.getCooldownManager().removeCooldown(mysticalBarrierCooldown);
                    int shieldHealth = Math.min(shieldMaxHealth, shieldBase + shieldIncrease * damageInstances.get());
                    if (pveMasterUpgrade2) {
                        PlayerFilter.entitiesAround(wp, 10, 10, 10)
                                    .aliveTeammatesOf(wp)
                                    .forEach(ally -> {
                                        giveShieldAlly(wp, ally, damageInstances.get());
                                    });
                    } else {
                        PlayerFilter.playingGame(wp.getGame())
                                    .aliveTeammatesOfExcludingSelf(wp)
                                    .closestFirst(wp)
                                    .limit(1)
                                    .forEach(ally -> {
                                        giveShieldAlly(wp, ally, shieldHealth);
                                    });
                    }
                },
                false,
                secondaryAbility -> !wp.getCooldownManager().hasCooldown(mysticalBarrierCooldown)
        );
        return true;
    }

    private void giveShield(WarlordsEntity from, @Nonnull WarlordsEntity to, int shieldHealth) {
        to.getCooldownManager().addCooldown(new RegularCooldown<>(
                name + " Shield",
                "SHIELD",
                Shield.class,
                new Shield(name, shieldHealth),
                from,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    EffectUtils.displayParticle(
                            Particle.FIREWORKS_SPARK,
                            to.getLocation().add(0, 1.5, 0),
                            1,
                            0.1,
                            0.05,
                            0.1,
                            0
                    );
                })
        ));
    }

    private void giveShieldAlly(@Nonnull WarlordsEntity wp, WarlordsEntity ally, int shieldHealth) {
        EffectUtils.playParticleLinkAnimation(wp.getLocation(), ally.getLocation(), 0, 180, 180, 2);
        Utils.playGlobalSound(wp.getLocation(), "arcanist.mysticalbarrier.giveshield", 2, 1.75f);

        giveShield(wp, ally, shieldHealth);

        boolean isSelf = wp.equals(ally);
        wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                .append(Component.text(" Your Mystical Barrier is now protecting ", NamedTextColor.GRAY))
                .append(Component.text(isSelf ? "yourself" : ally.getName(), NamedTextColor.YELLOW))
                .append(Component.text("!", NamedTextColor.GRAY))
        );
        if (!isSelf) {
            ally.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN
                    .append(Component.text(" " + wp.getName() + "'s ", NamedTextColor.GRAY))
                    .append(Component.text("Mystical Barrier", NamedTextColor.YELLOW))
                    .append(Component.text(" is now protecting you for ", NamedTextColor.GRAY))
                    .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                    .append(Component.text(" seconds!", NamedTextColor.GRAY))
            );
        }

        for (int i = 0; i < 3; i++) {
            FortifyingHex.giveFortifyingHex(wp, ally);
        }
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new MysticalBarrierBranch(abilityTree, this);
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public float getRuneTimerIncrease() {
        return runeTimerIncrease;
    }

    public void setRuneTimerIncrease(float runeTimerIncrease) {
        this.runeTimerIncrease = runeTimerIncrease;
    }

    public int getShieldMaxHealth() {
        return shieldMaxHealth;
    }

    public void setShieldMaxHealth(int shieldMaxHealth) {
        this.shieldMaxHealth = shieldMaxHealth;
    }

    public int getShieldIncrease() {
        return shieldIncrease;
    }

    public void setShieldIncrease(int shieldIncrease) {
        this.shieldIncrease = shieldIncrease;
    }

    public int getReactivateTickDuration() {
        return reactivateTickDuration;
    }

    public void setReactivateTickDuration(int reactivateTickDuration) {
        this.reactivateTickDuration = reactivateTickDuration;
    }

    public float getMeleeDamageReduction() {
        return meleeDamageReduction;
    }

    public void setMeleeDamageReduction(float meleeDamageReduction) {
        this.meleeDamageReduction = meleeDamageReduction;
    }
}
