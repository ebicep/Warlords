package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.WarlordsGameUpdatedEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Supports actually freezing the internalPlayers in the game
 */
public class GameFreezeOption implements Option, Listener {
    
    private static Listener GLOBAL_LISTENER = new Listener() {
        @EventHandler
        public void onEvent(PlayerMoveEvent e) {
            WarlordsPlayer wp = Warlords.getPlayer(e.getPlayer());
            if (wp != null && wp.getGame().isFrozen() && Utils.collectionHasItem(wp.getGame().getOptions(), o -> o instanceof GameFreezeOption)) {
                if (e.getPlayer().getVehicle() == null) {
                    e.setTo(e.getFrom());
                } else {
                    e.setCancelled(true);
                }
            }
        }
    };

    private Game game;
    private boolean isFrozen = false;

    @Override
    public void register(Game game) {
        this.game = game;

        game.registerEvents(this);
        if (GLOBAL_LISTENER != null) {
            Bukkit.getPluginManager().registerEvents(GLOBAL_LISTENER, Warlords.getInstance());
            GLOBAL_LISTENER = null;
        }
    }

    private void freeze() {
        if (game.getFrozenCauses().isEmpty()) {
            throw new IllegalStateException("Game is not marked as frozen");
        }
        isFrozen = true;
        String message = game.getFrozenCauses().get(0);
        game.forEachOnlinePlayerWithoutSpectators((p, team) -> {
            if (p.getVehicle() instanceof Horse) {
                ((EntityLiving) ((CraftEntity) p.getVehicle()).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0);
            }
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999999, 100000));
            PacketUtils.sendTitle(p, ChatColor.RED + "Game Paused", message, 0, 9999999, 0);
        });
    }

    private void unfreeze() {
        isFrozen = false;
        game.forEachOnlinePlayerWithoutSpectators((p, team) -> {
            if (p.getVehicle() instanceof Horse) {
                ((EntityLiving) ((CraftEntity) p.getVehicle()).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(.318);
            }
            PacketUtils.sendTitle(p, "", "", 0, 0, 0);
            p.removePotionEffect(PotionEffectType.BLINDNESS);
        });
    }

    @EventHandler
    public void onGameUpdated(WarlordsGameUpdatedEvent evt) {
        if (evt.getGame() != game) {
            return;
        }
        switch (evt.getKey()) {
            case Game.KEY_UPDATED_FROZEN:
                if (game.isFrozen() && !isFrozen) {
                    freeze();
                }
                if (!game.isFrozen() && isFrozen) {
                    unfreeze();
                }
                break;
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {
        if (game.getPlayerTeam(evt.getPlayer().getUniqueId()) != null && isFrozen) {
            freeze();
        }
    }

}
