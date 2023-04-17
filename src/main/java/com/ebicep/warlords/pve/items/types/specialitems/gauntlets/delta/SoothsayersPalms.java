package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega.LilithsClaws;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class SoothsayersPalms extends SpecialDeltaGauntlet implements AppliesToWarlordsPlayer {

    public SoothsayersPalms(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    public SoothsayersPalms() {

    }

    @Override
    public String getName() {
        return "Soothsayer's Palms";
    }

    @Override
    public String getBonus() {
        return "+1% chance to perform 1 random rune instead of the player's weapon right-click.";
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
                if (!Objects.equals(event.getWarlordsEntity(), warlordsPlayer)) {
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
                //temp scuffed account for energy cost
                float energyCost = ability.getEnergyCost();
                ability.setEnergyCost(0);
                ability.onActivate(warlordsPlayer, player);
                ability.setEnergyCost(energyCost);
                ability.addTimesUsed();
                AbstractPlayerClass.sendRightClickPacket(player);
            }
        });

    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new LilithsClaws(statPool);
    }
}
