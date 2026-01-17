package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.events.player.ingame.pve.WarlordsUpgradeTreeBuilderAddUpgradeEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
                @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
                public void onUpgradeAdd(WarlordsUpgradeTreeBuilderAddUpgradeEvent event) {
                    Bukkit.broadcast(Component.text("event called"));
                    if (!event.getWarlordsEntity().equals(warlordsPlayer)) {
                        return;
                    }
                    AtomicReference<Float> value = event.getValue();
                    value.getAndUpdate(aFloat -> aFloat * 1 + (upgradeEffectivenessIncreasePercent / 100f));
                }
            };
            warlordsPlayer.getGame().registerEvents(listener);
            // Implementation for:
            // 1. Modifying the scaling/multipliers of ability upgrades.
            // 2. Granting the player free upgrade currency or direct ability points.
        }

    }

}