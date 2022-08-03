package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.DamageHealCompleteCooldown;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class JudgementStrike extends AbstractStrikeBase {

    private int attacksDone = 0;
    private int speedOnCrit = 25;
    private int speedOnCritDuration = 2;
    private int strikeCritInterval = 4;

    public JudgementStrike() {
        super("Judgement Strike", 326, 441, 0, 70, 20, 185);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Strike the targeted enemy, dealing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage.\n" +
                "§7Every fourth attack is a §cguaranteed §7critical strike.\n" +
                "§7Critical strikes temporarily increase your movement\n" +
                "§7speed by §e" + speedOnCrit + "% §7for §e" + speedOnCritDuration + " §7seconds.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Players Struck", "" + timesUsed));

        return info;
    }

    @Override
    protected void onHit(@Nonnull WarlordsEntity wp, @Nonnull Player player, @Nonnull WarlordsEntity nearPlayer) {

        attacksDone++;
        float critChance = this.critChance;
        if (attacksDone == strikeCritInterval) {
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
                    event.getAttacker().getSpeed().addSpeedModifier("Judgement Speed", speedOnCrit, speedOnCritDuration * 20, "BASE");
                }
            }
        });
        nearPlayer.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
    }

    public int getSpeedOnCrit() {
        return speedOnCrit;
    }

    public void setSpeedOnCrit(int speedOnCrit) {
        this.speedOnCrit = speedOnCrit;
    }

    public int getStrikeCritInterval() {
        return strikeCritInterval;
    }

    public void setStrikeCritInterval(int strikeCritInterval) {
        this.strikeCritInterval = strikeCritInterval;
    }

    public int getSpeedOnCritDuration() {
        return speedOnCritDuration;
    }

    public void setSpeedOnCritDuration(int speedOnCritDuration) {
        this.speedOnCritDuration = speedOnCritDuration;
    }
}
