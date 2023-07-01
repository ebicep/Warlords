package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Repentance extends AbstractAbility implements Duration {

    private float pool = 0;
    private int tickDuration = 240;
    private int poolDecay = 60;
    private float damageConvertPercent = 10;
    private float energyConvertPercent = 3.5f;

    public Repentance() {
        super("Repentance", 0, 0, 31.32f, 20);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Taking damage empowers your damaging abilities and melee hits, restoring health and energy based on ")
                               .append(Component.text("10%", NamedTextColor.RED))
                               .append(Component.text(" + "))
                               .append(Component.text(damageConvertPercent + "%", NamedTextColor.RED))
                               .append(Component.text(" of the damage you've recently took. Lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), "paladin.barrieroflight.impact", 2, 1.35f);
        EffectUtils.playCylinderAnimation(player, 1, 255, 255, 255);

        pool += 2000;
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name, "REPE",
                Repentance.class,
                new Repentance(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                tickDuration
        ) {
            @Override
            public boolean distinct() {
                return true;
            }

            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                WarlordsEntity attacker = event.getAttacker();

                int healthToAdd = (int) (pool * (damageConvertPercent / 100f)) + 10;
                attacker.addHealingInstance(
                        attacker,
                        "Repentance",
                        Math.min(500, healthToAdd),
                        Math.min(500, healthToAdd),
                        0,
                        100,
                        false,
                        false
                );
                attacker.addEnergy(attacker, "Repentance", healthToAdd * (energyConvertPercent / 100f));
                pool *= .5;
            }
        });

        return true;
    }

    @Override
    public void runEverySecond() {
        if (pool > 0) {
            float newPool = pool * .8f - poolDecay;
            pool = Math.max(newPool, 0);
        }
    }

    public float getDamageConvertPercent() {
        return damageConvertPercent;
    }

    public void setDamageConvertPercent(float damageConvertPercent) {
        this.damageConvertPercent = damageConvertPercent;
    }

    public void addToPool(float amount) {
        this.pool = Math.min(3000, pool + amount);
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public int getPoolDecay() {
        return poolDecay;
    }

    public void setPoolDecay(int poolDecay) {
        this.poolDecay = poolDecay;
    }

    public float getEnergyConvertPercent() {
        return energyConvertPercent;
    }

    public void setEnergyConvertPercent(float energyConvertPercent) {
        this.energyConvertPercent = energyConvertPercent;
    }
}