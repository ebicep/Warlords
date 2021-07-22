package com.ebicep.warlords.util;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.ebicep.warlords.util.Utils.radiusAround;
import static com.ebicep.warlords.util.Utils.sortClosestBy;

// TODO run regex
// Search: (\n +)Utils\.filterOnlyEnemies\(([a-z]+), ([0-9.DF]+), ([0-9.DF]+), ([0-9.DF]+), ([a-z]+)\)
// Replace: $1PlayerFilter.entitiesAround($2, $3, $4, $5)$1    .enemiesOf($6)$1

public class PlayerFilter implements Iterable<WarlordsPlayer> {
    private static final Location LOCATION_CACHE_ENTITIES_AROUND = new Location(null, 0, 0, 0);
    private static final Location LOCATION_CACHE_CLOSEST = new Location(null, 0, 0, 0);

    private final Stream<WarlordsPlayer> stream;

    protected PlayerFilter(@Nonnull Stream<WarlordsPlayer> stream) {
        this.stream = stream;
    }

    @Nonnull
    public Stream<WarlordsPlayer> stream() {
        return stream;
    }

    /**
     * Adds new players to the list
     * @param player
     * @return The new {@code PlayerFilter}
     */
    @Nonnull
    public PlayerFilter concat(@Nonnull WarlordsPlayer ... player) {
        return new PlayerFilter(Stream.concat(stream, Stream.of(player)));
    }

    /**
     * Limits the amount of players iterated over.
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
     * Filters the list of players based on a condition
     * @param filter
     * @return
     */
    @Nonnull
    public PlayerFilter filter(@Nonnull Predicate<? super WarlordsPlayer> filter) {
        return new PlayerFilter(stream.filter(filter));
    }

    @Nonnull
    public PlayerFilter skip(long n) {
        return new PlayerFilter(stream.skip(n));
    }

    @Nonnull
    public PlayerFilter sorted(@Nonnull Comparator<? super WarlordsPlayer> comparator) {
        return new PlayerFilter(stream.sorted(comparator));
    }

    @Nonnull
    public PlayerFilter closestFirst(@Nonnull Location loc) {
        return sorted(sortClosestBy(WarlordsPlayer::getLocation, loc));
    }

    @Nonnull
    public PlayerFilter closestFirst(@Nonnull WarlordsPlayer loc) {
        loc.getLocation(LOCATION_CACHE_CLOSEST);
        return sorted(sortClosestBy(WarlordsPlayer::getLocation, LOCATION_CACHE_CLOSEST));
    }

    @Nonnull
    public PlayerFilter closestFirst(@Nonnull Entity loc) {
        loc.getLocation(LOCATION_CACHE_CLOSEST);
        return sorted(sortClosestBy(WarlordsPlayer::getLocation, LOCATION_CACHE_CLOSEST));
    }

    @Nonnull
    public PlayerFilter leastAliveFirst() {
        return sorted(Comparator.<WarlordsPlayer, Double>comparing(wp -> wp.getHealth() / (double)wp.getMaxHealth()));
    }

    @Nonnull
    public PlayerFilter mostAliveFirst() {
        return sorted(Comparator.<WarlordsPlayer, Double>comparing(wp -> wp.getHealth() / (double)wp.getMaxHealth()).reversed());
    }

    @Nonnull
    public PlayerFilter leastEnergeticFirst() {
        return sorted(Comparator.<WarlordsPlayer, Double>comparing(wp -> wp.getEnergy() / (double) wp.getMaxEnergy()));
    }

    @Nonnull
    public PlayerFilter mostEnergeticFirst() {
        return sorted(Comparator.<WarlordsPlayer, Double>comparing(wp -> wp.getEnergy() / (double) wp.getMaxEnergy()).reversed());
    }

    @Nonnull
    public PlayerFilter soulBindedFirst(WarlordsPlayer owner) {
        return sorted(Comparator.comparing(wp -> owner.getCooldownManager().hasBoundPlayer(wp)));
    }

    @Nonnull
    public PlayerFilter lookingAtFirst(WarlordsPlayer user) {
        return sorted((wp1, wp2) -> {
            int output;
            double wp1Dot = -Utils.getDotToPlayer(user.getEntity(), wp1.getEntity(), 0);
            double wp2Dot = -Utils.getDotToPlayer(user.getEntity(), wp2.getEntity(), 0);
            output = Double.compare(wp1Dot, wp2Dot);
            if (Math.abs(wp1Dot - wp2Dot) < .01) {
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
        return filter(WarlordsPlayer::isAlive);
    }

    @Nonnull
    public PlayerFilter isDeath() {
        return filter(WarlordsPlayer::isAlive);
    }

    @Nonnull
    public PlayerFilter enemiesOf(@Nonnull WarlordsPlayer player) {
        return filter(wp -> player.isEnemy(wp));
    }

    @Nonnull
    public PlayerFilter aliveEnemiesOf(@Nonnull WarlordsPlayer player) {
        return filter(wp -> player.isEnemyAlive(wp));
    }

    @Nonnull
    public PlayerFilter teammatesOf(@Nonnull WarlordsPlayer player) {
        return filter(wp -> player.isTeammate(wp));
    }

    @Nonnull
    public PlayerFilter aliveTeammatesOf(@Nonnull WarlordsPlayer player) {
        return filter(wp -> player.isTeammateAlive(wp));
    }

    @Nonnull
    public PlayerFilter teammatesOfExcludingSelf(@Nonnull WarlordsPlayer player) {
        return filter(wp -> player != wp && player.isTeammate(wp));
    }

    @Nonnull
    public PlayerFilter aliveTeammatesOfExcludingSelf(@Nonnull WarlordsPlayer player) {
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
    public PlayerFilter excluding(@Nonnull WarlordsPlayer ... exclude) {
        return exclude.length == 0 ? this : excluding0(new HashSet<>(Arrays.asList(exclude)));
    }

    @Nonnull
    public PlayerFilter excluding(@Nonnull Collection<WarlordsPlayer> exclude) {
        return exclude.isEmpty() ? this : excluding0(exclude instanceof Set ? (Set<WarlordsPlayer>)exclude : new HashSet<>(exclude));
    }

    @Nonnull
    protected PlayerFilter excluding0(@Nonnull Set<WarlordsPlayer> exclude) {
        return filter(p -> !exclude.contains(p));
    }

    @Override
    public void forEach(@Nonnull Consumer<? super WarlordsPlayer> action) {
        stream.forEach(action);
        stream.close();
    }

    public boolean first(@Nonnull Consumer<? super WarlordsPlayer> action) {
        Optional<WarlordsPlayer> findAny = this.findAny();
        if (!findAny.isPresent()) {
            return false;
        }
        action.accept(findAny.get());
        return true;
    }

    @Nonnull
    @Override
    public Iterator<WarlordsPlayer> iterator() {
        return stream.iterator();
    }

    @Nonnull
    public static PlayerFilter entitiesAround(@Nonnull WarlordsPlayer entity, double x, double y, double z) {
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
    protected static Stream<WarlordsPlayer> entitiesAround0(@Nonnull Location location, double x, double y, double z) {
        return entitiesAroundRectangle0(location, x, y, z)
            .filter(radiusAround(WarlordsPlayer::getLocation, location, x, y, z));
    }

    @Nonnull
    public static PlayerFilter entitiesAroundRectangle(@Nonnull WarlordsPlayer entity, double x, double y, double z) {
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
    protected static Stream<WarlordsPlayer> entitiesAroundRectangle0(@Nonnull Location location, double x, double y, double z) {
        return entities0(location
            .getWorld()
            .getNearbyEntities(location, x, y, z)
            .stream()
        );

    }

    @Nonnull
    public static PlayerFilter playingGame(@Nonnull Game game) {
        return new PlayerFilter(game.players().map(e -> Warlords.getPlayer(e.getKey())).filter(Objects::nonNull));
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
    protected static Stream<WarlordsPlayer> entities0(@Nonnull Stream<Entity> entities) {
        return entities
            .map(e -> Warlords.getPlayer(e))
            .filter(Objects::nonNull);
    }

    @Nonnull
    public Optional<WarlordsPlayer> findAny() {
        return stream.findAny();
    }

    @Nonnull
    public Optional<WarlordsPlayer> findFirst() {
        return stream.findFirst();
    }

    @Nullable
    public WarlordsPlayer findAnyOrNull() {
        return findAny().orElse(null);
    }

    @Nullable
    public WarlordsPlayer findFirstOrNull() {
        return findFirst().orElse(null);
    }

    @Nonnull
    public PlayerFilter requireLineOfSight(@Nonnull WarlordsPlayer warlordsPlayer) {
        return requireLineOfSight(warlordsPlayer.getEntity());
    }

    @Nonnull
    public PlayerFilter requireLineOfSight(@Nonnull LivingEntity entity) {
        return filter(wp -> Utils.isLookingAt(entity, wp.getEntity()) && Utils.hasLineOfSight(entity, wp.getEntity()));
    }

    @Nonnull
    public PlayerFilter requireLineOfSightIntervene(@Nonnull WarlordsPlayer warlordsPlayer) {
        return requireLineOfSightIntervene(warlordsPlayer.getEntity());
    }

    @Nonnull
    public PlayerFilter requireLineOfSightIntervene(@Nonnull LivingEntity entity) {
        return filter(wp -> Utils.isLookingAtIntervene(entity, wp.getEntity()) && Utils.hasLineOfSight(entity, wp.getEntity()));
    }

    @Nonnull
    public PlayerFilter lookingAtWave(@Nonnull WarlordsPlayer warlordsPlayer) {
        return lookingAtWave(warlordsPlayer.getEntity());
    }

    @Nonnull
    public PlayerFilter lookingAtWave(@Nonnull LivingEntity entity) {
        return filter(wp -> Utils.isLookingAtWave(entity, wp.getEntity()));
    }

}
