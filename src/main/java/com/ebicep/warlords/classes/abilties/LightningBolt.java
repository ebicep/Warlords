package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractPiercingProjectileBase;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.LocationBuilder;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class LightningBolt extends AbstractPiercingProjectileBase {

    public LightningBolt() {
        super("Lightning Bolt", 228, 385, 0, 60, 20, 200, 2.5, 60, false);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Hurl a fast, piercing bolt of lightning that\n" +
                "§7deals §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage to all enemies it\n" +
                "§7passes through. Each target hit reduces the\n" +
                "§7cooldown of Chain Lightning by §62 §7seconds.\n" +
                "\n" +
                "§7Has a maximum range of §e60 §7blocks.";
    }

    @Override
    protected String getActivationSound() {
        return "shaman.lightningbolt.activation";
    }

    @Override
    protected boolean shouldEndProjectileOnHit(InternalProjectile projectile, Block block) {
        return true;
    }

    @Override
    protected boolean shouldEndProjectileOnHit(InternalProjectile projectile, WarlordsPlayer wp) {
        return false;
    }

    @Override
    protected void onNonCancellingHit(InternalProjectile projectile, WarlordsPlayer hit, Location impactLocation) {
        WarlordsPlayer wp = projectile.getShooter();
        if (!projectile.getHit().contains(hit)) {
            projectile.getHit().add(hit);
            hit.damageHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);

            for (Player player1 : hit.getWorld().getPlayers()) {
                player1.playSound(impactLocation, "shaman.lightningbolt.impact", 2, 1);
            }

            //reducing chain cooldown
            wp.getSpec().getRed().subtractCooldown(2);
            if (wp.getEntity() instanceof Player) {
                wp.updateRedItem((Player) wp.getEntity());
            }
        }
    }

    @Override
    protected void onHit(InternalProjectile projectile, WarlordsPlayer hit) {
        final Location currentLocation = projectile.getCurrentLocation();
        ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.0F, 1, currentLocation, 500);
        for (Player player1 : projectile.getWorld().getPlayers()) {
            player1.playSound(currentLocation, "shaman.lightningbolt.impact", 2, 1);
        }

        WarlordsPlayer wp = projectile.getShooter();
        for (WarlordsPlayer warlordsPlayer : PlayerFilter
                .entitiesAround(currentLocation, 3, 3, 3)
                .aliveEnemiesOf(wp)
                .excluding(projectile.getHit())
        ) {
            //hitting player
            warlordsPlayer.damageHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);

            for (Player player1 : warlordsPlayer.getWorld().getPlayers()) {
                player1.playSound(warlordsPlayer.getLocation(), "shaman.lightningbolt.impact", 2, 1);
            }

            //reducing chain cooldown
            wp.getSpec().getRed().subtractCooldown(2);
            if (wp.getEntity() instanceof Player) {
                wp.updateRedItem((Player) wp.getEntity());
            }
        }
    }

    @Override
    protected Location getProjectileStartingLocation(WarlordsPlayer shooter, Location startingLocation) {
        return new LocationBuilder(startingLocation.clone()).addY(-.1).get();
    }

    @Override
    protected void onSpawn(InternalProjectile projectile) {
        super.onSpawn(projectile);
        ArmorStand armorStand = projectile.getWorld().spawn(projectile.getStartingLocation().clone().add(0, -1.7, 0), ArmorStand.class);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setMarker(true);
        armorStand.setHelmet(new ItemStack(Material.SAPLING, 1, (short) 3));
        armorStand.setHeadPose(new EulerAngle(-Math.atan2(
                projectile.getSpeed().getY(),
                Math.sqrt(
                        Math.pow(projectile.getSpeed().getX(), 2) +
                                Math.pow(projectile.getSpeed().getZ(), 2)
                )
        ), 0, 0));
        projectile.addTask(new InternalProjectileTask() {
            @Override
            public void run(InternalProjectile projectile) {
                armorStand.teleport(projectile.getCurrentLocation().clone().add(0, -1.7, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
            }

            @Override
            public void onDestroy(InternalProjectile projectile) {
                armorStand.remove();
            }
        });
    }

    @Override
    protected void playEffect(InternalProjectile projectile) {
        super.playEffect(projectile);
    }

    @Override
    @Deprecated
    protected void playEffect(Location currentLocation, int ticksLived) {
    }

    @Override
    @Deprecated
    protected void onHit(WarlordsPlayer shooter, Location currentLocation, Location startingLocation, WarlordsPlayer hit) {
        throw new UnsupportedOperationException("Deprecated");
    }

}