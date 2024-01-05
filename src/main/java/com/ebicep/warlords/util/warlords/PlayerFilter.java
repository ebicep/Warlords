package com.ebicep.warlords.util.warlords;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.ebicep.warlords.util.bukkit.LocationUtils.radiusAround;
import static com.ebicep.warlords.util.bukkit.LocationUtils.sortClosestBy;

// TODO run regex
// Search: (\n +)Utils\.filterOnlyEnemies\(([a-z]+), ([0-9.DF]+), ([0-9.DF]+), ([0-9.DF]+), ([a-z]+)\)
// Replace: $1PlayerFilter.entitiesAround($2, $3, $4, $5)$1    .enemiesOf($6)$1

public class PlayerFilter implements Iterable<WarlordsEntity> {
    private static final Location LOCATION_CACHE_ENTITIES_AROUND = new Location(null, 0, 0, 0);
    private static final Location LOCATION_CACHE_CLOSEST = new Location(null, 0, 0, 0);

    private final Stream<WarlordsEntity> stream;

    protected PlayerFilter(@Nonnull Stream<WarlordsEntity> stream) {
        this.stream = stream;
    }

    @Nonnull
    public Stream<WarlordsEntity> stream() {
        return stream;
    }

    /**
     * Adds new internalPlayers to the list
     *
     * @param player
     * @return The new {@code PlayerFilter}
     */
    @Nonnull
    public PlayerFilter concat(@Nonnull WarlordsEntity... player) {
        return new PlayerFilter(Stream.concat(stream, Stream.of(player)));
    }

    /**
     * Limits the amount of internalPlayers iterated over.
     * @see #closestFirst
     * @see #sorted
     * @param maxSize limit
     * @return new instance of this class
     */
    @Nonnull
    public PlayerFilter limit(long maxSize) {
        return new PlayerFilter(stream.limit(maxSize));
    }

    /**
     * Filters the list of internalPlayers based on a condition
     *
     * @param filter
     * @return
     */
    @Nonnull
    public PlayerFilter filter(@Nonnull Predicate<? super WarlordsEntity> filter) {
        return new PlayerFilter(stream.filter(filter));
    }

    @Nonnull
    public PlayerFilter skip(long n) {
        return new PlayerFilter(stream.skip(n));
    }

    @Nonnull
    public PlayerFilter sorted(@Nonnull Comparator<? super WarlordsEntity> comparator) {
        return new PlayerFilter(stream.sorted(comparator));
    }

    @Nonnull
    public PlayerFilter warlordPlayersFirst() {
        return new PlayerFilter(stream.sorted((o1, o2) -> {
            if (o1 instanceof WarlordsPlayer && !(o2 instanceof WarlordsPlayer)) {
                return -1;
            } else if (!(o1 instanceof WarlordsPlayer) && o2 instanceof WarlordsPlayer) {
                return 1;
            } else {
                return 0;
            }
        }));
    }

    @Nonnull
    public PlayerFilter closestWarlordPlayersFirst(@Nonnull Location loc) {
        return new PlayerFilter(stream.sorted((o1, o2) -> {
            if (o1 instanceof WarlordsPlayer && !(o2 instanceof WarlordsPlayer)) {
                return -1;
            } else if (!(o1 instanceof WarlordsPlayer) && o2 instanceof WarlordsPlayer) {
                return 1;
            } else {
                return Double.compare(o1.getLocation().distanceSquared(loc), o2.getLocation().distanceSquared(loc));
            }
        }));
    }

    @Nonnull
    public PlayerFilter closestFirst(@Nonnull Location loc) {
        return sorted(sortClosestBy(WarlordsEntity::getLocation, loc));
    }

    @Nonnull
    public PlayerFilter closestFirst(@Nonnull WarlordsEntity loc) {
        loc.getLocation(LOCATION_CACHE_CLOSEST);
        return sorted(sortClosestBy(WarlordsEntity::getLocation, LOCATION_CACHE_CLOSEST));
    }

    @Nonnull
    public PlayerFilter closestFirst(@Nonnull Entity loc) {
        loc.getLocation(LOCATION_CACHE_CLOSEST);
        return sorted(sortClosestBy(WarlordsEntity::getLocation, LOCATION_CACHE_CLOSEST));
    }

    @Nonnull
    public PlayerFilter leastAliveFirst() {
        return sorted(Comparator.comparing(wp -> wp.getCurrentHealth() / wp.getMaxHealth()));
    }

    @Nonnull
    public PlayerFilter mostAliveFirst() {
        return sorted(Comparator.<WarlordsEntity, Float>comparing(wp -> wp.getCurrentHealth() / wp.getMaxHealth()).reversed());
    }

    @Nonnull
    public PlayerFilter leastEnergeticFirst() {
        return sorted(Comparator.comparing(wp -> wp.getEnergy() / (double) wp.getMaxEnergy()));
    }

    @Nonnull
    public PlayerFilter mostEnergeticFirst() {
        return sorted(Comparator.<WarlordsEntity, Double>comparing(wp -> wp.getEnergy() / (double) wp.getMaxEnergy()).reversed());
    }

    @Nonnull
    public PlayerFilter soulBindedFirst(WarlordsEntity owner) {
        return sorted(Comparator.comparing(wp -> !owner.getCooldownManager().hasBoundPlayer(wp)));
    }

    @Nonnull
    public PlayerFilter lookingAtFirst(WarlordsEntity user) {
        return sorted((wp1, wp2) -> {
            int output;
            double wp1Dot = -LocationUtils.getDotToPlayer(user, wp1, 0);
            double wp2Dot = -LocationUtils.getDotToPlayer(user, wp2, 0);
            output = Double.compare(wp1Dot, wp2Dot);
            if (Math.abs(wp1Dot - wp2Dot) < .0125) {
                Location userLocation = user.getLocation();
                Location w1Location = wp1.getLocation();
                Location w2Location = wp2.getLocation();
                output = Double.compare(userLocation.distanceSquared(w1Location), userLocation.distanceSquared(w2Location));
            }
            return output;
        });
    }

    @Nonnull
    public PlayerFilter isAlive() {
        return filter(WarlordsEntity::isAlive);
    }

    @Nonnull
    public PlayerFilter isDead() {
        return filter(WarlordsEntity::isAlive);
    }

    @Nonnull
    public PlayerFilter enemiesOf(@Nonnull WarlordsEntity player) {
        return filter(player::isEnemy);
    }

    @Nonnull
    public PlayerFilter aliveEnemiesOf(@Nonnull WarlordsEntity player) {
        return filter(player::isEnemyAlive);
    }

    @Nonnull
    public PlayerFilter teammatesOf(@Nonnull WarlordsEntity player) {
        return filter(player::isTeammate);
    }

    @Nonnull
    public PlayerFilter aliveTeammatesOf(@Nonnull WarlordsEntity player) {
        return filter(player::isTeammateAlive);
    }

    @Nonnull
    public PlayerFilter teammatesOfExcludingSelf(@Nonnull WarlordsEntity player) {
        return filter(wp -> player != wp && player.isTeammate(wp));
    }

    @Nonnull
    public PlayerFilter aliveTeammatesOfExcludingSelf(@Nonnull WarlordsEntity player) {
        return filter(wp -> player != wp && player.isTeammateAlive(wp));
    }

    @Nonnull
    public PlayerFilter aliveMatchingTeam(@Nonnull Team team) {
        return filter(wp -> wp.getTeam() == team && wp.isAlive());
    }

    @Nonnull
    public PlayerFilter matchingTeam(@Nonnull Team team) {
        return filter(wp -> wp.getTeam() == team);
    }

    @Nonnull
    public PlayerFilter excluding(@Nonnull WarlordsEntity... exclude) {
        return exclude.length == 0 ? this : excluding0(new HashSet<>(Arrays.asList(exclude)));
    }

    @Nonnull
    public PlayerFilter excluding(@Nonnull Collection<WarlordsEntity> exclude) {
        return exclude.isEmpty() ? this : excluding0(exclude instanceof Set ? (Set<WarlordsEntity>) exclude : new HashSet<>(exclude));
    }

    @Nonnull
    protected PlayerFilter excluding0(@Nonnull Set<WarlordsEntity> exclude) {
        return filter(p -> !exclude.contains(p));
    }

    @Override
    public void forEach(@Nonnull Consumer<? super WarlordsEntity> action) {
        stream.forEach(action);
        stream.close();
    }

    public boolean first(@Nonnull Consumer<? super WarlordsEntity> action) {
        Optional<WarlordsEntity> findAny = this.findFirst();
        if (findAny.isEmpty()) {
            return false;
        }
        action.accept(findAny.get());
        return true;
    }

    @Nonnull
    @Override
    public Iterator<WarlordsEntity> iterator() {
        return stream.iterator();
    }

    @Nonnull
    public static PlayerFilter entitiesAround(@Nonnull WarlordsEntity entity, double x, double y, double z) {
        return entitiesAround(entity.getLocation(LOCATION_CACHE_ENTITIES_AROUND), x, y, z);
    }

    @Nonnull
    public static PlayerFilter entitiesAround(@Nonnull Entity entity, double x, double y, double z) {
        return entitiesAround(entity.getLocation(LOCATION_CACHE_ENTITIES_AROUND), x, y, z);
    }

    @Nonnull
    public static PlayerFilter entitiesAround(@Nonnull Location location, double x, double y, double z) {
        return new PlayerFilter(entitiesAround0(location, x, y, z));
    }

    @Nonnull
    protected static Stream<WarlordsEntity> entitiesAround0(@Nonnull Location location, double x, double y, double z) {
        return entitiesAroundRectangle0(location, x, y, z)
                .filter(radiusAround(WarlordsEntity::getLocation, location, x, y, z));
    }

    @Nonnull
    public static PlayerFilter entitiesAroundRectangle(@Nonnull WarlordsEntity entity, double x, double y, double z) {
        return entitiesAroundRectangle(entity.getLocation(LOCATION_CACHE_ENTITIES_AROUND), x, y, z);
    }

    @Nonnull
    public static PlayerFilter entitiesAroundRectangle(@Nonnull Entity entity, double x, double y, double z) {
        return entitiesAroundRectangle(entity.getLocation(LOCATION_CACHE_ENTITIES_AROUND), x, y, z);
    }
    @Nonnull
    public static PlayerFilter entitiesAroundRectangle(@Nonnull Location location, double x, double y, double z) {
        return new PlayerFilter(entitiesAroundRectangle0(location, x, y, z));
    }

    @Nonnull
    protected static Stream<WarlordsEntity> entitiesAroundRectangle0(@Nonnull Location location, double x, double y, double z) {
        return entities0(location
                .getWorld()
                .getNearbyEntities(location, x, y, z)
                .stream()
        );

    }

    @Nonnull
    public static PlayerFilter entitiesInRectangle(@Nonnull World world, double x1, double y1, double z1, double x2, double y2, double z2) {
        double minX = Math.min(x1, x2);
        double minY = Math.min(y1, y2);
        double minZ = Math.min(z1, z2);
        double maxX = Math.max(x1, x2);
        double maxY = Math.max(y1, y2);
        double maxZ = Math.max(z1, z2);

        return new PlayerFilter(world.getEntities().stream()
                                     .filter(e -> {
                                         e.getLocation(LOCATION_CACHE_ENTITIES_AROUND);
                                         double x = LOCATION_CACHE_ENTITIES_AROUND.getX();
                                         double y = LOCATION_CACHE_ENTITIES_AROUND.getY();
                                         double z = LOCATION_CACHE_ENTITIES_AROUND.getZ();

                                         return x > minX && x < maxX && y > minY && y < maxY && z > minZ && z < maxZ;
                                     })
                                     .map(Warlords::getPlayer)
                                     .filter(Objects::nonNull)
        );
    }

    @Nonnull
    public static PlayerFilter playingGame(@Nonnull Game game) {
        return new PlayerFilter(game.warlordsEntities());
    }

    @Nonnull
    public static PlayerFilter entities(@Nonnull Collection<Entity> entities) {
        return new PlayerFilter(entities0(entities.stream()));
    }

    @Nonnull
    public static PlayerFilter entities(@Nonnull Iterable<Entity> entities) {
        return entities(entities.iterator());
    }

    @Nonnull
    public static PlayerFilter entities(@Nonnull Iterator<Entity> entities) {
        return new PlayerFilter(entities0(StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(entities, Spliterator.ORDERED),
            false
        )));
    }

    @Nonnull
    public static PlayerFilter entities(@Nonnull Entity ... entities) {
        return new PlayerFilter(entities0(Stream.of(entities)));
    }

    @Nonnull
    protected static Stream<WarlordsEntity> entities0(@Nonnull Stream<Entity> entities) {
        return entities
                .map(Warlords::getPlayer)
                .filter(Objects::nonNull);
    }

    @Nonnull
    public Optional<WarlordsEntity> findAny() {
        return stream.findAny();
    }

    @Nonnull
    public Optional<WarlordsEntity> findFirst() {
        return stream.findFirst();
    }

    @Nullable
    public WarlordsEntity findAnyOrNull() {
        return findAny().orElse(null);
    }

    @Nullable
    public WarlordsEntity findFirstOrNull() {
        return findFirst().orElse(null);
    }

    @Nonnull
    public PlayerFilter requireLineOfSight(@Nonnull WarlordsEntity warlordsPlayer) {
        return filter(wp -> LocationUtils.isLookingAt(warlordsPlayer, wp) && LocationUtils.hasLineOfSight(warlordsPlayer, wp));
    }

    @Nonnull
    public PlayerFilter requireLineOfSightIntervene(@Nonnull WarlordsEntity warlordsPlayer) {
        return filter(wp -> LocationUtils.isLookingAtIntervene(warlordsPlayer, wp));
    }

    @Nonnull
    public PlayerFilter lookingAtWave(@Nonnull WarlordsEntity warlordsPlayer) {
        return filter(wp -> LocationUtils.isLookingAtWave(warlordsPlayer, wp));
    }

    public List<WarlordsEntity> toList() {
        return this.stream.collect(Collectors.toCollection(ArrayList::new));
    }

}
