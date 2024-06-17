package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractProjectile;
import com.ebicep.warlords.abilities.internal.Overheal;
import com.ebicep.warlords.abilities.internal.Splash;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.aquamancer.WaterBoltBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class WaterBolt extends AbstractProjectile implements WeaponAbilityIcon, Splash {

    public int teammatesHit = 0;
    public int enemiesHit = 0;

    private int maxFullDistance = 40;
    private float directHitMultiplier = 15;
    private FloatModifiable splashRadius = new FloatModifiable(4);
    private float minDamage = 231;
    private float maxDamage = 299;

    public WaterBolt() {
        super("Water Bolt", 315, 434, 0, 80, 20, 175, 2, 300, true);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Shoot a bolt of water that will burst for")
                               .append(formatRange(minDamage, maxDamage, NamedTextColor.RED))
                               .append(Component.text(" damage and restore"))
                               .append(formatRangeHealing(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" health to allies. A direct hit will cause "))
                               .append(Component.text(format(directHitMultiplier) + "%", NamedTextColor.GOLD))
                               .append(Component.text(" increased damage or healing for the target hit."))
                               .append(Component.text("\n\nHas an optimal range of "))
                               .append(Component.text(maxFullDistance, NamedTextColor.YELLOW))
                               .append(Component.text(" blocks."))
                               .append(Component.text("\n\nWater Bolt can overheal allies for up to "))
                               .append(Component.text("10%", NamedTextColor.GREEN))
                               .append(Component.text(" of their max health as bonus health for "))
                               .append(Component.text(Overheal.OVERHEAL_DURATION, NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Shots Fired", "" + timesUsed));
        info.add(new Pair<>("Direct Hits", "" + directHits));
        info.add(new Pair<>("Players Hit", "" + playersHit));
        info.add(new Pair<>("Teammates Hit", "" + teammatesHit));
        info.add(new Pair<>("Enemies Hit", "" + enemiesHit));
        info.add(new Pair<>("Dismounts", "" + numberOfDismounts));

        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new WaterBoltBranch(abilityTree, this);
    }

    @Override
    protected void playEffect(@Nonnull Location currentLocation, int animationTimer) {
        World world = currentLocation.getWorld();
        world.spawnParticle(Particle.DRIP_WATER, currentLocation, 2, 0.3, 0.3, 0.3, 0.1, null, true);
        world.spawnParticle(Particle.ENCHANTMENT_TABLE, currentLocation, 1, 0, 0, 0, 0.1, null, true);
        world.spawnParticle(Particle.VILLAGER_HAPPY, currentLocation, 1, 0, 0, 0, 0.1, null, true);
        world.spawnParticle(Particle.CLOUD, currentLocation, 1, 0, 0, 0, 0, null, true);
    }

    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        WarlordsEntity shooter = projectile.getShooter();
        Location startingLocation = projectile.getStartingLocation();
        Location currentLocation = projectile.getCurrentLocation();
        World world = currentLocation.getWorld();

        world.spawnParticle(Particle.HEART, currentLocation, 3, 1, 1, 1, 0.2, null, true);
        world.spawnParticle(Particle.VILLAGER_HAPPY, currentLocation, 5, 1, 1, 1, 0.2, null, true);

        Utils.playGlobalSound(currentLocation, "mage.waterbolt.impact", 2, 1);

        double distanceSquared = currentLocation.distanceSquared(startingLocation);
        double toReduceBy = maxFullDistance * maxFullDistance > distanceSquared ? 1 :
                            1 - (Math.sqrt(distanceSquared) - maxFullDistance) / 75;
        if (toReduceBy < .2) {
            toReduceBy = .2;
        }
        if (hit != null && !projectile.getHit().contains(hit)) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
            float cc = pveMasterUpgrade2 ? 100 : critChance;
            if (hit.isTeammate(shooter)) {
                teammatesHit++;
                hit.addHealingInstance(shooter,
                        name,
                        (float) (minDamageHeal.getCalculatedValue() * convertToMultiplicationDecimal(directHitMultiplier) * toReduceBy),
                        (float) (maxDamageHeal.getCalculatedValue() * convertToMultiplicationDecimal(directHitMultiplier) * toReduceBy),
                        cc,
                        critMultiplier,
                        EnumSet.of(InstanceFlags.CAN_OVERHEAL_OTHERS)
                );
                if (hit != shooter) {
                    Overheal.giveOverHeal(shooter, hit);
                }
                if (pveMasterUpgrade) {
                    increaseDamageOnHit(shooter, hit);
                }
            } else {
                enemiesHit++;
                if (hit.onHorse()) {
                    numberOfDismounts++;
                }
                hit.addDamageInstance(shooter,
                        name,
                        (float) (minDamage * convertToMultiplicationDecimal(directHitMultiplier) * toReduceBy),
                        (float) (maxDamage * convertToMultiplicationDecimal(directHitMultiplier) * toReduceBy),
                        cc,
                        critMultiplier
                );
            }
        }

        int playersHit = 0;
        float radius = splashRadius.getCalculatedValue();
        for (WarlordsEntity nearEntity : PlayerFilter
                .entitiesAround(currentLocation, radius, radius, radius)
                .isAlive()
                .excluding(projectile.getHit())
        ) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(nearEntity));
            playersHit++;
            if (nearEntity.isTeammate(shooter)) {
                teammatesHit++;
                nearEntity.addHealingInstance(
                        shooter,
                        name,
                        (float) (minDamageHeal.getCalculatedValue() * toReduceBy),
                        (float) (maxDamageHeal.getCalculatedValue() * toReduceBy),
                        critChance,
                        critMultiplier,
                        EnumSet.of(InstanceFlags.CAN_OVERHEAL_OTHERS)
                );
                if (nearEntity != shooter) {
                    Overheal.giveOverHeal(shooter, nearEntity);
                }
                if (pveMasterUpgrade) {
                    increaseDamageOnHit(shooter, nearEntity);
                }
            } else {
                enemiesHit++;
                if (nearEntity.onHorse()) {
                    numberOfDismounts++;
                }
                nearEntity.addDamageInstance(
                        shooter,
                        name,
                        (float) (minDamage * toReduceBy),
                        (float) (maxDamage * toReduceBy),
                        critChance,
                        critMultiplier
                );
            }
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
        return "mage.waterbolt.activation";
    }

    @Override
    protected float getSoundVolume() {
        return 2;
    }

    @Override
    protected float getSoundPitch() {
        return 1;
    }

    private void increaseDamageOnHit(WarlordsEntity giver, WarlordsEntity hit) {
        hit.getCooldownManager().removeCooldown(WaterBolt.class, false);
        hit.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "BOLT DMG",
                WaterBolt.class,
                new WaterBolt(),
                giver,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                10 * 20
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * 1.1f;
            }
        });
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

    public float getMinDamage() {
        return minDamage;
    }

    public void setMinDamage(float minDamage) {
        this.minDamage = minDamage;
    }

    public float getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(float maxDamage) {
        this.maxDamage = maxDamage;
    }


    @Override
    public FloatModifiable getSplashRadius() {
        return splashRadius;
    }
}
