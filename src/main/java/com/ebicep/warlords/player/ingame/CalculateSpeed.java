package com.ebicep.warlords.player.ingame;

import java.util.*;
import java.util.function.Consumer;

public class CalculateSpeed {
    private final float BASE_SPEED = 7.02f;
    private final Modifier baseModifier;
    private final float maxSpeed;
    private final List<Modifier> modifiers = new LinkedList<>();
    private final Consumer<Float> updateWalkingSpeed;
    private float baseSpeedToWalkingSpeed = 0.2825f / 113 * 100 / BASE_SPEED;
    private float minSpeed;
    private float lastSpeed = 0;
    private boolean changed = true;
    private boolean hasPendingTimers = false;
    private boolean hasEffectAlteringEffects = false;

    public CalculateSpeed(WarlordsEntity from, Consumer<Float> updateWalkingSpeed, float baseModifierValue) {
        // For some reason, the base speed of your weapon matters for your min speed, but your max speed is not affected by this
        this.minSpeed = BASE_SPEED * (1 + baseModifierValue / 100f) * (1 - .35f);
        this.maxSpeed = BASE_SPEED * 1.40f;
        this.updateWalkingSpeed = updateWalkingSpeed;
        this.baseModifier = new Modifier(from, "BASE", baseModifierValue, 0, Collections.emptyList(), false);
        this.modifiers.add(this.baseModifier);
    }

    public CalculateSpeed(WarlordsEntity from, Consumer<Float> updateWalkingSpeed, float baseModifierValue, boolean isPve) {
        this.minSpeed = BASE_SPEED * (1 + baseModifierValue / 100f) * (1 - 0.99f);
        this.maxSpeed = BASE_SPEED * 2.5f;
        this.updateWalkingSpeed = updateWalkingSpeed;
        this.baseModifier = new Modifier(from, "BASE PVE", baseModifierValue, 0, Collections.emptyList(), false);
        this.modifiers.add(this.baseModifier);
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

            if (speed < this.minSpeed) {
                speed = this.minSpeed;
            }
            if (speed > this.maxSpeed) {
                speed = this.maxSpeed;
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
                float walkSpeed = speed * baseSpeedToWalkingSpeed;
//                Bukkit.broadcastMessage("Speed updated ("+lastSpeed+" --> " +speed + ") walkSpeed: "+walkSpeed+" causes:");
//                for (Modifier mod : appliedEffects.values()) {
//                    Bukkit.broadcastMessage(String.valueOf(mod));
//                }
                lastSpeed = speed;
                this.updateWalkingSpeed.accept(walkSpeed);
            }
        }
    }

    /**
     * Add a speed change object
     *
     * @param from
     * @param name      Unique name of the effect source
     * @param modifier  a value like +30 or -20, in percent
     * @param duration  The duration of this speedchange, 0 means no duration
     * @param toDisable The modifiers this should override for as long as it is active
     * @return A runnable that can be used to manually remove this entry
     */
    public Runnable addSpeedModifier(WarlordsEntity from, String name, float modifier, int duration, String... toDisable) {
        return addSpeedModifier(from, name, modifier, duration, Arrays.asList(toDisable));
    }

    public Runnable addSpeedModifier(WarlordsEntity from, String name, float modifier, int duration, Collection<String> toDisable) {
        return addSpeedModifier(from, name, modifier, duration, false, toDisable);
    }

    public Runnable addSpeedModifier(WarlordsEntity from, String name, float modifier, int duration, boolean afterLimit, Collection<String> toDisable) {
        return addSpeedModifier(new Modifier(from,
                name,
                modifier,
                duration,
                toDisable,
                afterLimit
        ));
    }

    public Runnable addSpeedModifier(Modifier mod) {
        ListIterator<Modifier> iterator = this.modifiers.listIterator();
        while (iterator.hasNext()) {
            Modifier next = iterator.next();
            if (Objects.equals(next.name, mod.name)) {
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

    public Runnable addSpeedModifier(WarlordsEntity from, String name, int modifier, int duration, boolean afterLimit, String... toDisable) {
        return addSpeedModifier(from, name, modifier, duration, afterLimit, Arrays.asList(toDisable));
    }

    // Removes negative speed effects.
    public void removeSlownessModifiers() {
        boolean isChanged = this.modifiers.removeIf(modifier -> modifier.duration > 0 && modifier.calculatedModifier < 1);
        this.changed = changed || isChanged;
    }

    public void removeModifier(String name) {
        boolean isChanged = this.modifiers.removeIf(modifier -> modifier.name.equals(name));
        this.changed = changed || isChanged;
    }

    public void addBaseModifier(float add) {
        baseModifier.setModifier(baseModifier.modifier + add);
        changed = true;
    }

    public void setBaseSpeedToWalkingSpeed(float baseSpeedToWalkingSpeed) {
        this.baseSpeedToWalkingSpeed = baseSpeedToWalkingSpeed / 113 * 100 / BASE_SPEED;
    }

    public float getBaseSpeedToWalkingSpeed() {
        return baseSpeedToWalkingSpeed;
    }

    public List<Modifier> getModifiers() {
        return modifiers;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public float getLastSpeed() {
        return lastSpeed;
    }

    public static class Modifier {
        public final WarlordsEntity from;
        public final String name;
        public final boolean afterLimit;
        public final Collection<String> toDisable;
        public float modifier;
        public float calculatedModifier;
        public int duration;

        public Modifier(WarlordsEntity from, String name, float modifier, int duration, Collection<String> toDisable, boolean afterLimit) {
            this.from = from;
            this.name = name;
            this.modifier = modifier;
            this.calculatedModifier = 1 + modifier / 100f;
            this.duration = duration == 0 ? 0 : duration + 1; // add 1 tick to deal with effects lasting exactly as long
            this.afterLimit = afterLimit;
            this.toDisable = toDisable;
        }

        public void setModifier(float modifier) {
            this.modifier = modifier;
            this.calculatedModifier = 1 + modifier / 100f;
        }

        @Override
        public String toString() {
            return "Modifier{" + "name=" + name + ", modifier=" + modifier + " (" + calculatedModifier + "), duration=" + duration + ", toDisable=" + toDisable + '}';
        }
    }
}