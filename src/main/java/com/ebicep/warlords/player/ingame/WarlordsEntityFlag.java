package com.ebicep.warlords.player.ingame;

public enum WarlordsEntityFlag {

    NO_ENERGY_CONSUMPTION(false),
    DISABLE_COOLDOWNS(false),
    TAKE_DAMAGE(true),
    CAN_CRIT(true),
    GAIN_ENERGY(true), // does not effect messages

    ;

    private final boolean defaultValue;

    WarlordsEntityFlag(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }

}
