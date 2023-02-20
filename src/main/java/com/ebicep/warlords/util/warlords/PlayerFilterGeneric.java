package com.ebicep.warlords.util.warlords;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.ebicep.warlords.util.warlords.Utils.radiusAround;
import static com.ebicep.warlords.util.warlords.Utils.sortClosestBy;

// TODO run regex
// Search: (\n +)Utils\.filterOnlyEnemies\(([a-z]+), ([0-9.DF]+), ([0-9.DF]+), ([0-9.DF]+), ([a-z]+)\)
// Replace: $1PlayerFilter.entitiesAround($2, $3, $4, $5)$1    .enemiesOf($6)$1

public class PlayerFilterGeneric<T extends WarlordsEntity> implements Iterable<T> {
    private static final Location LOCATION_CACHE_ENTITIES_AROUND = new Location(null, 0, 0, 0);
    private static final Location LOCATION_CACHE_CLOSEST = new Location(null, 0, 0, 0);

    private final Stream<T> stream;

    protected PlayerFilterGeneric(@Nonnull Stream<T> stream) {
        this.stream = stream;
    }

    @Nonnull
    public Stream<T> stream() {
        return stream;
    }

    @Nonnull
    public PlayerFilterGeneric<WarlordsNPC> warlordsNPCs() {
        return new PlayerFilterGeneric<>(stream.filter(WarlordsNPC.class::isInstance).map(WarlordsNPC.class::cast));
    }

    @Nonnull
    public PlayerFilterGeneric<WarlordsPlayer> warlordsPlayers() {
        return new PlayerFilterGeneric<>(stream.filter(WarlordsPlayer.class::isInstance).map(WarlordsPlayer.class::cast));
    }

    /**
     * Adds new internalPlayers to the list
     *
     * @param player
     * @return The new {@code PlayerFilter}
     */
    @Nonnull
    public PlayerFilterGeneric<T> concat(@Nonnull T... player) {
        return new PlayerFilterGeneric<>(Stream.concat(stream, Stream.of(player)));
    }

    /**
     * Limits the amount of internalPlayers iterated over.
     *
     * @param maxSize limit
     * @return new instance of this class
     * @see #closestFirst
     * @see #sorted
     */
    @Nonnull
    public PlayerFilterGeneric<T> limit(long maxSize) {
        return new PlayerFilterGeneric<>(stream.limit(maxSize));
    }

    /**
     * Filters the list of internalPlayers based on a condition
     *
     * @param filter
     * @return
     */
    @Nonnull
    public PlayerFilterGeneric<T> filter(@Nonnull Predicate<? super T> filter) {
        return new PlayerFilterGeneric<>(stream.filter(filter));
    }

    @Nonnull
    public PlayerFilterGeneric<T> skip(long n) {
        return new PlayerFilterGeneric<>(stream.skip(n));
    }

    @Nonnull
    public PlayerFilterGeneric<T> sorted(@Nonnull Comparator<? super T> comparator) {
        return new PlayerFilterGeneric<>(stream.sorted(comparator));
    }

    @Nonnull
    public PlayerFilterGeneric<T> closestFirst(@Nonnull Location loc) {
        return sorted(sortClosestBy(WarlordsEntity::getLocation, loc));
    }

    @Nonnull
    public PlayerFilterGeneric<T> closestFirst(@Nonnull T loc) {
        loc.getLocation(LOCATION_CACHE_CLOSEST);
        return sorted(sortClosestBy(WarlordsEntity::getLocation, LOCATION_CACHE_CLOSEST));
    }

    @Nonnull
    public PlayerFilterGeneric<T> closestFirst(@Nonnull Entity loc) {
        loc.getLocation(LOCATION_CACHE_CLOSEST);
        return sorted(sortClosestBy(WarlordsEntity::getLocation, LOCATION_CACHE_CLOSEST));
    }

    @Nonnull
    public PlayerFilterGeneric<T> leastAliveFirst() {
        return sorted(Comparator.comparing(wp -> wp.getHealth() / wp.getMaxHealth()));
    }

    @Nonnull
    public PlayerFilterGeneric<T> mostAliveFirst() {
        return sorted(Comparator.<WarlordsEntity, Float>comparing(wp -> wp.getHealth() / wp.getMaxHealth()).reversed());
    }

    @Nonnull
    public PlayerFilterGeneric<T> leastEnergeticFirst() {
        return sorted(Comparator.comparing(wp -> wp.getEnergy() / (double) wp.getMaxEnergy()));
    }

    @Nonnull
    public PlayerFilterGeneric<T> mostEnergeticFirst() {
        return sorted(Comparator.<WarlordsEntity, Double>comparing(wp -> wp.getEnergy() / (double) wp.getMaxEnergy()).reversed());
    }

    @Nonnull
    public PlayerFilterGeneric<T> soulBindedFirst(T owner) {
        return sorted(Comparator.comparing(wp -> !owner.getCooldownManager().hasBoundPlayer(wp)));
    }

    @Nonnull
    public PlayerFilterGeneric<T> lookingAtFirst(T user) {
        return sorted((wp1, wp2) -> {
            int output;
            double wp1Dot = -Utils.getDotToPlayer(user.getEntity(), wp1.getEntity(), 0);
            double wp2Dot = -Utils.getDotToPlayer(user.getEntity(), wp2.getEntity(), 0);
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
    public PlayerFilterGeneric<T> isAlive() {
        return filter(WarlordsEntity::isAlive);
    }

    @Nonnull
    public PlayerFilterGeneric<T> isDead() {
        return filter(WarlordsEntity::isAlive);
    }

    @Nonnull
    public PlayerFilterGeneric<T> enemiesOf(@Nonnull WarlordsEntity player) {
        return filter(player::isEnemy);
    }

    @Nonnull
    public PlayerFilterGeneric<T> aliveEnemiesOf(@Nonnull WarlordsEntity player) {
        return filter(player::isEnemyAlive);
    }

    @Nonnull
    public PlayerFilterGeneric<T> teammatesOf(@Nonnull T player) {
        return filter(player::isTeammate);
    }

    @Nonnull
    public PlayerFilterGeneric<T> aliveTeammatesOf(@Nonnull T player) {
        return filter(player::isTeammateAlive);
    }

    @Nonnull
    public PlayerFilterGeneric<T> teammatesOfExcludingSelf(@Nonnull T player) {
        return filter(wp -> player != wp && player.isTeammate(wp));
    }

    @Nonnull
    public PlayerFilterGeneric<T> aliveTeammatesOfExcludingSelf(@Nonnull T player) {
        return filter(wp -> player != wp && player.isTeammateAlive(wp));
    }

    @Nonnull
    public PlayerFilterGeneric<T> aliveMatchingTeam(@Nonnull Team team) {
        return filter(wp -> wp.getTeam() == team && wp.isAlive());
    }

    @Nonnull
    public PlayerFilterGeneric<T> matchingTeam(@Nonnull Team team) {
        return filter(wp -> wp.getTeam() == team);
    }

    @Nonnull
    public PlayerFilterGeneric<T> excluding(@Nonnull T... exclude) {
        return exclude.length == 0 ? this : excluding0(new HashSet<>(Arrays.asList(exclude)));
    }

    @Nonnull
    public PlayerFilterGeneric<T> excluding(@Nonnull Collection<T> exclude) {
        return exclude.isEmpty() ? this : excluding0(exclude instanceof Set ? (Set<T>) exclude : new HashSet<>(exclude));
    }

    @Nonnull
    protected PlayerFilterGeneric<T> excluding0(@Nonnull Set<T> exclude) {
        return filter(p -> !exclude.contains(p));
    }

    @Override
    public void forEach(@Nonnull Consumer<? super T> action) {
        stream.forEach(action);
        stream.close();
    }

    public boolean first(@Nonnull Consumer<? super T> action) {
        Optional<T> findAny = this.findAny();
        if (!findAny.isPresent()) {
            return false;
        }
        action.accept(findAny.get());
        return true;
    }

    @Nonnull
    @Override
    public Iterator<T> iterator() {
        return stream.iterator();
    }

    @Nonnull
    public static PlayerFilterGeneric<WarlordsEntity> entitiesAround(@Nonnull WarlordsEntity entity, double x, double y, double z) {
        return entitiesAround(entity.getLocation(LOCATION_CACHE_ENTITIES_AROUND), x, y, z);
    }

    @Nonnull
    public static PlayerFilterGeneric<WarlordsEntity> entitiesAround(@Nonnull Entity entity, double x, double y, double z) {
        return entitiesAround(entity.getLocation(LOCATION_CACHE_ENTITIES_AROUND), x, y, z);
    }

    @Nonnull
    public static PlayerFilterGeneric<WarlordsEntity> entitiesAround(@Nonnull Location location, double x, double y, double z) {
        return new PlayerFilterGeneric<>(entitiesAround0(location, x, y, z));
    }

    @Nonnull
    protected static Stream<WarlordsEntity> entitiesAround0(@Nonnull Location location, double x, double y, double z) {
        return entitiesAroundRectangle0(location, x, y, z)
                .filter(radiusAround(WarlordsEntity::getLocation, location, x, y, z));
    }

    @Nonnull
    public static PlayerFilterGeneric<WarlordsEntity> entitiesAroundRectangle(@Nonnull WarlordsEntity entity, double x, double y, double z) {
        return entitiesAroundRectangle(entity.getLocation(LOCATION_CACHE_ENTITIES_AROUND), x, y, z);
    }

    @Nonnull
    public static PlayerFilterGeneric<WarlordsEntity> entitiesAroundRectangle(@Nonnull Entity entity, double x, double y, double z) {
        return entitiesAroundRectangle(entity.getLocation(LOCATION_CACHE_ENTITIES_AROUND), x, y, z);
    }

    @Nonnull
    public static PlayerFilterGeneric<WarlordsEntity> entitiesAroundRectangle(@Nonnull Location location, double x, double y, double z) {
        return new PlayerFilterGeneric<>(entitiesAroundRectangle0(location, x, y, z));
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
    public static PlayerFilterGeneric<WarlordsEntity> entitiesInRectangle(@Nonnull World world, double x1, double y1, double z1, double x2, double y2, double z2) {
        double minX = Math.min(x1, x2);
        double minY = Math.min(y1, y2);
        double minZ = Math.min(z1, z2);
        double maxX = Math.max(x1, x2);
        double maxY = Math.max(y1, y2);
        double maxZ = Math.max(z1, z2);

        return new PlayerFilterGeneric<>(world.getEntities().stream()
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
    public static PlayerFilterGeneric<WarlordsEntity> playingGame(@Nonnull Game game) {
        return new PlayerFilterGeneric<>(game.warlordsEntities());
    }

    @Nonnull
    public static PlayerFilterGeneric<WarlordsPlayer> playingGameWarlordsPlayers(@Nonnull Game game) {
        return new PlayerFilterGeneric<>(game.warlordsPlayers());
    }

    @Nonnull
    public static PlayerFilterGeneric<WarlordsNPC> playingGameWarlordsNPCs(@Nonnull Game game) {
        return new PlayerFilterGeneric<>(game.warlordsNPCs());
    }

    @Nonnull
    public static PlayerFilterGeneric<WarlordsEntity> entities(@Nonnull Collection<Entity> entities) {
        return new PlayerFilterGeneric<>(entities0(entities.stream()));
    }

    @Nonnull
    public static PlayerFilterGeneric<WarlordsEntity> entities(@Nonnull Iterable<Entity> entities) {
        return entities(entities.iterator());
    }

    @Nonnull
    public static PlayerFilterGeneric<WarlordsEntity> entities(@Nonnull Iterator<Entity> entities) {
        return new PlayerFilterGeneric<>(entities0(StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(entities, Spliterator.ORDERED),
                false
        )));
    }

    @Nonnull
    public static PlayerFilterGeneric<WarlordsEntity> entities(@Nonnull Entity... entities) {
        return new PlayerFilterGeneric<>(entities0(Stream.of(entities)));
    }

    @Nonnull
    protected static Stream<WarlordsEntity> entities0(@Nonnull Stream<Entity> entities) {
        return entities
                .map(Warlords::getPlayer)
                .filter(Objects::nonNull);
    }

    @Nonnull
    public Optional<T> findAny() {
        return stream.findAny();
    }

    @Nonnull
    public Optional<T> findFirst() {
        return stream.findFirst();
    }

    @Nullable
    public T findAnyOrNull() {
        return findAny().orElse(null);
    }

    @Nullable
    public T findFirstOrNull() {
        return findFirst().orElse(null);
    }

    @Nonnull
    public PlayerFilterGeneric<T> requireLineOfSight(@Nonnull T warlordsEntity) {
        return requireLineOfSight(warlordsEntity.getEntity());
    }

    @Nonnull
    public PlayerFilterGeneric<T> requireLineOfSight(@Nonnull LivingEntity entity) {
        return filter(wp -> Utils.isLookingAt(entity, wp.getEntity()) && Utils.hasLineOfSight(entity, wp.getEntity()));
    }

    @Nonnull
    public PlayerFilterGeneric<T> requireLineOfSightIntervene(@Nonnull T warlordsPlayer) {
        return requireLineOfSightIntervene(warlordsPlayer.getEntity());
    }

    @Nonnull
    public PlayerFilterGeneric<T> requireLineOfSightIntervene(@Nonnull LivingEntity entity) {
        return filter(wp -> Utils.isLookingAtIntervene(entity, wp.getEntity()));
    }

    @Nonnull
    public PlayerFilterGeneric<T> lookingAtWave(@Nonnull T warlordsEntity) {
        return lookingAtWave(warlordsEntity.getEntity());
    }

    @Nonnull
    public PlayerFilterGeneric<T> lookingAtWave(@Nonnull LivingEntity entity) {
        return filter(wp -> Utils.isLookingAtWave(entity, wp.getEntity()));
    }

    public List<T> toList() {
        return this.stream.collect(Collectors.toCollection(ArrayList::new));
    }

}
