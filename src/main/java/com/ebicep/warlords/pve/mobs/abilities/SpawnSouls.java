package com.ebicep.warlords.pve.mobs.abilities;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.flags.DynamicFlags;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SpawnSouls extends AbstractSpawnMobAbility {

    public static final List<Mob> SOULS = Arrays.asList(Mob.AGONIZED_SOUL, Mob.DEPRESSED_SOUL, Mob.FURIOUS_SOUL, Mob.TORMENTED_SOUL, Mob.VOLTAIC_SOUL);
    public static final int SPAWN_LIMIT = 50;

    public static List<Location> generateSpawnLocations(PveOption pveOption) {
        List<Location> locations;
        Location center = pveOption.getRandomSpawnLocation((WarlordsEntity) null);
        if (center == null) {
            locations = new ArrayList<>();
            PlayerFilter.playingGame(pveOption.getGame())
                        .findAny()
                        .ifPresent(warlordsEntity -> {
                            locations.add(warlordsEntity.getLocation());
                        });
        } else if (ThreadLocalRandom.current().nextBoolean()) {
            locations = LocationUtils.getSquare(center, 1.5f);
        } else {
            locations = LocationUtils.getCircle(center, 1.5f, 5);
        }
        return locations;

    }

    private List<Location> randomSpawnLocations;
    private Mob randomSoulToSpawn;

    public SpawnSouls(float cooldown) {
        super("Spawn Souls", cooldown, true);
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        boolean activate = super.onActivate(wp);
        if (activate) {
            Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 1.5f);
        }
        return activate;
    }

    @Override
    public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
        if (pveOption.getMobs().stream().filter(abstractMob -> SOULS.contains(abstractMob.getMobRegistry())).count() >= SPAWN_LIMIT) {
            return true;
        }
        randomSoulToSpawn = SOULS.get(ThreadLocalRandom.current().nextInt(SOULS.size()));
        randomSpawnLocations = generateSpawnLocations(pveOption);
        return super.onPveActivate(wp, pveOption);
    }

    @Override
    public AbstractMob createMob(@Nonnull WarlordsEntity wp) {
        if (randomSpawnLocations.isEmpty()) {
            randomSpawnLocations = generateSpawnLocations(pveOption);
        }
        AbstractMob spawnedMob = randomSoulToSpawn.createMob(randomSpawnLocations.remove(0));
        spawnedMob.getDynamicFlags().add(DynamicFlags.NO_INSIGNIA);
        return spawnedMob;
    }

    @Override
    public int getSpawnAmount() {
        return (int) (2 * pveOption.getGame().warlordsPlayers().count());
    }

}
