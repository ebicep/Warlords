package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class SoothsayersPalms extends SpecialDeltaGauntlet {

    @Override
    public String getName() {
        return "Soothsayer's Palms";
    }

    @Override
    public String getBonus() {
        return "+1% chance to perform 1 random rune instead of the player's Weapon Right Click.";
    }

    @Override
    public String getDescription() {
        return "Not even Nostradamus could predict what's coming next!";
    }

    @Override
    public Classes getClasses() {
        return Classes.MAGE;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onAbilityActivate(WarlordsAbilityActivateEvent event) {
                if (!event.getWarlordsEntity().equals(warlordsPlayer)) {
                    return;
                }
                AbstractPlayerClass playerSpec = warlordsPlayer.getSpec();
                if (!Objects.equals(event.getAbility(), playerSpec.getWeapon())) {
                    return;
                }
                if (ThreadLocalRandom.current().nextDouble() > 0.01) {
                    return;
                }
                event.setCancelled(true);
                AbstractAbility[] abilities = playerSpec.getAbilitiesExcludingWeapon();
                //picking random ability
                AbstractAbility ability = abilities[ThreadLocalRandom.current().nextInt(abilities.length)];
                Player player = event.getPlayer();
                ability.onActivate(warlordsPlayer, player);
                ability.addTimesUsed();
                AbstractPlayerClass.sendRightClickPacket(player);
            }
        });

    }
}
