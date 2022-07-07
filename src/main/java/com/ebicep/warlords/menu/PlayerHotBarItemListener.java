package com.ebicep.warlords.menu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.party.RegularGamesMenu;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Consumer;

import static com.ebicep.warlords.menu.debugmenu.DebugMenuGameOptions.StartMenu.openGamemodeMenu;
import static com.ebicep.warlords.menu.generalmenu.WarlordsShopMenu.openMainMenu;

public class PlayerHotBarItemListener implements Listener {

    private static final HashMap<UUID, Set<ItemListener>> playerHotBarItemListeners = new HashMap<>();

    public static void addItem(Player player, int slot, ItemListener listener) {
        playerHotBarItemListeners.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(listener);
        player.getInventory().setItem(slot, listener.getItemStack());
    }

    public static void giveLobbyHotBar(Player player, boolean fromGame) {
        UUID uuid = player.getUniqueId();

        PlayerSettings playerSettings = Warlords.getPlayerSettings(uuid);
        Specializations selectedSpec = playerSettings.getSelectedSpec();
        AbstractPlayerClass apc = selectedSpec.create.get();

        addItem(player, 1, new ItemListener(
                new ItemBuilder(apc.getWeapon().getItem(playerSettings.getWeaponSkins().getOrDefault(selectedSpec, Weapons.FELFLAME_BLADE).getItem()))
                        .name("§aWeapon Skin Preview")
                        .get(),
                e -> {

                }));

        if (player.hasPermission("warlords.game.debug")) {
            addItem(player, 3, new ItemListener(
                    new ItemBuilder(Material.EMERALD).name("§aDebug Menu").get(),
                    e -> Bukkit.getServer().dispatchCommand(e.getPlayer(), "wl")

            ));
        } else {
            addItem(player, 3, new ItemListener(
                    new ItemBuilder(Material.BLAZE_POWDER).name("§aStart Menu").get(),
                    e -> openGamemodeMenu(e.getPlayer())
            ));
        }

        addItem(player, 4, new ItemListener(
                new ItemBuilder(Material.NETHER_STAR).name("§aSelection Menu").get(),
                e -> openMainMenu(e.getPlayer())
        ));

        addItem(player, 5, new ItemListener(
                new ItemBuilder(Material.EYE_OF_ENDER).name("§aSpectate").get(),
                e -> Bukkit.getServer().dispatchCommand(e.getPlayer(), "spectate")
        ));

        addItem(player, 6, new ItemListener(
                new ItemBuilder(Material.DIAMOND_SWORD).name("§aWeapons").get(),
                e -> WeaponManagerMenu.openWeaponInventoryFromExternal(e.getPlayer())
        ));

        new BukkitRunnable() {

            @Override
            public void run() {
                addItem(player, 7, new ItemListener(
                        new ItemBuilder(Warlords.getHead(uuid)).name("§aLevel Rewards").get(),
                        e -> ExperienceManager.openLevelingRewardsMenu(e.getPlayer())
                ));

            }
        }.runTaskLater(Warlords.getInstance(), Warlords.getPlayerHeads().containsKey(uuid) ? 0 : 80);

        if (!fromGame) {
            Warlords.partyManager.getPartyFromAny(uuid).ifPresent(party -> {
                List<RegularGamesMenu.RegularGamePlayer> playerList = party.getRegularGamesMenu().getRegularGamePlayers();
                if (!playerList.isEmpty()) {
                    playerList.stream()
                            .filter(regularGamePlayer -> regularGamePlayer.getUuid().equals(uuid))
                            .findFirst()
                            .ifPresent(regularGamePlayer ->
                                    addItem(player, 8, new ItemListener(
                                            new ItemBuilder(regularGamePlayer.getTeam().item).name("§aTeam Builder").get(),
                                            e -> {
                                                Warlords.partyManager.getPartyFromAny(e.getPlayer().getUniqueId()).ifPresent(p -> {
                                                    List<RegularGamesMenu.RegularGamePlayer> playerList2 = p.getRegularGamesMenu().getRegularGamePlayers();
                                                    if (!playerList2.isEmpty()) {
                                                        p.getRegularGamesMenu().openMenuForPlayer(e.getPlayer());
                                                        new BukkitRunnable() {
                                                            @Override
                                                            public void run() {
                                                                if (e.getPlayer().getOpenInventory().getTopInventory().getName().equals("Team Builder")) {
                                                                    p.getRegularGamesMenu().openMenuForPlayer(e.getPlayer());
                                                                } else {
                                                                    this.cancel();
                                                                }
                                                            }
                                                        }.runTaskTimer(Warlords.getInstance(), 20, 10);
                                                    }
                                                });
                                            }
                                    ))
                            );
                }
            });
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack itemStack = e.getItem();
        if (itemStack == null) {
            return;
        }
        Set<ItemListener> itemListeners = playerHotBarItemListeners.get(e.getPlayer().getUniqueId());
        if (itemListeners == null) {
            return;
        }

        for (ItemListener itemListener : itemListeners) {
            if (itemListener.getItemStack().equals(itemStack)) {
                itemListener.getOnClick().accept(e);
            }
        }
    }

    public static class ItemListener {

        private final ItemStack itemStack;
        private final Consumer<PlayerInteractEvent> onClick;

        public ItemListener(ItemStack itemStack, Consumer<PlayerInteractEvent> onClick) {
            this.itemStack = itemStack;
            this.onClick = onClick;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public Consumer<PlayerInteractEvent> getOnClick() {
            return onClick;
        }

        //equals and hashcode method that only compares itemstack
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemListener that = (ItemListener) o;
            return itemStack.equals(that.itemStack);
        }

        @Override
        public int hashCode() {
            return Objects.hash(itemStack);
        }

    }
}

