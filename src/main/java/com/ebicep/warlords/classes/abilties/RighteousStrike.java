package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class RighteousStrike extends AbstractStrikeBase {

    public RighteousStrike() {
        super("Righteous Strike", 358, 455, 0, 90, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Strike the targeted enemy for §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage.\n" +
                "§7Each strike reduces the duration of your striked\n" +
                "§7target's active ability timers by §60.5 §7(§60.75 §7for\n" +
                "§7silenced targets) seconds.";
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer) {
        if (nearPlayer.getCooldownManager().hasCooldown(SoulShackle.class)) {
            nearPlayer.getCooldownManager().subtractTicksOnRegularCooldowns(CooldownTypes.ABILITY, 15);
        } else {
            nearPlayer.getCooldownManager().subtractTicksOnRegularCooldowns(CooldownTypes.ABILITY, 10);
        }
        nearPlayer.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
    }
}
