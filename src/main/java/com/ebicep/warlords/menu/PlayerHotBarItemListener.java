package com.ebicep.warlords.menu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.menu.generalmenu.WarlordsNewHotbarMenu;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.party.PartyPlayer;
import com.ebicep.warlords.party.RegularGamesMenu;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.util.bukkit.ComponentUtils;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

import static com.ebicep.warlords.menu.debugmenu.DebugMenuGameOptions.StartMenu.openGamemodeMenu;

public class PlayerHotBarItemListener implements Listener {

    public static final ItemStack DEBUG_MENU = new ItemBuilder(Material.EMERALD)
            .name(Component.text("Debug Menu", NamedTextColor.GREEN))
            .get();
    public static final ItemStack PVP_MENU = new ItemBuilder(Material.DIAMOND)
            .name(Component.text("PvP Menu", NamedTextColor.GREEN))
            .lore(
                    Component.text("View all information pertaining to PvP.", NamedTextColor.GRAY),
                    Component.empty(),
                    ComponentUtils.CLICK_TO_VIEW
            )
            .get();
    public static final ItemStack PVE_MENU = new ItemBuilder(Material.GOLD_INGOT)
            .name(Component.text("PvE Menu", NamedTextColor.GREEN))
            .lore(
                    Component.text("View all information pertaining to PvE.", NamedTextColor.GRAY),
                    Component.empty(),
                    ComponentUtils.CLICK_TO_VIEW
            )
            .get();
    public static final ItemStack START_MENU = new ItemBuilder(Material.BLAZE_POWDER)
            .name(Component.text("Start Menu", NamedTextColor.GREEN))
            .get();
    public static final ItemStack SPECTATE_MENU = new ItemBuilder(Material.ENDER_EYE)
            .name(Component.text("Spectate", NamedTextColor.GREEN))
            .lore(
                    Component.text("Spectate ongoing games.", NamedTextColor.GRAY),
                    Component.empty(),
                    Component.text("Click to open!", NamedTextColor.YELLOW)
            )
            .get();
    public static final ItemStack SELECTION_MENU = new ItemBuilder(Material.NETHER_STAR)
            .name(Component.text("Warlords Menu", NamedTextColor.GREEN))
            .lore(WordWrap.wrap(Component.text("View your specializations, settings, PvP/PvE stats, and more!", NamedTextColor.GRAY), 160))
            .addLore(
                    Component.empty(),
                    Component.text("Click to open!", NamedTextColor.YELLOW)
            )
            .get();
    public static final ItemStack SETTINGS_MENU = new ItemBuilder(Material.BEDROCK)
            .name(Component.text("Settings Menu", NamedTextColor.GREEN))
            .lore(
                    Component.text("View all your in game settings.", NamedTextColor.GRAY),
                    Component.empty(),
                    ComponentUtils.CLICK_TO_VIEW
            )
            .get();
    private static final HashMap<Integer, Consumer<PlayerInteractEvent>> SLOT_HOTBAR_LISTENER = new HashMap<>();

    static {
        SLOT_HOTBAR_LISTENER.put(0, e -> {
            Pair<Party, PartyPlayer> p = PartyManager.getPartyAndPartyPlayerFromAny(e.getPlayer().getUniqueId());
            if (p != null) {
                RegularGamesMenu regularGamesMenu = p.getA().getRegularGamesMenu();
                regularGamesMenu.openMenuForPlayer(e.getPlayer());
            }
        });
        SLOT_HOTBAR_LISTENER.put(1, e -> {
        });
        SLOT_HOTBAR_LISTENER.put(2, e -> {
//            WarlordsNewHotbarMenu.PvPMenu.openPvPMenu(e.getPlayer());
        });
        SLOT_HOTBAR_LISTENER.put(3, e -> {
            if (e.getPlayer().hasPermission("warlords.game.debug")) {
                Bukkit.getServer().dispatchCommand(e.getPlayer(), "wl");
            } else {
                openGamemodeMenu(e.getPlayer());
            }
        });
        SLOT_HOTBAR_LISTENER.put(4, e -> WarlordsNewHotbarMenu.SelectionMenu.openWarlordsMenu(e.getPlayer()));
        SLOT_HOTBAR_LISTENER.put(5, e -> Bukkit.getServer().dispatchCommand(e.getPlayer(), "spectate"));
//        SLOT_HOTBAR_LISTENER.put(6, e -> WarlordsNewHotbarMenu.PvEMenu.openPvEMenu(e.getPlayer()));
//        SLOT_HOTBAR_LISTENER.put(8, e -> WarlordsNewHotbarMenu.SettingsMenu.openSettingsMenu(e.getPlayer()));
    }

    public static void giveLobbyHotBar(Player player, boolean fromGame) {
        UUID uuid = player.getUniqueId();
        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(uuid);
        Specializations selectedSpec = playerSettings.getSelectedSpec();
        AbstractPlayerClass apc = selectedSpec.create.get();

        if (!fromGame) {
            Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(uuid);
            if (partyPlayerPair != null) {
                RegularGamesMenu regularGamesMenu = partyPlayerPair.getA().getRegularGamesMenu();
                regularGamesMenu.getRegularGameTeams().forEach((team, regularGameTeam) -> {
                    if (regularGameTeam.getTeamPlayers().stream().anyMatch(regularGamePlayer -> regularGamePlayer.getUuid().equals(uuid))) {
                        setItem(player,
                                0,
                                new ItemBuilder(team.getWool()).name(Component.text("Team Builder", NamedTextColor.GREEN)).get()
                        );
                    }
                });
            }
        }
        ItemStack weaponSkin = playerSettings.getWeaponSkins().getOrDefault(selectedSpec, Weapons.STEEL_SWORD).getItem();
        setItem(player,
                1,
                new ItemBuilder(apc.getWeapon().getItem(weaponSkin))
                        .name(Component.text("Weapon Skin Preview", NamedTextColor.GREEN))
                        .noLore()
                        .get()
        );

//        setItem(player, 2, PVP_MENU);

        if (player.hasPermission("warlords.game.debug")) {
            setItem(player, 3, DEBUG_MENU);
        } else {
            setItem(player, 3, START_MENU);
        }
        setItem(player, 4, SELECTION_MENU);
        setItem(player, 5, SPECTATE_MENU);
//        setItem(player, 6, PVE_MENU);
//        setItem(player, 8, SETTINGS_MENU);

    }

    public static void setItem(Player player, int slot, ItemStack itemStack) {
        player.getInventory().setItem(slot, itemStack);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!e.getPlayer().getWorld().getName().equals("MainLobby")) {
            return;
        }
        if (Warlords.getPlayer(e.getPlayer()) != null) {
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

