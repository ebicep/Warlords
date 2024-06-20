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
    private int radius = 10;
    private int shieldBase = 400;
    private int shieldIncrease = 80;
    private int shieldMaxHealth = 1200;
    private int reactivateTickDuration = 100;

    public MysticalBarrier() {
        super("Mystical Barrier", 28, 20);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Grant the target ally ")
                               .append(Component.text("3", NamedTextColor.BLUE))
                               .append(Component.text(" stacks of Fortifying Hex and the protection of magical spirits that reduce all melee damage taken by")
                                                .append(Component.text(format(meleeDamageReduction) + "%", NamedTextColor.YELLOW))
                                                .append(Component.text("and increase the attacker’s cooldowns by "))
                                                .append(Component.text(formatHundredths(runeTimerIncrease), NamedTextColor.GOLD))
                                                .append(Component.text(" seconds for every instance of damage they deal to the target.\n\nAfter "))
                                                .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                                                .append(Component.text(" seconds the spirits transform into a shield equal to"))
                                                .append(Component.text(shieldBase, NamedTextColor.YELLOW))
                                                .append(Component.text(" + "))
                                                .append(Component.text(shieldIncrease, NamedTextColor.YELLOW))
                                                .append(Component.text(" for each instance of damage taken, up to a maximum of "))
                                                .append(Component.text(shieldMaxHealth, NamedTextColor.YELLOW))
                                                .append(Component.text(" health, that lasts "))
                                                .append(Component.text(format(reactivateTickDuration / 20f), NamedTextColor.GOLD))
                                                .append(Component.text(" seconds.\n\nIf no ally is targeted, receive all the effects yourself.")));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), Sound.ITEM_ARMOR_EQUIP_DIAMOND, 2, 0.4f);
        Utils.playGlobalSound(wp.getLocation(), "arcanist.mysticalbarrier.activation", 2, 1);

        if (pveMasterUpgrade2) {
            giveBarrier(wp, wp);
            List<WarlordsEntity> targets = PlayerFilter
                    .entitiesAround(wp, radius, radius, radius)
                    .aliveTeammatesOfExcludingSelf(wp)
                    .limit(1)
                    .toList();
            if (targets.isEmpty()) {
                subtractCurrentCooldown(cooldown.getBaseValue() * .35f);
            } else {
                giveBarrier(wp, targets.get(0));
            }
        } else {
            List<WarlordsEntity> targets = PlayerFilter
                    .entitiesAround(wp, radius, radius, radius)
                    .aliveTeammatesOfExcludingSelf(wp)
                    .requireLineOfSightIntervene(wp)
                    .lookingAtFirst(wp)
                    .limit(1)
                    .toList();
            WarlordsEntity target = targets.isEmpty() ? wp : targets.get(0);
            giveBarrier(wp, target);
        }

        return true;
    }

    private void giveBarrier(@Nonnull WarlordsEntity wp, WarlordsEntity target) {
        AtomicInteger damageInstances = new AtomicInteger();

        boolean isSelf = wp.equals(target);
        wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                .append(Component.text(" Your ", NamedTextColor.GRAY))
                .append(Component.text(name, NamedTextColor.YELLOW))
                .append(Component.text(" is now protecting " + (isSelf ? "yourself" : target.getName()) + "!", NamedTextColor.GRAY))
        );
        if (!isSelf) {
            EffectUtils.playParticleLinkAnimation(wp.getLocation(), target.getLocation(), 0, 180, 180, 2);

            target.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN
                    .append(Component.text(" " + wp.getName() + "'s ", NamedTextColor.GRAY))
                    .append(Component.text("Mystical Barrier", NamedTextColor.YELLOW))
                    .append(Component.text(" is now protecting you for ", NamedTextColor.GRAY))
                    .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                    .append(Component.text(" seconds!", NamedTextColor.GRAY))
            );
        }

        for (int i = 0; i < 3; i++) {
            FortifyingHex.giveFortifyingHex(wp, target);
        }
        target.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "MYSTIC",
                MysticalBarrier.class,
                new MysticalBarrier(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (!target.isAlive()) {
                        return;
                    }
                    Utils.playGlobalSound(target.getLocation(), "arcanist.mysticalbarrier.giveshield", 2, 1.75f);
                    int shieldHealth = Math.min(shieldMaxHealth, shieldBase + shieldIncrease * damageInstances.get());
                    giveShield(wp, target, shieldHealth);
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    EffectUtils.playCircularEffectAround(
                            target.getGame(),
                            target.getLocation(),
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
                return event.getCause().isEmpty() ? currentDamageValue * convertToDivisionDecimal(meleeDamageReduction) : currentDamageValue;
            }

            @Override
            public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                event.getSource().getSpec().increaseAllCooldownTimersBy(runeTimerIncrease);
                damageInstances.getAndIncrement();
            }
        });
    }

    private void giveShield(WarlordsEntity from, @Nonnull WarlordsEntity to, int shieldHealth) {
        Shield shield = new Shield(name, shieldHealth);
        to.getCooldownManager().addCooldown(new RegularCooldown<>(
                name + " Shield",
                "SHIELD",
                Shield.class,
                shield,
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
                            2,
                            0.3,
                            0.2,
                            0.3,
                            0
                    );
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
                        we -> we.isTeammate(from)
                );
            }
        });
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
