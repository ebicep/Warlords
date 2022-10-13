package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Repentance extends AbstractAbility {

    private float pool = 0;
    private int duration = 12;
    private int poolDecay = 60;
    private int damageConvertPercent = 10;

    public Repentance() {
        super("Repentance", 0, 0, 31.32f, 20);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Taking damage empowers your damaging abilities and melee hits, restoring health and energy based on §c10 §7+ §c" +
                damageConvertPercent + "% §7of the damage you've recently took. Lasts §6" + duration + " §7seconds.";
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
        wp.getCooldownManager().addCooldown(new RegularCooldown<Repentance>(
                name, "REPE",
                Repentance.class,
                new Repentance(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                duration * 20
        ) {
            @Override
            public boolean distinct() {
                return true;
            }

            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                WarlordsEntity attacker = event.getAttacker();

                int healthToAdd = (int) (pool * (damageConvertPercent / 100f)) + 10;
                attacker.addHealingInstance(attacker, "Repentance", healthToAdd, healthToAdd, -1, 100, false, false);
                attacker.addEnergy(attacker, "Repentance", (float) (healthToAdd * .035));
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

    public float getPool() {
        return pool;
    }

    public void setPool(float pool) {
        this.pool = pool;
    }

    public int getDamageConvertPercent() {
        return damageConvertPercent;
    }

    public void setDamageConvertPercent(int damageConvertPercent) {
        this.damageConvertPercent = damageConvertPercent;
    }

    public void addToPool(float amount) {
        this.pool += amount;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPoolDecay() {
        return poolDecay;
    }

    public void setPoolDecay(int poolDecay) {
        this.poolDecay = poolDecay;
    }
}
