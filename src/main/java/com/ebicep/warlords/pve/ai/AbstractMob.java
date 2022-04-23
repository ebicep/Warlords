package com.ebicep.warlords.pve.ai;

import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public abstract class AbstractMob {

    private final String mobName;
    private final String mobDescription;
    private final int mobId;
    private final double mobHealth;
    private final double mobDamage;
    private final double mobSpeed;
    private final double mobArmor;

    private static final DecimalFormat decimalFormat = new DecimalFormat("#.#");

    static {
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
    }

    public AbstractMob(String mobName, String mobDescription, int mobId, double mobHealth, double mobDamage, double mobSpeed, double mobArmor) {
        this.mobName = mobName;
        this.mobDescription = mobDescription;
        this.mobId = mobId;
        this.mobHealth = mobHealth;
        this.mobDamage = mobDamage;
        this.mobSpeed = mobSpeed;
        this.mobArmor = mobArmor;
    }

    public abstract void onAttack(WarlordsPlayer hxp, Player player, AbstractMob mob);

    public void spawn() {
    }

    public String getMobName() {
        return mobName;
    }

    public String getMobDescription() {
        return mobDescription;
    }

    public int getMobId() {
        return mobId;
    }

    public double getMobHealth() {
        return mobHealth;
    }

    public double getMobDamage() {
        return mobDamage;
    }

    public double getMobSpeed() {
        return mobSpeed;
    }

    public double getMobArmor() {
        return mobArmor;
    }

    public String format(double input) {
        return decimalFormat.format(input);
    }
}