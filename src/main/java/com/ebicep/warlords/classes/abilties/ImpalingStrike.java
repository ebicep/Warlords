package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractStrikeBase;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ImpalingStrike extends AbstractStrikeBase {

    private final int leechDuration = 5;

    public ImpalingStrike() {
        super("Impaling Strike", 387.6f, 494.4f, 0, 90, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Impale an enemy, dealing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage\n" +
                "§7and afflict them with the leech curse for §6" + leechDuration + " §7seconds.\n" +
                "When an ally or you deals damage to an enemy\n" +
                "§7afflicted with the leeching effect, heal for\n" +
                "§a30% §7(§a15% §7for allies) of all damage dealt.";
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer) {
        nearPlayer.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
        nearPlayer.getCooldownManager().removeCooldown(ImpalingStrike.class);
        nearPlayer.getCooldownManager().addCooldown(new RegularCooldown<ImpalingStrike>(
                "Leech Debuff",
                "LEECH",
                ImpalingStrike.class,
                new ImpalingStrike(),
                wp,
                CooldownTypes.DEBUFF,
                cooldownManager -> {
                },
                leechDuration * 20
        ) {
            @Override
            public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                event.getAttacker().addHealingInstance(event.getAttacker(), "Leech", currentDamageValue * 0.3f, currentDamageValue * 0.3f, isCrit ? 100 : -1, 100, false, false);
            }
        });
    }
}
