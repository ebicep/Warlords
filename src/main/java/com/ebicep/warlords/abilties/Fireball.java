package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractProjectileBase;
import com.ebicep.warlords.abilties.internal.DamageCheck;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Fireball extends AbstractProjectileBase {

    private int maxFullDistance = 50;
    private double directHitMultiplier = 1.15;
    private float hitbox = 4;

    public Fireball() {
        super("Fireball", 334.4f, 433.4f, 0, 70, 20, 175, 2, 300, false);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Shoot a fireball that will explode for ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage. A direct hit will cause the enemy to take an additional "))
                               .append(Component.text("15%", NamedTextColor.RED))
                               .append(Component.text(" extra damage."))
                               .append(Component.text("\n\nHas an optimal range of "))
                               .append(Component.text(maxFullDistance, NamedTextColor.YELLOW))
                               .append(Component.text("blocks."));
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
        World world = currentLocation.getWorld();
        world.spawnParticle(Particle.DRIP_LAVA, currentLocation, 5, 0, 0, 0, 0.35, null, true);
        world.spawnParticle(Particle.SMOKE_NORMAL, currentLocation, 7, 0, 0, 0, 0.001, null, true);
        world.spawnParticle(Particle.FLAME, currentLocation, 1, 0, 0, 0, 0.06, null, true);
    }

    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        WarlordsEntity shooter = projectile.getShooter();
        Location startingLocation = projectile.getStartingLocation();
        Location currentLocation = projectile.getCurrentLocation();
        World world = currentLocation.getWorld();

        Utils.playGlobalSound(currentLocation, "mage.fireball.impact", 2, 1);

        world.spawnParticle(Particle.EXPLOSION_LARGE, currentLocation, 5, 0, 0, 0, 0.35, null, true);
        world.spawnParticle(Particle.LAVA, currentLocation, 10, 0.5F, 0, 0.5F, 1.5, null, true);
        world.spawnParticle(Particle.CLOUD, currentLocation, 3, 0.3F, 0.3F, 0.3F, 1, null, true);

        double distanceSquared = startingLocation.distanceSquared(currentLocation);
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
            hit.addDamageInstance(
                    shooter,
                    name,
                    (float) (minDamageHeal * directHitMultiplier * toReduceBy),
                    (float) (maxDamageHeal * directHitMultiplier * toReduceBy),
                    critChance,
                    critMultiplier,
                    false
            );

            if (pveUpgrade) {
                hit.getCooldownManager().removeCooldown(Fireball.class, false);
                hit.getCooldownManager().addCooldown(new RegularCooldown<>(
                        name,
                        "BRN",
                        Fireball.class,
                        new Fireball(),
                        shooter,
                        CooldownTypes.DEBUFF,
                        cooldownManager -> {
                        },
                        5 * 20,
                        Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                            if (ticksLeft % 20 == 0) {
                                float healthDamage = hit.getMaxHealth() * 0.005f;
                                if (healthDamage < DamageCheck.MINIMUM_DAMAGE) {
                                    healthDamage = DamageCheck.MINIMUM_DAMAGE;
                                }
                                if (healthDamage > DamageCheck.MAXIMUM_DAMAGE) {
                                    healthDamage = DamageCheck.MAXIMUM_DAMAGE;
                                }
                                hit.addDamageInstance(
                                        shooter,
                                        "Burn",
                                        healthDamage,
                                        healthDamage,
                                        0,
                                        100,
                                        false
                                );
                            }
                        })
                ) {
                    @Override
                    public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * 1.2f;
                    }
                });
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
            nearEntity.addDamageInstance(
                    shooter,
                    name,
                    (float) (minDamageHeal * toReduceBy),
                    (float) (maxDamageHeal * toReduceBy),
                    critChance,
                    critMultiplier,
                    false
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
        return "mage.fireball.activation";
    }

    @Override
    protected float getSoundVolume() {
        return 2;
    }

    @Override
    protected float getSoundPitch() {
        return 1;
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


}
