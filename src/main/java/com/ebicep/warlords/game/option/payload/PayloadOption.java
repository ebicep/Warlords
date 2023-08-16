package com.ebicep.warlords.game.option.payload;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.minecraft.util.Mth;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PayloadOption implements PveOption {

    private static final double MOVE_RADIUS = 5;
    private final ConcurrentHashMap<AbstractMob<?>, Integer> mobs = new ConcurrentHashMap<>();
    private final AtomicInteger ticksElapsed = new AtomicInteger(0);
    private final PayloadBrain brain;
    private final PayloadRenderer renderer = new PayloadRenderer();
    private final PayloadSpawns spawns;
    private final BossBar bossBar = BossBar.bossBar(Component.empty(), 0, BossBar.Color.WHITE, BossBar.Overlay.NOTCHED_10); //TODO name face direction of payload 🏎
    private final Team escortingTeam;
    @Nonnull
    private Game game;

    public PayloadOption(Location start, PayloadSpawns spawns, Team escortingTeam) {
        this.brain = new PayloadBrain(start);
        this.spawns = spawns;
        this.escortingTeam = escortingTeam;
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        this.renderer.init(game);
    }

    @Override
    public void start(@Nonnull Game game) {
        this.renderer.addRenderPathRunnable(game, brain.getStart(), brain.getPath());
        new GameRunnable(game) {

            @Override
            public void run() {
                Location oldLocation = brain.getCurrentLocation();

                int netEscorting = getNetEscorting(oldLocation);
                if (netEscorting > 0) {
                    brain.tick();
                }
                Location newLocation = brain.getCurrentLocation();
                renderer.move(newLocation);
                renderer.playEffects(ticksElapsed.get(), brain.getCurrentLocation().clone().add(0, .7, 0), MOVE_RADIUS);

                showBossBar(netEscorting);

//                spawns.tick(brain.getCurrentPathIndex() / brain.getPath().size(), newLocation, PayloadOption.this::spawnNewMob);

                ticksElapsed.incrementAndGet();
            }

            private int getNetEscorting(Location oldLocation) {
                int escorting = 0;
                int nonEscorting = 0;
                for (WarlordsEntity warlordsEntity : PlayerFilterGeneric
                        .entitiesAround(oldLocation, MOVE_RADIUS, MOVE_RADIUS, MOVE_RADIUS)
                        .filter(e -> Warlords.getPlayer(e.getUuid()) != null)
                        .warlordsPlayers()
                ) {
                    if (warlordsEntity.getTeam() == escortingTeam) {
                        escorting++;
                    } else {
                        nonEscorting++;
                    }
                }
                return escorting - nonEscorting;
            }

            private void showBossBar(int netEscorting) {
                if (netEscorting > 0) {
                    // https://en.wikipedia.org/wiki/List_of_Unicode_characters#Unicode_symbols:~:text=assigned%20code%20points-,Enclosed%20Alphanumerics,-%5Bedit%5D
                    String unicodeNumber = String.valueOf(Character.toChars((netEscorting <= 20 ? 0x2460 : 0x2470) + netEscorting - 1));
                    bossBar.name(Component.text("Ⓟ" + ">>>" + (netEscorting > 20 ? netEscorting : unicodeNumber))); // ⋙ 〉 ≫ ① ②
                } else {
                    bossBar.name(Component.empty());
                }
                bossBar.progress(Mth.clamp((float) (brain.getCurrentPathIndex() / brain.getPath().size()), 0, 1));
                game.forEachOnlinePlayer((player, team) -> player.showBossBar(bossBar));
            }

        }.runTaskTimer(0, 0);
    }

    @Nonnull
    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public Set<AbstractMob<?>> getMobs() {
        return mobs.keySet();
    }

    @Override
    public int getTicksElapsed() {
        return ticksElapsed.get();
    }

    @Override
    public ConcurrentHashMap<AbstractMob<?>, Integer> getMobsMap() {
        return mobs;
    }

    @Override
    public void spawnNewMob(AbstractMob<?> mob, Team team) {
        mob.toNPC(game, team, UUID.randomUUID(), this::modifyStats);
        game.addNPC(mob.getWarlordsNPC());
        mobs.put(mob, ticksElapsed.get());
        Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, mob));
    }

    @Override
    public PveRewards<?> getRewards() {
        return null; //TODO
    }

    protected void modifyStats(WarlordsNPC warlordsNPC) {
        //TODO
    }

}
