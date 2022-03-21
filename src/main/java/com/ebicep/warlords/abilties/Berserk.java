package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Berserk extends AbstractAbility {

    private final int duration = 18;
    // Percent
    private final int speedBuff = 30;
    private float damageIncrease = 30;
    private float damageTakenIncrease = 10;

    public Berserk() {
        super("Berserk", 0, 0, 46.98f, 30, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7You go into a berserker rage,\n" +
                "§7increasing your damage by §c" + format(damageIncrease) + "% §7and\n" +
                "§7movement speed by §e" + speedBuff + "%§7. While active,\n" +
                "§7you also take §c" + format(damageTakenIncrease) + "% §7more damage.\n" + "§7Lasts §6" + duration + " §7seconds.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        Berserk tempBerserk = new Berserk();
        wp.subtractEnergy(energyCost);
        Runnable cancelSpeed = wp.getSpeed().addSpeedModifier(name, speedBuff, duration * 20, "BASE");
        wp.getCooldownManager().addCooldown(new RegularCooldown<Berserk>(
                name,
                "BERS",
                Berserk.class,
                tempBerserk,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                duration * 20
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * (1 + damageTakenIncrease / 100);
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * (1 + damageIncrease / 100);
            }
        });

        Utils.playGlobalSound(player.getLocation(), "warrior.berserk.activation", 2, 1);

        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                if (wp.getCooldownManager().hasCooldown(tempBerserk)) {
                    Location location = wp.getLocation();
                    location.add(0, 2.1, 0);
                    ParticleEffect.VILLAGER_ANGRY.display(0, 0, 0, 0.1F, 1, location, 500);
                } else {
                    cancelSpeed.run();
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 3);

        return true;
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
}
