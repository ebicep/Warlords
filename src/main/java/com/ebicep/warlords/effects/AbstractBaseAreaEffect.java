package com.ebicep.warlords.effects;

import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;

public abstract class AbstractBaseAreaEffect<T extends EffectPlayer<?>> implements Iterable<T> {

    @Nonnull
    protected Location center;
    protected final List<T> effects = new ArrayList<>();
    protected double additionalYOffset = 0;

    @Nonnull
    public Location getCenter() {
        return center;
    }

    public void setCenter(@Nonnull Location center) {
        center.setY(center.getBlockY() + 0.01 + additionalYOffset);
        this.center = center;
    }

    public void addEffect(T effect) {
        this.effects.add(effect);
    }

    @SafeVarargs
    public final void addEffects(T... effect) {
        this.effects.addAll(Arrays.asList(effect));
    }

    public boolean removeEffect(T o) {
        return effects.remove(o);
    }

    public void clearEffects() {
        effects.clear();
    }

    @SafeVarargs
    public final void replaceEffects(Predicate<T> search, T... replaceWith) {
        ListIterator<T> itr = this.effects.listIterator();
        int replaced = 0;
        while (itr.hasNext()) {
            T value = itr.next();
            if (search.test(value)) {
                if (replaced < replaceWith.length) {
                    itr.set(replaceWith[replaced]);
                    replaced++;
                } else {
                    itr.remove();
                }
            }
        }
        for (; replaced < replaceWith.length; replaced++) {
            itr.add(replaceWith[replaced]);
        }
    }

    @Nonnull
    @Override
    public Iterator<T> iterator() {
        return effects.iterator();
    }

    public abstract void playEffects();
}
