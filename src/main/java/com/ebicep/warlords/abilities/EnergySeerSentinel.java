package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbilityDescriptionBuilder;
import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.abilities.internal.AbstractEnergySeer;
import com.ebicep.warlords.abilities.internal.Heals;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.sentinel.EnergySeerBranchSentinel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class EnergySeerSentinel extends AbstractEnergySeer<AbstractEnergySeer.EnergySeerData> implements Heals<EnergySeerSentinel.HealingValues> {

    private int damageResistance = 5;

    public EnergySeerSentinel() {
        super(AbstractAbilityBuilder.create("energySeerSentinel").pvp());
    }

    @Override
    protected void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.damageResistance = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageResistance"), int.class);
    }

    @Override
    public EnergySeerData getDataObject() {
        return new EnergySeerData();
    }

    @Override
    public Class<EnergySeerData> getDataClass() {
        return EnergySeerData.class;
    }

    @Override
    public TextComponent getBonus() {
        return Component.text("Your Fortifying Hexes gain an additional ")
                        .append(Component.text(damageResistance + "%", AbilityDescriptionBuilder.COLOR_BROWN))
                        .append(Component.text(" damage resistance"));
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new EnergySeerBranchSentinel(abilityTree, this);
    }

    public int getDamageResistance() {
        return damageResistance;
    }

    public void setDamageResistance(int damageResistance) {
        this.damageResistance = damageResistance;
    }

}
