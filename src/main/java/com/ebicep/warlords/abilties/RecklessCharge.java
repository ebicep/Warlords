package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecklessCharge extends AbstractAbility implements Listener {

    private static final List<UUID> stunnedPlayers = new ArrayList<>();
    private int stunTimeInTicks = 10;

    public RecklessCharge() {
        super("Reckless Charge", 457, 601, 9.32f, 60, 20, 200);
    }

    @Override
    public void updateDescription(Player player) {
        double stunDuration = stunTimeInTicks == 10 ? 0.5 : 0.75;
        description = "§7Charge forward, dealing §c" + format(minDamageHeal) + "\n" +
                "§7- §c" + format(maxDamageHeal) + " §7damage to all enemies\n" +
                "§7you pass through. Enemies hit are\n" +
                "§5IMMOBILIZED§7, preventing movement\n" +
                "§7for §6" + stunDuration + " §7seconds. Charge is reduced\n" +
                "§7when carrying a flag.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        Location location = player.getLocation();
        location.setPitch(0);

        Location chargeLocation = location.clone();
        double chargeDistance;
        List<WarlordsPlayer> playersHit = new ArrayList<>();

        boolean inAir = false;
        if (location.getWorld().getBlockAt(location.clone().add(0, -1, 0)).getType() != Material.AIR) {
            inAir = true;
            //travels 5 blocks
            chargeDistance = 5;
        } else {
            //travels 7 at peak jump
            chargeDistance = Math.max(Math.min(Utils.getDistance(player, .1) * 5, 6.9), 6);
        }

        boolean finalInAir = inAir;

        if (finalInAir) {
            new GameRunnable(wp.getGame()) {

                @Override
                public void run() {
                    wp.setVelocity(location.getDirection().multiply(2).setY(.2));
                }
            }.runTaskLater(0);
        } else {
            player.setVelocity(location.getDirection().multiply(1.5).setY(.2));
        }

        Utils.playGlobalSound(player.getLocation(), "warrior.seismicwave.activation", 2, 1);


        double finalChargeDistance = chargeDistance;
        new GameRunnable(wp.getGame()) {
            //safety precaution
            int maxChargeDuration = 5;

            @Override
            public void run() {
                //cancel charge if hit a block, making the player stand still
                if (wp.getLocation().distanceSquared(chargeLocation) > finalChargeDistance * finalChargeDistance ||
                        (wp.getEntity().getVelocity().getX() == 0 && wp.getEntity().getVelocity().getZ() == 0) ||
                        maxChargeDuration <= 0
                ) {
                    wp.setVelocity(new Vector(0, 0, 0));
                    this.cancel();
                }
                for (int i = 0; i < 4; i++) {
                    ParticleEffect.REDSTONE.display(
                            new ParticleEffect.OrdinaryColor(255, 0, 0),
                            wp.getLocation().clone().add((Math.random() * 1.5) - .75, .5 + (Math.random() * 2) - 1, (Math.random() * 1.5) - .75),
                            500);
                }
                PlayerFilter.entitiesAround(wp, 2.5, 5, 2.5)
                        .excluding(playersHit)
                        .aliveEnemiesOf(wp)
                        .forEach(enemy -> {
                            playersHit.add(enemy);
                            stunnedPlayers.add(enemy.getUuid());
                            enemy.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
                            new GameRunnable(wp.getGame()) {
                                @Override
                                public void run() {
                                    stunnedPlayers.remove(enemy.getUuid());
                                }
                            }.runTaskLater(getStunTimeInTicks()); //.5 seconds
                            if (enemy.getEntity() instanceof Player) {
                                PacketUtils.sendTitle((Player) enemy.getEntity(), "", "§dIMMOBILIZED", 0, 10, 0);
                            }
                        });

                maxChargeDuration--;
            }

        }.runTaskTimer(1, 0);

        return true;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (stunnedPlayers.contains(e.getPlayer().getUniqueId())) {
            if (
                (e.getFrom().getX() != e.getTo().getX() ||
                e.getFrom().getZ() != e.getTo().getZ()) &&
                !(e instanceof PlayerTeleportEvent)
            ) {
                e.getPlayer().teleport(e.getFrom());
            }
        }
    }

    public int getStunTimeInTicks() {
        return stunTimeInTicks;
    }

    public void setStunTimeInTicks(int stunTimeInTicks) {
        this.stunTimeInTicks = stunTimeInTicks;
    }
}
