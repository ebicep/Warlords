package com.ebicep.warlords.game.option.payload;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.commands.MobCommand;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PayloadOption implements PveOption {

    private static final double MOVE_RADIUS = 5;
    private static final int BOSS_BAR_FILL_SPACE = 45;
    private static final int BOSS_BAR_ESCORT_SPACE = 6;
    private final ConcurrentHashMap<AbstractMob, MobData> mobs = new ConcurrentHashMap<>();
    private final AtomicInteger ticksElapsed = new AtomicInteger(0);
    private final PayloadBrain brain;
    private final PayloadRendererCoalCart renderer = new PayloadRendererCoalCart();
    private final PayloadSpawns spawns;
    private final BossBar bossBar = BossBar.bossBar(Component.empty(), 0, BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_10);
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

        game.registerEvents(new Listener() {

            @EventHandler
            public void onDeath(WarlordsDeathEvent event) {
                WarlordsEntity we = event.getWarlordsEntity();
                WarlordsEntity killer = event.getKiller();

                if (we instanceof WarlordsNPC) {
                    AbstractMob mobToRemove = ((WarlordsNPC) we).getMob();
                    if (mobs.containsKey(mobToRemove)) {
                        mobToRemove.onDeath(killer, we.getDeathLocation(), PayloadOption.this);
                        new GameRunnable(game) {
                            @Override
                            public void run() {
                                mobs.remove(mobToRemove);
                                game.getPlayers().remove(we.getUuid());
                                Warlords.removePlayer(we.getUuid());
                                //game.removePlayer(we.getUuid());
                            }
                        }.runTaskLater(1);

                        if (killer instanceof WarlordsPlayer) {
                            killer.getMinuteStats().addMobKill(mobToRemove.getName());
                            we.getHitBy().forEach((assisted, value) -> assisted.getMinuteStats().addMobAssist(mobToRemove.getName()));
                        }

                    }
                    MobCommand.SPAWNED_MOBS.remove(mobToRemove);
                } else if (we instanceof WarlordsPlayer && killer instanceof WarlordsNPC) {
                    if (mobs.containsKey(((WarlordsNPC) killer).getMob())) {
                        we.getMinuteStats().addMobDeath(((WarlordsNPC) killer).getMob().getName());
                    }
                }
            }
        });
    }

    @Override
    public void start(@Nonnull Game game) {
        this.renderer.addRenderPathRunnable(game, brain.getStart(), brain.getPath());
        new GameRunnable(game) {

            @Override
            public void run() {
                if (game.getState(EndState.class).isPresent()) {
                    cancel();
                    return;
                }
                Location oldLocation = brain.getCurrentLocation();

                int netEscorting = getNetEscorting(oldLocation);
                if (netEscorting > 0) {
                    boolean reachedEnd = brain.tick(netEscorting);
                    if (reachedEnd) {
                        Bukkit.getPluginManager().callEvent(new WarlordsGameTriggerWinEvent(game, PayloadOption.this, escortingTeam));
                    }
                }
                Location newLocation = brain.getCurrentLocation();
                renderer.move(newLocation);
                renderer.playEffects(ticksElapsed.get(), brain.getCurrentLocation().clone().add(0, .7, 0), MOVE_RADIUS);

                showBossBar(netEscorting);

                spawns.tick(brain.getMappedPathIndex() / brain.getPath().size(), newLocation, PayloadOption.this::spawnNewMob);

                if (ticksElapsed.get() % 10 == 0) {
                    spawns.renderSpawnLocations();
//                renderer.renderPath(brain.getPath());
                }
                ticksElapsed.incrementAndGet();
            }

            private int getNetEscorting(Location oldLocation) {
                int escorting = 0;
                int nonEscorting = 0;
                for (WarlordsEntity warlordsEntity : PlayerFilterGeneric
                        .entitiesAround(oldLocation, MOVE_RADIUS, MOVE_RADIUS, MOVE_RADIUS)
                        .filter(e -> Warlords.getPlayer(e.getUuid()) != null)
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
                float progress = (float) (brain.getMappedPathIndex() / brain.getPath().size());
                String pushing = "";
                boolean escorting = netEscorting > 0;
                if (escorting) {
                    // https://en.wikipedia.org/wiki/List_of_Unicode_characters#Unicode_symbols:~:text=assigned%20code%20points-,Enclosed%20Alphanumerics,-%5Bedit%5D
                    // https://www.compart.com/en/unicode/search?q=Dingbat+Negative+Circled+#characters
                    // String unicodeNumber = String.valueOf(Character.toChars((netEscorting <= 20 ? 0x2460 : 0x2470) + netEscorting - 1));
                    String unicodeNumber = String.valueOf(Character.toChars((netEscorting <= 10 ? 0x2776 : 0x24E1) + netEscorting - 1));
                    pushing = ">>>" + (netEscorting > 20 ? netEscorting : unicodeNumber); // ⋙ 〉 ≫ ① ②
                }
                bossBar.name(Component.textOfChildren(
                        Component.text(" ".repeat((int) (progress * BOSS_BAR_FILL_SPACE) + (escorting ? BOSS_BAR_ESCORT_SPACE : 0))),
                        Component.text("🄿", NamedTextColor.BLUE),
                        Component.text(pushing),
                        Component.text(" ".repeat((int) ((1 - progress) * BOSS_BAR_FILL_SPACE)))
                ));
                bossBar.progress(MathUtils.clamp(progress, 0, 1));
                game.forEachOnlinePlayer((player, team) -> player.showBossBar(bossBar));
            }

        }.runTaskTimer(0, 0);
    }

    protected void modifyStats(WarlordsNPC warlordsNPC) {
        //TODO
    }

    @Nonnull
    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public Set<AbstractMob> getMobs() {
        return mobs.keySet();
    }

    @Override
    public int getTicksElapsed() {
        return ticksElapsed.get();
    }

    @Override
    public ConcurrentHashMap<AbstractMob, ? extends MobData> getMobsMap() {
        return mobs;
    }

    @Override
    public void spawnNewMob(AbstractMob mob, Team team) {
        mob.toNPC(game, team, this::modifyStats);
        game.addNPC(mob.getWarlordsNPC());
        mobs.put(mob, new MobData(ticksElapsed.get()));
        Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, mob));
    }

    @Override
    public PveRewards<?> getRewards() {
        return null; //TODO
    }

}
