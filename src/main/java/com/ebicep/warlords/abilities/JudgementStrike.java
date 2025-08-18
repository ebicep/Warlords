package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.assassin.JudgementStrikeBranch;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class JudgementStrike extends AbstractStrike<JudgementStrike, JudgementStrike.JudgementStrikeStats> implements Damages<JudgementStrike.DamageValues>, Heals<JudgementStrike.HealingValues> {

    protected int attacksDone = 0;
    private final JudgementStrikeStats stats = new JudgementStrikeStats();
    private final DamageValues damageValues = new DamageValues();
    private final HealingValues healingValues = new HealingValues();
    private int speedOnCrit = 25;
    private int speedOnCritDuration = 2;
    private int strikeCritInterval = 4;
    private int damageIncrease;
    private int damageIncreaseHealthThreshold;
    private float orderCooldownReduction;

    public JudgementStrike() {
        super(AbstractAbilityBuilder.create("judgementStrike").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.speedOnCrit = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("speedOnCrit"), int.class);
        this.speedOnCritDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("speedOnCritDuration"), int.class);
        this.strikeCritInterval = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("strikeCritInterval"), int.class);
        this.damageIncrease = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageIncrease"), int.class);
        this.damageIncreaseHealthThreshold = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageIncreaseHealthThreshold"), int.class);
        this.orderCooldownReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("orderCooldownReduction"), float.class);
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
            float damageMultiplier = convertToMultiplicationDecimal(
                    (nearPlayer.getCurrentHealth() / nearPlayer.getMaxBaseHealth()) < damageIncreaseHealthThreshold / 100f
                    ? damageIncrease
                    : 0
            );
            nearPlayer.addInstance(InstanceBuilder.damage()
                                                  .ability(this)
                                                  .source(wp)
                                                  .min((damageValues.strikeDamage.getMinValue() + extraDamage) * damageMultiplier)
                                                  .max((damageValues.strikeDamage.getMaxValue() + extraDamage) * damageMultiplier)
                                                  .critChance(critChance)
                                                  .critMultiplier(damageValues.strikeDamage.getCritMultiplierValue())
            ).ifPresent(finalEvent -> {
                if (finalEvent.isCrit()) {
                    wp.addSpeedModifier(wp, "Judgement Speed", speedOnCrit, speedOnCritDuration * 20);
                }
                if (healingValues.strikeHealing.getValue() != 0 && finalEvent.isDead()) {
                    wp.addInstance(InstanceBuilder.healing().ability(this).source(wp).value(healingValues.strikeHealing));
                }
                for (AbstractAbility ability : wp.getAbilitiesImplementing(OrderOfEviscerateLike.class)) {
                    ability.subtractCurrentCooldown(orderCooldownReduction);
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

    @Override
    public JudgementStrikeStats getAbilityStats() {
        return stats;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Strike the targeted enemy, dealing ")
                .damage(damageValues.strikeDamage)
                .text("damage. Deals ")
                .percent(damageIncrease, NamedTextColor.RED)
                .text(" more damage to enemies below ")
                .percent(damageIncreaseHealthThreshold, NamedTextColor.RED)
                .text(" health.")
                .emptyLine()
                .text("Every strike reduces the cooldown of Order of Eviscerate by ")
                .durationSeconds(orderCooldownReduction)
                .text(". Every ")
                .text(strikeCritInterval, NamedTextColor.BLUE)
                .text("th strike is a guaranteed critical strike. Critical strikes temporarily increase your movement speed by ")
                .percent(speedOnCrit, NamedTextColor.WHITE)
                .text(" for ")
                .durationSeconds(speedOnCritDuration)
                .text(".")
                .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new JudgementStrikeBranch(abilityTree, this);
    }

    public int getStrikeCritInterval() {
        return strikeCritInterval;
    }

    public void setStrikeCritInterval(int strikeCritInterval) {
        this.strikeCritInterval = strikeCritInterval;
    }

    public int getSpeedOnCrit() {
        return speedOnCrit;
    }

    public void setSpeedOnCrit(int speedOnCrit) {
        this.speedOnCrit = speedOnCrit;
    }

    public int getSpeedOnCritDuration() {
        return speedOnCritDuration;
    }

    public void setSpeedOnCritDuration(int speedOnCritDuration) {
        this.speedOnCritDuration = speedOnCritDuration;
    }

    public int getDamageIncreaseHealthThreshold() {
        return damageIncreaseHealthThreshold;
    }

    public void setDamageIncreaseHealthThreshold(int damageIncreaseHealthThreshold) {
        this.damageIncreaseHealthThreshold = damageIncreaseHealthThreshold;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable strikeDamage = new Value.RangedValueCritable(326, 441, 20, 185);

        private List<Value> values = List.of(strikeDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.strikeDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("strikeDamage"), Value.RangedValueCritable.class);
            this.values = List.of(strikeDamage);
        }

        public Value.RangedValueCritable getStrikeDamage() {
            return strikeDamage;
        }

    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.SetValue strikeHealing = new Value.SetValue(0);

        private List<Value> values = List.of(strikeHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.strikeHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameHealing("strikeHealing"), Value.SetValue.class);
            this.values = List.of(strikeHealing);
        }

        public Value.SetValue getStrikeHealing() {
            return strikeHealing;
        }

    }

    public static class JudgementStrikeStats extends AbstractStrikeStats<JudgementStrike, JudgementStrikeStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public JudgementStrikeStats merge(JudgementStrikeStats other, int multiplier) {
            JudgementStrikeStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<JudgementStrikeStats> getClazz() {
            return JudgementStrikeStats.class;
        }

        @Override
        public JudgementStrikeStats create() {
            return new JudgementStrikeStats();
        }

    }

}
