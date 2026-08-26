package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Infernal extends BaseSet {

    private static final int FIRE_DURATION_TICKS = 3 * GameRunnable.SECOND;
    private static final int DAMAGE_INTERVAL_TICKS = GameRunnable.SECOND;
    private static final float CURRENT_HEALTH_DAMAGE_PERCENT = 5;

    @Override
    public String getConfigFieldName() {
        return "infernal";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of();
    }

    public class Bonus implements SetBonus.Bonus {

        private final Map<LocationUtils.LocationBlockHolder, Integer> fireExpireTicks = new HashMap<>();
        private final Map<LocationUtils.LocationBlockHolder, Integer> fireNextDamageTicks = new HashMap<>();
        private final Map<LocationUtils.LocationBlockHolder, Material> originalMaterials = new HashMap<>();
        private final Set<LocationUtils.LocationBlockHolder> gameTrackedBlocks = new HashSet<>();
        private final Map<UUID, Integer> lastDamagedTicks = new HashMap<>();
        private LocationUtils.LocationBlockHolder lastPlayerBlock;
        private int ticksElapsed;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getGame().registerEvents(new Listener() {

                @EventHandler
                public void onBlockFade(BlockFadeEvent event) {
                    if (isActiveFire(event.getBlock())) {
                        event.setCancelled(true);
                    }
                }

                @EventHandler
                public void onBlockSpread(BlockSpreadEvent event) {
                    if (isActiveFire(event.getSource())) {
                        event.setCancelled(true);
                    }
                }

                @EventHandler
                public void onBlockBurn(BlockBurnEvent event) {
                    Block ignitingBlock = event.getIgnitingBlock();
                    if (ignitingBlock != null && isActiveFire(ignitingBlock)) {
                        event.setCancelled(true);
                    }
                }

                @EventHandler
                public void onCombust(EntityCombustEvent event) {
                    if (!isActiveFire(event.getEntity().getLocation().getBlock())) {
                        return;
                    }
                    event.setCancelled(true);
                    if (event.getEntity().getUniqueId().equals(warlordsPlayer.getUuid())) {
                        event.getEntity().setFireTicks(0);
                    }
                }

                @EventHandler
                public void onFireDamage(EntityDamageEvent event) {
                    if (!event.getEntity().getUniqueId().equals(warlordsPlayer.getUuid())) {
                        return;
                    }
                    if (event.getCause() != EntityDamageEvent.DamageCause.FIRE && event.getCause() != EntityDamageEvent.DamageCause.FIRE_TICK) {
                        return;
                    }
                    if (isActiveFire(event.getEntity().getLocation().getBlock())) {
                        event.setCancelled(true);
                        event.getEntity().setFireTicks(0);
                    }
                }

            });

            new GameRunnable(warlordsPlayer.getGame()) {
                @Override
                public void run() {
                    ticksElapsed++;

                    if (warlordsPlayer.isOnline() && !warlordsPlayer.isDead()) {
                        updateTrail(warlordsPlayer);
                        if (warlordsPlayer.getEntity() instanceof Player player && isActiveFire(player.getLocation().getBlock())) {
                            player.setFireTicks(0);
                        }
                    } else {
                        lastPlayerBlock = null;
                    }

                    damageActiveFire(warlordsPlayer);
                    cleanupExpiredFire(warlordsPlayer);

                    if (ticksElapsed % FIRE_DURATION_TICKS == 0) {
                        lastDamagedTicks.entrySet().removeIf(entry -> ticksElapsed - entry.getValue() >= FIRE_DURATION_TICKS);
                    }
                }
            }.runTaskTimer(0, 1);
        }

        private void updateTrail(WarlordsPlayer warlordsPlayer) {
            LocationUtils.LocationBlockHolder currentPlayerBlock = new LocationUtils.LocationBlockHolder(warlordsPlayer.getLocation());
            if (lastPlayerBlock != null && !lastPlayerBlock.equals(currentPlayerBlock)) {
                placeFire(warlordsPlayer, lastPlayerBlock);
            }
            lastPlayerBlock = currentPlayerBlock;
        }

        private void placeFire(WarlordsPlayer warlordsPlayer, LocationUtils.LocationBlockHolder blockHolder) {
            Block block = blockHolder.getBlock();
            if (fireExpireTicks.containsKey(blockHolder)) {
                if (block.getType() == Material.FIRE) {
                    fireExpireTicks.put(blockHolder, ticksElapsed + FIRE_DURATION_TICKS);
                    fireNextDamageTicks.put(blockHolder, ticksElapsed + DAMAGE_INTERVAL_TICKS);
                    return;
                }
                removeFire(warlordsPlayer, blockHolder, false);
            }

            Material originalMaterial = block.getType();
            if (!originalMaterial.isAir() || !block.getRelative(BlockFace.DOWN).getType().isSolid()) {
                return;
            }

            if (!warlordsPlayer.getGame().getPreviousBlocks().containsKey(blockHolder)) {
                warlordsPlayer.getGame().getPreviousBlocks().put(blockHolder, originalMaterial);
                gameTrackedBlocks.add(blockHolder);
            }

            originalMaterials.put(blockHolder, originalMaterial);
            fireExpireTicks.put(blockHolder, ticksElapsed + FIRE_DURATION_TICKS);
            fireNextDamageTicks.put(blockHolder, ticksElapsed + DAMAGE_INTERVAL_TICKS);
            block.setType(Material.FIRE, false);
        }

        private void damageActiveFire(WarlordsPlayer warlordsPlayer) {
            for (Map.Entry<LocationUtils.LocationBlockHolder, Integer> entry : fireNextDamageTicks.entrySet()) {
                if (ticksElapsed < entry.getValue()) {
                    continue;
                }

                LocationUtils.LocationBlockHolder blockHolder = entry.getKey();
                if (!isActiveFire(blockHolder.getBlock())) {
                    continue;
                }

                Location center = blockHolder.getBlock().getLocation().add(0.5, 0.5, 0.5);
                PlayerFilter.entitiesAround(center, 0.75, 1.5, 0.75)
                        .aliveEnemiesOf(warlordsPlayer)
                        .forEach(enemy -> {
                            if (!(enemy instanceof WarlordsNPC)) {
                                return;
                            }
                            int lastDamagedTick = lastDamagedTicks.getOrDefault(enemy.getUuid(), Integer.MIN_VALUE / 2);
                            if (ticksElapsed - lastDamagedTick < DAMAGE_INTERVAL_TICKS) {
                                return;
                            }

                            float damage = enemy.getCurrentHealth() * (CURRENT_HEALTH_DAMAGE_PERCENT / 100f);
                            if (damage <= 0) {
                                return;
                            }

                            enemy.addInstance(InstanceBuilder
                                    .damage()
                                    .cause(getName())
                                    .source(warlordsPlayer)
                                    .value(damage)
                                    .flags(
                                            InstanceFlags.DOT,
                                            InstanceFlags.IGNORE_SOURCE_DAMAGE_BOOST,
                                            InstanceFlags.NO_HEALING_ORBS,
                                            InstanceFlags.NO_HEALING_LEECH,
                                            InstanceFlags.NO_LUST_HEALING,
                                            InstanceFlags.NO_HIT_SOUND
                                    )
                            );
                            lastDamagedTicks.put(enemy.getUuid(), ticksElapsed);
                        });

                entry.setValue(entry.getValue() + DAMAGE_INTERVAL_TICKS);
            }
        }

        private void cleanupExpiredFire(WarlordsPlayer warlordsPlayer) {
            for (LocationUtils.LocationBlockHolder blockHolder : new ArrayList<>(fireExpireTicks.keySet())) {
                if (blockHolder.getBlock().getType() != Material.FIRE) {
                    removeFire(warlordsPlayer, blockHolder, false);
                    continue;
                }
                if (ticksElapsed >= fireExpireTicks.get(blockHolder)) {
                    removeFire(warlordsPlayer, blockHolder, true);
                }
            }
        }

        private void removeFire(WarlordsPlayer warlordsPlayer, LocationUtils.LocationBlockHolder blockHolder, boolean restoreBlock) {
            Block block = blockHolder.getBlock();
            Material originalMaterial = originalMaterials.remove(blockHolder);
            if (restoreBlock && originalMaterial != null && block.getType() == Material.FIRE) {
                block.setType(originalMaterial, false);
            }
            fireExpireTicks.remove(blockHolder);
            fireNextDamageTicks.remove(blockHolder);
            if (gameTrackedBlocks.remove(blockHolder)) {
                warlordsPlayer.getGame().getPreviousBlocks().remove(blockHolder);
            }
        }

        private boolean isActiveFire(Block block) {
            LocationUtils.LocationBlockHolder blockHolder = new LocationUtils.LocationBlockHolder(block.getLocation());
            return block.getType() == Material.FIRE && fireExpireTicks.containsKey(blockHolder);
        }

    }

}
