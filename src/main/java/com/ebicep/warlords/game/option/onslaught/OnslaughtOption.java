package com.ebicep.warlords.game.option.onslaught;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsAddCurrencyFinalEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.PveOption;
import com.ebicep.warlords.game.option.WeaponOption;
import com.ebicep.warlords.game.option.cuboid.BoundingBoxOption;
import com.ebicep.warlords.game.option.marker.SpawnLocationMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.option.wavedefense.waves.Wave;
import com.ebicep.warlords.game.option.wavedefense.waves.WaveList;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.commands.MobCommand;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.AutoUpgradeProfile;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class OnslaughtOption implements Option, PveOption {

    private Game game;
    private final Team team;
    private Wave currentMobSet;
    private final WaveList mobSet;
    private final AtomicInteger ticksElapsed = new AtomicInteger(0);
    private final ConcurrentHashMap<AbstractMob<?>, Integer> mobs = new ConcurrentHashMap<>();
    private int spawnCount = 0;
    private int spawnLimit;
    private Location lastLocation;
    private float integrityCounter = 100;

    public OnslaughtOption(Team team, WaveList waves) {
        this.team = team;
        this.mobSet = waves;
        this.currentMobSet = this.mobSet.getWave(0, new Random());
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        for (Option o : game.getOptions()) {
            if (o instanceof BoundingBoxOption) {
                BoundingBoxOption boundingBoxOption = (BoundingBoxOption) o;
                lastLocation = boundingBoxOption.getCenter();
            }
        }

        game.registerEvents(new Listener() {

            @EventHandler
            public void onEvent(WarlordsDamageHealingEvent event) {
                WarlordsEntity attacker = event.getAttacker();
                WarlordsEntity receiver = event.getPlayer();

                if (event.isDamageInstance()) {
                    if (attacker instanceof WarlordsNPC) {
                        AbstractMob<?> mob = ((WarlordsNPC) attacker).getMob();
                        if (mobs.containsKey(mob)) {
                            mob.onAttack(attacker, receiver, event);
                        }
                    }

                    if (receiver instanceof WarlordsNPC) {
                        AbstractMob<?> mob = ((WarlordsNPC) receiver).getMob();
                        if (mobs.containsKey(mob)) {
                            mob.onDamageTaken(receiver, attacker, event);
                        }
                    }
                }
            }

            @EventHandler
            public void onEvent(WarlordsDeathEvent event) {
                WarlordsEntity we = event.getPlayer();
                WarlordsEntity killer = event.getKiller();

                if (we instanceof WarlordsNPC) {
                    AbstractMob<?> mobToRemove = ((WarlordsNPC) we).getMob();
                    if (mobs.containsKey(mobToRemove)) {
                        mobToRemove.onDeath(killer, we.getDeathLocation(), OnslaughtOption.this);
                        new GameRunnable(game) {
                            @Override
                            public void run() {
                                integrityCounter += 1;
                                if (integrityCounter >= 100) {
                                    integrityCounter = 100;
                                }
                                spawnCount--;
                                mobs.remove(mobToRemove);
                                game.getPlayers().remove(we.getUuid());
                                Warlords.removePlayer(we.getUuid());
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
                if (mobs.keySet().stream().noneMatch(abstractMob -> Objects.equals(abstractMob.getEntity(), entityLiving))) {
                    return;
                }
                if (entityLiving instanceof EntityInsentient) {
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
                                    .ifPresent(wp -> {
                                        if (!(wp.getEntity() instanceof Player)) {
                                            event.setTarget(wp.getEntity());
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
        });

        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(5, "percentage") {
            @Nonnull
            @Override
            public List<String> computeLines(@Nullable WarlordsPlayer player) {
                return Collections.singletonList("Difficulty: " + currentMobSet.getMessage());
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(5, "percentage") {
            @Nonnull
            @Override
            public List<String> computeLines(@Nullable WarlordsPlayer player) {
                return Collections.singletonList(integrityScoreboard());
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(6, "kills") {
            @Nonnull
            @Override
            public List<String> computeLines(@Nullable WarlordsPlayer player) {
                return healthScoreboard(game);
            }
        });
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer) {
            player.setInPve(true);
            if (player.getEntity() instanceof Player) {
                game.setPlayerTeam((OfflinePlayer) player.getEntity(), Team.BLUE);
                player.setTeam(Team.BLUE);
                player.updateArmor();
            }

            DatabaseManager.getPlayer(player.getUuid(), databasePlayer -> {
                Optional<AbstractWeapon> optionalWeapon = databasePlayer
                        .getPveStats()
                        .getWeaponInventory()
                        .stream()
                        .filter(AbstractWeapon::isBound)
                        .filter(abstractWeapon -> abstractWeapon.getSpecializations() == player.getSpecClass())
                        .findFirst();
                optionalWeapon.ifPresent(abstractWeapon -> {
                    WarlordsPlayer wp = (WarlordsPlayer) player;

                    ((WarlordsPlayer) player).getCosmeticSettings().setWeaponSkin(abstractWeapon.getSelectedWeaponSkin());
                    wp.setWeapon(abstractWeapon);
                    abstractWeapon.applyToWarlordsPlayer(wp);
                    player.updateEntity();
                    player.getSpec().updateCustomStats();
                });
            });
        }
    }

    @Override
    public void start(@Nonnull Game game) {
        if (DatabaseManager.guildService != null) {
            HashMap<Guild, HashSet<UUID>> guilds = new HashMap<>();
            List<UUID> uuids = game.playersWithoutSpectators().map(Map.Entry::getKey).collect(Collectors.toList());
            for (Guild guild : GuildManager.GUILDS) {
                for (UUID uuid : uuids) {
                    Optional<GuildPlayer> guildPlayer = guild.getPlayerMatchingUUID(uuid);
                    if (guildPlayer.isPresent() && guildPlayer.get().getJoinDate().isBefore(Instant.now().minus(2, ChronoUnit.DAYS))) {
                        guilds.computeIfAbsent(guild, k -> new HashSet<>()).add(uuid);
                    }
                }
            }
            System.out.println(guilds);
            guilds.forEach((guild, validUUIDs) -> {
                for (AbstractGuildUpgrade<?> upgrade : guild.getUpgrades()) {
                    System.out.println("Upgrading " + upgrade.getUpgrade().getName() + " for " + validUUIDs.size() + " players");
                    upgrade.getUpgrade().onGame(game, validUUIDs, upgrade.getTier());
                }
            });
        }

        new GameRunnable(game) {
            int counter = 0;
            @Override
            public void run() {
                ticksElapsed.getAndIncrement();
                counter++;
                if (counter % 20 == 0) {
                    integrityCounter -= getIntegrityDecay((int) game.warlordsPlayers().count());
                }

                if (integrityCounter <= 0) {
                    getGame().setNextState(new EndState(game, null));
                    this.cancel();
                }

                for (AbstractMob<?> mob : new ArrayList<>(mobs.keySet())) {
                    mob.whileAlive(mobs.get(mob) - ticksElapsed.get(), OnslaughtOption.this);
                }
            }
        }.runTaskTimer(10 * GameRunnable.SECOND, 0);

        new GameRunnable(game) {
            WarlordsEntity lastSpawn = null;

            @Override
            public void run() {
                if (game.getState() instanceof EndState) {
                    this.cancel();
                    return;
                }

                if (spawnCount >= getSpawnLimit(playerCount())) {
                    return;
                }

                if (lastSpawn == null) {
                    lastSpawn = spawn(getSpawnLocation(null));
                } else {
                    lastSpawn = spawn(getSpawnLocation(lastSpawn));
                }
                lastSpawn.getLocation(lastLocation);

                spawnCount++;
            }

            public WarlordsEntity spawn(Location loc) {
                currentMobSet = mobSet.getWave((game.getState().getTicksElapsed() / 20) / 60, new Random());
                AbstractMob<?> abstractMob = currentMobSet.spawnRandomMonster(loc);
                mobs.put(abstractMob, ticksElapsed.get());
                WarlordsNPC wpc = abstractMob.toNPC(game, team, UUID.randomUUID(), OnslaughtOption.this::modifyStats);
                Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, abstractMob));
                return wpc;
            }

            private Location getSpawnLocation(WarlordsEntity entity) {
                List<Location> candidates = new ArrayList<>();
                double priority = Double.NEGATIVE_INFINITY;
                for (SpawnLocationMarker marker : getGame().getMarkers(SpawnLocationMarker.class)) {
                    if (entity == null) {
                        return marker.getLocation();
                    }
                    if (candidates.isEmpty()) {
                        candidates.add(marker.getLocation());
                        priority = marker.getPriority(entity);
                    } else {
                        double newPriority = marker.getPriority(entity);
                        if (newPriority >= priority) {
                            if (newPriority > priority) {
                                candidates.clear();
                                priority = newPriority;
                            }
                            candidates.add(marker.getLocation());
                        }
                    }
                }
                if (!candidates.isEmpty()) {
                    return candidates.get((int) (Math.random() * candidates.size()));
                }
                return lastLocation;
            }

        }.runTaskTimer(10 * GameRunnable.SECOND, 6);
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer wp, Player player) {
        AbstractWeapon weapon = wp.getWeapon();
        if (weapon == null) {
            WeaponOption.showWeaponStats(wp, player);
        } else {
            WeaponOption.showPvEWeapon(wp, player);
        }

        player.getInventory().setItem(7, new ItemBuilder(Material.GOLD_NUGGET).name(ChatColor.GREEN + "Upgrade Talisman").get());
        if (wp.getWeapon() instanceof AbstractLegendaryWeapon) {
            ((AbstractLegendaryWeapon) wp.getWeapon()).updateAbilityItem(wp, player);
        }
    }

    @Override
    public void onSpecChange(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer) {
            ((WarlordsPlayer) player).resetAbilityTree();
        }
    }

    private List<String> healthScoreboard(Game game) {
        List<String> list = new ArrayList<>();
        for (WarlordsEntity we : PlayerFilter.playingGame(game).filter(e -> e instanceof WarlordsPlayer)) {
            float healthRatio = we.getHealth() / we.getMaxHealth();
            ChatColor healthColor;
            String name = we.getName();
            String newName;

            if (healthRatio >= .5) {
                healthColor = ChatColor.GREEN;
            } else if (healthRatio >= .25) {
                healthColor = ChatColor.YELLOW;
            } else {
                healthColor = ChatColor.RED;
            }

            if (name.length() >= 8) {
                newName = name.substring(0, 8);
            } else {
                newName = name;
            }

            list.add(newName + ": " + (we.isDead() ? ChatColor.DARK_RED + "DEAD" : healthColor +
                    "❤ " + (int) we.getHealth()) +
                    ChatColor.RESET + " / " +
                    ChatColor.RED + "⚔ " + we.getMinuteStats()
                        .total()
                        .getKills());
        }

        return list;
    }

    private String integrityScoreboard() {
        ChatColor color;
        if (integrityCounter >= 50) {
            color = ChatColor.AQUA;
        } else if (integrityCounter >= 25) {
            color = ChatColor.GOLD;
        } else {
            color = ChatColor.RED;
        }

        return "Soul Energy: " + color + (Math.round(integrityCounter) + "%");
    }

    @Override
    public void spawnNewMob(AbstractMob<?> mob) {
        mob.toNPC(game, Team.RED, UUID.randomUUID(), this::modifyStats);
        game.addNPC(mob.getWarlordsNPC());
        mobs.put(mob, ticksElapsed.get());
        Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, mob));
    }

    @Override
    public Set<AbstractMob<?>> getMobs() {
        return mobs.keySet();
    }

    @Override
    public Game getGame() {
        return game;
    }

    public int getSpawnLimit(int playerCount) {
        switch (playerCount) {
            case 1:
                return 5;
            case 2:
                return 10;
            case 3:
                return 15;
            case 4:
                return 20;
            case 5:
                return 25;
            case 6:
                return 30;
        }

        return spawnLimit;
    }

    public void setSpawnLimit(int spawnLimit) {
        this.spawnLimit = spawnLimit;
    }

    public float getIntegrityDecay(int playerCount) {
        switch (playerCount) {
            case 1:
                return 0.5f;
            case 2:
                return 0.75f;
            case 3:
                return 1;
            case 4:
                return 1.5f;
            case 5:
            case 6:
                return 2;
        }

        return 1.5f;
    }

    private void modifyStats(WarlordsNPC warlordsNPC) {
        warlordsNPC.getMob().onSpawn(OnslaughtOption.this);
        /*
         * Base scale of 900
         *
         * The higher the scale is the longer it takes to increase per interval.
         */
        double scale = 900.0;
        long playerCount = game.warlordsPlayers().count();
        // Flag check whether mob is a boss.
        boolean bossFlagCheck = playerCount > 1 && warlordsNPC.getMobTier() == MobTier.BOSS;
        // Reduce base scale by 75 for each player after 2 or more players in game instance.
        double modifiedScale = scale - (playerCount > 1 ? (50 * playerCount) : 0);
        // Divide scale based on game time.
        double modifier = (game.getState().getTicksElapsed() / 1000f) / modifiedScale + 1;

        // Multiply health & min/max melee damage by waveCounter + 1 ^ base damage.
        int minMeleeDamage = (int) Math.pow(warlordsNPC.getMinMeleeDamage(), modifier);
        int maxMeleeDamage = (int) Math.pow(warlordsNPC.getMaxMeleeDamage(), modifier);
        float health = (float) Math.pow(warlordsNPC.getMaxBaseHealth(), modifier);
        // Increase boss health by 25% for each player in game instance.
        float bossMultiplier = 1 + (0.25f * playerCount);

        // Final health value after applying all modifiers.
        float finalHealth = health * (bossFlagCheck ? bossMultiplier : 1);
        warlordsNPC.setMaxBaseHealth(finalHealth);
        warlordsNPC.setMaxHealth(finalHealth);
        warlordsNPC.setHealth(finalHealth);

        warlordsNPC.setMinMeleeDamage(minMeleeDamage);
        warlordsNPC.setMaxMeleeDamage(maxMeleeDamage);
    }

    @Override
    public int playerCount() {
        return (int) game.warlordsPlayers().count();
    }
}
