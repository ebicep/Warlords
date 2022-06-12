package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractPiercingProjectileBase;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;

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
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Shots Fired", "" + timesUsed));
        info.add(new Pair<>("Players Hit", "" + playersHit));
        info.add(new Pair<>("Dismounts", "" + numberOfDismounts));

        return info;
    }

    @Override
    protected String getActivationSound() {
        return "shaman.lightningbolt.activation";
    }

    @Override
    protected float getSoundPitch() {
        return 1;
    }

    @Override
    protected float getSoundVolume() {
        return 2;
    }

    @Override
    protected boolean shouldEndProjectileOnHit(InternalProjectile projectile, Block block) {
        return true;
    }

    @Override
    protected boolean shouldEndProjectileOnHit(InternalProjectile projectile, WarlordsEntity wp) {
        return false;
    }

    @Override
    protected void onNonCancellingHit(InternalProjectile projectile, WarlordsEntity hit, Location impactLocation) {
        WarlordsEntity wp = projectile.getShooter();
        if (!projectile.getHit().contains(hit)) {
            projectile.getHit().add(hit);
            playersHit++;
            if (hit.onHorse()) {
                numberOfDismounts++;
            }
            Utils.playGlobalSound(impactLocation, "shaman.lightningbolt.impact", 2, 1);

            hit.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);

            //reducing chain cooldown
            wp.getSpec().getRed().subtractCooldown(2);
            if (wp.getEntity() instanceof Player) {
                wp.updateRedItem((Player) wp.getEntity());
            }
        }
    }

    @Override
    protected int onHit(InternalProjectile projectile, WarlordsEntity hit) {
        WarlordsEntity wp = projectile.getShooter();
        Location currentLocation = projectile.getCurrentLocation();

        Utils.playGlobalSound(currentLocation, "shaman.lightningbolt.impact", 2, 1);

        ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.0F, 1, currentLocation, 500);

        int playersHit = 0;
        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(currentLocation, 3, 3, 3)
                .aliveEnemiesOf(wp)
                .excluding(projectile.getHit())
        ) {
            playersHit++;
            if (enemy.onHorse()) {
                numberOfDismounts++;
            }
            Utils.playGlobalSound(enemy.getLocation(), "shaman.lightningbolt.impact", 2, 1);

            //hitting player
            enemy.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);

            //reducing chain cooldown
            wp.getSpec().getRed().subtractCooldown(2);
            if (wp.getEntity() instanceof Player) {
                wp.updateRedItem((Player) wp.getEntity());
            }
        }

        return playersHit;
    }

    @Override
    protected Location getProjectileStartingLocation(WarlordsEntity shooter, Location startingLocation) {
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


}