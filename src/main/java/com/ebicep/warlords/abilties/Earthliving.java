package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Earthliving extends AbstractAbility {

    private final int duration = 8;
    private int procChance = 40;

    public Earthliving() {
        super("Earthliving Weapon", 0, 0, 15.66f, 30, 25, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Imbue your weapon with the power of the\n" +
                "§7Earth, causing each of your melee attacks\n" +
                "§7to have a §e" + procChance + "% §7chance to heal you and §e2\n" +
                "§7nearby allies for §a240% §7weapon damage.\n" +
                "§7Lasts §6" + duration + " §7seconds." +
                "\n\n" +
                "§7The first hit is guaranteed to activate Earthliving.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        Earthliving tempEarthliving = new Earthliving();
        final boolean[] firstProc = {true};
        wp.getCooldownManager().addCooldown(new RegularCooldown<Earthliving>(
                name,
                "EARTH",
                Earthliving.class,
                tempEarthliving,
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

                    int earthlivingActivate = (int) (Math.random() * 100);
                    if (firstProc[0]) {
                        firstProc[0] = false;
                        earthlivingActivate = 0;
                    }
                    if (earthlivingActivate < procChance) {

                        attacker.addHealingInstance(attacker, "Earthliving Weapon", 132 * 2.4f, 179 * 2.4f, 25, 200, false, false);

                        victim.getGameState().getGame().forEachOnlinePlayerWithoutSpectators((p, t) -> {
                            p.playSound(victim.getLocation(), "shaman.earthlivingweapon.impact", 2, 1);
                        });

                        for (WarlordsPlayer nearPlayer : PlayerFilter
                                .entitiesAround(attacker, 6, 6, 6)
                                .aliveTeammatesOfExcludingSelf(attacker)
                                .limit(2)
                        ) {
                            nearPlayer.addHealingInstance(attacker, "Earthliving Weapon", 132 * 2.4f, 179 * 2.4f, 25, 200, false, false);
                        }
                    }
                }
            }
        });

        Utils.playGlobalSound(player.getLocation(), "shaman.earthlivingweapon.activation", 2, 1);

        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                if (wp.getCooldownManager().hasCooldown(tempEarthliving)) {
                    Location location = wp.getLocation();
                    location.add(0, 1.2, 0);
                    ParticleEffect.VILLAGER_HAPPY.display(0.3F, 0.3F, 0.3F, 0.1F, 2, location, 500);
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


