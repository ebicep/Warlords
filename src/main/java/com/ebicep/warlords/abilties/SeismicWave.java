package com.ebicep.warlords.abilties;

import com.ebicep.customentities.nms.CustomFallingBlock;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.AbstractTimeWarpBase;
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
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SeismicWave extends AbstractAbility {

    public int playersHit = 0;
    public int carrierHit = 0;
    public int warpsKnockbacked = 0;

    private float velocity = 1.25f;
    private int waveSize = 8; // foward amount
    private int waveWidth = 2; // sideways amount (2 => 2 to left and 2 to right)

    public SeismicWave(String name, float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, float critChance, float critMultiplier) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Send a wave of incredible force forward that deals ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage to all enemies hit and knocks them back slightly."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Hit", "" + playersHit));
        info.add(new Pair<>("Carriers Hit", "" + carrierHit));
        info.add(new Pair<>("Warps Knockbacked", "" + warpsKnockbacked));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), "warrior.seismicwave.activation", 2, 1);

        List<List<Location>> fallingBlockLocations = new ArrayList<>();
        List<CustomFallingBlock> customFallingBlocks = new ArrayList<>();

        Location location = player.getLocation();
        for (int i = 0; i < waveSize; i++) {
            fallingBlockLocations.add(getWave(location, i));
        }


        List<WarlordsEntity> playersHit = new ArrayList<>();
        for (int i = 0, fallingBlockLocationsSize = fallingBlockLocations.size(); i < fallingBlockLocationsSize; i++) {
            List<Location> fallingBlockLocation = fallingBlockLocations.get(i);
            for (Location loc : fallingBlockLocation) {
                for (WarlordsEntity waveTarget : PlayerFilter
                        .entitiesAroundRectangle(loc, .6, 4, .6)
                        .aliveEnemiesOf(wp)
                        .excluding(playersHit)
                        .closestFirst(wp)
                ) {
                    this.playersHit++;
                    if (waveTarget.hasFlag()) {
                        carrierHit++;
                    }
                    if (waveTarget.getCooldownManager().hasCooldownExtends(AbstractTimeWarpBase.class) && FlagHolder.playerTryingToPick(waveTarget)) {
                        warpsKnockbacked++;
                    }

                    playersHit.add(waveTarget);
                    final Vector v = player.getLocation().toVector().subtract(waveTarget.getLocation().toVector()).normalize().multiply(-velocity).setY(0.25);
                    waveTarget.setVelocity(name, v, false, false);
                    if (pveUpgrade) {
                        float multiplier = (1.5f / 15f) * Math.min(i + 1, 15) + 1;
                        waveTarget.addDamageInstance(wp, name, minDamageHeal * multiplier, maxDamageHeal * multiplier, critChance, critMultiplier, false);
                    } else {
                        waveTarget.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
                    }
                }
            }
        }
        new GameRunnable(wp.getGame()) {

            @Override
            public void run() {
                for (List<Location> fallingBlockLocation : fallingBlockLocations) {
                    for (Location location : fallingBlockLocation) {
                        if (location.getWorld().getBlockAt(location.clone().add(0, 1, 0)).getType() == Material.AIR) {
                            FallingBlock fallingBlock = addFallingBlock(location);
                            customFallingBlocks.add(new CustomFallingBlock(fallingBlock, wp, SeismicWave.this));
                            WarlordsEvents.addEntityUUID(fallingBlock);
                        }
                    }
                    fallingBlockLocations.remove(fallingBlockLocation);
                    break;
                }

                for (int i = 0; i < customFallingBlocks.size(); i++) {
                    CustomFallingBlock cfb = customFallingBlocks.get(i);
                    cfb.setTicksLived(cfb.getTicksLived() + 1);
                    if (LocationUtils.getDistance(cfb.getFallingBlock().getLocation(), .05) <= .25 || cfb.getTicksLived() > 10) {
                        cfb.getFallingBlock().remove();
                        customFallingBlocks.remove(i);
                        i--;
                    }
                }

                if (fallingBlockLocations.isEmpty() && customFallingBlocks.isEmpty()) {
                    this.cancel();
                }
            }

        }.runTaskTimer(0, 0);

        return true;
    }

    private List<Location> getWave(Location center, int distance) {
        List<Location> locations = new ArrayList<>();
        Location location = new Location(center.getWorld(), center.getX(), center.getY(), center.getZ());
        location.setDirection(center.getDirection());
        location.setPitch(0);
        locations.add(location.add(location.getDirection().multiply(distance)));
        for (int i = 1; i <= waveWidth; i++) {
            locations.add(location.clone().add(LocationUtils.getLeftDirection(location).multiply(i)));
            locations.add(location.clone().add(LocationUtils.getRightDirection(location).multiply(i)));
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
        return fallingBlock;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public int getWaveSize() {
        return waveSize;
    }

    public void setWaveSize(int waveSize) {
        this.waveSize = waveSize;
    }

    public int getWaveWidth() {
        return waveWidth;
    }

    public void setWaveWidth(int waveWidth) {
        this.waveWidth = waveWidth;
    }
}