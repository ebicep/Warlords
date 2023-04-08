package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsStrikeEvent;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class SamsonsFists extends SpecialDeltaGauntlet implements AppliesToWarlordsPlayer {
    @Override
    public Classes getClasses() {
        return Classes.PALADIN;
    }

    @Override
    public String getName() {
        return "Samson's Fists";
    }

    @Override
    public String getBonus() {
        return "Weapon right-clicks deals moderate knockback, at slightly increased energy cost.";
    }

    @Override
    public String getDescription() {
        return "Don't cut your hair!";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
        AbstractAbility weapon = warlordsPlayer.getSpec().getWeapon();
        weapon.setEnergyCost(weapon.getEnergyCost() + 15);
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onStrike(WarlordsStrikeEvent event) {
                if (!Objects.equals(event.getWarlordsEntity(), warlordsPlayer)) {
                    return;
                }
                WarlordsEntity strikedEntity = event.getStrikedEntity();
                //same kb as mithra immolation
                Utils.addKnockback(getName(), warlordsPlayer.getLocation(), strikedEntity, -.5, 0.1f);
            }
        });
    }
}
