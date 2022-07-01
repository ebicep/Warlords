package com.ebicep.warlords.pve.ai;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public abstract class AbstractMob {

    private final String mobName;
    private final String mobSubName;
    private final int mobId;
    private final double mobHealth;
    private final double mobDamage;
    private final double mobSpeed;
    private final double mobArmor;

    private static final DecimalFormat decimalFormat = new DecimalFormat("#.#");

    static {
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
    }

    public AbstractMob(String mobName, String mobSubName, int mobId, double mobHealth, double mobDamage, double mobSpeed, double mobArmor) {
        this.mobName = mobName;
        this.mobSubName = mobSubName;
        this.mobId = mobId;
        this.mobHealth = mobHealth;
        this.mobDamage = mobDamage;
        this.mobSpeed = mobSpeed;
        this.mobArmor = mobArmor;
    }

    public abstract void onAttack(WarlordsEntity hxp, Player player, AbstractMob mob);

    public void spawn() {
    }

    public String getMobName() {
        return mobName;
    }

    public String getMobSubName() {
        return mobSubName;
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