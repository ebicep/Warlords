package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractProjectile;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.cryomancer.FrostboltBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FrostBolt extends AbstractProjectile {

    private int maxFullDistance = 30;
    private double directHitMultiplier = 1.15;
    private float hitbox = 4;
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
                               .append(Component.text("15%", NamedTextColor.RED))
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
    protected void playEffect(@Nonnull Location currentLocation, int animationTimer) {
        currentLocation.getWorld().spawnParticle(
                Particle.CLOUD,
                currentLocation,
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
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        WarlordsEntity shooter = projectile.getShooter();
        Location startingLocation = projectile.getStartingLocation();
        Location currentLocation = projectile.getCurrentLocation();
        World world = currentLocation.getWorld();

        Utils.playGlobalSound(currentLocation, "mage.frostbolt.impact", 2, 1);

        world.spawnParticle(Particle.EXPLOSION_LARGE, currentLocation, 1, 0, 0, 0, 0, null, true);
        world.spawnParticle(Particle.CLOUD, currentLocation, 3, .3, .3, .3, 1, null, true);


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
                    (float) (minDamageHeal * directHitMultiplier * toReduceBy),
                    (float) (maxDamageHeal * directHitMultiplier * toReduceBy),
                    critChance,
                    critMultiplier
            );
            if (pveMasterUpgrade) {
                freezeExplodeOnHit(shooter, hit);
            }
        }

        int playersHit = 0;
        for (WarlordsEntity nearEntity : PlayerFilter
                .entitiesAround(currentLocation, hitbox, hitbox, hitbox)
                .aliveEnemiesOf(shooter)
                .excluding(projectile.getHit())
        ) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(nearEntity));
            playersHit++;
            if (nearEntity.onHorse()) {
                numberOfDismounts++;
            }
            nearEntity.addSpeedModifier(shooter, "Frostbolt", -slowness, 2 * 20);
            nearEntity.addDamageInstance(
                    shooter,
                    name,
                    (float) (minDamageHeal * toReduceBy),
                    (float) (maxDamageHeal * toReduceBy),
                    critChance,
                    critMultiplier
            );
        }

        return playersHit;
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        this.playEffect(projectile);
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
        return 1;
    }

    private void freezeExplodeOnHit(WarlordsEntity giver, WarlordsEntity hit) {
        new GameRunnable(giver.getGame()) {
            @Override
            public void run() {
                for (WarlordsEntity freezeTarget : PlayerFilter
                        .entitiesAround(hit, 3, 3, 3)
                        .aliveEnemiesOf(giver)
                ) {
                    new FallingBlockWaveEffect(freezeTarget.getLocation(), 3, 1.1, Material.PACKED_ICE).play();
                    Utils.playGlobalSound(freezeTarget.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 2, 0.7f);
                    Utils.playGlobalSound(freezeTarget.getLocation(), Sound.BLOCK_GLASS_BREAK, 2, 0.1f);
                    freezeTarget.addDamageInstance(giver, name, 409, 554, -1, 100);
                }
            }
        }.runTaskLater(30);
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new FrostboltBranch(abilityTree, this);
    }

    public int getMaxFullDistance() {
        return maxFullDistance;
    }

    public void setMaxFullDistance(int maxFullDistance) {
        this.maxFullDistance = maxFullDistance;
    }

    public double getDirectHitMultiplier() {
        return directHitMultiplier;
    }

    public void setDirectHitMultiplier(double directHitMultiplier) {
        this.directHitMultiplier = directHitMultiplier;
    }

    public float getHitbox() {
        return hitbox;
    }

    public void setHitbox(float hitbox) {
        this.hitbox = hitbox;
    }

    public int getSlowness() {
        return slowness;
    }

    public void setSlowness(int slowness) {
        this.slowness = slowness;
    }


}
