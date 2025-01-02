package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.GameMode;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;

public class BossRushStartTrait extends WarlordsTrait {

    private int ticks = 0;
    private long lastPlayerCount = 0;
    private long lastPlayerCountInLobby = 0;

    public BossRushStartTrait() {
        super("BossRushStartTrait");
    }

    @Override
    public void onAttach() {
        updateHologram(true);
    }

    @Override
    public void run() {
        if (ticks++ % 20 != 0) {
            return;
        }
        updateHologram(false);
    }

    private void updateHologram(boolean init) {
        long playerCount = Warlords.getGameManager().getPlayerCount(GameMode.BOSS_RUSH);
        long playerCountInLobby = Warlords.getGameManager().getPlayerCountInLobby(GameMode.BOSS_RUSH);
        if (init || playerCount != lastPlayerCount || playerCountInLobby != lastPlayerCountInLobby) {
            lastPlayerCount = playerCount;
            lastPlayerCountInLobby = playerCountInLobby;
            HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
            hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + playerCount + " Players");
            hologramTrait.setLine(1, ChatColor.GRAY.toString() + playerCountInLobby + " in Lobby");
            if (init) {
                hologramTrait.setLine(2, ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Boss Rush");
                hologramTrait.setLine(3, ChatColor.RED + ChatColor.BOLD.toString() + "IN DEVELOPMENT");
            }
        }
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        event.getClicker().getPlayer().sendMessage(Component.text("Boss Rush is currently in development, check back later!", NamedTextColor.RED));
    }

    @Override
    public void leftClick(NPCLeftClickEvent event) {
        event.getClicker().getPlayer().sendMessage(Component.text("Boss Rush is currently in development, check back later!", NamedTextColor.RED));
    }
}
