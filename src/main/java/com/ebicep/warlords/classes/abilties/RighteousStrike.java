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
        description = "§7Strike the targeted enemy for §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + "§7.\n" +
                "§7Each strike reduces the duration of your striked\n" +
                "§7target's active ability timers by §60.5 §7seconds.";
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer) {

        nearPlayer.getCooldownManager().getCooldowns().forEach((cooldown) -> {
            if (cooldown.getCooldownType() == CooldownTypes.ABILITY && cooldown instanceof RegularCooldown) {
                ((RegularCooldown<?>) cooldown).subtractTime(10);
            }
        });

        nearPlayer.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
    }
}
