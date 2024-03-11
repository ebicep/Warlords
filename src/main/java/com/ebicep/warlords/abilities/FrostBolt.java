package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractPiercingProjectile;
import com.ebicep.warlords.abilities.internal.Splash;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.cryomancer.FrostboltBranch;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FrostBolt extends AbstractPiercingProjectile implements WeaponAbilityIcon, Splash {

    private int maxFullDistance = 30;
    private float directHitMultiplier = 15;
    private FloatModifiable splash = new FloatModifiable(4);
    private int slowness = 25;

    public FrostBolt() {
        super("Frostbolt", 268.8f, 345.45f, 0, 70, 20, 175, 2, 300, false);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Shoot a frostbolt that will shatter for ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage and slow by "))
                               .append(Component.text(slowness + "%", NamedTextColor.YELLOW))
                               .append(Component.text("for "))
                               .append(Component.text("2", NamedTextColor.GOLD))
                               .append(Component.text(" seconds. A direct hit will cause the enemy to take an additional "))
                               .append(Component.text(format(directHitMultiplier) + "%", NamedTextColor.RED))
                               .append(Component.text(" extra damage."))
                               .append(Component.newline())
                               .append(Component.text("Has an optimal range of "))
                               .append(Component.text(maxFullDistance, NamedTextColor.YELLOW))
                               .append(Component.text(" blocks."));

    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Shots Fired", "" + timesUsed));
        info.add(new Pair<>("Direct Hits", "" + directHits));
        info.add(new Pair<>("Players Hit", "" + playersHit));
        info.add(new Pair<>("Dismounts", "" + numberOfDismounts));

        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new FrostboltBranch(abilityTree, this);
    }

    @Override
    protected void playEffect(@Nonnull Location currentLocation, int animationTimer) {
        if (pveMasterUpgrade2) {
            return;
        }
        EffectUtils.displayParticle(
                Particle.CLOUD,
                currentLocation,
                1
        );
    }

    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        if (pveMasterUpgrade2) {
            return 0;
        }
        WarlordsEntity shooter = projectile.getShooter();
        Location startingLocation = projectile.getStartingLocation();
        Location currentLocation = projectile.getCurrentLocation();

        Utils.playGlobalSound(currentLocation, "mage.frostbolt.impact", 2, 1);

        EffectUtils.displayParticle(Particle.EXPLOSION_LARGE, currentLocation, 1);
        EffectUtils.displayParticle(Particle.CLOUD, currentLocation, 3, 0.3, 0.3, 0.3, 1);


        double distanceSquared = currentLocation.distanceSquared(startingLocation);
        double toReduceBy = maxFullDistance * maxFullDistance > distanceSquared ? 1 :
                            1 - (Math.sqrt(distanceSquared) - maxFullDistance) / 75;
        if (toReduceBy < .2) {
            toReduceBy = .2;
        }
        if (hit != null && !projectile.getHit().contains(hit)) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
            if (hit.onHorse()) {
                numberOfDismounts++;
            }
            hit.addSpeedModifier(shooter, "Frostbolt", -slowness, 2 * 20);
            hit.addDamageInstance(
                    shooter,
                    name,
                    (float) (minDamageHeal.getCalculatedValue() * convertToMultiplicationDecimal(directHitMultiplier) * toReduceBy),
                    (float) (maxDamageHeal.getCalculatedValue() * convertToMultiplicationDecimal(directHitMultiplier) * toReduceBy),
                    critChance,
                    critMultiplier
            );
            if (pveMasterUpgrade) {
                freezeExplodeOnHit(shooter, hit);
            }
        }

        int playersHit = 0;
        float splashRadius = splash.getCalculatedValue();
        for (WarlordsEntity nearEntity : PlayerFilter
                .entitiesAround(currentLocation, splashRadius, splashRadius, splashRadius)
                .aliveEnemiesOf(shooter)
                .excluding(projectile.getHit())
        ) {
            playersHit = hit(projectile, shooter, toReduceBy, playersHit, nearEntity);
        }

        return playersHit;
    }

    @Override
    protected boolean shouldEndProjectileOnHit(@Nonnull InternalProjectile projectile, WarlordsEntity wp) {
        return !pveMasterUpgrade2;
    }

    @Override
    protected boolean shouldEndProjectileOnHit(@Nonnull InternalProjectile projectile, Block block) {
        return true;
    }

    @Override
    protected void onNonCancellingHit(@Nonnull InternalProjectile projectile, @Nonnull WarlordsEntity hit, @Nonnull Location impactLocation) {
        if (!pveMasterUpgrade2) {
            return;
        }
        WarlordsEntity shooter = projectile.getShooter();
        Location startingLocation = projectile.getStartingLocation();
        Location currentLocation = projectile.getCurrentLocation();

        double distanceSquared = currentLocation.distanceSquared(startingLocation);
        double toReduceBy = maxFullDistance * maxFullDistance > distanceSquared ? 1 :
                            1 - (Math.sqrt(distanceSquared) - maxFullDistance) / 75;
        if (toReduceBy < .2) {
            toReduceBy = .2;
        }
        if (projectile.getHit().size() == 0) {
            toReduceBy += .15;
        }
        hit(projectile, shooter, toReduceBy, playersHit, hit);
        hit.addSpeedModifier(shooter, "Splintered Ice", -25, 40);
        EffectUtils.displayParticle(
                Particle.SNOWBALL,
                hit.getLocation().add(0, 1, 0),
                10,
                .2,
                .2,
                .2,
                0
        );
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        this.playEffect(projectile);
        if (!pveMasterUpgrade2) {
            return;
        }
        List<ArmorStand> icicles = new ArrayList<>();
        LocationBuilder startLocation = new LocationBuilder(projectile.getStartingLocation().clone().add(0, -1.1, 0));
        for (int i = 0; i < 4; i++) {
            icicles.add(Utils.spawnArmorStand(startLocation, armorStand -> {
                armorStand.setMarker(true);
                armorStand.setSmall(true);
                armorStand.getEquipment().setHelmet(new ItemStack(Material.ICE));
                armorStand.setHeadPose(new EulerAngle(-Math.atan2(
                        projectile.getSpeed().getY(),
                        Math.sqrt(
                                Math.pow(projectile.getSpeed().getX(), 2) +
                                        Math.pow(projectile.getSpeed().getZ(), 2)
                        )
                ), 0, Math.toRadians(45)));
            }));
            startLocation.forward(.75);
        }

        projectile.addTask(new InternalProjectileTask() {
            @Override
            public void run(InternalProjectile projectile) {
                for (int i = 0; i < icicles.size(); i++) {
                    ArmorStand armorStand = icicles.get(i);
                    LocationBuilder location = new LocationBuilder(projectile.getCurrentLocation().clone().add(0, -1.1, 0));
                    location.forward(.75 * i);
                    armorStand.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            }

            @Override
            public void onDestroy(InternalProjectile projectile) {
                icicles.forEach(Entity::remove);
                EffectUtils.displayParticle(Particle.CLOUD, icicles.get(3).getLocation(), 10, 0.2, 0.2, 0.2, 0);
            }
        });
    }

    @Override
    protected String getActivationSound() {
        return "mage.frostbolt.activation";
    }

    @Override
    protected float getSoundVolume() {
        return 2;
    }

    @Override
    protected float getSoundPitch() {
        return pveMasterUpgrade2 ? 2f : 1;
    }

    private void freezeExplodeOnHit(WarlordsEntity giver, WarlordsEntity hit) {
        new GameRunnable(giver.getGame()) {
            @Override
            public void run() {
                new FallingBlockWaveEffect(hit.getLocation(), 3, 1.1, Material.PACKED_ICE).play();
                for (WarlordsEntity freezeTarget : PlayerFilter
                        .entitiesAround(hit, 3, 3, 3)
                        .aliveEnemiesOf(giver)
                ) {
                    Utils.playGlobalSound(freezeTarget.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 2, 0.7f);
                    Utils.playGlobalSound(freezeTarget.getLocation(), Sound.BLOCK_GLASS_BREAK, 2, 0.1f);
                    freezeTarget.addDamageInstance(giver, name, 409, 554, -1, 100);
                }
            }
        }.runTaskLater(30);
    }

    private int hit(@Nonnull InternalProjectile projectile, WarlordsEntity shooter, double damageModifier, int playersHit, WarlordsEntity nearEntity) {
        getProjectiles(projectile).forEach(p -> p.getHit().add(nearEntity));
        playersHit++;
        if (nearEntity.onHorse()) {
            numberOfDismounts++;
        }
        nearEntity.addSpeedModifier(shooter, "Frostbolt", -slowness, 2 * 20);
        nearEntity.addDamageInstance(
                shooter,
                name,
                (float) (minDamageHeal.getCalculatedValue() * damageModifier),
                (float) (maxDamageHeal.getCalculatedValue() * damageModifier),
                critChance,
                critMultiplier
        );
        return playersHit;
    }

    public int getMaxFullDistance() {
        return maxFullDistance;
    }

    public void setMaxFullDistance(int maxFullDistance) {
        this.maxFullDistance = maxFullDistance;
    }

    public float getDirectHitMultiplier() {
        return directHitMultiplier;
    }

    public void setDirectHitMultiplier(float directHitMultiplier) {
        this.directHitMultiplier = directHitMultiplier;
    }

    public int getSlowness() {
        return slowness;
    }

    public void setSlowness(int slowness) {
        this.slowness = slowness;
    }


    @Override
    public FloatModifiable getSplashRadius() {
        return splash;
    }
}
