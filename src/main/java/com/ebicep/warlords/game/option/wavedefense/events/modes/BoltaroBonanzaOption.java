package com.ebicep.warlords.game.option.wavedefense.events.modes;

import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.SpawnLocationMarker;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.AbstractMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.bosses.bossminions.BoltaroExiled;
import com.ebicep.warlords.game.option.wavedefense.mobs.events.boltarobonanza.EventBoltaroShadow;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BoltaroBonanzaOption implements Option {

    private final AtomicInteger highestSplitValue = new AtomicInteger();

    @Override
    public void start(@Nonnull Game game) {
        for (Option option : game.getOptions()) {
            if (option instanceof WaveDefenseOption) {
                startSpawnTask(game, (WaveDefenseOption) option);
                break;
            }
        }

        game.registerEvents(new Listener() {

            final EventPointsOption eventPointsOption = game
                    .getOptions()
                    .stream()
                    .filter(option -> option instanceof EventPointsOption)
                    .map(EventPointsOption.class::cast)
                    .findAny()
                    .orElse(null);

            @EventHandler
            public void onMobSpawn(WarlordsMobSpawnEvent event) {
                AbstractMob<?> mob = event.getMob();
                if (mob instanceof EventBoltaroShadow) {
                    int split = ((EventBoltaroShadow) mob).getSplit();
                    if (split > highestSplitValue.get()) {
                        if (eventPointsOption != null) {
                            eventPointsOption.getPoints().replaceAll((uuid, integer) -> integer + 1000);
                        }
                        highestSplitValue.set(split);
                    }
                }
            }
        });
    }

    @Override
    public void sendEventStatsMessage(@Nonnull Game game, @Nonnull Player player) {
        ChatUtils.sendMessage(player,
                true,
                ChatColor.WHITE + "Highest Split: " + ChatColor.GOLD + highestSplitValue.get()
        );
    }

    private void startSpawnTask(@Nonnull Game game, WaveDefenseOption option) {
        int playerCount = (int) game.warlordsPlayers().count();
        List<SpawnLocationMarker> spawnLocations = game.getMarkers(SpawnLocationMarker.class);

        new GameRunnable(game) {

            @Override
            public void run() {
                for (int i = 0; i < playerCount * 3; i++) {
                    //random spawn location
                    SpawnLocationMarker spawnLocation = spawnLocations.get((int) (Math.random() * spawnLocations.size()));
                    option.spawnNewMob(new BoltaroExiled(spawnLocation.getLocation()));
                }
            }

        }.runTaskTimer(0, 20 * 30);
    }

    public int getHighestSplitValue() {
        return highestSplitValue.get();
    }
}
