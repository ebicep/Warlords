package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public abstract class AbstractGroundSlam extends AbstractAbility implements PurpleAbilityIcon, HitBox {

    public int playersHit = 0;
    public int carrierHit = 0;
    public int warpsKnockbacked = 0;

    private FloatModifiable slamSize = new FloatModifiable(6);
    private float velocity = 1.25f;
    protected boolean trueDamage = false;

    public AbstractGroundSlam(float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, float critChance, float critMultiplier) {
        this(minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, 0);
    }

    public AbstractGroundSlam(float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, float critChance, float critMultiplier, float startCooldown) {
        super("Ground Slam", minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, startCooldown);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Slam the ground, creating a shockwave around you that deals ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage and knocks enemies back slightly."));

    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        Utils.playGlobalSound(wp.getLocation(), "warrior.groundslam.activation", 2, 1);

        UUID abilityUUID = UUID.randomUUID();
        activateAbility(wp, 1, abilityUUID, false);

        if (pveMasterUpgrade || pveMasterUpgrade2) {
            wp.setVelocity(name, new Vector(0, 1.2, 0), true);
            new GameRunnable(wp.getGame()) {
                boolean wasOnGround = true;
                int counter = 0;

                @Override
                public void run() {
                    counter++;
                    // if player never lands in the span of 10 seconds, remove damage.
                    if (counter == 200 || wp.isDead()) {
                        this.cancel();
                    }

                    boolean hitGround = wp.getEntity().isOnGround();

                    if (wasOnGround && !hitGround) {
                        wasOnGround = false;
                    }

                    if (!wasOnGround && hitGround) {
                        wasOnGround = true;

                        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_IRON_GOLEM_DEATH, 2, 0.2f);
                        Utils.playGlobalSound(wp.getLocation(), "warrior.groundslam.activation", 2, 0.8f);
                        activateAbility(wp, pveMasterUpgrade ? 1.5f : 1f, abilityUUID, true);
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 0);
        }
        return true;
    }

    protected void activateAbility(@Nonnull WarlordsEntity wp, float damageMultiplier, UUID abilityUUID, boolean second) {
        List<List<Location>> fallingBlockLocations = new ArrayList<>();
        Set<WarlordsEntity> currentPlayersHit = new HashSet<>();
        Location location = wp.getLocation();

        float radius = slamSize.getCalculatedValue();
        for (int i = 0; i < radius; i++) {
            fallingBlockLocations.add(LocationUtils.getCircle(location, i, (i * ((int) (Math.PI * 2)))));
        }

        fallingBlockLocations.get(0).add(wp.getLocation());

        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                for (List<Location> fallingBlockLocation : fallingBlockLocations) {
                    for (Location location : fallingBlockLocation) {
                        Utils.addFallingBlock(location);
                        // Damage
                        for (WarlordsEntity slamTarget : PlayerFilter
                                .entitiesAroundRectangle(location.clone().add(0, -.75, 0), 0.75, 4.5, 0.75)
                                .aliveEnemiesOf(wp)
                                .excluding(currentPlayersHit)
                        ) {
                            playersHit++;
                            if (slamTarget.hasFlag()) {
                                carrierHit++;
                            }

                            if (slamTarget.getCooldownManager().hasCooldownExtends(AbstractTimeWarp.class) && FlagHolder.playerTryingToPick(slamTarget)) {
                                warpsKnockbacked++;
                            }

                            currentPlayersHit.add(slamTarget);
                            final Location loc = slamTarget.getLocation();
                            final Vector v = wp.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-velocity).setY(0.25);
                            slamTarget.setVelocity(name, v, false, false);
                            slamDamage(wp, slamTarget, damageMultiplier, abilityUUID);
                        }
                    }

                    fallingBlockLocations.remove(fallingBlockLocation);
                    break;
                }

                if (fallingBlockLocations.isEmpty()) {
                    if (second) {
                        onSecondSlamHit(wp, currentPlayersHit);
                    }
                    this.cancel();
                }
            }

        }.runTaskTimer(0, 2);
    }

    protected void slamDamage(WarlordsEntity wp, WarlordsEntity slamTarget, float damageMultiplier, UUID abilityUUID) {

    }

    protected void onSecondSlamHit(WarlordsEntity wp, Set<WarlordsEntity> playersHit) {

    }

    @Override
    public void runEveryTick(@Nullable WarlordsEntity warlordsEntity) {
        slamSize.tick();
        super.runEveryTick(warlordsEntity);
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public void setTrueDamage(boolean trueDamage) {
        this.trueDamage = trueDamage;
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return slamSize;
    }
}

