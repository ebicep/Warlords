package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ImpalingStrike extends AbstractStrikeBase {
    public ImpalingStrike() {
        super("Impaling Strike", 323, 412, 0, 100, 20, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Impale an enemy, dealing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage\n" +
                "§7and afflict them with the leech curse for §65 §7seconds.\n" +
                "When an ally or you deals melee damage to an\n" +
                "§7enemy afflicted with the leeching effect, heal\n" +
                "§7for §a50% §7of the melee damage dealt.";
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer) {
        nearPlayer.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
        nearPlayer.getCooldownManager().removeCooldown(ImpalingStrike.class);
        nearPlayer.getCooldownManager().addRegularCooldown("Leech Debuff", "LEECH", ImpalingStrike.class, new ImpalingStrike(), wp, CooldownTypes.DEBUFF, cooldownManager -> {
        }, 5 * 20);
    }
}
