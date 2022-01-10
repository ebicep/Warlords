package com.ebicep.warlords.player;

import java.util.*;
import java.util.function.Consumer;

public class CalculateSpeed {
    private final float BASE_SPEED = 7.02f;
    private final float BASE_SPEED_TO_WALKING_SPEED = 0.2825f / 113 * 100 / BASE_SPEED;

    private final float minspeed;
    private final float maxspeed;
    private final List<Modifier> modifiers = new LinkedList<>();
    private final Consumer<Float> updateWalkingSpeed;
    private float lastSpeed = 0;
    private boolean changed = true;
    private boolean hasPendingTimers = false;
    private boolean hasEffectAlteringEffects = false;

    public CalculateSpeed(Consumer<Float> updateWalkingSpeed, int baseModifier) {
        // For some reason, the base speed of your weapon matters for your min speed, but your max speed is not affected by this
        this.minspeed = BASE_SPEED * (1 + baseModifier / 100f) * (1 - .35f);
        this.maxspeed = BASE_SPEED * 1.40f;
        this.updateWalkingSpeed = updateWalkingSpeed;
        this.modifiers.add(new Modifier("BASE", baseModifier, 0, Collections.emptyList(), false));
    }

    /**
     * Called every tick. This function calculates the new speed and updates the player
     */

    public void updateSpeed() {
        if (changed || hasPendingTimers) {
            boolean hasPendingTimers = false;
            boolean hasEffectAlteringEffects = false;
            Iterator<Modifier> iterator = this.modifiers.iterator();
            while (iterator.hasNext()) {
                Modifier next = iterator.next();
                if (next.duration != 0) {
                    next.duration--;
                    if (next.duration == 0) {
                        iterator.remove();
                        changed = true;
                        continue;
                    }
                    hasPendingTimers = true;
                    if (!next.toDisable.isEmpty()) {
                        hasEffectAlteringEffects = true;
                    }
                }
            }
            this.hasEffectAlteringEffects = hasEffectAlteringEffects;
            this.hasPendingTimers = hasPendingTimers;
        }
        if (changed) {
            changed = false;
            float speed = BASE_SPEED;

            Map<String, Modifier> appliedEffects = new HashMap<>();
            for (Modifier next : this.modifiers) {
                if (next.afterLimit) {
                    continue;
                }
                if (hasEffectAlteringEffects) {
                    for (String toDisable : next.toDisable) {
                        Modifier mod = appliedEffects.put(toDisable, null);
                        if (mod != null) {
                            speed /= mod.calculatedModifier;
                        }
                    }
                    if (appliedEffects.containsKey(next.name)) {
                        continue;
                    }
                }
                speed *= next.calculatedModifier;
                appliedEffects.put(next.name, next);
            }

            if (speed < this.minspeed) {
                speed = this.minspeed;
            }
            if (speed > this.maxspeed) {
                speed = this.maxspeed;
            }

            for (Modifier next : this.modifiers) {
                if (!next.afterLimit) {
                    continue;
                }
                if (hasEffectAlteringEffects) {
                    for (String toDisable : next.toDisable) {
                        Modifier mod = appliedEffects.put(toDisable, null);
                        if (mod != null) {
                            speed /= mod.calculatedModifier;
                        }
                    }
                    if (appliedEffects.containsKey(next.name)) {
                        continue;
                    }
                }
                speed *= next.calculatedModifier;
                appliedEffects.put(next.name, next);
            }

            if (speed != lastSpeed) {
                float walkSpeed = speed * BASE_SPEED_TO_WALKING_SPEED;
                //Bukkit.broadcastMessage("Speed updated ("+lastSpeed+" --> " +speed + ") walkSpeed: "+walkSpeed+" causes:");
                for (Modifier mod : appliedEffects.values()) {
                    //Bukkit.broadcastMessage(String.valueOf(mod));
                }
                lastSpeed = speed;
                this.updateWalkingSpeed.accept(walkSpeed);
            }
        }
    }

    /**
     * Add a speed change object
     *
     * @param name      Unique name of the effect source
     * @param modifier  a value like +30 or -20, in percent
     * @param duration  The duration of this speedchange, 0 means no duration
     * @param toDisable The modifiers this should override for as long as it is active
     * @return A runnable that can be used to manually remove this entry
     */
    public Runnable addSpeedModifier(String name, int modifier, int duration, String... toDisable) {
        return addSpeedModifier(name, modifier, duration, Arrays.asList(toDisable));
    }

    public Runnable addSpeedModifier(String name, int modifier, int duration, Collection<String> toDisable) {
        return addSpeedModifier(name, modifier, duration, false, toDisable);
    }

    public Runnable addSpeedModifier(String name, int modifier, int duration, boolean afterLimit, String... toDisable) {
        return addSpeedModifier(name, modifier, duration, afterLimit, Arrays.asList(toDisable));
    }

    public Runnable addSpeedModifier(String name, int modifier, int duration, boolean afterLimit, Collection<String> toDisable) {
        Modifier mod = new Modifier(name, modifier, duration == 0 ? 0 : duration + 1, toDisable, afterLimit); // add 1 tick to deal with effects lasting exactly as long
        ListIterator<Modifier> iterator = this.modifiers.listIterator();
        while (iterator.hasNext()) {
            Modifier next = iterator.next();
            if (Objects.equals(next.name, name)) {
                iterator.set(mod);
                return () -> modifiers.remove(mod);
            }
        }
        modifiers.add(mod);
        changed = true;
        return () -> {
            modifiers.remove(mod);
            changed = true;
        };
    }

    // Removes negative speed effects.
    public void removeSlownessModifiers() {
        boolean isChanged = this.modifiers.removeIf(modifier -> modifier.duration > 0 && modifier.calculatedModifier < 1);
        this.changed = changed || isChanged;
    }

    private static class Modifier {
        public final String name;
        public final int modifier;
        public final float calculatedModifier;
        public int duration;
        public final boolean afterLimit;
        public final Collection<String> toDisable;

        public Modifier(String name, int modifier, int duration, Collection<String> toDisable, boolean afterLimit) {
            this.name = name;
            this.modifier = modifier;
            this.calculatedModifier = 1 + modifier / 100f;
            this.duration = duration;
            this.afterLimit = afterLimit;
            this.toDisable = toDisable;
        }

        @Override
        public String toString() {
            return "Modifier{" + "name=" + name + ", modifier=" + modifier + " (" + calculatedModifier + "), duration=" + duration + ", toDisable=" + toDisable + '}';
        }
    }
}