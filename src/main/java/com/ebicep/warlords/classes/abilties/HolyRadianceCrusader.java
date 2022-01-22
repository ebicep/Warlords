package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;

public class HolyRadianceCrusader extends AbstractAbility {

    private final int radius = 6;
    private final int markRadius = 15;
    private int markDuration = 8;

    public HolyRadianceCrusader(float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        super("Holy Radiance", minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Radiate with holy energy, healing\n" +
                "§7yourself and all nearby allies for\n" +
                "§a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7health." +
                "\n\n" +
                "§7You may look at an ally to mark\n" +
                "§7them for §6" + markDuration + " §7seconds. Increasing\n" +
                "§7their EPS by §e5 §7and speed by §e20%\n" +
                "§7§7for the duration. Mark has an optimal\n" +
                "§7range of §e" + markRadius + " §7blocks. However,\n" +
                "§7marking players from far away\n" +
                "§7will not give them healing.";

    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {

        for (WarlordsPlayer p : PlayerFilter
                .entitiesAround(player, markRadius, markRadius, markRadius)
                .aliveTeammatesOfExcludingSelf(wp)
                .lookingAtFirst(wp)
                .limit(1)
        ) {
            if (Utils.isLookingAtMark(player, p.getEntity()) && Utils.hasLineOfSight(player, p.getEntity())) {
                wp.subtractEnergy(energyCost);

                for (Player player1 : player.getWorld().getPlayers()) {
                    player1.playSound(player.getLocation(), "paladin.consecrate.activation", 2, 0.65f);
                }

                PacketPlayOutAnimation playOutAnimation = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 0);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(playOutAnimation);

                // chain particles
                Location lineLocation = player.getLocation().add(0, 1, 0);
                lineLocation.setDirection(lineLocation.toVector().subtract(p.getLocation().add(0, 1, 0).toVector()).multiply(-1));
                for (int i = 0; i < Math.floor(player.getLocation().distance(p.getLocation())) * 2; i++) {
                    ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(255, 170, 0), lineLocation, 500);
                    lineLocation.add(lineLocation.getDirection().multiply(.5));
                }

                Location from = wp.getLocation().add(0, -0.6, 0);
                Location to = p.getLocation().add(0, -0.6, 0);
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
                            if (armorStand.getTicksLived() > 8) {
                                armorStand.remove();
                                chains.remove(i);
                                i--;
                            }
                        }

                    }

                }.runTaskTimer(Warlords.getInstance(), 0, 0);

                HolyRadianceCrusader tempMark = new HolyRadianceCrusader(minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
                p.getCooldownManager().addCooldown(name, HolyRadianceCrusader.this.getClass(), tempMark, "CRUS MARK", markDuration, wp, CooldownTypes.BUFF);
                p.getSpeed().addSpeedModifier("Crusader Mark Speed", 20, 20 * markDuration, "BASE");

                player.sendMessage(WarlordsPlayer.RECEIVE_ARROW + ChatColor.GRAY + " You have marked " + ChatColor.YELLOW + p.getName() + ChatColor.GRAY +"!");
                p.sendMessage(WarlordsPlayer.RECEIVE_ARROW + ChatColor.GRAY + " You have been granted " + ChatColor.YELLOW + "Crusader's Mark" + ChatColor.GRAY + " by " + wp.getName() + "!");

                new GameRunnable(wp.getGame()) {
                    @Override
                    public void run() {
                        if (!p.getCooldownManager().getCooldown(HolyRadianceCrusader.class).isEmpty()) {
                            Location playerLoc = p.getLocation();
                            Location particleLoc = playerLoc.clone();
                            for (int i = 0; i < 4; i++) {
                                for (int j = 0; j < 10; j++) {
                                    double angle = j / 8D * Math.PI * 2;
                                    double width = 1;
                                    particleLoc.setX(playerLoc.getX() + Math.sin(angle) * width);
                                    particleLoc.setY(playerLoc.getY() + i / 6D);
                                    particleLoc.setZ(playerLoc.getZ() + Math.cos(angle) * width);

                                    ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(255, 170, 0), particleLoc, 500);
                                }
                            }
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(0, 10);
            } else {
                player.sendMessage("§cYour mark was out of range or you did not target a player!");
            }
        }

        wp.subtractEnergy(energyCost);
        for (WarlordsPlayer p : PlayerFilter
                .entitiesAround(player, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
        ) {
            wp.getGame().registerGameTask(
                    new FlyingArmorStand(wp.getLocation(), p, wp, 1.1).runTaskTimer(Warlords.getInstance(), 1, 1)
            );
        }

        wp.addHealingInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false, false);

        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.holyradiance.activation", 2, 1);
        }

        Location particleLoc = player.getLocation().add(0, 1.2, 0);
        ParticleEffect.VILLAGER_HAPPY.display(1, 1, 1, 0.1F, 2, particleLoc, 500);
        ParticleEffect.SPELL.display(1, 1, 1, 0.06F, 12, particleLoc, 500);
    }

    private class FlyingArmorStand extends BukkitRunnable {

        private WarlordsPlayer target;
        private WarlordsPlayer owner;
        private double speed;
        private ArmorStand armorStand;

        public FlyingArmorStand(Location location, WarlordsPlayer target, WarlordsPlayer owner, double speed) {
            this.armorStand = location.getWorld().spawn(location, ArmorStand.class);
            armorStand.setGravity(false);
            armorStand.setVisible(false);
            this.target = target;
            this.speed = speed;
            this.owner = owner;
        }

        @Override
        public void cancel() {
            super.cancel();
            armorStand.remove();
        }

        @Override
        public void run() {
            if (!owner.getGame().isFrozen()) {

                if (this.target.isDead()) {
                    this.cancel();
                    return;
                }

                if (target.getWorld() != armorStand.getWorld()) {
                    this.cancel();
                    return;
                }

                Location targetLocation = target.getLocation();
                Location armorStandLocation = armorStand.getLocation();
                double distance = targetLocation.distanceSquared(armorStandLocation);

                if (distance < speed * speed) {
                    target.addHealingInstance(owner, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false, false);
                    this.cancel();
                    return;
                }

                targetLocation.subtract(armorStandLocation);
                //System.out.println(Math.max(speed * 3.25 / targetLocation.lengthSquared() / 2, speed / 10));
                targetLocation.multiply(Math.max(speed * 3.25 / targetLocation.lengthSquared() / 2, speed / 10));

                armorStandLocation.add(targetLocation);
                this.armorStand.teleport(armorStandLocation);

                ParticleEffect.SPELL.display(0.01f, 0, 0.01f, 0.1f, 2, armorStandLocation.add(0, 1.75, 0), 500);
            }
        }
    }

    public void setMarkDuration(int markDuration) {
        this.markDuration = markDuration;
    }
}