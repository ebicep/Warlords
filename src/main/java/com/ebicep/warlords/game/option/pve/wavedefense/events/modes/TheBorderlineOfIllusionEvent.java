package com.ebicep.warlords.game.option.pve.wavedefense.events.modes;

import com.ebicep.warlords.events.EventFlags;
import com.ebicep.warlords.events.game.pve.WarlordsGameWaveClearEvent;
import com.ebicep.warlords.events.player.ingame.pve.drops.WarlordsDropWeaponEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.EventGameEndOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class TheBorderlineOfIllusionEvent implements Option, EventGameEndOption {

    private static final int BONUS = 10;
    private float weaponDropBonus = 0;

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(new Listener() {
            @EventHandler
            public void onWaveClear(WarlordsGameWaveClearEvent event) {
                int waveCleared = event.getWaveCleared();
                if (waveCleared != 0 && waveCleared % 15 == 0) {
                    increaseWeaponDropBonus(game);
                }
            }

            @EventHandler
            public void onWeaponDrop(WarlordsDropWeaponEvent event) {
                event.getEventFlags().remove(EventFlags.GUILD);
                event.addModifier(weaponDropBonus);
            }
        });
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer) {
            AbilityTree abilityTree = ((WarlordsPlayer) player).getAbilityTree();
            abilityTree.setMaxMasterUpgrades(5);
        }
    }

    private void increaseWeaponDropBonus(Game game) {
        weaponDropBonus += BONUS / 100f;
        game.forEachOnlinePlayer((player, team) -> player.sendMessage(Component.text("The chance of a weapon dropping has increased by " + BONUS + "%!", NamedTextColor.GREEN)));
    }

}
