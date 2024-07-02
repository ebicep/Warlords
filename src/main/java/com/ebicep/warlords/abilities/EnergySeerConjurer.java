package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractEnergySeer;
import com.ebicep.warlords.abilities.internal.Heals;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsEnergyUseEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.conjurer.EnergySeerBranchConjurer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Particle;

public class EnergySeerConjurer extends AbstractEnergySeer<EnergySeerConjurer.EnergySeerConjurerData> implements Heals<EnergySeerConjurer.HealingValues> {

    private int damageIncrease = 10;

    @Override
    public TextComponent getBonus() {
        return Component.text("Increase your damage by ")
                        .append(Component.text(damageIncrease + "%", NamedTextColor.RED));
    }

    @Override
    protected void onEnd(WarlordsEntity wp, EnergySeerConjurer.EnergySeerConjurerData data) {
        super.onEnd(wp, data);
        wp.addEnergy(wp, "Replicating Sight", data.getAllyEnergyUsed());
        EffectUtils.displayParticle(
                Particle.REDSTONE,
                wp.getLocation().add(0, 1.2, 0),
                3,
                0.3,
                0.2,
                0.3,
                0,
                new Particle.DustOptions(Color.fromRGB(255, 255, 0), 2)
        );
    }

    @Override
    public Class<EnergySeerConjurerData> getDataClass() {
        return EnergySeerConjurerData.class;
    }

    @Override
    public EnergySeerConjurerData getDataObject() {
        return new EnergySeerConjurerData();
    }

    @Override
    protected void onEnergyUsed(WarlordsEntity wp, WarlordsEnergyUseEvent.Post event, EnergySeerConjurerData data) {
        if (!pveMasterUpgrade2) {
            return;
        }
        WarlordsEntity warlordsEntity = event.getWarlordsEntity();
        if (warlordsEntity.isEnemy(wp) || warlordsEntity.equals(wp)) {
            return;
        }
        float amount = event.getEnergyUsed() * .1f;
        data.setAllyEnergyUsed(data.getAllyEnergyUsed() + amount);

    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new EnergySeerBranchConjurer(abilityTree, this);
    }

    public int getDamageIncrease() {
        return damageIncrease;
    }

    public void setDamageIncrease(int damageIncrease) {
        this.damageIncrease = damageIncrease;
    }

    public static class EnergySeerConjurerData extends EnergySeerData {

        private float allyEnergyUsed = 0;

        public void setAllyEnergyUsed(float allyEnergyUsed) {
            this.allyEnergyUsed = allyEnergyUsed;
        }

        public float getAllyEnergyUsed() {
            return allyEnergyUsed;
        }

    }

}
