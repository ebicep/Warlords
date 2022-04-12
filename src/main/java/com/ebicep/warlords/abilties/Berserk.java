package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Berserk extends AbstractAbility {
    protected int hitsDoneAmplified = 0;
    protected int hitsTakenAmplified = 0;

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
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Hits Done Amplified", "" + hitsDoneAmplified));
        info.add(new Pair<>("Hits Taken Amplified", "" + hitsTakenAmplified));

        return info;
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        Utils.playGlobalSound(player.getLocation(), "warrior.berserk.activation", 2, 1);

        Berserk tempBerserk = new Berserk();
        Runnable cancelSpeed = wp.getSpeed().addSpeedModifier(name, speedBuff, duration * 20, "BASE");
        wp.getCooldownManager().addCooldown(new RegularCooldown<Berserk>(
                name,
                "BERS",
                Berserk.class,
                tempBerserk,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    cancelSpeed.run();
                },
                duration * 20,
                ticksLeft -> {
                    if (ticksLeft % 3 == 0) {
                        ParticleEffect.VILLAGER_ANGRY.display(
                                0,
                                0,
                                0,
                                0.1f,
                                1,
                                wp.getLocation().add(0, 1.2, 0),
                                500
                        );
                    }
                }
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                hitsTakenAmplified++;
                return currentDamageValue * (1 + damageTakenIncrease / 100);
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                hitsDoneAmplified++;
                return currentDamageValue * (1 + damageIncrease / 100);
            }
        });

        /*
        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                if (wp.getCooldownManager().hasCooldown(tempBerserk)) {
                    ParticleEffect.VILLAGER_ANGRY.display(
                            0,
                            0,
                            0,
                            0.1f,
                            1,
                            wp.getLocation().add(0, 1.2, 0),
                            500
                    );
                } else {
                    cancelSpeed.run();
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 3);

         */

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
