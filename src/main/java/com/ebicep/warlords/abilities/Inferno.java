package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.pyromancer.InfernoBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inferno extends AbstractAbility implements OrangeAbilityIcon, Duration {

    public int hitsAmplified = 0;

    private int maxHits = 40;
    private int tickDuration = 360;
    private int critChanceIncrease = 30;
    private int critMultiplierIncrease = 30;

    public Inferno() {
        super("Inferno", 0, 0, 46.98f, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Combust into a molten inferno, increasing your Crit Chance by ")
                               .append(Component.text(critChanceIncrease + "%", NamedTextColor.RED))
                               .append(Component.text(" and your Crit Multiplier by "))
                               .append(Component.text(critMultiplierIncrease + "%", NamedTextColor.RED))
                               .append(Component.text(". Lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Hits Amplified", "" + hitsAmplified));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "mage.inferno.activation", 2, 1);

        Inferno tempInferno = new Inferno();
        if (pveMasterUpgrade) {
            wp.getCooldownManager().removeCooldown(Inferno.class, false);
        }
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "INFR",
                Inferno.class,
                tempInferno,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        Location loc = wp.getLocation().add(0, 1.2, 0);
                        EffectUtils.displayParticle(Particle.DRIP_LAVA, loc, 1, 0.5, 0.3, 0.5, 0.4);
                        EffectUtils.displayParticle(Particle.FLAME, loc, 1, 0.5, 0.3, 0.5, 0.0001);
                        EffectUtils.displayParticle(Particle.CRIT, loc, 1, 0.5, 0.3, 0.5, 0.0001);
                    }
                })
        ) {
            int finalMaxHits = maxHits;

            @Override
            public boolean distinct() {
                return true;
            }

            @Override
            public void damageDoBeforeVariableSetFromAttacker(WarlordsDamageHealingEvent event) {
                if (pveMasterUpgrade2 && event.getAbility().equals("Ignite")) {
                    event.setMinForce(event.getMin() * 2);
                    event.setMaxForce(event.getMax() * 2);
                }
            }

            @Override
            public void onDeathFromEnemies(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit, boolean isKiller) {
                if (pveMasterUpgrade2 && isKiller) {
                    wp.addEnergy(wp, "Inferno", 30);
                }
            }

            @Override
            public float addCritChanceFromAttacker(WarlordsDamageHealingEvent event, float currentCritChance) {
                if (event.getAbility().isEmpty()) {
                    return currentCritChance;
                }
                hitsAmplified++;
                return currentCritChance + critChanceIncrease;
            }

            @Override
            public float addCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, float currentCritMultiplier) {
                if (event.getAbility().isEmpty()) {
                    return currentCritMultiplier;
                }
                return currentCritMultiplier + critMultiplierIncrease;
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (pveMasterUpgrade2) {
                    return currentDamageValue * 1.2f;
                }
                return currentDamageValue;
            }

            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (pveMasterUpgrade) {
                    if (isCrit && !(finalMaxHits <= 0)) {
                        subtractCurrentCooldown(0.5f);
                        setTicksLeft(getTicksLeft() + 5);
                        finalMaxHits--;
                    }
                }
            }
        });

        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new InfernoBranch(abilityTree, this);
    }

    public int getHitsAmplified() {
        return hitsAmplified;
    }

    public int getCritChanceIncrease() {
        return critChanceIncrease;
    }

    public void setCritChanceIncrease(int critChanceIncrease) {
        this.critChanceIncrease = critChanceIncrease;
    }

    public int getCritMultiplierIncrease() {
        return critMultiplierIncrease;
    }

    public void setCritMultiplierIncrease(int critMultiplierIncrease) {
        this.critMultiplierIncrease = critMultiplierIncrease;
    }


    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public int getMaxHits() {
        return maxHits;
    }

    public void setMaxHits(int maxHits) {
        this.maxHits = maxHits;
    }
}
