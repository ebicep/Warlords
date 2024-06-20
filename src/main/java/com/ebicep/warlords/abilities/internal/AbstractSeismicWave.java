package com.ebicep.warlords.abilities.internal;

import com.ebicep.customentities.nms.SelfRemovingFallingBlock;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractSeismicWave extends AbstractAbility implements RedAbilityIcon {

    public int playersHit = 0;
    public int carrierHit = 0;
    public int warpsKnockbacked = 0;

    protected float velocity = 1.25f;
    private int waveLength = 8; // foward amount
    private int waveWidth = 2; // sideways amount (2 => 2 to left and 2 to right)

    public AbstractSeismicWave(float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, float critChance, float critMultiplier) {
        super("Seismic Wave", minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Send a wave of incredible force forward that deals ")
                               .append(Damages.formatDamage(getWaveDamage()))
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
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "warrior.seismicwave.activation", 2, 1);

        List<List<Location>> fallingBlockLocations = new ArrayList<>();
        List<SelfRemovingFallingBlock> selfRemovingFallingBlocks = new ArrayList<>();

        Location location = wp.getLocation();
        for (int i = 0; i < waveLength; i++) {
            fallingBlockLocations.add(getWaveSideLocations(location, i));
        }

        UUID abilityUUID = UUID.randomUUID();
        List<WarlordsEntity> playersHit = new ArrayList<>();
        for (int i = 0; i < fallingBlockLocations.size(); i++) {
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
                    if (waveTarget.getCooldownManager().hasCooldownExtends(AbstractTimeWarp.class) && FlagHolder.playerTryingToPick(waveTarget)) {
                        warpsKnockbacked++;
                    }

                    playersHit.add(waveTarget);
                    final Vector v = wp.getLocation().toVector().subtract(waveTarget.getLocation().toVector()).normalize().multiply(-velocity).setY(0.25);
                    waveTarget.setVelocity(name, v, false, false);

                    onHit(wp, abilityUUID, playersHit, i, waveTarget);
                }
            }
        }
        new GameRunnable(wp.getGame()) {

            @Override
            public void run() {
                for (List<Location> fallingBlockLocation : fallingBlockLocations) {
                    for (Location location : fallingBlockLocation) {
                        Utils.addFallingBlock(location);
                    }
                    fallingBlockLocations.remove(fallingBlockLocation);
                    break;
                }
                if (fallingBlockLocations.isEmpty() && selfRemovingFallingBlocks.isEmpty()) {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 0);
        return true;
    }

    private List<Location> getWaveSideLocations(Location center, int distance) {
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

    protected void onHit(@Nonnull WarlordsEntity wp, UUID abilityUUID, List<WarlordsEntity> playersHit, int i, WarlordsEntity waveTarget) {
    }

    public abstract Value.RangedValueCritable getWaveDamage();

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public int getWaveLength() {
        return waveLength;
    }

    public void setWaveLength(int waveLength) {
        this.waveLength = waveLength;
    }

    public int getWaveWidth() {
        return waveWidth;
    }

    public void setWaveWidth(int waveWidth) {
        this.waveWidth = waveWidth;
    }
}