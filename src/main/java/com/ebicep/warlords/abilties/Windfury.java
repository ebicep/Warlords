package com.ebicep.warlords.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.SkillBoosts;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Windfury extends AbstractAbility {

    private int procChance = 35;
    private final int duration = 8;

    public Windfury() {
        super("Windfury Weapon", 0, 0, 15.66f, 30, 25, 200);
    }

    @Override
    public void updateDescription(Player player) {
        int weaponDamage = procChance == 35 ? 135 : 155;
        description = "§7Imbue your weapon with the power\n" +
                "§7of the wind, causing each of your\n" +
                "§7melee attacks to have a §e" + procChance + "% §7chance\n" +
                "§7to hit §e2 §7additional times for §c" + weaponDamage + "%\n" +
                "§7weapon damage. The first melee hit is\n" +
                "§7guaranteed to activate Windfury. Lasts §6" + duration + "\n" +
                "§7seconds.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        Windfury tempWindfury = new Windfury();
        final boolean[] firstProc = {true};
        wp.getCooldownManager().addCooldown(new RegularCooldown<Windfury>(
                name,
                "FURY",
                Windfury.class,
                tempWindfury,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                duration * 20
        ) {
            @Override
            public void onEndFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (event.getAbility().isEmpty()) {
                    WarlordsPlayer victim = event.getPlayer();
                    WarlordsPlayer attacker = event.getAttacker();
                    float min = event.getMin();
                    float max = event.getMax();

                    int windfuryActivate = (int) (Math.random() * 100);
                    if (firstProc[0]) {
                        firstProc[0] = false;
                        windfuryActivate = 0;
                    }
                    if (windfuryActivate < procChance) {
                        new BukkitRunnable() {
                            int counter = 0;

                            @Override
                            public void run() {
                                victim.getGameState().getGame().forEachOnlinePlayerWithoutSpectators((player1, t) -> {
                                    player1.playSound(victim.getLocation(), "shaman.windfuryweapon.impact", 2, 1);
                                });

                                if (Warlords.getPlayerSettings(attacker.getUuid()).getSkillBoostForClass() == SkillBoosts.WINDFURY_WEAPON) {
                                    victim.addDamageInstance(attacker, "Windfury Weapon", min * 1.35f * 1.2f, max * 1.35f * 1.2f, 25, 200, false);
                                } else {
                                    victim.addDamageInstance(attacker, "Windfury Weapon", min * 1.35f, max * 1.35f, 25, 200, false);
                                }

                                counter++;

                                if (counter == 2) {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(Warlords.getInstance(), 3, 3);
                    }
                }
            }
        });

        Utils.playGlobalSound(player.getLocation(), "shaman.windfuryweapon.activation", 2, 1);

        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                if (wp.getCooldownManager().hasCooldown(tempWindfury)) {
                    Location location = wp.getLocation();
                    location.add(0, 1.2, 0);
                    ParticleEffect.CRIT.display(0.2F, 0F, 0.2F, 0.1F, 3, location, 500);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 4);

        return true;
    }

    public int getProcChance() {
        return procChance;
    }

    public void setProcChance(int procChance) {
        this.procChance = procChance;
    }
}
