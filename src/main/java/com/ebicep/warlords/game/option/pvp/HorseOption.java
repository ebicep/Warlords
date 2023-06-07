package com.ebicep.warlords.game.option.pvp;

import co.aikar.commands.CommandIssuer;
import com.ebicep.customentities.nms.CustomHorse;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.UUID;

public class HorseOption implements Option, Listener {

    public static final ItemStack HORSE_ITEM = new ItemBuilder(Material.GOLD_BARDING)
            .name(ChatColor.GREEN + "Mount " + ChatColor.GRAY + "- §eRight-Click!")
            .lore(ChatColor.GRAY + "Cooldown: §b15 seconds",
                    "",
                    ChatColor.GRAY + "Call your steed to assists you in battle"
            )
            .get();

    public static void activateHorseForPlayer(WarlordsEntity warlordsEntity) {
        if (!(warlordsEntity instanceof WarlordsPlayer)) {
            return;
        }
        for (Option option : warlordsEntity.getGame().getOptions()) {
            if (option instanceof HorseOption) {
                HashMap<UUID, CustomHorse> horses = ((HorseOption) option).getPlayerHorses();
                CustomHorse customHorse = horses.computeIfAbsent(warlordsEntity.getUuid(),
                        k -> new CustomHorse(((CraftWorld) warlordsEntity.getWorld()).getHandle(), warlordsEntity)
                );
                customHorse.spawn();
                break;
            }
        }
    }

    public HashMap<UUID, CustomHorse> getPlayerHorses() {
        return playerHorses;
    }

    private final HashMap<UUID, CustomHorse> playerHorses = new HashMap<>();

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(this);
        new GameRunnable(game) {

            @Override
            public void run() {
                // Decrementing mount cooldown.
                game.warlordsPlayers().forEach(wp -> {
                    if (wp.getHorseCooldown() > 0 && !wp.getEntity().isInsideVehicle()) {
                        wp.setHorseCooldown(wp.getHorseCooldown() - .05f);
                        Player player = wp.getEntity() instanceof Player ? (Player) wp.getEntity() : null;
                        if (player != null) {
                            updateInventory(wp, player);
                        }
                    }
                });
            }
        }.runTaskTimer(0, 0);
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer) {
            playerHorses.put(player.getUuid(), new CustomHorse(((CraftWorld) player.getWorld()).getHandle(), player));
        }
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        if (warlordsPlayer.getHorseCooldown() > 0) {
            player.getInventory()
                  .setItem(7, new ItemStack(Material.IRON_BARDING, (int) warlordsPlayer.getHorseCooldown() + 1));
        } else {
            player.getInventory().setItem(7, HORSE_ITEM);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Action action = e.getAction();
        Location location = player.getLocation();
        WarlordsEntity wp = Warlords.getPlayer(player);

        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            ItemStack itemHeld = player.getItemInHand();
            int heldItemSlot = player.getInventory().getHeldItemSlot();
            if (wp == null || !wp.isAlive() || wp.getGame().isFrozen()) {
                return;
            }
            if (itemHeld.getType() != Material.GOLD_BARDING) {
                return;
            }
            if (heldItemSlot != 7 || player.getVehicle() != null || !(wp.getHorseCooldown() <= 0)) {
                return;
            }
            if (!Utils.isMountableZone(location) || Utils.blocksInFrontOfLocation(location)) {
                player.sendMessage(ChatColor.RED + "You can't mount here!");
                return;
            }
            double distance = Utils.getDistance(player, .25);
            if (distance >= 2) {
                player.sendMessage(ChatColor.RED + "You can't mount in the air!");
            } else if (wp.getCarriedFlag() != null) {
                player.sendMessage(ChatColor.RED + "You can't mount while holding the flag!");
            } else {
                player.playSound(player.getLocation(), "mountup", 1, 1);
                if (CustomHorse.record.contains(player.getUniqueId())) {
                    ChatChannels.sendDebugMessage((CommandIssuer) null, player.getUniqueId() + " - " + player.getName() + " - " + playerHorses.get(player.getUniqueId()), false);
                }
                CustomHorse customHorse = playerHorses.computeIfAbsent(player.getUniqueId(),
                        k -> new CustomHorse(((CraftWorld) player.getWorld()).getHandle(), wp)
                );
                if (CustomHorse.record.contains(player.getUniqueId())) {
                    ChatChannels.sendDebugMessage((CommandIssuer) null, player.getUniqueId() + " - " + player.getName() + " - " + customHorse, false);
                }
                customHorse.spawn();
                if (!wp.isDisableCooldowns()) {
                    wp.setHorseCooldown((float) (customHorse.getCooldown() * wp.getCooldownModifier()));
                }
            }
        }
    }
}
