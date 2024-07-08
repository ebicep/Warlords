package com.ebicep.warlords.game.option;

import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

public class DummySpawnOption implements Option {

    private final Location loc;
    private final Team team;
    private final Consumer<WarlordsNPC> onTestDummyCreate;
    private WarlordsNPC testDummy;

    public DummySpawnOption(Location loc, Team team) {
        this(loc, team, warlordsNPC -> {});
    }

    public DummySpawnOption(Location loc, Team team, Consumer<WarlordsNPC> onTestDummyCreate) {
        this.loc = loc;
        this.team = team;
        this.onTestDummyCreate = onTestDummyCreate;
    }

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(new Listener() {
            @EventHandler
            public void onDummyDeath(WarlordsDeathEvent event) {
                WarlordsEntity dead = event.getWarlordsEntity();
                if (Objects.equals(dead, testDummy)) {
                    new GameRunnable(game) {

                        @Override
                        public void run() {
                            testDummy.respawn();
                        }
                    }.runTaskLater(10);
                }
            }
        });
    }

    @Override
    public void start(@Nonnull Game game) {
        // Delay spawn by 5 seconds to avoid Null reference in PlayingState
        new GameRunnable(game) {
            @Override
            public void run() {
                if (getGame().getState() instanceof EndState) {
                    ChatUtils.MessageType.WARLORDS.sendErrorMessage("[DEBUG] CAUGHT INVALID DUMMY SPAWN - game was ended before initial spawn.");
                    return;
                }

                WarlordsNPC dummyNPC = Mob.TEST_DUMMY.createMob(loc).toNPC(game, team, warlordsNPC -> warlordsNPC.getMob().onSpawn(null));
                dummyNPC.setNameColor(team.getTeamColor());
                onTestDummyCreate.accept(dummyNPC);
                testDummy = game.addNPC(dummyNPC);
                if (testDummy.getEntity() instanceof LivingEntity livingEntity) {
                    livingEntity.setRemoveWhenFarAway(false);
                }
                testDummy.teleport(loc);
                testDummy.setTakeDamage(true);
                testDummy.updateHealth();
            }
        }.runTaskLater(100);
    }
}