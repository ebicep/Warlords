package com.ebicep.warlords.pve.items.types.specialitems.tome.omega;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class TomeOfTheft extends SpecialOmegaTome implements AppliesToWarlordsPlayer {
    public TomeOfTheft() {

    }

    public TomeOfTheft(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (!event.getWarlordsEntity().equals(warlordsPlayer)) {
                    return;
                }
                if (event.isHealingInstance()) {
                    return;
                }
                if (ThreadLocalRandom.current().nextDouble() < .1) {
                    // doenst make logical sense for item to dodge but whatever
                    warlordsPlayer.sendMessage(Component.text("Your " + getName() + " dodged ", NamedTextColor.GREEN)
                                                        .append(event.getSource().getColoredName())
                                                        .append(Component.text("'s attack.")));
                    event.setCancelled(true);
                }
            }
        });
    }

    @Override
    public String getName() {
        return "Tome of Theft";
    }

    @Override
    public String getBonus() {
        return "10% of all attacks are dodged.";
    }

    @Override
    public String getDescription() {
        return "Finally! A purchase worth my while.";
    }

}
