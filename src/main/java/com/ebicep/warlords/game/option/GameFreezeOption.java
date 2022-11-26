package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.game.WarlordsGameUpdatedEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Supports actually freezing the internalPlayers in the game
 */
public class GameFreezeOption implements Option, Listener {

    public static final int RESUME_TIME = 5;

    private static Listener GLOBAL_LISTENER = new Listener() {
        @EventHandler
        public void onEvent(PlayerMoveEvent e) {
            WarlordsEntity wp = Warlords.getPlayer(e.getPlayer());
            if (wp != null && wp.getGame().isFrozen() && Utils.collectionHasItem(wp.getGame().getOptions(), o -> o instanceof GameFreezeOption)) {
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
                game.forEachOnlinePlayerWithoutSpectators((p, team) -> PacketUtils.sendTitle(p,
                        ChatColor.BLUE + "Resuming in... " + ChatColor.GREEN + timer,
                        "",
                        0,
                        40,
                        0
                ));
                if (timer == 0) {
                    game.clearFrozenCauses();
                    game.setUnfreezeCooldown(false);
                    for (Option option : game.getOptions()) {
                        if (option instanceof WaveDefenseOption) {
                            ((WaveDefenseOption) option).getMobs().forEach(abstractMob -> {
                                EntityInsentient entityInsentient = abstractMob.getEntity().get();
                                NBTTagCompound tag = entityInsentient.getNBTTag();
                                if (tag == null) {
                                    tag = new NBTTagCompound();
                                }
                                entityInsentient.c(tag);
                                tag.setByte("NoAI", (byte) 0);
                                entityInsentient.f(tag);
                            });
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
    public void register(Game game) {
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
            case Game.KEY_UPDATED_FROZEN:
                if (game.isFrozen()) {
                    freeze();
                }
                if (!game.isFrozen()) {
                    unfreeze();
                }
                break;
        }
    }

    private void freeze() {
        if (game.getFrozenCauses().isEmpty()) {
            throw new IllegalStateException("Game is not marked as frozen");
        }
        for (Option option : game.getOptions()) {
            if (option instanceof WaveDefenseOption) {
                ((WaveDefenseOption) option).getMobs().forEach(abstractMob -> {
                    EntityInsentient entityInsentient = abstractMob.getEntity().get();
                    NBTTagCompound tag = entityInsentient.getNBTTag();
                    if (tag == null) {
                        tag = new NBTTagCompound();
                    }
                    entityInsentient.c(tag);
                    tag.setByte("NoAI", (byte) 1);
                    entityInsentient.f(tag);
                });
            }
        }
        String message = game.getFrozenCauses().get(0);
        game.forEachOnlinePlayerWithoutSpectators((p, team) -> freezePlayer(p, message));
    }

    private void unfreeze() {
        game.forEachOnlinePlayerWithoutSpectators((p, team) -> unfreezePlayer(p));
    }

    private void freezePlayer(Player p, String message) {
        if (p.getVehicle() instanceof Horse) {
            ((EntityLiving) ((CraftEntity) p.getVehicle()).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0);
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999999, 100000));
        PacketUtils.sendTitle(p, ChatColor.RED + "Game Paused", message, 0, 9999999, 0);
    }

    private void unfreezePlayer(Player p) {
        if (p.getVehicle() instanceof Horse) {
            ((EntityLiving) ((CraftEntity) p.getVehicle()).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(.318);
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
