package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractPiercingProjectile;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class NotAShield extends AbstractPiercingProjectile {

    private double hitBox = 3;
    private float runeTickIncrease = 1.5f;
    private int allyHitDamageReduction = 10;
    private int allyHexStacks = 1;
    private int allyHexTickDuration = 60;
    private int maxAlliesHit = 10;

    public NotAShield() {
        super("Not A Shield", 329, 445, 12, 45, 20, 165, 1, 20, true);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Throw a large shield forward that cuts through all enemies and allies. Enemies hit take ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage and have their rune timers increase by "))
                               .append(Component.text(format(runeTickIncrease), NamedTextColor.GOLD))
                               .append(Component.text(". Allies hit pick up a piece of the shield, reducing its damage by "))
                               .append(Component.text(allyHitDamageReduction + "%", NamedTextColor.RED))
                               .append(Component.text(" while giving them "))
                               .append(Component.text(allyHexStacks, NamedTextColor.BLUE))
                               .append(Component.text(" stack of Fortifying Hex that lasts "))
                               .append(Component.text(format(allyHexTickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text("seconds. Has a range of "))
                               .append(Component.text(format(maxDistance / 2), NamedTextColor.YELLOW))
                               .append(Component.text("blocks.\n\nAfter traveling "))
                               .append(Component.text(format(maxDistance / 2), NamedTextColor.YELLOW))
                               .append(Component.text(" blocks, the shield returns to the location you threw it at, hitting all possible targets again. If "))
                               .append(Component.text(maxAlliesHit, NamedTextColor.YELLOW))
                               .append(Component.text(" allies are hit with this shield, the shield shatters, ending its trajectory."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        return info;
    }

    @Override
    protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {

    }

    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        return 0;
    }

    @Override
    protected void updateSpeed(InternalProjectile projectile) {
        Location startingLocation = projectile.getStartingLocation();
        Location currentLocation = projectile.getCurrentLocation();
        Vector speed = projectile.getSpeed();
        double distance = startingLocation.distanceSquared(currentLocation);
        if (distance > (maxDistance * maxDistance) / 4) {
            speed.multiply(-1);
        }
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
    protected void onNonCancellingHit(@Nonnull InternalProjectile projectile, @Nonnull WarlordsEntity hit, @Nonnull Location impactLocation) {
        WarlordsEntity wp = projectile.getShooter();
        Location currentLocation = projectile.getCurrentLocation();

        getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
        if (hit.onHorse()) {
            numberOfDismounts++;
        }
        List<WarlordsEntity> hits = projectile.getHit();
        boolean isTeammate = hit.isTeammate(wp);
        int teammatesHit = (int) hits.stream().filter(we -> we.isTeammate(wp)).count();
        if (isTeammate) {
            FortifyingHex.giveFortifyingHex(wp, hit);
            if (teammatesHit >= 10) {
                wp.playSound(impactLocation, Sound.ITEM_SHIELD_BREAK, 1, 1);
                projectile.cancel();
            }
        } else {
            float reduction = 1 - (teammatesHit * allyHitDamageReduction / 100f);
//            hit.addDamageInstance(
//                    wp,
//                    name,
//                    minDamageHeal.getCalculatedValue() * reduction,
//                    maxDamageHeal.getCalculatedValue() * reduction,
//                    critChance,
//                    critMultiplier
//            );
            hit.getSpec().increaseAllCooldownTimersBy(runeTickIncrease);
            wp.playSound(impactLocation, Sound.ITEM_SHIELD_BLOCK, 1, 1);
        }
    }

    @Override
    protected Location modifyProjectileStartingLocation(WarlordsEntity shooter, Location startingLocation) {
        return new LocationBuilder(startingLocation.clone()).addY(-.5).backward(0f);
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        ArmorStand fallenSoul = Utils.spawnArmorStand(projectile.getStartingLocation().clone().add(0, -1.7, 0), armorStand -> {
            armorStand.setMarker(true);
            armorStand.getEquipment().setHelmet(new ItemStack(Material.SHIELD));
        });

        projectile.addTask(new InternalProjectileTask() {
            @Override
            public void run(InternalProjectile projectile) {
                fallenSoul.teleport(projectile.getCurrentLocation().clone().add(0, -1.7, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                projectile.getCurrentLocation().getWorld().spawnParticle(
                        Particle.CRIT,
                        projectile.getCurrentLocation().clone().add(0, 0, 0),
                        2,
                        0,
                        0,
                        0,
                        0,
                        null,
                        true
                );
            }

            @Override
            public void onDestroy(InternalProjectile projectile) {
                fallenSoul.remove();
                projectile.getCurrentLocation().getWorld().spawnParticle(
                        Particle.CRIT_MAGIC,
                        projectile.getCurrentLocation(),
                        1,
                        0,
                        0,
                        0,
                        0.7f,
                        null,
                        true
                );
            }
        });
    }

    @Nullable
    @Override
    protected String getActivationSound() {
        return null;
    }

    @Override
    protected float getSoundVolume() {
        return 0;
    }

    @Override
    protected float getSoundPitch() {
        return 0;
    }
}
