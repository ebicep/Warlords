package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class JudgementStrike extends AbstractStrikeBase {
    private boolean pveUpgradeStrikeHeal = false;
    private boolean pveUpgradeMaster = false;

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
        ).ifPresent(warlordsDamageHealingFinalEvent -> {
            if (warlordsDamageHealingFinalEvent.isCrit()) {
                wp.getSpeed().addSpeedModifier("Judgement Speed", speedOnCrit, speedOnCritDuration * 20, "BASE");
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

    public boolean isPveUpgradeStrikeHeal() {
        return pveUpgradeStrikeHeal;
    }

    public void setPveUpgradeStrikeHeal(boolean pveUpgradeStrikeHeal) {
        this.pveUpgradeStrikeHeal = pveUpgradeStrikeHeal;
    }

    public boolean isPveUpgradeMaster() {
        return pveUpgradeMaster;
    }

    public void setPveUpgradeMaster(boolean pveUpgradeMaster) {
        this.pveUpgradeMaster = pveUpgradeMaster;
    }
}
