package com.ebicep.warlords.player.ingame.motionsystem;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.motionsystem.motionaddon.MotionAddon;
import com.ebicep.warlords.player.ingame.motionsystem.motionaddon.RemovalCondition;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MotionModifier {

    private final String name;
    private final List<MotionAddon> addons = new ArrayList<>();
    @Nullable
    private final WarlordsEntity from;
    private float modifier;
    private int ticksLeft;
    private Runnable onChange = () -> {};

    public MotionModifier(@Nullable WarlordsEntity from, String name, float modifier, int duration, List<MotionAddon> addons) {
        this.from = from;
        this.name = name;
        this.modifier = modifier;
        this.ticksLeft = duration;
        this.addons.add(new RemovalCondition() {
            @Override
            public String addonName() {
                return "Tick Removal Condition";
            }

            @Override
            public boolean removeAnyMatch() {
                return ticksLeft == 0;
            }
        });
        this.addons.addAll(addons);
    }

    public void tick() {
        if (ticksLeft > 0) {
            ticksLeft--;
        }
    }

    public void setOnChange(Runnable onChange) {
        this.onChange = onChange;
    }

    public int getTicksLeft() {
        return ticksLeft;
    }

    public void setTicksLeft(int ticksLeft) {
        this.ticksLeft = ticksLeft;
    }

    public @Nullable WarlordsEntity getFrom() {
        return from;
    }

    public List<MotionAddon> getAddons() {
        return addons;
    }

    public String getName() {
        return name;
    }

    public float getModifier() {
        return modifier;
    }

    public void setModifier(float modifier) {
        if (this.modifier != modifier) {
            this.modifier = modifier;
            onChange.run();
        }
    }

}
