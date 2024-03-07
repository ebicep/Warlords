package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractProjectile;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.abilities.internal.Splash;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.pyromancer.FireballBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class Fireball extends AbstractProjectile implements WeaponAbilityIcon, Splash {

    private int maxFullDistance = 50;
    private float directHitMultiplier = 15;
    private FloatModifiable splashRadius = new FloatModifiable(4);

    public Fireball() {
        super("Fireball", 334.4f, 433.4f, 0, 70, 20, 175, 2, 300, false);
    }

    public Fireball(float cooldown) {
        super("Fireball", 334.4f, 433.4f, cooldown, 70, 20, 175, 2, 300, false);
    }

    public Fireball(float minDamageHeal, float maxDamageHeal, float cooldown) {
        super("Fireball", minDamageHeal, maxDamageHeal, cooldown, 70, 20, 175, 2, 300, false);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Shoot a fireball that will explode for ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage. A direct hit will cause the enemy to take an additional "))
                               .append(Component.text(format(directHitMultiplier) + "%", NamedTextColor.RED))
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
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new FireballBranch(abilityTree, this);
    }

    @Override
    protected void playEffect(@Nonnull Location currentLocation, int animationTimer) {
        EffectUtils.displayParticle(Particle.DRIP_LAVA, currentLocation, 5, 0, 0, 0, 0.35);
        EffectUtils.displayParticle(Particle.SMOKE_NORMAL, currentLocation, 7, 0, 0, 0, 0.001);
        EffectUtils.displayParticle(Particle.FLAME, currentLocation, 1, 0, 0, 0, 0.06);
    }

    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        WarlordsEntity shooter = projectile.getShooter();
        Location startingLocation = projectile.getStartingLocation();
        Location currentLocation = projectile.getCurrentLocation();

        Utils.playGlobalSound(currentLocation, "mage.fireball.impact", 2, 1);

        EffectUtils.displayParticle(Particle.EXPLOSION_LARGE, currentLocation, 5, 0, 0, 0, 0.35);
        EffectUtils.displayParticle(Particle.LAVA, currentLocation, 10, 0.5F, 0, 0.5F, 1.5);
        EffectUtils.displayParticle(Particle.CLOUD, currentLocation, 3, 0.3F, 0.3F, 0.3F, 1);

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
                    (float) (minDamageHeal.getCalculatedValue() * convertToMultiplicationDecimal(directHitMultiplier) * toReduceBy),
                    (float) (maxDamageHeal.getCalculatedValue() * convertToMultiplicationDecimal(directHitMultiplier) * toReduceBy),
                    critChance,
                    critMultiplier
            );

            if (pveMasterUpgrade) {
                applyBurnEffect(hit, shooter);
            } else if (pveMasterUpgrade2) {
                applyIgniteEffect(hit, shooter);
            }
        }

        int playersHit = 0;
        float radius = splashRadius.getCalculatedValue();
        for (WarlordsEntity nearEntity : PlayerFilter
                .entitiesAround(currentLocation, radius, radius, radius)
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
                    (float) (minDamageHeal.getCalculatedValue() * toReduceBy),
                    (float) (maxDamageHeal.getCalculatedValue() * toReduceBy),
                    critChance,
                    critMultiplier
            );
        }

        return playersHit;
    }

    private void applyBurnEffect(@Nonnull WarlordsEntity hit, WarlordsEntity shooter) {
        hit.getCooldownManager().removeCooldownByName("Burn");
        hit.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Burn",
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
                        healthDamage = DamageCheck.clamp(healthDamage);
                        hit.addDamageInstance(
                                shooter,
                                "Burn",
                                healthDamage,
                                healthDamage,
                                0,
                                100,
                                EnumSet.of(InstanceFlags.DOT)
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

    private void applyIgniteEffect(@Nonnull WarlordsEntity hit, WarlordsEntity shooter) {
        if (hit.getCooldownManager().hasCooldownFromName("Ignite")) {
            return;
        }
        hit.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Ignite",
                "IGN",
                Fireball.class,
                new Fireball(),
                shooter,
                CooldownTypes.DEBUFF,
                cooldownManager -> {
                    PlayerFilter.entitiesAround(hit, 3, 3, 3)
                                .aliveTeammatesOf(hit)
                                .forEach(warlordsEntity -> {
                                    warlordsEntity.addDamageInstance(
                                            shooter,
                                            "Ignite",
                                            450,
                                            650,
                                            0,
                                            100,
                                            EnumSet.of(InstanceFlags.TRUE_DAMAGE)
                                    );
                                });
                },
                20
        ));
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

    @Override
    public void runEveryTick(@javax.annotation.Nullable WarlordsEntity warlordsEntity) {
        splashRadius.tick();
        super.runEveryTick(warlordsEntity);
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

    @Override
    public FloatModifiable getSplashRadius() {
        return splashRadius;
    }

}
