package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.apothecary.ImpalingStrikeBranch;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ImpalingStrike extends AbstractStrike<ImpalingStrike, ImpalingStrike.ImpalingStrikeStats> implements Damages<ImpalingStrike.DamageValues> {

    private final ImpalingStrikeStats stats = new ImpalingStrikeStats();
    private final DamageValues damageValues = new DamageValues();
    private int leechTickDuration = 120;
    private float leechAmount = 8;

    public ImpalingStrike() {
        super(AbstractAbilityBuilder.create("impalingStrike").pvp());
    }
    public ImpalingStrike(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.leechTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("leechTickDuration"), int.class);
        this.leechAmount = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("leechAmount"), float.class);
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "rogue.apothecarystrike.activation", 2, 0.5f);
        Utils.playGlobalSound(location, "mage.fireball.activation", 2, 1.8f);
        randomHitEffect(location, 7, 100, 255, 100);
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull WarlordsEntity nearPlayer) {
        int multiplier = pveMasterUpgrade && nearPlayer.getCooldownManager().hasCooldownFromName("Leech Debuff") ? 3 : 1;
        Leech.giveLeechCooldown(Leech.LeechInstance.create(wp, nearPlayer).withImpalingStrike(this));
        nearPlayer.addInstance(InstanceBuilder
                .damage()
                .ability(this)
                .source(wp)
                .min(damageValues.strikeDamage.getMinValue() * multiplier)
                .max(damageValues.strikeDamage.getMaxValue() * multiplier)
                .crit(damageValues.strikeDamage)
        );
        if (pveMasterUpgrade || pveMasterUpgrade2) {
            additionalHit(
                    pveMasterUpgrade ? 2 : 5,
                    wp,
                    nearPlayer,
                    warlordsEntity -> {
                        Leech.giveLeechCooldown(Leech.LeechInstance.create(wp, warlordsEntity).withImpalingStrike(this));
                        warlordsEntity.addInstance(InstanceBuilder
                                .damage()
                                .ability(this)
                                .source(wp)
                                .value(damageValues.strikeDamage)
                        );
                    }
            );
        }
        return true;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public ImpalingStrikeStats getAbilityStats() {
        return stats;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Impale an enemy, dealing")
                                               .damage(damageValues.strikeDamage)
                                               .text("damage and inflicting them with ")
                                               .text("LEECH", NamedTextColor.DARK_GREEN)
                                               .text(" for ")
                                               .durationTicks(leechTickDuration)
                                               .text(". Whenever you or an ally deals damage to an enemy with")
                                               .text("LEECH", NamedTextColor.DARK_GREEN)
                                               .text(", they heal for ")
                                               .percent(leechAmount, NamedTextColor.GREEN)
                                               .text(" of true damage dealt.")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new ImpalingStrikeBranch(abilityTree, this);
    }

    public int getLeechTickDuration() {
        return leechTickDuration;
    }

    public void setLeechTickDuration(int leechTickDuration) {
        this.leechTickDuration = leechTickDuration;
    }

    public float getLeechAmount() {
        return leechAmount;
    }

    public void setLeechAmount(float leechAmount) {
        this.leechAmount = leechAmount;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable strikeDamage = new Value.RangedValueCritable(323, 427, 20, 175);

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

    public static class ImpalingStrikeStats extends AbstractStrikeStats<ImpalingStrike, ImpalingStrikeStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public ImpalingStrikeStats merge(ImpalingStrikeStats other, int multiplier) {
            ImpalingStrikeStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<ImpalingStrikeStats> getClazz() {
            return ImpalingStrikeStats.class;
        }

        @Override
        public ImpalingStrikeStats create() {
            return new ImpalingStrikeStats();
        }

    }

}
