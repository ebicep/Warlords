package com.ebicep.warlords.game.option.pve.effigytrails;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.commands.MobCommand;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.citizensnpcs.api.ai.EntityTarget;
import net.citizensnpcs.api.ai.event.NavigationBeginEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class EffigyTrialOption implements PveOption {

    private final ConcurrentHashMap<AbstractMob, MobData> mobs = new ConcurrentHashMap<>();
    private final AtomicInteger ticksElapsed = new AtomicInteger(0);
    private final EffigyChargeManager chargeManager;
    private EffigyTrialsRewards effigyTrialsRewards;
    private Game game;

    public EffigyTrialOption(EffigyChargeManager chargeManager) {
        this.chargeManager = chargeManager;
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        this.effigyTrialsRewards = new EffigyTrialsRewards(this);
        game.registerEvents(new Listener() {

            @EventHandler
            public void onFinalDamageHeal(WarlordsDamageHealingFinalEvent event) {
                WarlordsEntity attacker = event.getSource();
                if (!(attacker instanceof WarlordsPlayer)) {
                    return;
                }
            }

            @EventHandler(ignoreCancelled = true)
            public void onDeath(WarlordsDeathEvent event) {
                WarlordsEntity we = event.getWarlordsEntity();
                WarlordsEntity killer = event.getKiller();

                if (we instanceof WarlordsNPC) {
                    AbstractMob mobToRemove = ((WarlordsNPC) we).getMob();
                    if (mobs.containsKey(mobToRemove)) {
                        mobToRemove.onDeath(killer, we.getLocation(), EffigyTrialOption.this);
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

            @EventHandler
            public void onMobStartNavigating(NavigationBeginEvent event) {
                NPC npc = event.getNPC();
                // handle setting targetedBy/targeting
                EntityTarget entityTarget = npc.getNavigator().getEntityTarget();
                if (entityTarget == null) {
                    return;
                }
                if (!(npc.data().get(WarlordsEntity.WARLORDS_ENTITY_METADATA) instanceof WarlordsNPC warlordsNPC)) {
                    return;
                }
                WarlordsEntity targetWarlordsEntity = Warlords.getPlayer(entityTarget.getTarget());
                if (targetWarlordsEntity == null) {
                    return;
                }
                warlordsNPC.getMob().onEntityTarget(targetWarlordsEntity);
            }

        });
    }

    @Override
    public void start(@Nonnull Game game) {

    }

    @Override
    public Set<AbstractMob> getMobs() {
        return mobs.keySet();
    }

    @Override
    public ConcurrentHashMap<AbstractMob, ? extends MobData> getMobsMap() {
        return mobs;
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public int getTicksElapsed() {
        return ticksElapsed.get();
    }

    @Override
    public void spawnNewMob(AbstractMob mob, Team team) {
        game.addNPC(mob.toNPC(game, team, this::modifyStats));
        mobs.put(mob, new MobData(ticksElapsed.get()));
        Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, mob));
    }

    private void modifyStats(WarlordsNPC warlordsNPC) {
        warlordsNPC.getMob().onSpawn(this);

    }

    @Override
    public PveRewards<?> getRewards() {
        return effigyTrialsRewards;
    }

}
