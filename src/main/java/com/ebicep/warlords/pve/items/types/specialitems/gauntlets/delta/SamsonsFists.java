package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsStrikeEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega.HandsOfTheHolyCorpse;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.Set;

public class SamsonsFists extends SpecialDeltaGauntlet implements AppliesToWarlordsPlayer {
    public SamsonsFists() {

    }

    public SamsonsFists(Set<BasicStatPool> statPool) {
        super(statPool);
    }

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
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        AbstractAbility weapon = warlordsPlayer.getSpec().getWeapon();
        weapon.getEnergyCost().addAdditiveModifier("Samson's Fists", 5);
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onStrike(WarlordsStrikeEvent event) {
                if (!Objects.equals(event.getWarlordsEntity(), warlordsPlayer)) {
                    return;
                }
                WarlordsEntity strikedEntity = event.getStrikedEntity();
                //same kb as mithra immolation
                Utils.addKnockback(getName(), warlordsPlayer.getLocation(), strikedEntity, -.7, 0.15f);
            }
        });
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new HandsOfTheHolyCorpse(statPool);
    }
}
