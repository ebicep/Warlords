package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractStrike;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.flags.Unexecutable;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.assassin.JudgementStrikeBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class JudgementStrike extends AbstractStrike {

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
        description = Component.text("Strike the targeted enemy, dealing ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text("damage. Every fourth attack is a "))
                               .append(Component.text("guaranteed", NamedTextColor.RED))
                               .append(Component.text(" critical strike. Critical strikes temporarily increase your movement speed by "))
                               .append(Component.text(speedOnCrit + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" for "))
                               .append(Component.text(speedOnCritDuration, NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Players Struck", "" + timesUsed));

        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new JudgementStrikeBranch(abilityTree, this);
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "warrior.revenant.orbsoflife", 2, 1.7f);
        Utils.playGlobalSound(location, "mage.frostbolt.activation", 2, 2);
        randomHitEffect(location, 7, 255, 255, 255);
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull WarlordsEntity nearPlayer) {
        for (int i = 0; i < (pveMasterUpgrade2 ? 2 : 1); i++) {
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
                    critMultiplier
            ).ifPresent(finalEvent -> {
                if (finalEvent.isCrit()) {
                    wp.addSpeedModifier(wp, "Judgement Speed", speedOnCrit, speedOnCritDuration * 20, "BASE");
                }
                if (pveMasterUpgrade) {
                    if (nearPlayer instanceof WarlordsNPC warlordsNPC &&
                            finalEvent.getFinalHealth() <= (nearPlayer.getMaxHealth() * .3) &&
                            !(warlordsNPC.getMob() instanceof Unexecutable)
                    ) {
                        nearPlayer.addDamageInstance(
                                wp,
                                "Execute",
                                nearPlayer.getCurrentHealth() + 1,
                                nearPlayer.getCurrentHealth() + 1,
                                0,
                                100,
                                EnumSet.of(InstanceFlags.IGNORE_SELF_RES)
                        ).ifPresent(finalEvent2 -> {
                            if (strikeHeal != 0 && finalEvent2.isDead()) {
                                wp.addHealingInstance(wp, name, strikeHeal, strikeHeal, 0, 100);
                            }
                        });
                    }
                }
                if (strikeHeal != 0 && finalEvent.isDead()) {
                    wp.addHealingInstance(wp, name, strikeHeal, strikeHeal, 0, 100);
                }
            });
        }

        return true;
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


    public float getStrikeHeal() {
        return strikeHeal;
    }

    public void setStrikeHeal(float strikeHeal) {
        this.strikeHeal = strikeHeal;
    }
}
