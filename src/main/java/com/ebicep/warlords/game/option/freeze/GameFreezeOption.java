package com.ebicep.warlords.game.option.freeze;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.game.WarlordsGameUpdatedEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.java.JavaUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

/**
 * Supports actually freezing the internalPlayers in the game
 */
public class GameFreezeOption implements Option, Listener {

    public static final int RESUME_TIME = 5;

    private static Listener GLOBAL_LISTENER = new Listener() {
        @EventHandler
        public void onEvent(PlayerMoveEvent e) {
            WarlordsEntity wp = Warlords.getPlayer(e.getPlayer());
            if (wp != null && wp.getGame().isFrozen() && JavaUtils.collectionHasItem(wp.getGame().getOptions(), o -> o instanceof GameFreezeOption)) {
                if (wp.isDead()) {
                    e.getPlayer().teleport(e.getPlayer().getLocation());
                } else if (e.getPlayer().getVehicle() == null) {
                    e.setTo(e.getFrom());
                } else {
                    e.setCancelled(true);
                }
            }
        }
    };

    public static void resumeGame(Game game) {
        for (Option option : game.getOptions()) {
            if (option instanceof GameFreezeOption) {
                ((GameFreezeOption) option).resume();
            }
        }
    }

    private void resume() {
        //Do nothing while the game is being resumed
        if (game.isUnfreezeCooldown()) {
            return;
        }
        game.setUnfreezeCooldown(true);
        new BukkitRunnable() {

            int timer = RESUME_TIME;

            @Override
            public void run() {
                game.forEachOnlinePlayerWithoutSpectators((p, team) ->
                        p.showTitle(Title.title(
                                Component.text("Resuming in... ", NamedTextColor.BLUE)
                                         .append(Component.text(timer, NamedTextColor.GREEN)),
                                Component.empty(),
                                Title.Times.times(Ticks.duration(0), Ticks.duration(40), Ticks.duration(0))
                        ))
                );
                if (timer == 0) {
                    game.clearFrozenCauses();
                    game.setUnfreezeCooldown(false);
                    for (Option option : game.getOptions()) {
                        if (option instanceof PveOption pveOption) {
                            pveOption.getMobs()
                                     .stream()
                                     .map(abstractMob -> abstractMob.getEntity().get())
                                     .forEach(mob -> mob.setNoAi(false));
                        }
                    }
                    this.cancel();
                }
                timer--;
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 20);
    }

    private Game game;

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;

        game.registerEvents(this);
        if (GLOBAL_LISTENER != null) {
            Bukkit.getPluginManager().registerEvents(GLOBAL_LISTENER, Warlords.getInstance());
            GLOBAL_LISTENER = null;
        }
    }

    @EventHandler
    public void onGameUpdated(WarlordsGameUpdatedEvent evt) {
        if (evt.getGame() != game) {
            return;
        }
        switch (evt.getKey()) {
            case Game.KEY_UPDATED_FROZEN -> {
                if (game.isFrozen()) {
                    freeze();
                }
                if (!game.isFrozen()) {
                    unfreeze();
                }
            }
        }
    }

    private void freeze() {
        if (game.getFrozenCauses().isEmpty()) {
            throw new IllegalStateException("Game is not marked as frozen");
        }
        for (Option option : game.getOptions()) {
            if (option instanceof PveOption pveOption) {
                pveOption.getMobs()
                         .stream()
                         .map(abstractMob -> abstractMob.getEntity().get())
                         .forEach(mob -> mob.setNoAi(true));
            }
        }
        Component message = game.getFrozenCauses().get(0);
        game.forEachOnlinePlayerWithoutSpectators((p, team) -> freezePlayer(p, message));
    }

    private void unfreeze() {
        game.forEachOnlinePlayerWithoutSpectators((p, team) -> unfreezePlayer(p));
    }

    private void freezePlayer(Player p, Component message) {
        if (p.getVehicle() instanceof Horse horse) {
            horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999999, 100000));
        p.showTitle(Title.title(
                Component.text("Game Paused", NamedTextColor.RED),
                message,
                Title.Times.times(Ticks.duration(0), Ticks.duration(9999999), Ticks.duration(0))
        ));
    }

    private void unfreezePlayer(Player p) {
        if (p.getVehicle() instanceof Horse horse) {
            horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.318);
        }
        PacketUtils.sendTitle(p, "", "", 0, 0, 0);
        p.removePotionEffect(PotionEffectType.BLINDNESS);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {
        Player p = evt.getPlayer();
        if (game.getPlayerTeam(evt.getPlayer().getUniqueId()) != null) {
            if (game.isFrozen()) {
                freezePlayer(p, game.getFrozenCauses().get(0));
            } else {
                unfreezePlayer(p);
            }
        }
    }

}
