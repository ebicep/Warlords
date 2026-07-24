package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.java.RandomCollection;
import org.bukkit.Location;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class AnomalyMobSet {

    private final RandomCollection<Mob> randomCollection = new RandomCollection<>();
    private final Set<Mob> mobs = new LinkedHashSet<>();

    public AnomalyMobSet add(Mob mob) {
        double weight = randomCollection.getSize() == 0
                ? 1
                : randomCollection.getTotal() / randomCollection.getSize();
        return add(weight, mob);
    }

    public AnomalyMobSet add(double weight, Mob mob) {
        randomCollection.add(weight, mob);
        mobs.add(mob);
        return this;
    }

    public Mob getRandomMob() {
        Mob mob = randomCollection.next();
        if (mob == null) {
            throw new IllegalStateException("An anomaly mob set cannot be empty");
        }
        return mob;
    }

    public AbstractMob createMob(Location location) {
        return getRandomMob().createMob(location);
    }

    public List<Mob> getMobs() {
        return List.copyOf(mobs);
    }
}
