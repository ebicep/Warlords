package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class RighteousStrike extends AbstractStrikeBase {

    public RighteousStrike() {
        super("Righteous Strike", 359, 460, 0, 90, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Deal " + format(minDamageHeal) + " - " + format(maxDamageHeal);
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer) {

        nearPlayer.getCooldownManager().getCooldowns().forEach((cooldown) -> {
            if (cooldown.getCooldownType() == CooldownTypes.ABILITY && cooldown instanceof RegularCooldown) {
                ((RegularCooldown<?>) cooldown).subtractTime(20);
            }
        });

        nearPlayer.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
    }
}
