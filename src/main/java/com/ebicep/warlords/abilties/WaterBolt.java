package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractProjectileBase;
import com.ebicep.warlords.abilties.internal.Overheal;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.WarlordsEntity;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class WaterBolt extends AbstractProjectileBase {

    private int teammatesHit = 0;
    private int enemiesHit = 0;

    private static final int MAX_FULL_DAMAGE_DISTANCE = 40;
    private static final double DIRECT_HIT_MULTIPLIER = 1.15;
    private static final float HITBOX = 4;

    public WaterBolt() {
        super("Water Bolt", 315, 434, 0, 80, 20, 175, 2, 300, true);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Shoot a bolt of water that will burst\n" +
                "§7for §c231 §7- §c299 §7damage and restore\n" +
                "§a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7health to allies. A\n" +
                "§7direct hit will cause §a15% §7increased\n" +
                "§7damage or healing for the target hit." +
                "\n\n" +
                "§7Has an optimal range of §e" + MAX_FULL_DAMAGE_DISTANCE + " §7blocks." +
                "\n\n" +
                "§7Water Bolt can overheal allies for up to\n" +
                "§a10% §7of their max health as bonus health\n" +
                "§7for §6" + Overheal.OVERHEAL_DURATION + " §7seconds.";
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
    protected String getActivationSound() {
        return "mage.waterbolt.activation";
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
    protected void playEffect(@Nonnull Location currentLocation, int animationTimer) {
        ParticleEffect.DRIP_WATER.display(0.3f, 0.3f, 0.3f, 0.1F, 2, currentLocation, 500);
        ParticleEffect.ENCHANTMENT_TABLE.display(0, 0, 0, 0.1F, 1, currentLocation, 500);
        ParticleEffect.VILLAGER_HAPPY.display(0, 0, 0, 0.1F, 1, currentLocation, 500);
        ParticleEffect.CLOUD.display(0, 0, 0, 0F, 1, currentLocation, 500);
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        this.playEffect(projectile);
    }

    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        WarlordsEntity shooter = projectile.getShooter();
        Location startingLocation = projectile.getStartingLocation();
        Location currentLocation = projectile.getCurrentLocation();

        ParticleEffect.HEART.display(1, 1, 1, 0.2F, 3, currentLocation, 500);
        ParticleEffect.VILLAGER_HAPPY.display(1, 1, 1, 0.2F, 5, currentLocation, 500);

        Utils.playGlobalSound(currentLocation, "mage.waterbolt.impact", 2, 1);

        double distanceSquared = currentLocation.distanceSquared(startingLocation);
        double toReduceBy = MAX_FULL_DAMAGE_DISTANCE * MAX_FULL_DAMAGE_DISTANCE > distanceSquared ? 1 :
                1 - (Math.sqrt(distanceSquared) - MAX_FULL_DAMAGE_DISTANCE) / 75;
        if (toReduceBy < .2) toReduceBy = .2;
        if (hit != null && !projectile.getHit().contains(hit)) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
            if (hit.isTeammate(shooter)) {
                teammatesHit++;
                hit.addHealingInstance(shooter,
                        name,
                        (float) (minDamageHeal * DIRECT_HIT_MULTIPLIER * toReduceBy),
                        (float) (maxDamageHeal * DIRECT_HIT_MULTIPLIER * toReduceBy),
                        critChance,
                        critMultiplier,
                        false, false);
                if (hit != shooter) {
                    hit.getCooldownManager().removeCooldownByObject(Overheal.OVERHEAL_MARKER);
                    hit.getCooldownManager().addRegularCooldown("Overheal",
                            "OVERHEAL", Overheal.class, Overheal.OVERHEAL_MARKER, shooter, CooldownTypes.BUFF, cooldownManager -> {
                            }, Overheal.OVERHEAL_DURATION * 20);
                    ;
                }
            } else {
                enemiesHit++;
                if (hit.onHorse()) {
                    numberOfDismounts++;
                }
                hit.addDamageInstance(shooter,
                        name,
                        (float) (231 * DIRECT_HIT_MULTIPLIER * toReduceBy),
                        (float) (299 * DIRECT_HIT_MULTIPLIER * toReduceBy),
                        critChance,
                        critMultiplier,
                        false);
            }
        }

        int playersHit = 0;
        for (WarlordsEntity nearEntity : PlayerFilter
                .entitiesAround(currentLocation, HITBOX, HITBOX, HITBOX)
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
                        (float) (minDamageHeal * toReduceBy),
                        (float) (maxDamageHeal * toReduceBy),
                        critChance,
                        critMultiplier,
                        false, false);
                if (nearEntity != shooter) {
                    nearEntity.getCooldownManager().removeCooldownByObject(Overheal.OVERHEAL_MARKER);
                    nearEntity.getCooldownManager().addRegularCooldown("Overheal",
                            "OVERHEAL", Overheal.class, Overheal.OVERHEAL_MARKER, shooter, CooldownTypes.BUFF, cooldownManager -> {
                            }, Overheal.OVERHEAL_DURATION * 20);
                    ;
                }
            } else {
                enemiesHit++;
                if (nearEntity.onHorse()) {
                    numberOfDismounts++;
                }
                nearEntity.addDamageInstance(
                        shooter,
                        name,
                        (float) (231 * toReduceBy),
                        (float) (299 * toReduceBy),
                        critChance,
                        critMultiplier,
                        false);
            }
        }

        return playersHit;
    }

}
