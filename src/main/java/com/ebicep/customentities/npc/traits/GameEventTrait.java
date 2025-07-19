package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.holograms.Hologram;
import com.ebicep.holograms.HologramDataText;
import com.ebicep.holograms.HologramManager;
import com.ebicep.holograms.VisibilityType;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.DateUtil;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;

public class GameEventTrait extends WarlordsTrait {

    private int ticks = 0;
    private Hologram hologram = null;
    private HologramDataText hologramDataText = null;

    public GameEventTrait() {
        super("GameEventTrait");
    }

    @Override
    public void run() {
        Location location = this.getNPC().getStoredLocation();
        if (location == null) {
            return;
        }
        if (ticks % 600 == 0) {
            updateHologram(location);
        }
        ticks++;
    }

    private void updateHologram(Location location) {
        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
        if (ticks != 0 && !currentGameEvent.isActive()) {
            return;
        }
        long playerCount = Warlords.getGameManager().getPlayerCount(GameMode.EVENT_WAVE_DEFENSE);
        long playerCountInLobby = Warlords.getGameManager().getPlayerCountInLobby(GameMode.EVENT_WAVE_DEFENSE);
        String timeTill = DateUtil.getTimeTill(currentGameEvent.getEndDate(),
                true,
                true,
                true,
                false
        );
        boolean ended = timeTill.equals("0 seconds");
        if (ended && !currentGameEvent.isGaveRewards()) {
            if (Warlords.getGameManager().getPlayerCountInLobby(GameMode.EVENT_WAVE_DEFENSE) > 0) {
                Warlords.getGameManager().getGames().stream()
                        .filter(gameHolder -> gameHolder.getGame() != null && gameHolder.getGame().getGameMode() == GameMode.EVENT_WAVE_DEFENSE)
                        .forEach(GameManager.GameHolder::forceEndGame);
            }
            if (Warlords.getGameManager().getPlayerCount(GameMode.EVENT_WAVE_DEFENSE) > 0) {
                return;
            }
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                DatabaseGameEvent.sendGameEventMessage(onlinePlayer,
                        ChatColor.RED + currentGameEvent.getEvent().name + " Event " + ChatColor.GREEN + "has just ended!"
                );
            }
            currentGameEvent.setGaveRewards(true);
            currentGameEvent.giveRewards();
            Warlords.newChain()
                    .async(() -> DatabaseManager.gameEventsService.update(currentGameEvent))
                    .execute();
        }
        ComponentBuilder componentBuilder = ComponentBuilder.create(
                ended ? "Event has ended!" : "Ends in " + timeTill,
                NamedTextColor.GOLD,
                TextDecoration.BOLD
        );
        componentBuilder.newLine(currentGameEvent.getEvent().name, NamedTextColor.RED);
        componentBuilder.newLine(playerCountInLobby + " in Lobby", NamedTextColor.GRAY);
        componentBuilder.newLine(playerCount + " Players", NamedTextColor.YELLOW, TextDecoration.BOLD);
        if (hologram == null) {
            hologramDataText = new HologramDataText.Builder<>(componentBuilder.build())
                    .setBillboard(Display.Billboard.FIXED)
                    .build();
            hologram = new Hologram.Builder(
                    "eventHologram",
                    location.clone().add(0, 2.5, 0),
                    player -> hologramDataText
            ).setVisibility(VisibilityType.ALL).build();
            HologramManager.addHologram(hologram);
        } else if (hologramDataText != null) {
            hologramDataText.setComponent(componentBuilder.build());
            HologramManager.updateHologram(hologram);
        }
    }


    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        DatabaseGameEvent.currentGameEvent.getEvent().openMenu(player);
    }

}
