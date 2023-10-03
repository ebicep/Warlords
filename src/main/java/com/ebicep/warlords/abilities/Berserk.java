package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.berserker.BerserkBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Berserk extends AbstractAbility implements OrangeAbilityIcon, Duration {

    public int hitsDoneAmplified = 0;
    public int hitsTakenAmplified = 0;

    private int tickDuration = 360;
    private int speedBuff = 30;
    private float damageIncrease = 30;
    private float damageTakenIncrease = 10;

    public Berserk() {
        super("Berserk", 0, 0, 46.98f, 30);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("You go into a berserker rage, increasing your damage by ")
                               .append(Component.text(format(damageIncrease) + "%", NamedTextColor.RED))
                               .append(Component.text(" and movement speed by "))
                               .append(Component.text(speedBuff + "%", NamedTextColor.YELLOW))
                               .append(Component.text(". While active, you also take "))
                               .append(Component.text(format(damageTakenIncrease) + "%", NamedTextColor.RED))
                               .append(Component.text(" more damage. Lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Hits Done Amplified", "" + hitsDoneAmplified));
        info.add(new Pair<>("Hits Taken Amplified", "" + hitsTakenAmplified));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(name, energyCost, false);
        Utils.playGlobalSound(player.getLocation(), "warrior.berserk.activation", 2, 1);

        Runnable cancelSpeed = wp.addSpeedModifier(wp, name, speedBuff, tickDuration, "BASE");

        Berserk tempBerserk = new Berserk();
        wp.getCooldownManager().removeCooldown(Berserk.class, false);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "BERS",
                Berserk.class,
                tempBerserk,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    cancelSpeed.run();
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        EffectUtils.displayParticle(
                                Particle.VILLAGER_ANGRY,
                                wp.getLocation().add(0, 1.75, 0),
                                1,
                                0,
                                0,
                                0,
                                0.1F
                        );
                    }
                })
        ) {
            int multiplier = 0;

            @Override
            public float addCritChanceFromAttacker(WarlordsDamageHealingEvent event, float currentCritChance) {
                if (pveMasterUpgrade) {
                    if (event.getAbility().isEmpty() || event.getAbility().equals("Time Warp")) {
                        return currentCritChance;
                    }
                    float critBoost = (0.2f * multiplier);
                    if (critBoost > 50) {
                        critBoost = 50;
                    }
                    return currentCritChance + critBoost;
                }
                return currentCritChance;
            }

            @Override
            public float addCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, float currentCritMultiplier) {
                if (pveMasterUpgrade) {
                    if (event.getAbility().isEmpty() || event.getAbility().equals("Time Warp")) {
                        return currentCritMultiplier;
                    }
                    float critBoost = (0.2f * multiplier);
                    if (critBoost > 50) {
                        critBoost = 50;
                    }
                    return currentCritMultiplier + critBoost;
                }
                return currentCritMultiplier;
            }

            @Override
            public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                hitsTakenAmplified++;
                return currentDamageValue * convertToMultiplicationDecimal(damageTakenIncrease);
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                hitsDoneAmplified++;
                multiplier++;
                float increase = damageIncrease;
                if (pveMasterUpgrade2) {
                    CooldownManager cooldownManager = event.getWarlordsEntity().getCooldownManager();
                    if (cooldownManager.hasCooldownFromName("Bleed") || cooldownManager.hasCooldownFromName("Wounding Strike")) {
                        increase += 40;
                    }
                }
                return currentDamageValue * convertToMultiplicationDecimal(increase);
            }

            @Override
            public float getAbilityMultiplicativeCooldownMult(AbstractAbility ability) {
                if (pveMasterUpgrade2 && !(ability instanceof Berserk)) {
                    return 0.80f;
                }
                return 1;
            }
        });

        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new BerserkBranch(abilityTree, this);
    }

    public float getDamageIncrease() {
        return damageIncrease;
    }

    public void setDamageIncrease(float damageIncrease) {
        this.damageIncrease = damageIncrease;
    }

    public float getDamageTakenIncrease() {
        return damageTakenIncrease;
    }

    public void setDamageTakenIncrease(float damageTakenIncrease) {
        this.damageTakenIncrease = damageTakenIncrease;
    }

    public int getSpeedBuff() {
        return speedBuff;
    }

    public void setSpeedBuff(int speedBuff) {
        this.speedBuff = speedBuff;
    }


    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }
}
