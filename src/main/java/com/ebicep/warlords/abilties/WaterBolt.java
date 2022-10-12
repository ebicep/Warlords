package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractProjectileBase;
import com.ebicep.warlords.abilties.internal.Overheal;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
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
    private boolean pveUpgrade = false;
    private int teammatesHit = 0;
    private int enemiesHit = 0;

    private int maxFullDistance = 40;
    private double directHitMultiplier = 1.15;
    private float hitbox = 4;

    private float minDamage = 231;
    private float maxDamage = 299;

    public WaterBolt() {
        super("Water Bolt", 315, 434, 0, 80, 20, 175, 2, 300, true);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Shoot a bolt of water that will burst for" +
                formatRangeDamage(minDamage, maxDamage) + "damage and restore" + formatRangeHealing(minDamageHeal, maxDamageHeal) +
                "health to allies. A direct hit will cause §a15% §7increased damage or healing for the target hit." +
                "\n\nHas an optimal range of §e" + maxFullDistance + " §7blocks." +
                "\n\nWater Bolt can overheal allies for up to §a10% §7of their max health as bonus health for §6" + Overheal.OVERHEAL_DURATION + " §7seconds.";
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
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        WarlordsEntity shooter = projectile.getShooter();
        Location startingLocation = projectile.getStartingLocation();
        Location currentLocation = projectile.getCurrentLocation();

        ParticleEffect.HEART.display(1, 1, 1, 0.2F, 3, currentLocation, 500);
        ParticleEffect.VILLAGER_HAPPY.display(1, 1, 1, 0.2F, 5, currentLocation, 500);

        Utils.playGlobalSound(currentLocation, "mage.waterbolt.impact", 2, 1);

        double distanceSquared = currentLocation.distanceSquared(startingLocation);
        double toReduceBy = maxFullDistance * maxFullDistance > distanceSquared ? 1 :
                1 - (Math.sqrt(distanceSquared) - maxFullDistance) / 75;
        if (toReduceBy < .2) {
            toReduceBy = .2;
        }
        if (hit != null && !projectile.getHit().contains(hit)) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
            if (hit.isTeammate(shooter)) {
                teammatesHit++;
                hit.addHealingInstance(shooter,
                        name,
                        (float) (minDamageHeal * directHitMultiplier * toReduceBy),
                        (float) (maxDamageHeal * directHitMultiplier * toReduceBy),
                        critChance,
                        critMultiplier,
                        false, false
                );
                if (hit != shooter) {
                    hit.getCooldownManager().removeCooldownByObject(Overheal.OVERHEAL_MARKER);
                    hit.getCooldownManager().addRegularCooldown("Overheal",
                            "OVERHEAL", Overheal.class, Overheal.OVERHEAL_MARKER, shooter, CooldownTypes.BUFF, cooldownManager -> {
                            }, Overheal.OVERHEAL_DURATION * 20
                    );
                }
                if (pveUpgrade) {
                    increaseDamageOnHit(shooter, hit);
                }
            } else {
                enemiesHit++;
                if (hit.onHorse()) {
                    numberOfDismounts++;
                }
                hit.addDamageInstance(shooter,
                        name,
                        (float) (minDamage * directHitMultiplier * toReduceBy),
                        (float) (maxDamage * directHitMultiplier * toReduceBy),
                        critChance,
                        critMultiplier,
                        false
                );
            }
        }

        int playersHit = 0;
        for (WarlordsEntity nearEntity : PlayerFilter
                .entitiesAround(currentLocation, hitbox, hitbox, hitbox)
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
                        false, false
                );
                if (nearEntity != shooter) {
                    nearEntity.getCooldownManager().removeCooldownByObject(Overheal.OVERHEAL_MARKER);
                    nearEntity.getCooldownManager().addRegularCooldown("Overheal",
                            "OVERHEAL", Overheal.class, Overheal.OVERHEAL_MARKER, shooter, CooldownTypes.BUFF, cooldownManager -> {
                            }, Overheal.OVERHEAL_DURATION * 20
                    );
                    ;
                }
                if (pveUpgrade) {
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
                        critMultiplier,
                        false
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

    private void increaseDamageOnHit(WarlordsEntity giver, WarlordsEntity hit) {
        hit.getCooldownManager().removeCooldown(WaterBolt.class);
        hit.getCooldownManager().addCooldown(new RegularCooldown<WaterBolt>(
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
                return currentDamageValue * 1.15f;
            }
        });
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

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }
}
