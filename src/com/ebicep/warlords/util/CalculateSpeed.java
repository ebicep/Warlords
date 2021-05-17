package com.ebicep.warlords.util;

import java.util.*;
import java.util.function.Consumer;
import org.bukkit.Bukkit;

public class CalculateSpeed {
    private final float BASE_SPEED = 7.02f;
    private final float BASE_SPEED_TO_WALKING_SPEED = 0.2825f/113*100/BASE_SPEED;

    private final float minspeed;
    private final float maxspeed;
    private final List<Modifier> modifiers = new LinkedList<>();
    private final Consumer<Float> updateWalkingSpeed;
    private float lastSpeed = 0;

    public CalculateSpeed(Consumer<Float> updateWalkingSpeed, int baseModifier) {
        // For some reason, the base speed of your weapon matters for your min speed, but your max speed is not affected by this
        this.minspeed = BASE_SPEED * (1 + baseModifier / 100f) * (1 - .35f);
        this.maxspeed = BASE_SPEED * 1.40f;
        this.updateWalkingSpeed = updateWalkingSpeed;
        this.modifiers.add(new Modifier("BASE", baseModifier, 0));
    }

    /**
     * Called every tick. This function calculates the new speed and updates the player
     */

    public void updateSpeed() {
        Iterator<Modifier> iterator = this.modifiers.iterator();
        float speed = BASE_SPEED;
        while(iterator.hasNext()) {
            Modifier next = iterator.next();
            if(next.duration != 0) {
                next.duration--;
                if(next.duration == 0) {
                    iterator.remove();
                    continue;
                }
            }
            speed *= next.calculatedModifier;
        }
        if(speed < this.minspeed) {
            speed = this.minspeed;
        }
        if(speed > this.maxspeed) {
            speed = this.maxspeed;
        }
        if(speed != lastSpeed) {
            float walkSpeed = speed * BASE_SPEED_TO_WALKING_SPEED;
            // DEBUG - Bukkit.broadcastMessage("Speed updated ("+lastSpeed+" --> " +speed + ") walkSpeed: "+walkSpeed+" causes:");
            for(Modifier mod : this.modifiers) {
                Bukkit.broadcastMessage(mod.toString());
            }
            lastSpeed = speed;
            this.updateWalkingSpeed.accept(walkSpeed);
        }
    }

    /**
     * Add a speed change object
     * @param name Unique name of the effect source
     * @param modifier a value like +30 or -20, in percent
     * @param duration The duration of this speedchange, 0 means no duration
     * @return A runnable that can be used to manually remove this entry
     */

    public Runnable changeCurrentSpeed(String name, int modifier, int duration) {
        Modifier mod = new Modifier(name, modifier, duration);
        ListIterator<Modifier> iterator = this.modifiers.listIterator();
        while(iterator.hasNext()) {
            Modifier next = iterator.next();
            if(Objects.equals(next.name, name)) {
                iterator.set(mod);
                return () -> modifiers.remove(mod);
            }
        }
        modifiers.add(mod);
        return () -> modifiers.remove(mod);
    }

    private static class Modifier {
        public final String name;
        public final int modifier;
        public final float calculatedModifier;
        public int duration;

        public Modifier(String name, int modifier, int duration) {
            this.name = name;
            this.modifier = modifier;
            this.calculatedModifier = 1 + modifier / 100f;
            this.duration = duration;
        }

        @Override
        public String toString() {
            return "Modifier{" + "name=" + name + ", modifier=" + modifier + " (" + calculatedModifier + "), duration=" + duration + '}';
        }
    }
}