package com.ebicep.customentities.npc;

import com.ebicep.customentities.npc.traits.SupporterShopTrait;
import com.ebicep.holograms.Hologram;
import com.ebicep.holograms.HologramDataText;
import com.ebicep.holograms.HologramManager;
import com.ebicep.holograms.VisibilityType;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.VillagerProfession;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public final class SupporterShopNPC {

    private static boolean created;

    private SupporterShopNPC() {
    }

    public static void create() {
        if (created) {
            return;
        }
        created = true;

        NPCManager.registerTrait(SupporterShopTrait.class, "SupporterShopTrait");

        NPC npc = NPCManager.NPC_REGISTRY.createNPC(EntityType.VILLAGER, "supporter-shop");
        npc.addTrait(SupporterShopTrait.class);
        npc.getOrAddTrait(VillagerProfession.class).setProfession(Villager.Profession.CLERIC);

        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        Location location = new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 29.5, 81, 163.5, 90, 0);
        npc.spawn(location);

        HologramDataText hologramDataText = new HologramDataText.Builder<>(ComponentBuilder.create(
                "Supporter Shop",
                NamedTextColor.GOLD
        ).build()).setBillboard(Display.Billboard.CENTER).build();
        HologramManager.addHologram(new Hologram.Builder(
                        "supporterShop",
                        location.clone().add(0, 2.1, 0),
                        player -> hologramDataText
                ).setVisibility(VisibilityType.ALL).build()
        );
    }
}
