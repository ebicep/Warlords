package com.ebicep.warlords.game.option.freeze;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.game.WarlordsGameUpdatedEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pvp.HorseOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
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
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Supports actually freezing the internalPlayers in the game
 */
public class GameFreezeOption implements Option, Listener {

    public static final int RESUME_TIME = 5;
    public static final String KEY_UPDATED_FROZEN = "frozen";

    public static boolean isGameFrozen(Game game) {
        return game.getOption(GameFreezeOption.class).stream().anyMatch(GameFreezeOption::isFrozen);
    }

    public static void resumeGame(Game game) {
        for (Option option : game.getOptions()) {
            if (option instanceof GameFreezeOption) {
                ((GameFreezeOption) option).resume();
            }
        }
    }

    private final List<Component> frozenCauses = new CopyOnWriteArrayList<>();
    private boolean unfreezeCooldown = false;

    private void resume() {
        //Do nothing while the game is being resumed
        if (unfreezeCooldown) {
            return;
        }
        unfreezeCooldown = true;
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
                    clearFrozenCauses();
                    setUnfreezeCooldown(false);
                    for (Option option : game.getOptions()) {
                        if (option instanceof PveOption pveOption) {
                            pveOption.getMobs().forEach(mob -> mob.toggleStun(true));
                        }
                    }
                    this.cancel();
                }
                timer--;
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 20);
    }

    private Game game;
    private Set<UUID> playersWithHorsePreFreeze = new HashSet<>();

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;

        game.registerEvents(this);
    }

    @EventHandler
    public void onGameUpdated(WarlordsGameUpdatedEvent evt) {
        if (evt.getGame() != game) {
            return;
        }
        switch (evt.getKey()) {
            case KEY_UPDATED_FROZEN -> {
                if (isFrozen()) {
                    freeze();
                }
                if (!isFrozen()) {
                    unfreeze();
                }
            }
        }
    }

    @EventHandler
    public void onEvent(PlayerMoveEvent e) {
        WarlordsEntity wp = Warlords.getPlayer(e.getPlayer());
        if (wp != null && isFrozen()) {
            if (wp.isDead()) {
                e.getPlayer().teleport(e.getPlayer().getLocation());
            } else if (e.getPlayer().getVehicle() == null) {
                e.setTo(e.getFrom());
            } else {
                e.setCancelled(true);
            }
        }
    }

    private void freeze() {
        if (getFrozenCauses().isEmpty()) {
            throw new IllegalStateException("Game is not marked as frozen");
        }
        for (Option option : game.getOptions()) {
            if (option instanceof PveOption pveOption) {
                pveOption.getMobs().forEach(mob -> mob.toggleStun(false));
            }
        }
        Component message = getFrozenCauses().get(0);
        playersWithHorsePreFreeze.clear();
        game.forEachOnlinePlayerWithoutSpectators((p, team) -> freezePlayer(p, message));
    }

    private void unfreeze() {
        game.forEachOnlinePlayerWithoutSpectators((p, team) -> unfreezePlayer(p));
        playersWithHorsePreFreeze.clear();
    }

    private void freezePlayer(Player p, Component message) {
        if (p.getVehicle() instanceof Horse) {
            p.getVehicle().remove();
            playersWithHorsePreFreeze.add(p.getUniqueId());
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999999, 100000));
        p.showTitle(Title.title(
                Component.text("Game Paused", NamedTextColor.RED),
                message,
                Title.Times.times(Ticks.duration(0), Ticks.duration(9999999), Ticks.duration(0))
        ));
    }

    private void unfreezePlayer(Player p) {
        WarlordsEntity wp = Warlords.getPlayer(p);
        if (wp != null && playersWithHorsePreFreeze.contains(p.getUniqueId())) {
            HorseOption.activateHorseForPlayer(wp, false);
        }
        p.clearTitle();
        p.removePotionEffect(PotionEffectType.BLINDNESS);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {
        Player p = evt.getPlayer();
        if (game.getPlayerTeam(evt.getPlayer().getUniqueId()) != null) {
            if (isFrozen()) {
                freezePlayer(p, getFrozenCauses().get(0));
            } else {
                unfreezePlayer(p);
            }
        }
    }

    /**
     * Check if the game is frozen
     *
     * @return true if the game is frozen
     */
    public boolean isFrozen() {
        return !frozenCauses.isEmpty();
    }

    @Nonnull
    public List<Component> getFrozenCauses() {
        return Collections.unmodifiableList(frozenCauses);
    }

    public void addFrozenCause(Component cause) {
        frozenCauses.add(cause);
        Bukkit.getPluginManager().callEvent(new WarlordsGameUpdatedEvent(game, KEY_UPDATED_FROZEN));
    }

    public void removeFrozenCause(Component cause) {
        frozenCauses.remove(cause);
        Bukkit.getPluginManager().callEvent(new WarlordsGameUpdatedEvent(game, KEY_UPDATED_FROZEN));
    }

    public void clearFrozenCauses() {
        frozenCauses.clear();
        Bukkit.getPluginManager().callEvent(new WarlordsGameUpdatedEvent(game, KEY_UPDATED_FROZEN));
    }

    public boolean isUnfreezeCooldown() {
        return unfreezeCooldown;
    }

    public void setUnfreezeCooldown(boolean unfreezeCooldown) {
        this.unfreezeCooldown = unfreezeCooldown;
    }

}
