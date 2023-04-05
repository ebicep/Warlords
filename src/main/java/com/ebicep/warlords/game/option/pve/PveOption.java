package com.ebicep.warlords.game.option.pve;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.pve.*;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.AutoUpgradeProfile;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface PveOption {

    int playerCount();

    default int mobCount() {
        return (int) getMobs()
                .stream()
                .filter(mob -> mob.getWarlordsNPC().getTeam() == Team.RED)
                .count();
    }

    Set<AbstractMob<?>> getMobs();

    int getTicksElapsed();

    default int getWaveCounter() {
        return 1;
    }

    default DifficultyIndex getDifficulty() {
        return DifficultyIndex.NORMAL;
    }

    default void spawnNewMob(AbstractMob<?> mob) {
        spawnNewMob(mob, Team.RED);
    }

    void spawnNewMob(AbstractMob<?> mob, Team team);

    default boolean isPauseMobSpawn() {
        return false;
    }

    default void setPauseMobSpawn(boolean pauseMobSpawn) {
    }

    default Listener getBaseListener() {
        return new Listener() {
            @EventHandler
            public void onEvent(WarlordsDamageHealingEvent event) {
                WarlordsEntity attacker = event.getAttacker();
                WarlordsEntity receiver = event.getPlayer();

                if (event.isDamageInstance()) {
                    if (attacker instanceof WarlordsNPC) {
                        AbstractMob<?> mob = ((WarlordsNPC) attacker).getMob();
                        if (getMobsMap().containsKey(mob)) {
                            mob.onAttack(attacker, receiver, event);
                        }
                    }

                    if (receiver instanceof WarlordsNPC) {
                        AbstractMob<?> mob = ((WarlordsNPC) receiver).getMob();
                        if (getMobsMap().containsKey(mob)) {
                            mob.onDamageTaken(receiver, attacker, event);
                        }
                    }
                }
            }

            @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
            public void onWin(WarlordsGameTriggerWinEvent event) {
                getRewards().storeRewards();
            }

            @EventHandler
            public void onAddCurrency(WarlordsAddCurrencyFinalEvent event) {
                WarlordsEntity player = event.getPlayer();
                if (!(player instanceof WarlordsPlayer)) {
                    return;
                }
                WarlordsPlayer warlordsPlayer = (WarlordsPlayer) player;
                AbilityTree abilityTree = ((WarlordsPlayer) player).getAbilityTree();
                if (abilityTree == null) {
                    return;
                }
                AutoUpgradeProfile autoUpgradeProfile = abilityTree.getAutoUpgradeProfile();
                List<AutoUpgradeProfile.AutoUpgradeEntry> autoUpgradeEntries = autoUpgradeProfile.getAutoUpgradeEntries();
                for (AutoUpgradeProfile.AutoUpgradeEntry entry : autoUpgradeEntries) {
                    AbstractUpgradeBranch<?> upgradeBranch = abilityTree.getUpgradeBranches().get(entry.getBranchIndex());
                    AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType upgradeType = entry.getUpgradeType();
                    List<Upgrade> upgradeList = upgradeType.getUpgradeFunction.apply(upgradeBranch);
                    Upgrade upgrade = upgradeList.get(entry.getUpgradeIndex());
                    if (upgrade.isUnlocked()) {
                        continue;
                    }
                    if (player.getCurrency() < upgrade.getCurrencyCost() && upgradeBranch.getFreeUpgrades() <= 0) {
                        return;
                    }
                    if (upgradeType == AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.MASTER) {
                        upgradeBranch.purchaseMasterUpgrade(warlordsPlayer, true);
                    } else {
                        upgradeBranch.purchaseUpgrade(upgradeList, warlordsPlayer, upgrade, entry.getUpgradeIndex(), true);
                    }
                }
            }

            @EventHandler
            public void onMobTarget(EntityTargetLivingEntityEvent event) {
                Entity entity = ((CraftEntity) event.getEntity()).getHandle();
                if (!(entity instanceof EntityLiving)) {
                    return;
                }
                EntityLiving entityLiving = (EntityLiving) entity;
                if (getMobsMap().keySet().stream().noneMatch(abstractMob -> Objects.equals(abstractMob.getEntity(), entityLiving))) {
                    return;
                }
                if (entityLiving instanceof EntityInsentient) {
                    Game game = getGame();
                    LivingEntity newTarget = event.getTarget();
                    EntityLiving oldTarget = ((EntityInsentient) entityLiving).getGoalTarget();
                    if (entityLiving.hasEffect(MobEffectList.BLINDNESS) && newTarget != null) {
                        event.setCancelled(true);
                        return;
                    }
                    if (newTarget == null) {
                        if (oldTarget instanceof EntityPlayer) {
                            //setting target to player zombie
                            game.warlordsPlayers()
                                .filter(warlordsPlayer -> warlordsPlayer.getUuid().equals(oldTarget.getUniqueID()))
                                .findFirst()
                                .ifPresent(warlordsPlayer -> {
                                    if (!(warlordsPlayer.getEntity() instanceof Player)) {
                                        event.setTarget(warlordsPlayer.getEntity());
                                    }
                                });
                        }
                    } else {
                        if (oldTarget instanceof EntityZombie) {
                            //makes sure player that rejoins is still the target
                            game.warlordsPlayers()
                                .filter(warlordsPlayer -> ((CraftEntity) warlordsPlayer.getEntity()).getHandle().equals(oldTarget))
                                .findFirst()
                                .ifPresent(warlordsPlayer -> event.setCancelled(true));
                        }
                        if (newTarget.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                            event.setCancelled(true);
                        }
                    }
                }
            }

            @EventHandler
            public void onWeaponDrop(WarlordsGiveWeaponEvent event) {
                getRewards().getPlayerRewards(event.getPlayer().getUuid())
                            .getWeaponsFound()
                            .add(event.getWeapon());
            }

            @EventHandler
            public void onMobDrop(WarlordsGiveMobDropEvent event) {
                getRewards().getPlayerRewards(event.getPlayer().getUuid())
                            .getMobDropsGained()
                            .merge(event.getMobDrop(), 1L, Long::sum);
            }

            @EventHandler
            public void onItemDrop(WarlordsGiveItemEvent event) {
                getRewards().getPlayerRewards(event.getPlayer().getUuid())
                            .getItemsFound()
                            .add(event.getItem());
            }

            @EventHandler
            public void onItemDrop(WarlordsGiveBlessingFoundEvent event) {
                getRewards().getPlayerRewards(event.getPlayer().getUuid())
                            .addBlessingsFound();
            }
        };
    }

    ConcurrentHashMap<AbstractMob<?>, Integer> getMobsMap();

    PveRewards<?> getRewards();

    Game getGame();

}
