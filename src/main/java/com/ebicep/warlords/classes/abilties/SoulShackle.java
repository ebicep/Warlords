package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SoulShackle extends AbstractAbility {

    private final int shackleRange = 12;

    public SoulShackle() {
        super("Soul Shackle", 327, 443, 8, 40, 20, 140);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Shackle up to 1 enemy and deal" + format(minDamageHeal) + "" + format(maxDamageHeal) + "damage." +
                "Shackled enemies are silenced for 2 seconds," +
                "making them unable to use their main attack for" +
                "the duration.";
    }

    @Override
    public void onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);

        for (WarlordsPlayer shackleTarget : PlayerFilter
                .entitiesAround(player, shackleRange, shackleRange, shackleRange)
                .aliveEnemiesOf(wp)
                .lookingAtFirst(wp)
                .limit(1)
        ) {
            shackleTarget.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
            shackleTarget.getCooldownManager().addCooldown("Shackle Silence", this.getClass(), SoulShackle.class, "SILENCE", 2, wp, CooldownTypes.DEBUFF);

            for (Player player1 : player.getWorld().getPlayers()) {
                player1.playSound(player.getLocation(), "warrior.intervene.impact", 2, 0.2f);
                player1.playSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 2, 2);
            }

            Location from = wp.getLocation().add(0, -0.6, 0);
            Location to = shackleTarget.getLocation().add(0, -0.6, 0);
            from.setDirection(from.toVector().subtract(to.toVector()).multiply(-1));
            List<ArmorStand> chains = new ArrayList<>();
            int maxDistance = (int) Math.round(to.distance(from));
            for (int i = 0; i < maxDistance; i++) {
                ArmorStand chain = from.getWorld().spawn(from, ArmorStand.class);
                chain.setHeadPose(new EulerAngle(from.getDirection().getY() * -1, 0, 0));
                chain.setGravity(false);
                chain.setVisible(false);
                chain.setBasePlate(false);
                chain.setMarker(true);
                chain.setHelmet(new ItemStack(Material.PUMPKIN));
                from.add(from.getDirection().multiply(1.1));
                chains.add(chain);
                if(to.distanceSquared(from) < .3) {
                    break;
                }
            }

            new BukkitRunnable() {

                @Override
                public void run() {
                    if (chains.size() == 0) {
                        this.cancel();
                    }

                    for (int i = 0; i < chains.size(); i++) {
                        ArmorStand armorStand = chains.get(i);
                        if (armorStand.getTicksLived() > 20) {
                            armorStand.remove();
                            chains.remove(i);
                            i--;
                        }
                    }

                }

            }.runTaskTimer(Warlords.getInstance(), 0, 0);

            wp.getGame().getGameTasks().put(

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!shackleTarget.getCooldownManager().getCooldown(SoulShackle.class).isEmpty()) {
                                Location playerLoc = shackleTarget.getLocation();
                                Location particleLoc = playerLoc.clone();
                                for (int i = 0; i < 10; i++) {
                                    for (int j = 0; j < 10; j++) {
                                        double angle = j / 10D * Math.PI * 2;
                                        double width = 1.075;
                                        particleLoc.setX(playerLoc.getX() + Math.sin(angle) * width);
                                        particleLoc.setY(playerLoc.getY() + i / 5D);
                                        particleLoc.setZ(playerLoc.getZ() + Math.cos(angle) * width);

                                        ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(0, 0, 0), particleLoc, 500);
                                    }
                                }
                                for (Player player1 : player.getWorld().getPlayers()) {
                                    player1.playSound(playerLoc, Sound.DIG_SAND, 2, 2);
                                }
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(Warlords.getInstance(), 0, 4),
                    System.currentTimeMillis()
            );
        }

    }

}
