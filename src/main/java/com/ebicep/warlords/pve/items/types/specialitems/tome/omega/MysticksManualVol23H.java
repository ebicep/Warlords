package com.ebicep.warlords.pve.items.types.specialitems.tome.omega;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsEnergyUseEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class MysticksManualVol23H extends SpecialOmegaTome implements AppliesToWarlordsPlayer {

    public MysticksManualVol23H() {

    }

    public MysticksManualVol23H(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "Published since 846!";
    }

    @Override
    public String getBonus() {
        return "+10% chance for an ability to not expend energy.";
    }

    @Override
    public String getName() {
        return "Mystick's Manual: Vol. 23-H";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        List<String> abilityNames = warlordsPlayer
                .getAbilities()
                .stream()
                .map(AbstractAbility::getName)
                .toList();
        warlordsPlayer.getGame().registerEvents(new Listener() {
            @EventHandler
            public void beforeEnergyUse(WarlordsEnergyUseEvent.Pre event) {
                if (!event.getWarlordsEntity().equals(warlordsPlayer)) {
                    return;
                }
                String from = event.getFrom();
                if (!abilityNames.contains(from)) {
                    return;
                }
                if (ThreadLocalRandom.current().nextFloat() > 0.1f) {
                    return;
                }
                if (warlordsPlayer.getEntity() instanceof Player player) {
                    AbstractItem.sendItemMessage(
                            player,
                            getHoverComponent().append(Component.text(" prevented " + from + " from expending energy!", NamedTextColor.GRAY))
                    );
                }
                event.setCancelled(true);
            }
        });
    }

}
