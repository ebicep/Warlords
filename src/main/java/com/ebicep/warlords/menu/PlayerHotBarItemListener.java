package com.ebicep.warlords.menu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.party.RegularGamesMenu;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.rewards.RewardInventory;
import com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Consumer;

import static com.ebicep.warlords.menu.debugmenu.DebugMenuGameOptions.StartMenu.openGamemodeMenu;
import static com.ebicep.warlords.menu.generalmenu.WarlordsShopMenu.openMainMenu;

public class PlayerHotBarItemListener implements Listener {

    private static final HashMap<UUID, Set<ItemListener>> playerHotBarItemListeners = new HashMap<>();

    private static final Pair<ItemStack, Consumer<PlayerInteractEvent>> DEBUG_MENU = new Pair<>(
            new ItemBuilder(Material.EMERALD).name("§aDebug Menu").get(),
            e -> Bukkit.getServer().dispatchCommand(e.getPlayer(), "wl"));
    private static final Pair<ItemStack, Consumer<PlayerInteractEvent>> START_MENU = new Pair<>(
            new ItemBuilder(Material.BLAZE_POWDER).name("§aStart Menu").get(),
            e -> openGamemodeMenu(e.getPlayer()));
    private static final Pair<ItemStack, Consumer<PlayerInteractEvent>> SELECTION_MENU = new Pair<>(
            new ItemBuilder(Material.NETHER_STAR).name("§aSelection Menu").get(),
            e -> openMainMenu(e.getPlayer()));
    private static final Pair<ItemStack, Consumer<PlayerInteractEvent>> SPECTATE_MENU = new Pair<>(
            new ItemBuilder(Material.EYE_OF_ENDER).name("§aSpectate").get(),
            e -> Bukkit.getServer().dispatchCommand(e.getPlayer(), "spectate"));
    private static final Pair<ItemStack, Consumer<PlayerInteractEvent>> WEAPONS_MENU = new Pair<>(
            new ItemBuilder(Material.DIAMOND_SWORD).name("§aWeapons").get(),
            e -> WeaponManagerMenu.openWeaponInventoryFromExternal(e.getPlayer()));
    private static final Pair<ItemStack, Consumer<PlayerInteractEvent>> REWARD_INVENTORY_MENU = new Pair<>(
            new ItemBuilder(Material.CHEST).name("§aReward Inventory").get(),
            e -> RewardInventory.openRewardInventory(e.getPlayer(), 1));
    private static final List<Pair<ItemStack, Consumer<PlayerInteractEvent>>> STATIC_ITEM_LIST = new ArrayList<Pair<ItemStack, Consumer<PlayerInteractEvent>>>() {{
        add(DEBUG_MENU);
        add(START_MENU);
        add(SELECTION_MENU);
        add(SPECTATE_MENU);
        add(WEAPONS_MENU);
        add(REWARD_INVENTORY_MENU);
    }};

    public static void addItems(Player player) {
        for (ItemListener listener : playerHotBarItemListeners.get(player.getUniqueId())) {
            player.getInventory().setItem(listener.getSlot(), listener.getItemStack());
        }
    }

    public static void addItems(Player player, Set<ItemListener> listeners) {
        if (!playerHotBarItemListeners.containsKey(player.getUniqueId())) {
            playerHotBarItemListeners.put(player.getUniqueId(), listeners);
        }
        for (ItemListener listener : listeners) {
            player.getInventory().setItem(listener.getSlot(), listener.getItemStack());
        }
    }

    public static void addStaticItem(Player player, int slot, Pair<ItemStack, Consumer<PlayerInteractEvent>> pair) {
        player.getInventory().setItem(slot, pair.getA());
    }

    public static void giveLobbyHotBar(Player player, boolean fromGame) {
        UUID uuid = player.getUniqueId();

        if (!playerHotBarItemListeners.containsKey(uuid)) {
            Set<ItemListener> listeners = new HashSet<>();

            PlayerSettings playerSettings = Warlords.getPlayerSettings(uuid);
            Specializations selectedSpec = playerSettings.getSelectedSpec();
            AbstractPlayerClass apc = selectedSpec.create.get();
            listeners.add(new ItemListener(
                    1,
                    new ItemBuilder(apc.getWeapon().getItem(playerSettings.getWeaponSkins().getOrDefault(selectedSpec, Weapons.FELFLAME_BLADE).getItem()))
                            .name("§aWeapon Skin Preview")
                            .get(),
                    e -> {
                    })
            );

            if (!fromGame) {
                Warlords.partyManager.getPartyFromAny(uuid).ifPresent(party -> {
                    List<RegularGamesMenu.RegularGamePlayer> playerList = party.getRegularGamesMenu().getRegularGamePlayers();
                    if (!playerList.isEmpty()) {
                        playerList.stream()
                                .filter(regularGamePlayer -> regularGamePlayer.getUuid().equals(uuid))
                                .findFirst()
                                .ifPresent(regularGamePlayer ->
                                        listeners.add(new ItemListener(
                                                2,
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

            listeners.add(new ItemListener(
                    7,
                    new ItemBuilder(HeadUtils.getHead(uuid)).name("§aLevel Rewards").get(),
                    e -> ExperienceManager.openLevelingRewardsMenu(e.getPlayer())
            ));

            addItems(player, listeners);
        } else {
            addItems(player);
        }

        if (player.hasPermission("warlords.game.debug")) {
            addStaticItem(player, 3, DEBUG_MENU);
        } else {
            addStaticItem(player, 3, START_MENU);
        }
        addStaticItem(player, 4, SELECTION_MENU);
        addStaticItem(player, 5, SPECTATE_MENU);
        addStaticItem(player, 6, WEAPONS_MENU);
        addStaticItem(player, 8, REWARD_INVENTORY_MENU);
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
            if (
                    itemStack.getType() == Material.SKULL_ITEM && itemListener.itemStack.getType() == Material.SKULL_ITEM && Objects.equals(((SkullMeta) itemStack.getItemMeta()).getOwner(), ((SkullMeta) itemListener.itemStack.getItemMeta()).getOwner()) ||
                            itemListener.getItemStack().equals(itemStack)
            ) {
                itemListener.getOnClick().accept(e);
                return;
            }
        }

        for (Pair<ItemStack, Consumer<PlayerInteractEvent>> itemStackConsumerPair : STATIC_ITEM_LIST) {
            ItemStack is = itemStackConsumerPair.getA();
            if (itemStack.getType() == Material.SKULL_ITEM && is.getType() == Material.SKULL_ITEM && Objects.equals(((SkullMeta) itemStack.getItemMeta()).getOwner(), ((SkullMeta) is.getItemMeta()).getOwner()) || is.equals(itemStack)) {
                itemStackConsumerPair.getB().accept(e);
            }
        }

    }

    public static class ItemListener {

        private final int slot;
        private final ItemStack itemStack;
        private final Consumer<PlayerInteractEvent> onClick;

        public ItemListener(int slot, ItemStack itemStack, Consumer<PlayerInteractEvent> onClick) {
            this.slot = slot;
            this.itemStack = itemStack;
            this.onClick = onClick;
        }

        public int getSlot() {
            return slot;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public Consumer<PlayerInteractEvent> getOnClick() {
            return onClick;
        }

        //equals and hashcode method that only compares itemstack unless type if skull then also compare owner
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemListener that = (ItemListener) o;
            if (itemStack.getType() == Material.SKULL_ITEM && that.itemStack.getType() == Material.SKULL_ITEM) {
                return Objects.equals(((SkullMeta) itemStack.getItemMeta()).getOwner(), ((SkullMeta) that.itemStack.getItemMeta()).getOwner());
            } else {
                return itemStack.equals(that.itemStack);
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(itemStack);
        }

    }
}

