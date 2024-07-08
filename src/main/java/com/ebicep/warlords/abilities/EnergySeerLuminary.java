package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractEnergySeer;
import com.ebicep.warlords.abilities.internal.Heals;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
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

public class EnergySeerLuminary extends AbstractEnergySeer<AbstractEnergySeer.EnergySeerData> implements PurpleAbilityIcon, Heals<EnergySeerLuminary.HealingValues> {

    private int healingIncrease = 20;

    @Override
    public TextComponent getBonus() {
        return Component.text("Increase your healing by ")
                        .append(Component.text(healingIncrease + "%", NamedTextColor.GREEN));
    }


    @Override
    protected void onEnd(WarlordsEntity wp, EnergySeerData data) {
        super.onEnd(wp, data);
        if (pveMasterUpgrade2) {
            PlayerFilter.entitiesAround(wp, 10, 10, 10)
                        .aliveTeammatesOfExcludingSelf(wp)
                        .forEach(warlordsEntity -> {
                            MercifulHex.giveMercifulHex(wp, warlordsEntity);
                            EffectUtils.playParticleLinkAnimation(warlordsEntity.getLocation(), wp.getLocation(), Particle.VILLAGER_HAPPY, 1, 1.25, -1);
                        });
        }
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
