package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class JudgementStrike extends AbstractStrikeBase {
    private boolean pveUpgrade = false;

    private int attacksDone = 0;
    private int speedOnCrit = 25; // %
    private int speedOnCritDuration = 2;
    private int strikeCritInterval = 4;

    private float strikeHeal = 0;

    public JudgementStrike() {
        super("Judgement Strike", 326, 441, 0, 70, 20, 185);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Strike the targeted enemy, dealing" + formatRangeDamage(minDamageHeal, maxDamageHeal) +
                "damage. Every fourth attack is a §cguaranteed §7critical strike. Critical strikes temporarily increase your movement speed by §e" +
                speedOnCrit + "% §7for §e" + speedOnCritDuration + " §7seconds.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Players Struck", "" + timesUsed));

        return info;
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull Player player, @Nonnull WarlordsEntity nearPlayer) {
        attacksDone++;
        float critChance = this.critChance;
        if (attacksDone == strikeCritInterval) {
            attacksDone = 0;
            critChance = 100;
        }
        nearPlayer.addDamageInstance(
                wp,
                name,
                minDamageHeal,
                maxDamageHeal,
                critChance,
                critMultiplier,
                false
        ).ifPresent(finalEvent -> {
            if (finalEvent.isCrit()) {
                wp.getSpeed().addSpeedModifier("Judgement Speed", speedOnCrit, speedOnCritDuration * 20, "BASE");
            }
            if (strikeHeal != 0 && finalEvent.isDead()) {
                wp.addHealingInstance(wp, name, strikeHeal, strikeHeal, -1, 100, false, false);
            }
            if (pveUpgrade) {
                if (
                        nearPlayer instanceof WarlordsNPC &&
                                finalEvent.getFinalHealth() <= (nearPlayer.getMaxHealth() * .25) &&
                                ((WarlordsNPC) nearPlayer).getMobTier() != MobTier.BOSS
                ) {
                    nearPlayer.die(nearPlayer);
                }
            }
        });

        return true;
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "warrior.revenant.orbsoflife", 2, 1.7f);
        Utils.playGlobalSound(location, "mage.frostbolt.activation", 2, 2);
        randomHitEffect(location, 7, 255, 255, 255);
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

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }

    public float getStrikeHeal() {
        return strikeHeal;
    }

    public void setStrikeHeal(float strikeHeal) {
        this.strikeHeal = strikeHeal;
    }
}
