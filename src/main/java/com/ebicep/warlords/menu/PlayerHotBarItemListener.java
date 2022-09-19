package com.ebicep.warlords.menu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.party.PartyPlayer;
import com.ebicep.warlords.party.RegularGamesMenu;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.rewards.RewardInventory;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static com.ebicep.warlords.menu.debugmenu.DebugMenuGameOptions.StartMenu.openGamemodeMenu;
import static com.ebicep.warlords.menu.generalmenu.WarlordsShopMenu.openMainMenu;

public class PlayerHotBarItemListener implements Listener {


    private static final ItemStack DEBUG_MENU = new ItemBuilder(Material.EMERALD).name("§aDebug Menu").get();
    private static final ItemStack START_MENU = new ItemBuilder(Material.BLAZE_POWDER).name("§aStart Menu").get();
    private static final ItemStack SPECTATE_MENU = new ItemBuilder(Material.EYE_OF_ENDER).name("§aSpectate").get();
    private static final ItemStack WEAPONS_MENU = new ItemBuilder(Material.DIAMOND_SWORD).name("§aWeapons").get();
    private static final ItemStack REWARD_INVENTORY_MENU = new ItemBuilder(Material.ENDER_CHEST).name("§aReward Inventory").get();
    private static final ItemStack SELECTION_MENU = new ItemBuilder(Material.NETHER_STAR).name("§aSelection Menu").get();
    private static final HashMap<Integer, Consumer<PlayerInteractEvent>> SLOT_HOTBAR_LISTENER = new HashMap<>();

    static {
        SLOT_HOTBAR_LISTENER.put(1, e -> {
        });
        SLOT_HOTBAR_LISTENER.put(2, e -> {
            Pair<Party, PartyPlayer> p = PartyManager.getPartyAndPartyPlayerFromAny(e.getPlayer().getUniqueId());
            if (p != null) {
                List<RegularGamesMenu.RegularGamePlayer> playerList2 = p.getA().getRegularGamesMenu().getRegularGamePlayers();
                if (!playerList2.isEmpty()) {
                    p.getA().getRegularGamesMenu().openMenuForPlayer(e.getPlayer());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (e.getPlayer().getOpenInventory().getTopInventory().getName().equals("Team Builder")) {
                                p.getA().getRegularGamesMenu().openMenuForPlayer(e.getPlayer());
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(Warlords.getInstance(), 20, 10);
                }
            }
        });
        SLOT_HOTBAR_LISTENER.put(3, e -> {
            if (e.getPlayer().hasPermission("warlords.game.debug")) {
                Bukkit.getServer().dispatchCommand(e.getPlayer(), "wl");
            } else {
                openGamemodeMenu(e.getPlayer());
            }
        });
        SLOT_HOTBAR_LISTENER.put(4, e -> openMainMenu(e.getPlayer()));
        SLOT_HOTBAR_LISTENER.put(5, e -> Bukkit.getServer().dispatchCommand(e.getPlayer(), "spectate"));
        SLOT_HOTBAR_LISTENER.put(6, e -> WeaponManagerMenu.openWeaponInventoryFromExternal(e.getPlayer()));
        SLOT_HOTBAR_LISTENER.put(7, e -> ExperienceManager.openLevelingRewardsMenu(e.getPlayer()));
        SLOT_HOTBAR_LISTENER.put(8, e -> RewardInventory.openRewardInventory(e.getPlayer(), 1));
    }

    public static void giveLobbyHotBar(Player player, boolean fromGame) {
        UUID uuid = player.getUniqueId();
        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(uuid);
        Specializations selectedSpec = playerSettings.getSelectedSpec();
        AbstractPlayerClass apc = selectedSpec.create.get();

        setItem(player,
                1,
                new ItemBuilder(apc.getWeapon().getItem(playerSettings.getWeaponSkins().getOrDefault(selectedSpec, Weapons.FELFLAME_BLADE).getItem()))
                        .name("§aWeapon Skin Preview")
                        .get()
        );
        if (!fromGame) {
            Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(uuid);
            if (partyPlayerPair != null) {
                List<RegularGamesMenu.RegularGamePlayer> playerList = partyPlayerPair.getA().getRegularGamesMenu().getRegularGamePlayers();
                if (!playerList.isEmpty()) {
                    playerList.stream()
                            .filter(regularGamePlayer -> regularGamePlayer.getUuid().equals(uuid))
                            .findFirst()
                            .ifPresent(regularGamePlayer -> setItem(player, 2, new ItemBuilder(regularGamePlayer.getTeam().item).name("§aTeam Builder").get()));
                }
            }
        }

        if (player.hasPermission("warlords.game.debug")) {
            setItem(player, 3, DEBUG_MENU);
        } else {
            setItem(player, 3, START_MENU);
        }
        setItem(player, 4, SELECTION_MENU);
        setItem(player, 5, SPECTATE_MENU);

        if (fromGame) {
            giveLobbyHotBarDatabase(player);
        } else if (DatabaseManager.enabled) {
            setItem(player, 7, new ItemBuilder(HeadUtils.getHead(uuid)).name("§aLevel Rewards").get());
            setItem(player, 8, REWARD_INVENTORY_MENU);
        }
    }

    public static void setItem(Player player, int slot, ItemStack itemStack) {
        player.getInventory().setItem(slot, itemStack);
    }

    public static void giveLobbyHotBarDatabase(Player player) {
        if (DatabaseManager.enabled) {
            updateWeaponManagerItem(player);
            setItem(player, 7, new ItemBuilder(HeadUtils.getHead(player.getUniqueId())).name("§aLevel Rewards").get());
            setItem(player, 8, REWARD_INVENTORY_MENU);
        }
    }

    public static void updateWeaponManagerItem(Player player) {
        UUID uuid = player.getUniqueId();
        if (DatabaseManager.playerService != null) {
            DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(uuid);
            updateWeaponManagerItem(player, databasePlayer);
        } else {
            setItem(player, 6, WEAPONS_MENU);
        }
    }

    public static void updateWeaponManagerItem(Player player, DatabasePlayer databasePlayer) {
        List<AbstractWeapon> weapons = databasePlayer.getPveStats().getWeaponInventory();
        Optional<AbstractWeapon> optionalWeapon = weapons.stream()
                .filter(AbstractWeapon::isBound)
                .filter(abstractWeapon -> abstractWeapon.getSpecializations() == databasePlayer.getLastSpec())
                .findFirst();
        if (optionalWeapon.isPresent()) {
            setItem(player, 6, optionalWeapon.get().generateItemStack());
            return;
        }
        setItem(player, 6, WEAPONS_MENU);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!e.getPlayer().getWorld().getName().equals("MainLobby")) {
            return;
        }
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack itemStack = e.getItem();
        if (itemStack == null) {
            return;
        }
        int slot = e.getPlayer().getInventory().getHeldItemSlot();
        if (SLOT_HOTBAR_LISTENER.containsKey(slot)) {
            SLOT_HOTBAR_LISTENER.get(slot).accept(e);
        }
    }
}

