package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HolyRadianceAvenger extends AbstractAbility {

    private final int radius = 6;
    private final int markRadius = 15;
    private int markDuration = 8;

    public HolyRadianceAvenger(float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        super("Holy Radiance", minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Radiate with holy energy, healing\n" +
                "§7yourself and all nearby allies for\n" +
                "§a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7health." +
                "\n\n" +
                "§7You may look at an enemy to mark\n" +
                "§7them for §6" + markDuration + " §7seconds. Mark has an\n" +
                "§7optimal range of §e" + markRadius + " §7blocks. However,\n" +
                "§7marking players from far away\n" +
                "§7will not give them healing.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        wp.addHealingInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false, false);
        wp.subtractEnergy(energyCost);

        for (WarlordsPlayer markTarget : PlayerFilter
                .entitiesAround(player, markRadius, markRadius, markRadius)
                .aliveEnemiesOf(wp)
                .lookingAtFirst(wp)
                .limit(1)
        ) {
            if (Utils.isLookingAtMark(player, markTarget.getEntity()) && Utils.hasLineOfSight(player, markTarget.getEntity())) {

                for (Player player1 : player.getWorld().getPlayers()) {
                    player1.playSound(player.getLocation(), "paladin.consecrate.activation", 2, 0.65f);
                }

                // chain particles
                EffectUtils.playParticleLinkAnimation(wp.getLocation(), markTarget.getLocation(), 0, 255, 70);
                EffectUtils.playChainAnimation(wp, markTarget, Material.PUMPKIN, 8);

                HolyRadianceAvenger tempMark = new HolyRadianceAvenger(minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
                markTarget.getCooldownManager().addRegularCooldown(
                        name,
                        "AVENGER MARK",
                        HolyRadianceAvenger.class,
                        tempMark,
                        wp,
                        CooldownTypes.DEBUFF,
                        cooldownManager -> {},
                        markDuration * 20
                );

                wp.sendMessage(WarlordsPlayer.RECEIVE_ARROW + ChatColor.GRAY + " You have marked " + ChatColor.GOLD + markTarget.getName() + ChatColor.GRAY + "!");
                markTarget.sendMessage(WarlordsPlayer.RECEIVE_ARROW + ChatColor.GRAY + " You have been cursed with " + ChatColor.GOLD + "Avenger's Mark" + ChatColor.GRAY + " by " + wp.getName() + "!");

                new GameRunnable(wp.getGame()) {
                    @Override
                    public void run() {
                        if (markTarget.getCooldownManager().hasCooldown(tempMark)) {
                            EffectUtils.playCylinderAnimation(markTarget.getLocation(), 1, 250, 25, 25);
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(0, 10);
            } else {
                player.sendMessage("§cYour mark was out of range or you did not target a player!");
            }
        }

        for (WarlordsPlayer p : PlayerFilter
                .entitiesAround(player, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
        ) {
            wp.getGame().registerGameTask(
                    new FlyingArmorStand(wp.getLocation(), p, wp, 1.1).runTaskTimer(Warlords.getInstance(), 1, 1)
            );
        }

        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.holyradiance.activation", 2, 1);
        }

        Location particleLoc = player.getLocation().add(0, 1.2, 0);
        ParticleEffect.VILLAGER_HAPPY.display(1, 1, 1, 0.1F, 2, particleLoc, 500);
        ParticleEffect.SPELL.display(1, 1, 1, 0.06F, 12, particleLoc, 500);

        return true;
    }

    private class FlyingArmorStand extends BukkitRunnable {

        private final WarlordsPlayer target;
        private final WarlordsPlayer owner;
        private final double speed;
        private final ArmorStand armorStand;

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