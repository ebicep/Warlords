package com.ebicep.warlords.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.shaman.specs.spiritguard.Spiritguard;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.entity.Player;

public class Repentance extends AbstractAbility {

    private float pool = 0;
    private int damageConvertPercent = 10;
    private final int duration = 12;

    public Repentance() {
        super("Repentance", 0, 0, 31.32f, 20, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Taking damage empowers your damaging\n" +
                "§7abilities and melee hits, restoring health\n" +
                "§7and energy based on §c10 §7+ §c" + damageConvertPercent + "% §7of the\n" +
                "§7damage you've recently took. Lasts §6" + duration + " §7seconds.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        pool += 2000;
        assert warlordsPlayer != null;
        warlordsPlayer.getCooldownManager().addCooldown(new RegularCooldown<Repentance>(
                name, "REPE",
                Repentance.class,
                new Repentance(),
                warlordsPlayer,
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
                WarlordsPlayer attacker = event.getAttacker();
                if (attacker.getSpec() instanceof Spiritguard) {
                    Repentance repentance = (Repentance) attacker.getSpec().getBlue();
                    int healthToAdd = (int) (repentance.getPool() * (repentance.getDamageConvertPercent() / 100f)) + 10;
                    attacker.addHealingInstance(attacker, "Repentance", healthToAdd, healthToAdd, -1, 100, false, false);
                    repentance.setPool(repentance.getPool() * .5f);
                    attacker.addEnergy(attacker, "Repentance", (float) (healthToAdd * .035));
                }
            }
        });

        Utils.playGlobalSound(player.getLocation(), "paladin.barrieroflight.impact", 2, 1.35f);

        EffectUtils.playCylinderAnimation(player, 1, 255, 255, 255);

        return true;
    }

    public float getPool() {
        return pool;
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

    public void setPool(float pool) {
        this.pool = pool;
    }
    
    @Override
    public void runEverySecond() {
        if (pool > 0) {
            float newPool = pool * .8f - 60;
            pool = Math.max(newPool, 0);
        }
    }
}
