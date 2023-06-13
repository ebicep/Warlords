package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractPiercingProjectile;
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
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public class MercifulHex extends AbstractPiercingProjectile {

    private int minDamage = 310;
    private int maxDamage = 418;
    private int subsequentReduction = 30;
    private int minSelfHeal = 329;
    private int maxSelfHeal = 443;
    private double hitBox = 3.5;

    public MercifulHex() {
        super("Merciful Hex", 438, 591, 0, 100, 20, 180, 2.5, 20, true);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Send a merciful gust of wind forward, passing through all allies and enemies. The first ally and enemy to receive the wind will heal for ")
                               .append(formatRangeHealing(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" health and take "))
                               .append(formatRangeDamage(minDamage, maxDamage))
                               .append(Component.text(" damage, respectively. All other allies and enemies the wind passes through will only receive "))
                               .append(Component.text(subsequentReduction, NamedTextColor.YELLOW))
                               .append(Component.text(" of the effect. Also heal yourself by "))
                               .append(formatRangeHealing(minSelfHeal, maxSelfHeal))
                               .append(Component.text(".\n\nHas a maximum range of "))
                               .append(Component.text(format(maxDistance), NamedTextColor.YELLOW))
                               .append(Component.text(" blocks."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {

    }

    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        return 0;
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

        Utils.playGlobalSound(currentLocation, "shaman.lightningbolt.impact", 2, 1);

        for (WarlordsEntity warlordsEntity : PlayerFilter
                .entitiesAround(currentLocation, hitBox, hitBox, hitBox)
                .excluding(projectile.getHit())
        ) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(warlordsEntity));
            if (warlordsEntity.onHorse()) {
                numberOfDismounts++;
            }
            Set<WarlordsEntity> hits = getHit(projectile);
            boolean isTeammate = warlordsEntity.isTeammate(wp);
            if (isTeammate) {
                int teammatesHit = (int) hits.stream().filter(we -> we.isTeammate(wp)).count();
                float reduction = 1 - subsequentReduction / 100f;
                boolean firstHit = teammatesHit == 1;
                if (firstHit) {
                    reduction = 1;
                    wp.addHealingInstance(
                            wp,
                            name,
                            minSelfHeal,
                            maxSelfHeal,
                            critChance,
                            critMultiplier,
                            false,
                            false
                    );
                }
                warlordsEntity.addHealingInstance(
                        wp,
                        name,
                        minDamageHeal * reduction,
                        maxDamageHeal * reduction,
                        critChance,
                        critMultiplier,
                        false,
                        false
                );

            } else {
                int enemiesHit = (int) hits.stream().filter(we -> we.isEnemy(wp)).count();
                float reduction = enemiesHit == 1 ? 1 : (1 - subsequentReduction / 100f);
                warlordsEntity.addDamageInstance(
                        wp,
                        name,
                        minDamage * reduction,
                        maxDamage * reduction,
                        critChance,
                        critMultiplier,
                        false
                );
            }
        }
    }

    @Override
    protected Location getProjectileStartingLocation(WarlordsEntity shooter, Location startingLocation) {
        return new LocationBuilder(startingLocation.clone()).addY(-.5).backward(0f);
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        ArmorStand fallenSoul = Utils.spawnArmorStand(projectile.getStartingLocation().clone().add(0, -1.7, 0), armorStand -> {
            armorStand.setMarker(true);
            armorStand.getEquipment().setHelmet(new ItemStack(Material.ACACIA_FENCE_GATE));
            armorStand.setHeadPose(new EulerAngle(-Math.atan2(
                    projectile.getSpeed().getY(),
                    Math.sqrt(
                            Math.pow(projectile.getSpeed().getX(), 2) +
                                    Math.pow(projectile.getSpeed().getZ(), 2)
                    )
            ), 0, Math.toRadians(90))); //TODO shift hitbox if no new model is created
        });

        projectile.addTask(new InternalProjectileTask() {
            @Override
            public void run(InternalProjectile projectile) {
                fallenSoul.teleport(projectile.getCurrentLocation().clone().add(0, -1.7, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                projectile.getCurrentLocation().getWorld().spawnParticle(
                        Particle.SPELL_WITCH,
                        projectile.getCurrentLocation().clone().add(0, 0, 0),
                        1,
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
                        Particle.SPELL_WITCH,
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
