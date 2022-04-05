package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.DamageHealCompleteCooldown;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class JudgementStrike extends AbstractStrikeBase {

    int attacksDone = 0;

    public JudgementStrike() {
        super("Judgement Strike", 326, 441, 0, 70, 20, 185);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Strike the targeted enemy, dealing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage.\n" +
                "§7Every fourth attack is a §cguaranteed §7critical strike.\n" +
                "§7Critical strikes temporarily increase your movement\n" +
                "§7speed by §e25% §7for §e2 §7seconds.";
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer) {

        attacksDone++;
        int critChance = this.critChance;
        if (attacksDone == 4) {
            attacksDone = 0;
            critChance = 100;
        }

        wp.getCooldownManager().addCooldown(new DamageHealCompleteCooldown<JudgementStrike>(
                "Judgment Strike",
                "",
                JudgementStrike.class,
                new JudgementStrike(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                }
        ) {
            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (event.getAbility().equals("Judgement Strike") && isCrit) {
                    event.getAttacker().getSpeed().addSpeedModifier("Judgement Speed", 25, 2 * 20, "BASE");
                }
            }
        });
        nearPlayer.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
    }
}
