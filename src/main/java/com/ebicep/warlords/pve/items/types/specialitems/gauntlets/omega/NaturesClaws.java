package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega;

import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class NaturesClaws extends SpecialOmegaGauntlet implements AppliesToWarlordsPlayer {
    public NaturesClaws() {

    }

    public NaturesClaws(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "Survival of the fittest, at it's finest.";
    }

    @Override
    public String getBonus() {
        return "+5% chance to gain a 200 HP 10s long shield instead of dealing damage or healing.";
    }

    @Override
    public String getName() {
        return "Nature's Claws";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getGame().registerEvents(new Listener() {
            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (!event.getAttacker().equals(warlordsPlayer)) {
                    return;
                }
                if (ThreadLocalRandom.current().nextDouble() > 0.05) {
                    return;
                }
                event.setCancelled(true);
                warlordsPlayer.sendMessage(Component.text("Nature's Claws has given you a shield instead!", NamedTextColor.GREEN));
                warlordsPlayer.getCooldownManager().addCooldown(new RegularCooldown<>(
                        getName(),
                        null,
                        Shield.class,
                        new Shield(getName(), 200),
                        warlordsPlayer,
                        CooldownTypes.ABILITY,
                        cooldownManager -> {
                        },
                        200
                ));
            }
        });
    }


}
