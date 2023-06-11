package com.ebicep.warlords.abilties;

import com.ebicep.customentities.nms.CustomFallingBlock;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.AbstractTimeWarp;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroundSlam extends AbstractAbility {

    public int playersHit = 0;
    public int carrierHit = 0;
    public int warpsKnockbacked = 0;

    private int slamSize = 6;
    private float velocity = 1.25f;
    private boolean trueDamage = false;

    public GroundSlam(float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, float critChance, float critMultiplier) {
        super("Ground Slam", minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
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
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(wp.getLocation(), "warrior.groundslam.activation", 2, 1);

        UUID abilityUUID = UUID.randomUUID();
        activateAbility(wp, 1, abilityUUID);

        if (pveUpgrade) {
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
                        activateAbility(wp, 1.5f, abilityUUID);
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 0);
        }
        return true;
    }

    private void activateAbility(@Nonnull WarlordsEntity wp, float damageMultiplier, UUID abilityUUID) {
        List<List<Location>> fallingBlockLocations = new ArrayList<>();
        List<CustomFallingBlock> customFallingBlocks = new ArrayList<>();
        List<WarlordsEntity> currentPlayersHit = new ArrayList<>();
        Location location = wp.getLocation();

        for (int i = 0; i < slamSize; i++) {
            fallingBlockLocations.add(getCircle(location, i, (i * ((int) (Math.PI * 2)))));
        }

        fallingBlockLocations.get(0).add(wp.getLocation());

        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                for (List<Location> fallingBlockLocation : fallingBlockLocations) {
                    for (Location location : fallingBlockLocation) {
                        if (location.getWorld().getBlockAt(location.clone().add(0, 1, 0)).getType() == Material.AIR) {
                            FallingBlock fallingBlock = addFallingBlock(location.clone());
                            customFallingBlocks.add(new CustomFallingBlock(fallingBlock, wp, GroundSlam.this));
                            WarlordsEvents.addEntityUUID(fallingBlock);
                        }
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

                            slamTarget.addDamageInstance(
                                    wp,
                                    name,
                                    minDamageHeal * damageMultiplier,
                                    maxDamageHeal * damageMultiplier,
                                    critChance,
                                    critMultiplier,
                                    trueDamage,
                                    abilityUUID
                            );
                        }
                    }

                    fallingBlockLocations.remove(fallingBlockLocation);
                    break;
                }

                if (fallingBlockLocations.isEmpty()) {
                    this.cancel();
                }
            }

        }.runTaskTimer(0, 2);

        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                for (int i = 0; i < customFallingBlocks.size(); i++) {
                    CustomFallingBlock customFallingBlock = customFallingBlocks.get(i);
                    customFallingBlock.setTicksLived(customFallingBlock.getTicksLived() + 1);
                    if (LocationUtils.getDistance(
                            customFallingBlock.getFallingBlock().getLocation(), .05) <= .25 ||
                            customFallingBlock.getTicksLived() > 10
                    ) {
                        customFallingBlock.getFallingBlock().remove();
                        customFallingBlocks.remove(i);
                        i--;
                    }
                }
                if (fallingBlockLocations.isEmpty() && customFallingBlocks.isEmpty()) {
                    this.cancel();
                }
            }

        }.runTaskTimer(0, 0);
    }

    /**
     * Return A List Of Locations That
     * Make Up A Circle Using A Provided
     * Center, Radius, And Desired Points.
     *
     * @param center
     * @param radius
     * @param amount
     * @return
     */
    private List<Location> getCircle(Location center, float radius, int amount) {
        World world = center.getWorld();
        double increment = ((2 * Math.PI) / amount);
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            float angle = (float) (i * increment);
            float x = (float) (center.getX() + (radius * Math.cos(angle)));
            float z = (float) (center.getZ() + (radius * Math.sin(angle)));
            Location location = new Location(world, x, center.getY(), z);
            locations.add(location);
        }
        return locations;
    }

    private FallingBlock addFallingBlock(Location location) {
        if (location.getWorld().getBlockAt(location).getType() != Material.AIR) {
            location.add(0, 1, 0);
        }
        Location blockToGet = location.clone().add(0, -1, 0);
        if (location.getWorld().getBlockAt(location.clone().add(0, -1, 0)).getType() == Material.AIR) {
            blockToGet.add(0, -1, 0);
            if (location.getWorld().getBlockAt(location.clone().add(0, -2, 0)).getType() == Material.AIR) {
                blockToGet.add(0, -1, 0);
            }
        }
        Material type = location.getWorld().getBlockAt(blockToGet).getType();
        if (type == Material.GRASS) {
            if ((int) (Math.random() * 3) == 2) {
                type = Material.DIRT;
            }
        }
        FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location, type.createBlockData());
        fallingBlock.setVelocity(new Vector(0, .14, 0));
        fallingBlock.setDropItem(false);
        WarlordsEvents.addEntityUUID(fallingBlock);
        return fallingBlock;
    }

    public int getSlamSize() {
        return slamSize;
    }

    public void setSlamSize(int slamSize) {
        this.slamSize = slamSize;
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
}

