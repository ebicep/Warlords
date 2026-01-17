package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.events.player.ingame.pve.WarlordsUpgradeTreeBuilderAddUpgradeEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class SynapticOverload extends BaseSet {

    private int upgradeEffectivenessIncreasePercent;
    private int freeAbilityUpgrades;

    @Override
    public void init() {
        super.init();
        this.upgradeEffectivenessIncreasePercent = getValue("upgradeEffectivenessIncreasePercent", int.class);
        this.freeAbilityUpgrades = getValue("freeAbilityUpgrades", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "synapticOverload";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(upgradeEffectivenessIncreasePercent, freeAbilityUpgrades);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            Listener listener = new Listener() {
                @EventHandler
                public void onUpgradeAdd(WarlordsUpgradeTreeBuilderAddUpgradeEvent event) {
                    if (!event.getWarlordsEntity().equals(warlordsPlayer)) {
                        return;
                    }
                    event.getValue().addModifier(
                            FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER,
                            getName() + " " + Integer.toHexString(hashCode()),
                            upgradeEffectivenessIncreasePercent / 100f
                    );
                }
            };
            warlordsPlayer.getGame().registerEvents(listener);
            warlordsPlayer.getAbilityTree().setFreeUpgrades(warlordsPlayer.getAbilityTree().getFreeUpgrades() + freeAbilityUpgrades);
        }

    }

}