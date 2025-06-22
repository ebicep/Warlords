package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbilityDescriptionBuilder;
import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.abilities.internal.AbstractEnergySeer;
import com.ebicep.warlords.abilities.internal.Heals;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.luminary.EnergySeerBranchLuminary;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class EnergySeerLuminary extends AbstractEnergySeer<AbstractEnergySeer.EnergySeerData> implements PurpleAbilityIcon, Heals<EnergySeerLuminary.HealingValues> {

    private int healingIncrease = 20;
    private int hexPierceIncrease;

    public EnergySeerLuminary() {
        super(AbstractAbilityBuilder.create("energySeerLuminary").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.healingIncrease = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("healingIncrease"), int.class);
        this.hexPierceIncrease = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hexPierceIncrease"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        wp.getAbilitiesMatching(MercifulHex.class).forEach(mercifulHex -> {
            mercifulHex.setMaxAlliesHit(mercifulHex.getMaxAlliesHit() + hexPierceIncrease);
        });
        return super.onActivateInternal(wp);
    }

    @Override
    public void updateDescription(Player player) {
        AbilityDescriptionBuilder builder = AbilityDescriptionBuilder.create("");
        if (inPve) {
            builder.append(getBonus()).text(", gain ");
        } else {
            builder.text("Gain ");
        }
        description = builder
                .energy(energyRestore)
                .text(" and add ")
                .text(hexPierceIncrease, NamedTextColor.BLUE)
                .text(" pierce to Merciful Hex. For ")
                .durationTicks(tickDuration)
                .text(". When Energy Seer ends, lose ")
                .energy(epsDecrease)
                .text(" per second and gain")
                .percent(speedBuff, NamedTextColor.YELLOW)
                .text(" speed for ")
                .durationTicks(postEffectTickDuration)
                .text(".")
                .build();
    }

    @Override
    public TextComponent getBonus() {
        return Component.text("Increase your healing by ").append(Component.text(healingIncrease + "%", NamedTextColor.GREEN));
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
    protected void onEnd(WarlordsEntity wp, EnergySeerData data) {
        super.onEnd(wp, data);
        if (pveMasterUpgrade2) {
            PlayerFilter.entitiesAround(wp, 10, 10, 10).aliveTeammatesOfExcludingSelf(wp).forEach(warlordsEntity -> {
                MercifulHex.giveMercifulHex(wp, warlordsEntity);
                EffectUtils.playParticleLinkAnimation(warlordsEntity.getLocation(), wp.getLocation(), Particle.HAPPY_VILLAGER, 1, 1.25, -1);
            });
        }
    }

    @Override
    protected void onEndForce(WarlordsEntity wp, EnergySeerData data) {
        wp.getAbilitiesMatching(MercifulHex.class).forEach(mercifulHex -> {
            mercifulHex.setMaxAlliesHit(mercifulHex.getMaxAlliesHit() - hexPierceIncrease);
        });
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new EnergySeerBranchLuminary(abilityTree, this);
    }

    public int getHealingIncrease() {
        return healingIncrease;
    }

    public void setHealingIncrease(int healingIncrease) {
        this.healingIncrease = healingIncrease;
    }

}
