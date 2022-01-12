package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Shotty extends AbstractStrikeBase {
    public Shotty() {
        super("Shotty", 100, 101, 0, 0, 100, 12);
    }

    @Override
    public void updateDescription(Player player) {

    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer) {
        nearPlayer.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
        nearPlayer.getCooldownManager().addCooldown("LEECH DEBUFF", this.getClass(), Shotty.class, "LEECH", 10, wp, CooldownTypes.DEBUFF);
    }
}
