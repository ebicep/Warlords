package com.ebicep.warlords.player.cooldowns;

import com.ebicep.warlords.player.WarlordsPlayer;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CooldownFilter<T extends AbstractCooldown<?>> implements Iterable<T> {

    private final Stream<T> stream;
    private Class<T> clazz;

    public CooldownFilter(Stream<T> stream) {
        this.stream = stream;
    }

    public CooldownFilter(List<AbstractCooldown<?>> abstractCooldownList, Class<T> clazz) {
        this.stream = abstractCooldownList.stream().filter(clazz::isInstance).map(clazz::cast);
        this.clazz = clazz;
    }

    public CooldownFilter(CooldownManager cooldownManager, Class<T> clazz) {
        this.stream = cooldownManager.getCooldowns().stream().filter(clazz::isInstance).map(clazz::cast);
        this.clazz = clazz;
    }

    public CooldownFilter(WarlordsPlayer warlordsPlayer, Class<T> clazz) {
        this.stream = warlordsPlayer.getCooldownManager().getCooldowns().stream().filter(clazz::isInstance).map(clazz::cast);
        this.clazz = clazz;
    }

    public CooldownFilter<T> filterName(String name) {
        return new CooldownFilter<>(stream.filter(abstractCooldown -> abstractCooldown.getName() != null && abstractCooldown.getName().equalsIgnoreCase(name)));
    }

    public CooldownFilter<T> filterNameActionBar(String name) {
        return new CooldownFilter<>(stream.filter(abstractCooldown -> abstractCooldown.getActionBarName() != null && abstractCooldown.getActionBarName().equalsIgnoreCase(name)));
    }

    public <R> CooldownFilter<T> filterCooldownClass(Class<R> clazz) {
        return new CooldownFilter<>(stream.filter(cd -> Objects.equals(clazz, cd.getCooldownClass())));
    }

    public CooldownFilter<T> filterCooldownObject(Object object) {
        return new CooldownFilter<>(stream.filter(cd -> Objects.equals(object, cd.getCooldownObject())));
    }

    public CooldownFilter<T> filterCooldownFrom(WarlordsPlayer warlordsPlayer) {
        return new CooldownFilter<>(stream.filter(cd -> Objects.equals(warlordsPlayer, cd.getFrom())));
    }

    public CooldownFilter<T> filter(Predicate<T> predicate) {
        return new CooldownFilter<>(stream.filter(predicate));
    }

    public <R> Stream<R> filterCooldownClassAndMapToObjectsOfClass(Class<R> clazz) {
        return stream.filter(cd -> Objects.equals(clazz, cd.getCooldownClass()))
                .map(t -> t.getCooldownObject())
                .filter(Objects::nonNull)
                .map(clazz::cast);
    }

    public <R> Stream<R> mapToObjectsOfClass(Class<R> clazz) {
        return stream.map(t -> t.getCooldownObject())
                .filter(Objects::nonNull)
                .map(clazz::cast);
    }

    public <R> Optional<R> findFirstObject(Object object, Class<R> clazz) {
        assert object.getClass() == clazz;
        return stream.filter(cd -> cd.getCooldownObject() != null && cd.getCooldownObject().equals(object))
                .map(cd -> cd.getCooldownObject())
                .filter(Objects::nonNull)
                .findFirst()
                .map(clazz::cast);
    }

    public Optional<T> findFirst() {
        return stream.findFirst();
    }

    public Optional<T> findAny() {
        return stream.findAny();
    }

    public <R> Optional<R> findFirstObjectOfClass(Class<R> clazz) {
        return stream.findFirst().map(t -> clazz.cast(t.getCooldownObject()));
    }

    public Stream<T> stream() {
        return stream;
    }

    @Nonnull
    @Override
    public Iterator<T> iterator() {
        return stream.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        stream.forEach(action);
        stream.close();
    }
}
