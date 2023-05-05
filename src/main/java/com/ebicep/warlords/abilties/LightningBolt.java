package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractPiercingProjectileBase;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class LightningBolt extends AbstractPiercingProjectileBase {

    private double hitbox = 3;

    public LightningBolt() {
        super("Lightning Bolt", 228, 385, 0, 60, 20, 200, 2.5, 60, false);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Hurl a fast, piercing bolt of lightning that deals ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" to all enemies it passes through. Each target hit reduces the cooldown of Chain Lightning by "))
                               .append(Component.text("2", NamedTextColor.GOLD))
                               .append(Component.text(" seconds."))
                               .append(Component.text("\n\nHas a maximum range of "))
                               .append(Component.text(format(maxDistance), NamedTextColor.YELLOW))
                               .append(Component.text(" blocks."));

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
    protected void playEffect(@Nonnull InternalProjectile projectile) {
        super.playEffect(projectile);
    }

    @Override
    @Deprecated
    protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {
    }

    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, WarlordsEntity hit) {
        WarlordsEntity wp = projectile.getShooter();
        Location currentLocation = projectile.getCurrentLocation();

        Utils.playGlobalSound(currentLocation, "shaman.lightningbolt.impact", 2, 1);

        currentLocation.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, currentLocation, 1, 0, 0, 0, 0, null, true);

        int playersHit = 0;
        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(currentLocation, hitbox, hitbox, hitbox)
                .aliveEnemiesOf(wp)
                .excluding(projectile.getHit())
        ) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(enemy));
            playersHit++;
            if (enemy.onHorse()) {
                numberOfDismounts++;
            }
            Utils.playGlobalSound(enemy.getLocation(), "shaman.lightningbolt.impact", 2, 1);

            //hitting player
            enemy.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);

            //reducing chain cooldown
            if (!(wp.isInPve() && projectile.getHit().size() > 2)) {
                wp.subtractRedCooldown(2);
            }
        }

        return playersHit;
    }

    @Override
    protected boolean shouldEndProjectileOnHit(@Nonnull InternalProjectile projectile, WarlordsEntity wp) {
        return false;
    }

    @Override
    protected boolean shouldEndProjectileOnHit(@Nonnull InternalProjectile projectile, Block block) {
        return true;
    }

    @Override
    protected void onNonCancellingHit(InternalProjectile projectile, @Nonnull WarlordsEntity hit, @Nonnull Location impactLocation) {
        WarlordsEntity wp = projectile.getShooter();
        if (!projectile.getHit().contains(hit)) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
            playersHit++;
            if (hit.onHorse()) {
                numberOfDismounts++;
            }
            Utils.playGlobalSound(impactLocation, "shaman.lightningbolt.impact", 2, 1);

            hit.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);

            //reducing chain cooldown
            if (!(wp.isInPve() && projectile.getHit().size() > 2)) {
                wp.subtractRedCooldown(2);
            }
        }
    }

    @Override
    protected Location getProjectileStartingLocation(WarlordsEntity shooter, Location startingLocation) {
        return new LocationBuilder(startingLocation.clone()).addY(-.1);
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        ArmorStand armorStand = projectile.getWorld().spawn(projectile.getStartingLocation().clone().add(0, -1.7, 0), ArmorStand.class);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setMarker(true);
        armorStand.getEquipment().setHelmet(new ItemStack(Material.JUNGLE_SAPLING));
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
    protected String getActivationSound() {
        return "shaman.lightningbolt.activation";
    }

    @Override
    protected float getSoundVolume() {
        return 2;
    }

    @Override
    protected float getSoundPitch() {
        return 1;
    }

    public double getHitbox() {
        return hitbox;
    }

    public void setHitbox(double hitbox) {
        this.hitbox = hitbox;
    }
}