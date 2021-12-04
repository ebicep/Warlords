package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.Matrix4d;
import com.ebicep.warlords.util.PacketUtils;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BlindingAssault extends AbstractAbility {

    int maxAssaultDuration = 12;

    public BlindingAssault() {
        super("Blinding Assault", 466, 612, 16f, 40, 25, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Dash forward, dealing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage to all enemies you pass\n" +
                    "§7through. Hitting subsequent enemies reduces the\n" +
                    "§7damage by §c10% §7(down to §c50%§7.) Blinding Assault\n" +
                    "§7has no verticality.";
    }

    @Override
    public void onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        Location playerLoc = player.getLocation();

        player.setVelocity(playerLoc.getDirection().multiply(2).setY(.1));

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(playerLoc, "shaman.chainlightning.impact", 2, 1.5f);
        }

        List<WarlordsPlayer> playersHit = new ArrayList<>();

        wp.getGame().getGameTasks().put(

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        for (int i = 0; i < 7; i++) {
                            ParticleEffect.REDSTONE.display(
                                    new ParticleEffect.OrdinaryColor(255, 255, 255),
                                    playerLoc.clone().add((Math.random() * 2) - 1, 1.2 + (Math.random() * 2) - 1, (Math.random() * 2) - 1),
                                    500);

                        }

                        PlayerFilter.entitiesAround(player, 2.5, 5, 2.5)
                                .excluding(playersHit)
                                .aliveEnemiesOf(wp)
                                .forEach(enemy -> {
                                    playersHit.add(enemy);
                                    enemy.damageHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
                                    for (Player player1 : player.getWorld().getPlayers()) {
                                        player1.playSound(playerLoc, "warrior.revenant.orbsoflife", 2, 1.9f);
                                    }
                                });

                        maxAssaultDuration--;

                        if (maxAssaultDuration <= 0 || wp.isDead()) {
                            this.cancel();
                        }
                    }

                }.runTaskTimer(Warlords.getInstance(), 1, 0),
                System.currentTimeMillis()
        );
    }
}
