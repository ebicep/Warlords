package com.ebicep.warlords.player.cooldowns.cooldowns;

import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.cooldowns.CooldownManager;
import org.checkerframework.checker.units.qual.C;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;
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
        this.stream = cooldownManager.getAbilityCooldowns().stream().filter(clazz::isInstance).map(clazz::cast);
        this.clazz = clazz;
    }

    public CooldownFilter(WarlordsPlayer warlordsPlayer, Class<T> clazz) {
        this.stream = warlordsPlayer.getCooldownManager().getAbilityCooldowns().stream().filter(clazz::isInstance).map(clazz::cast);
        this.clazz = clazz;
    }

    public CooldownFilter<T> filterName(String name) {
        return new CooldownFilter<T>(stream.filter(abstractCooldown -> abstractCooldown.getName().equalsIgnoreCase(name)));
    }

    public CooldownFilter<T> filterNameActionBar(String name) {
        return new CooldownFilter<T>(stream.filter(abstractCooldown -> abstractCooldown.getActionBarName().equalsIgnoreCase(name)));
    }

    public <R> CooldownFilter<T> filterCooldownClass(Class<R> clazz) {
        return new CooldownFilter<>(stream.filter(cd -> cd.getCooldownClass().equals(clazz)));
    }

    public CooldownFilter<T> filterCooldownObject(Object object) {
        return new CooldownFilter<>(stream.filter(cd -> cd.getCooldownObject().equals(object)));
    }

    public CooldownFilter<T> filter(Predicate<T> predicate) {
        return new CooldownFilter<>(stream.filter(predicate));
    }

    public <R> Stream<R> filterCooldownClassAndMapToObjectsOfClass(Class<R> clazz) {
        return stream.filter(cd -> cd.getCooldownClass().equals(clazz)).map(t -> t.getCooldownObject()).map(clazz::cast);
    }

    public <R> Stream<R> mapToObjectsOfClass(Class<R> clazz) {
        return stream.map(t -> t.getCooldownObject()).map(clazz::cast);
    }

    public <R> Optional<R> findFirstObject(Object object, Class<R> clazz) {
        assert object.getClass() == clazz;
        return stream.filter(cd -> cd.getCooldownObject().equals(object)).findFirst().map(clazz::cast);
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

    public Stream<T> getStream() {
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
