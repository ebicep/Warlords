package com.ebicep.warlords.database.repositories.events.pojos;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.customentities.npc.traits.GameEventTrait;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;

public enum GameEvents {

    BOLTARO("Boltaro") {
        @Override
        public void setMenu(Menu menu) {
            menu.setItem(
                    3,
                    1,
                    new ItemBuilder(Material.BLAZE_POWDER).name(ChatColor.GREEN + "Start a private Boltaro event game").get(),
                    (m, e) -> {}
            );
            menu.setItem(
                    5,
                    1,
                    new ItemBuilder(Material.REDSTONE_COMPARATOR).name(ChatColor.GREEN + "Join a public Boltaro event game").get(),
                    (m, e) -> {}
            );
        }
    },

    ;

    public static NPC npc;

    public final String name;

    GameEvents(String name) {
        this.name = name;
    }

    public void createNPC() {
        NPCManager.registerTrait(GameEventTrait.class, "GameEventTrait");

        npc = NPCManager.npcRegistry.createNPC(EntityType.PLAYER, "event");
        npc.addTrait(GameEventTrait.class);
        //npc.getOrAddTrait(SkinTrait.class).setSkinName("");
        npc.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2539.5, 50, 744.5, 90, 0));
    }

    public void openMenu(Player player) {
        Menu menu = new Menu(name + " Event", 9 * 6);

        setMenu(menu);

        //TODO SHOP

        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public abstract void setMenu(Menu menu);
}
