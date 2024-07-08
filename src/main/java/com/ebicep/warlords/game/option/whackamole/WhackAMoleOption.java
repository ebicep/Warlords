package com.ebicep.warlords.game.option.whackamole;

import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class WhackAMoleOption implements PveOption, Listener {
    private static final Mob[] MOLES = {
            Mob.WHACK_A_MOLE_ARMOR_STAND
    };
    private final List<Hole> holes = new ArrayList<>();
    private final ConcurrentHashMap<AbstractMob, WhackAMoleMobData> mobs = new ConcurrentHashMap<>();
    private Game game;
    private int ticksElapsed;
    private int score;
    private int speed = 20;

    public WhackAMoleOption(List<Hole> holes) {
        this.holes.addAll(holes);
    }

    private void resetHole(Hole hole) {
        hole.init(game);
        Mob mob = MOLES[(int) (ThreadLocalRandom.current().nextDouble() * MOLES.length)];
        AbstractMob abstractMob = mob.createMob(hole.getBottomLocation());
        game.addNPC(abstractMob.toNPC(game, Team.RED, warlordsNPC -> warlordsNPC.getMob().onSpawn(this)));
        hole.setMob(abstractMob);
        this.mobs.put(abstractMob, new WhackAMoleMobData(ticksElapsed, hole));
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        game.registerEvents(this);
        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(10, "score") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                return Collections.singletonList(Component.text("Score: ").append(Component.text(score, NamedTextColor.GREEN)));
            }
        });
    }

    @Override
    public void start(@Nonnull Game game) {
        holes.forEach(this::resetHole);
        new GameRunnable(game) {
            @Override
            public void run() {
                ticksElapsed++;
                if (ticksElapsed % speed == 0) {
                    int randomHole = (int) (ThreadLocalRandom.current().nextDouble() * holes.size());
                    Hole hole = holes.get(randomHole);
                    hole.rise();
                }
                if (ticksElapsed % 200 == 0 && speed > 9) {
                    speed--;
                }
            }
        }.runTaskTimer(0, 0);
    }

    @EventHandler
    public void onMoleDeath(WarlordsDeathEvent event) {
        WarlordsEntity dead = event.getWarlordsEntity();
        if (!(dead instanceof WarlordsNPC warlordsNPC)) {
            return;
        }
        AbstractMob mob = warlordsNPC.getMob();
        Hole hole = mobs.get(mob).getHole();
        if (hole == null) {
            return;
        }
        despawnMob(mob);
        hole.disable();
        new GameRunnable(game) {
            @Override
            public void run() {
                resetHole(hole);
            }
        }.runTaskLater(20);
        score++;
    }

    @EventHandler
    public void onAbilityActivate(WarlordsAbilityActivateEvent.Pre event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamageHeal(WarlordsDamageHealingEvent event) {
        WarlordsEntity mole = event.getWarlordsEntity();
        if (!(mole instanceof WarlordsNPC warlordsNPC)) {
            event.setCancelled(true);
            return;
        }
        AbstractMob mob = warlordsNPC.getMob();
        Hole hole = mobs.get(mob).getHole();
        if (hole == null) {
            event.setCancelled(true);
            return;
        }
        if (!hole.isAttackable()) {
            event.setCancelled(true);
            return;
        }
        event.setMin(100);
        event.setMax(100);
        event.setCritChance(0);
        event.setCritMultiplier(100);
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player.getEntity() instanceof Player) {
            getGame().setPlayerTeam((OfflinePlayer) player.getEntity(), Team.BLUE);
            player.setTeam(Team.BLUE);
            player.updateArmor();
        }
        for (int i = 1; i < player.getAbilities().size(); i++) {
            player.getAbilities().remove(i);
            i--;
        }
        player.updateInventory(false);
    }

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
        return ticksElapsed;
    }

    @Override
    public ConcurrentHashMap<AbstractMob, WhackAMoleMobData> getMobsMap() {
        return mobs;
    }

    @Override
    public void spawnNewMob(AbstractMob mob, Team team) {
//        game.addNPC(mob.toNPC(game, Team.RED, warlordsNPC -> {}));
    }

    @Override
    public PveRewards<?> getRewards() {
        return null;
    }

    public static class WhackAMoleMobData extends MobData {

        private final Hole hole;

        public WhackAMoleMobData(int spawnTick, Hole hole) {
            super(spawnTick);
            this.hole = hole;
        }

        public Hole getHole() {
            return hole;
        }
    }
}
