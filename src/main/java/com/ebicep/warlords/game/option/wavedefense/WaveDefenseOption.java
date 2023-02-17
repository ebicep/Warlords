package com.ebicep.warlords.game.option.wavedefense;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.game.pve.WarlordsGameWaveClearEvent;
import com.ebicep.warlords.events.game.pve.WarlordsGameWaveEditEvent;
import com.ebicep.warlords.events.game.pve.WarlordsGameWaveRespawnEvent;
import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsAddCurrencyFinalEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveMobDropEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveWeaponEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.PveOption;
import com.ebicep.warlords.game.option.WeaponOption;
import com.ebicep.warlords.game.option.cuboid.BoundingBoxOption;
import com.ebicep.warlords.game.option.marker.SpawnLocationMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
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
import com.ebicep.warlords.pve.DifficultyIndex;
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
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.ebicep.warlords.util.chat.ChatUtils.sendMessage;
import static com.ebicep.warlords.util.warlords.Utils.iterable;


public class WaveDefenseOption implements Option, PveOption {
    private static final int SCOREBOARD_PRIORITY = 5;
    SimpleScoreboardHandler scoreboard;
    private final ConcurrentHashMap<AbstractMob<?>, Integer> mobs = new ConcurrentHashMap<>();
    private final Team team;
    private final WaveList waves;
    private final DifficultyIndex difficulty;
    private final int maxWave;
    private final WaveDefenseStats waveDefenseStats = new WaveDefenseStats();
    private final AtomicInteger ticksElapsed = new AtomicInteger(0);
    private int waveCounter = 0;
    private int spawnCount = 0;
    private Wave currentWave;
    @Nonnull
    private Game game;
    private Location lastLocation = new Location(null, 0, 0, 0);
    @Nullable
    private BukkitTask spawner;
    private BiFunction<WaveDefenseOption, WarlordsPlayer, List<String>> waveSidebarOverride = null;

    public WaveDefenseOption(Team team, WaveList waves, DifficultyIndex difficulty) {
        this.team = team;
        this.waves = waves;
        this.difficulty = difficulty;
        this.maxWave = difficulty.getMaxWaves();
    }

    public WaveDefenseOption(
            Team team,
            WaveList waves,
            DifficultyIndex difficulty,
            BiFunction<WaveDefenseOption, WarlordsPlayer, List<String>> waveSidebarOverride
    ) {
        this.team = team;
        this.waves = waves;
        this.difficulty = difficulty;
        this.maxWave = difficulty.getMaxWaves();
        this.waveSidebarOverride = waveSidebarOverride;
    }

    @Override
    public void register(Game game) {
        this.game = game;
        for (Option o : game.getOptions()) {
            if (o instanceof BoundingBoxOption) {
                BoundingBoxOption boundingBoxOption = (BoundingBoxOption) o;
                lastLocation = boundingBoxOption.getCenter();
            }
        }
        CoinGainOption coinGainOption = game
                .getOptions()
                .stream()
                .filter(CoinGainOption.class::isInstance)
                .map(CoinGainOption.class::cast)
                .findAny()
                .orElse(null);

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
            public void onFinalDamageHeal(WarlordsDamageHealingFinalEvent event) {
                WarlordsEntity attacker = event.getAttacker();
                waveDefenseStats.getPlayerWaveDefenseStats(attacker.getUuid())
                                .getWaveDamage()
                                .merge(waveCounter, (long) event.getValue(), Long::sum);
            }

            @EventHandler
            public void onEvent(WarlordsDeathEvent event) {
                WarlordsEntity we = event.getPlayer();
                WarlordsEntity killer = event.getKiller();

                if (we instanceof WarlordsNPC) {
                    AbstractMob<?> mobToRemove = ((WarlordsNPC) we).getMob();
                    if (mobs.containsKey(mobToRemove)) {
                        mobToRemove.onDeath(killer, we.getDeathLocation(), WaveDefenseOption.this);
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

                        if (coinGainOption == null) {
                            return;
                        }
                        if (coinGainOption.getMobCoinValues()
                                          .values()
                                          .stream()
                                          .anyMatch(stringLongLinkedHashMap -> stringLongLinkedHashMap.containsKey(mobToRemove.getName()))
                        ) {
                            waveDefenseStats.getMobsKilled().merge(mobToRemove.getName(), 1L, Long::sum);
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
            public void onWeaponDrop(WarlordsGiveWeaponEvent event) {
                waveDefenseStats.getPlayerWaveDefenseStats(event.getPlayer().getUuid())
                                .getWeaponsFound()
                                .add(event.getWeapon());
            }

            @EventHandler
            public void onMobDrop(WarlordsGiveMobDropEvent event) {
                waveDefenseStats.getPlayerWaveDefenseStats(event.getPlayer().getUuid())
                                .getMobDropsGained()
                                .merge(event.getMobDrop(), 1L, Long::sum);
            }

            @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
            public void onWin(WarlordsGameTriggerWinEvent event) {
                waveDefenseStats.cacheBaseCoinSummary(WaveDefenseOption.this);
                waveDefenseStats.storeWeaponFragmentGain(WaveDefenseOption.this);
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
            public void onNewWave(WarlordsGameWaveClearEvent event) {
                WarlordsGameWaveRespawnEvent respawnEvent = new WarlordsGameWaveRespawnEvent(game);
                Bukkit.getPluginManager().callEvent(respawnEvent);
                if (respawnEvent.isCancelled()) {
                    return;
                }
                game.warlordsPlayers().forEach(warlordsPlayer -> {
                    if (warlordsPlayer.isDead()) {
                        warlordsPlayer.respawn();
                    }
                });
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
                    //break;
                }
            }

        });
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(SCOREBOARD_PRIORITY, "wave") {
            @Nonnull
            @Override
            public List<String> computeLines(@Nullable WarlordsPlayer player) {
                if (waveSidebarOverride != null) {
                    return waveSidebarOverride.apply(WaveDefenseOption.this, player);
                }
                return Collections.singletonList("Wave: " + ChatColor.GREEN + waveCounter + ChatColor.RESET + (maxWave != Integer.MAX_VALUE ? "/" + ChatColor.GREEN + maxWave : "") + ChatColor.RESET + (currentWave != null && currentWave.getMessage() != null ? " (" + currentWave.getMessage() + ")" : ""));
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(SCOREBOARD_PRIORITY, "wave") {
            @Nonnull
            @Override
            public List<String> computeLines(@Nullable WarlordsPlayer player) {
                return Collections.singletonList("Monsters Left: " + (spawnCount > 0 || ticksElapsed.get() < 10 ? ChatColor.RED : ChatColor.GREEN) + mobs.size());
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(6, "kills") {
            @Nonnull
            @Override
            public List<String> computeLines(@Nullable WarlordsPlayer player) {
                return healthScoreboard(game);
            }
        });

        new TimerSkipAbleMarker() {
            @Override
            public int getDelay() {
                return 0;
            }

            @Override
            public void skipTimer(int delay) {
                newWave();
                Bukkit.getPluginManager().callEvent(new WarlordsGameWaveEditEvent(game, waveCounter - 1));
            }

        }.register(game);
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

            @Override
            public void run() {
                if (game.getState() instanceof EndState) {
                    this.cancel();
                    return;
                }

                if (mobs.isEmpty() && spawnCount == 0) {
                    newWave();

                    if (waveCounter > 1) {
                        Bukkit.getPluginManager().callEvent(new WarlordsGameWaveClearEvent(game, waveCounter - 1));
                    }

                    if (difficulty == DifficultyIndex.ENDLESS) {
                        switch (waveCounter) {
                            case 50:
                            case 100:
                                getGame().forEachOnlineWarlordsPlayer(wp -> {
                                    wp.getAbilityTree().setMaxMasterUpgrades(wp.getAbilityTree().getMaxMasterUpgrades() + 1);
                                    wp.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "+1 Master Upgrade");
                                });
                                break;
                        }
                    }
                }

                for (AbstractMob<?> mob : new ArrayList<>(mobs.keySet())) {
                    mob.whileAlive(mobs.get(mob) - ticksElapsed.get(), WaveDefenseOption.this);
                }

                //check every 10 seconds for mobs in void
                if (ticksElapsed.get() % 200 == 0) {
                    for (SpawnLocationMarker marker : getGame().getMarkers(SpawnLocationMarker.class)) {
                        Location location = marker.getLocation();
                        for (AbstractMob<?> mob : new ArrayList<>(mobs.keySet())) {
                            if (mob.getWarlordsNPC().getLocation().getY() < -50) {
                                mob.getWarlordsNPC().teleport(location);
                            }
                        }
                        break;
                    }
                }

                ticksElapsed.getAndIncrement();
            }
        }.runTaskTimer(20, 0);
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
                    WarlordsPlayer warlordsPlayer = (WarlordsPlayer) player;

                    ((WarlordsPlayer) player).getCosmeticSettings().setWeaponSkin(abstractWeapon.getSelectedWeaponSkin());
                    warlordsPlayer.setWeapon(abstractWeapon);
                    abstractWeapon.applyToWarlordsPlayer(warlordsPlayer);
                    player.updateEntity();
                    player.getSpec().updateCustomStats();
                });
            });
        }
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        AbstractWeapon weapon = warlordsPlayer.getWeapon();
        if (weapon == null) {
            WeaponOption.showWeaponStats(warlordsPlayer, player);
        } else {
            WeaponOption.showPvEWeapon(warlordsPlayer, player);
        }

        player.getInventory().setItem(7, new ItemBuilder(Material.GOLD_NUGGET).name(ChatColor.GREEN + "Upgrade Talisman").get());
        if (warlordsPlayer.getWeapon() instanceof AbstractLegendaryWeapon) {
            ((AbstractLegendaryWeapon) warlordsPlayer.getWeapon()).updateAbilityItem(warlordsPlayer, player);
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

            list.add(newName + ": " + (we.isDead() ? ChatColor.DARK_RED + "DEAD" : healthColor + "❤ " + (int) we.getHealth()) + ChatColor.RESET + " / " + ChatColor.RED + "⚔ " + we.getMinuteStats()
                                                                                                                                                                                   .total()
                                                                                                                                                                                   .getKills());
        }

        return list;
    }

    public void newWave() {
        if (currentWave != null) {
            String message;
            if (currentWave.getMessage() != null) {
                message = ChatColor.GREEN + "Wave complete! (" + currentWave.getMessage() + ")";
            } else {
                message = ChatColor.GREEN + "Wave complete!";
            }

            for (Map.Entry<Player, Team> entry : iterable(game.onlinePlayers())) {
                sendMessage(entry.getKey(), false, message);
                entry.getKey().playSound(entry.getKey().getLocation(), Sound.LEVEL_UP, 500, 2);
            }
        }
        waveCounter++;
        currentWave = waves.getWave(waveCounter, new Random());
        spawnCount = currentWave.getMonsterCount();
        int spawns = spawnCount;
        spawns *= getSpawnCountMultiplier((int) game.warlordsPlayers().count());

        for (Map.Entry<Player, Team> entry : iterable(game.onlinePlayers())) {
            if (currentWave.getMessage() != null) {
                sendMessage(entry.getKey(),
                        false,
                        ChatColor.YELLOW + "A boss will spawn in §c" + currentWave.getDelay() / 20 + " §eseconds!"
                );
            } else {
                sendMessage(entry.getKey(),
                        false,
                        ChatColor.YELLOW + "A wave of §c§l" + spawns + "§e monsters will spawn in §c" + currentWave.getDelay() / 20 + " §eseconds!"
                );
            }

            float soundPitch = 0.8f;
            String wavePrefix = "§eWave ";
            if (waveCounter >= 10) {
                soundPitch = 0.75f;
                wavePrefix = "§eWave ";
            }
            if (waveCounter >= 25) {
                soundPitch = 0.7f;
                wavePrefix = "§6Wave ";
            }
            if (waveCounter >= 50) {
                soundPitch = 0.65f;
                wavePrefix = "§7Wave ";
            }
            if (waveCounter >= 60) {
                soundPitch = 0.5f;
                wavePrefix = "§8§lWave ";
            }
            if (waveCounter >= 70) {
                soundPitch = 0.4f;
                wavePrefix = "§d§lWave ";
            }
            if (waveCounter >= 80) {
                soundPitch = 0.3f;
                wavePrefix = "§5§lWave ";
            }
            if (waveCounter >= 90) {
                soundPitch = 0.2f;
                wavePrefix = "§5W§5§k§la§5§lve ";
            }
            if (waveCounter >= 100) {
                soundPitch = 0.1f;
                wavePrefix = "§4W§4§k§la§4§lve ";
            }
            if (waveCounter >= 101) {
                wavePrefix = "§0W§0§k§la§0§lv§0§k§le§4§l ";
            }

            entry.getKey().playSound(entry.getKey().getLocation(), Sound.WITHER_SPAWN, 500, soundPitch);
            PacketUtils.sendTitle(entry.getKey(), wavePrefix + waveCounter, "", 20, 60, 20);
        }
        startSpawnTask();
    }

    public float getSpawnCountMultiplier(int playerCount) {
        switch (playerCount) {
            case 2:
                return 1.25f;
            case 3:
            case 4:
                return 1.5f;
            case 5:
            case 6:
                return 2;
            case 7:
            case 8:
                return 2.5f;
        }

        return 1;
    }

    public void startSpawnTask() {
        if (spawner != null) {
            spawner.cancel();
            spawner = null;
        }

        if (spawnCount == 0) {
            return;
        }

        if (currentWave.getMessage() == null) {
            spawnCount *= getSpawnCountMultiplier((int) game.warlordsPlayers().count());
        }

        spawner = new GameRunnable(game) {
            WarlordsEntity lastSpawn = null;
            int counter = 0;

            @Override
            public void run() {
                if (game.getState() instanceof EndState) {
                    this.cancel();
                    return;
                }

                counter++;
                if (lastSpawn == null) {
                    lastSpawn = spawn(getSpawnLocation(null));
                } else {
                    lastSpawn = spawn(getSpawnLocation(lastSpawn));
                }
                lastSpawn.getLocation(lastLocation);

                spawnCount--;
                if (spawnCount <= 0) {
                    spawner.cancel();
                    spawner = null;
                }
            }

            public WarlordsEntity spawn(Location loc) {
                AbstractMob<?> abstractMob = currentWave.spawnRandomMonster(loc);
                mobs.put(abstractMob, ticksElapsed.get());
                WarlordsNPC npc = abstractMob.toNPC(game, team, UUID.randomUUID(), WaveDefenseOption.this::modifyStats);
                Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, abstractMob));
                return npc;
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

        }.runTaskTimer(currentWave.getDelay(), 8);
    }

    private void modifyStats(WarlordsNPC warlordsNPC) {
        warlordsNPC.getMob().onSpawn(WaveDefenseOption.this);

        boolean isEndless = difficulty == DifficultyIndex.ENDLESS;
        /*
         * Base scale of 600
         *
         * The higher the scale is the longer it takes to increase per interval.
         */
        double scale = isEndless ? 1200.0 : 600.0;
        long playerCount = game.warlordsPlayers().count();
        // Flag check whether mob is a boss.
        boolean bossFlagCheck = playerCount > 1 && warlordsNPC.getMobTier() == MobTier.BOSS;
        // Reduce base scale by 75/100 for each player after 2 or more players in game instance.
        double modifiedScale = scale - (playerCount > 1 ? (isEndless ? 100 : 75) * playerCount : 0);
        // Divide scale based on wave count.
        double modifier = waveCounter / modifiedScale + 1;

        // Multiply health & min/max melee damage by waveCounter + 1 ^ base damage.
        int minMeleeDamage = (int) Math.pow(warlordsNPC.getMinMeleeDamage(), modifier);
        int maxMeleeDamage = (int) Math.pow(warlordsNPC.getMaxMeleeDamage(), modifier);
        float health = (float) Math.pow(warlordsNPC.getMaxBaseHealth(), modifier);
        // Increase boss health by 25% for each player in game instance.
        float bossMultiplier = 1 + (0.25f * playerCount);

        // Multiply damage/health by given difficulty.
        float difficultyMultiplier;
        switch (difficulty) {
            case EASY:
                difficultyMultiplier = 0.75f;
                break;
            case HARD:
                difficultyMultiplier = 1.5f;
                break;
            default:
                difficultyMultiplier = 1;
                break;
        }

        // Final health value after applying all modifiers.
        float finalHealth = (health * difficultyMultiplier) * (bossFlagCheck ? bossMultiplier : 1);
        warlordsNPC.setMaxBaseHealth(finalHealth);
        warlordsNPC.setMaxHealth(finalHealth);
        warlordsNPC.setHealth(finalHealth);

        int endlessFlagCheckMin = isEndless ? minMeleeDamage : (int) (warlordsNPC.getMinMeleeDamage() * difficultyMultiplier);
        int endlessFlagCheckMax = isEndless ? maxMeleeDamage : (int) (warlordsNPC.getMaxMeleeDamage() * difficultyMultiplier);
        warlordsNPC.setMinMeleeDamage(endlessFlagCheckMin);
        warlordsNPC.setMaxMeleeDamage(endlessFlagCheckMax);
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
    public int getWaveCounter() {
        return waveCounter;
    }

    public void setWaveCounter(int waveCounter) {
        this.waveCounter = waveCounter - 1;
        newWave();
    }

    public int getWavesCleared() {
        if (waveCounter <= 1) {
            return 0;
        }
        return waveCounter - 1;
    }

    public Wave getCurrentWave() {
        return currentWave;
    }

    public WaveList getWaves() {
        return waves;
    }

    @Override
    @Nonnull
    public Game getGame() {
        return game;
    }

    public int getMaxWave() {
        return maxWave;
    }

    public int getSpawnCount() {
        return spawnCount;
    }

    public void setSpawnCount(int spawnCount) {
        this.spawnCount = spawnCount;
    }

    public WaveDefenseStats getWaveDefenseStats() {
        return waveDefenseStats;
    }

    @Override
    public int playerCount() {
        return (int) game.warlordsPlayers().count();
    }

    @Override
    public DifficultyIndex getDifficulty() {
        return difficulty;
    }
}
