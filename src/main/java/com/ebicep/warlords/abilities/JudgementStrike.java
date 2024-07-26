package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.assassin.JudgementStrikeBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class JudgementStrike extends AbstractStrike implements Damages<JudgementStrike.DamageValues>, Heals<JudgementStrike.HealingValues> {

    private final DamageValues damageValues = new DamageValues();
    private final HealingValues healingValues = new HealingValues();
    private int attacksDone = 0;
    private int speedOnCrit = 25; // %
    private int speedOnCritDuration = 2;
    private int strikeCritInterval = 4;

    public JudgementStrike() {
        super("Judgement Strike", 0, 70);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Strike the targeted enemy, dealing ")
                .damage(damageValues.strikeDamage)
                .text("damage. Every fourth attack is a ")
                .text("guaranteed", NamedTextColor.RED)
                .text(" critical strike. Critical strikes temporarily increase your movement speed by ")
                .percent(speedOnCrit, NamedTextColor.YELLOW)
                .text(" for ")
                .durationSeconds(speedOnCritDuration)
                .text(".")
                .build();
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
            float critChance = damageValues.strikeDamage.getCritChanceValue();
            if (attacksDone == strikeCritInterval) {
                attacksDone = 0;
                critChance = 100;
            }
            float extraDamage = pveMasterUpgrade ? DamageCheck.clamp(nearPlayer.getMaxHealth() * 0.01f) : 0;
            nearPlayer.addInstance(InstanceBuilder
                    .damage()
                    .ability(this)
                    .source(wp)
                    .min(damageValues.strikeDamage.getMinValue() + extraDamage)
                    .max(damageValues.strikeDamage.getMaxValue() + extraDamage)
                    .critChance(critChance)
                    .critMultiplier(damageValues.strikeDamage.getCritMultiplierValue())
            ).ifPresent(finalEvent -> {
                if (finalEvent.isCrit()) {
                    wp.addSpeedModifier(wp, "Judgement Speed", speedOnCrit, speedOnCritDuration * 20, "BASE");
                }
                if (healingValues.strikeHealing.getValue() != 0 && finalEvent.isDead()) {
                    wp.addInstance(InstanceBuilder
                            .healing()
                            .ability(this)
                            .source(wp)
                            .value(healingValues.strikeHealing)
                    );
                }
            });
        }

        return true;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable strikeDamage = new Value.RangedValueCritable(326, 441, 20, 185);
        private final List<Value> values = List.of(strikeDamage);

        public Value.RangedValueCritable getStrikeDamage() {
            return strikeDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.SetValue strikeHealing = new Value.SetValue(0);
        private final List<Value> values = List.of(strikeHealing);

        public Value.SetValue getStrikeHealing() {
            return strikeHealing;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}
