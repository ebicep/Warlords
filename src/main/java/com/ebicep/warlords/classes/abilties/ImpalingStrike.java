package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ImpalingStrike extends AbstractStrikeBase {
    public ImpalingStrike() {
        super("Impaling Strike", 323, 412, 0, 100, 20, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "";
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer) {
        nearPlayer.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
        nearPlayer.getCooldownManager().addCooldown("LEECH DEBUFF", this.getClass(), ImpalingStrike.class, "LEECH", 10, wp, CooldownTypes.DEBUFF);
    }
}
