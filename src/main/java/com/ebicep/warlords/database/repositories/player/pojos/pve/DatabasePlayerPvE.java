package com.ebicep.warlords.database.repositories.player.pojos.pve;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.commands.debugcommands.misc.AdminCommand;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.events.pojos.GameEventReward;
import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.DatabasePlayerPvEEventStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.OnslaughtDatabaseStatInformation;
import com.ebicep.warlords.events.player.PreWeaponSalvageEvent;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.option.pve.onslaught.PouchReward;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairEntry;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager;
import com.ebicep.warlords.pve.events.supplydrop.SupplyDropEntry;
import com.ebicep.warlords.pve.items.ItemsManager;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.pve.quests.Quests;
import com.ebicep.warlords.pve.rewards.types.CompensationReward;
import com.ebicep.warlords.pve.rewards.types.MasterworksFairReward;
import com.ebicep.warlords.pve.rewards.types.PatreonReward;
import com.ebicep.warlords.pve.upgrades.AutoUpgradeProfile;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu;
import com.ebicep.warlords.pve.weapons.weaponaddons.Salvageable;
import com.ebicep.warlords.pve.weapons.weapontypes.StarterWeapon;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabasePlayerPvE extends DatabasePlayerPvEDifficultyStats implements DatabasePlayer {

    //DIFFICULTY STATS
    @Field("easy_stats")
    private DatabasePlayerPvEDifficultyStats easyStats = new DatabasePlayerPvEDifficultyStats();
    @Field("normal_stats")
    private DatabasePlayerPvEDifficultyStats normalStats = new DatabasePlayerPvEDifficultyStats();
    @Field("hard_stats")
    private DatabasePlayerPvEDifficultyStats hardStats = new DatabasePlayerPvEDifficultyStats();
    @Field("endless_stats")
    private DatabasePlayerPvEDifficultyStats endlessStats = new DatabasePlayerPvEDifficultyStats();
    //TODO MOVE ALL ABOVE
    @Field("wave_defense_stats")
    private DatabasePlayerPvEDifficultyStats waveDefenseStats = new DatabasePlayerPvEDifficultyStats();
    @Field("onslaught_stats")
    private OnslaughtDatabaseStatInformation onslaughtStats = new OnslaughtDatabaseStatInformation();
    //EVENTS
    @Field("event_stats")
    private DatabasePlayerPvEEventStats eventStats = new DatabasePlayerPvEEventStats();

    //GENERAL

    //SUPPLY DROP
    @Field("supply_drop_rewards")
    private List<SupplyDropEntry> supplyDropEntries = new ArrayList<>();
    //MASTERWORKS FAIR
    @Field("masterworks_fair_submissions")
    private List<MasterworksFairEntry> masterworksFairEntries = new ArrayList<>();
    //REWARDS
    @Field("masterworks_fair_rewards")
    private List<MasterworksFairReward> masterworksFairRewards = new ArrayList<>();
    @Field("patreon")
    private boolean currentlyPatreon = false;
    @Field("patreon_rewards")
    private List<PatreonReward> patreonRewards = new ArrayList<>();
    @Field("compensation_rewards")
    private List<CompensationReward> compensationRewards = new ArrayList<>();
    @Field("game_event_rewards")
    private List<GameEventReward> gameEventRewards = new ArrayList<>();
    @Field("pouch_rewards")
    private List<PouchReward> pouchRewards = new ArrayList<>();
    //WEAPONS
    @Field("weapon_inventory")
    private List<AbstractWeapon> weaponInventory = new ArrayList<>();
    //ITEMS
    @Field("item_manager")
    private ItemsManager itemsManager = new ItemsManager();

    //CURRENCIES
    private Map<Currencies, Long> currencies = new LinkedHashMap<>() {{
        for (Currencies value : Currencies.VALUES) {
            put(value, 0L);
        }
    }};
    //MOB DROPS
    @Field("mob_drops")
    private Map<MobDrops, Long> mobDrops = new LinkedHashMap<>();
    @Field("completed_tutorial")
    private boolean completedTutorial = false;
    @Field("quests_completed")
    private Map<Quests, Long> questsCompleted = new HashMap<>();
    @Field("auto_upgrade_profiles")
    private Map<Specializations, List<AutoUpgradeProfile>> autoUpgradeProfiles = new HashMap<>();
    @Field("auto_save_upgrade_profile")
    private boolean autoSaveUpgradeProfile;

    @Field("illusion_vendor_rewards_purchased")
    private Map<String, Long> illusionVendorRewardsPurchased = new HashMap<>();

    @Override
    public void updateCustomStats(
            com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer databasePlayer, DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert gamePlayer instanceof DatabaseGamePlayerPvEWaveDefense;

        DatabaseGamePlayerPvEWaveDefense gamePlayerPvE = (DatabaseGamePlayerPvEWaveDefense) gamePlayer;

        //COINS
        addCurrency(Currencies.COIN, gamePlayerPvE.getCoinsGained() * multiplier);
        //GUILDS
        Pair<Guild, GuildPlayer> guildGuildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(gamePlayer.getUuid());
        if (playersCollection == PlayersCollections.LIFETIME && guildGuildPlayerPair != null) {
            Guild guild = guildGuildPlayerPair.getA();
            GuildPlayer guildPlayer = guildGuildPlayerPair.getB();

            guild.addCurrentCoins(gamePlayerPvE.getGuildCoinsGained() * multiplier);
            guild.addExperience(gamePlayerPvE.getGuildExpGained() * multiplier);
            guildPlayer.addCoins(gamePlayerPvE.getGuildCoinsGained() * multiplier);
            guildPlayer.addExperience(gamePlayerPvE.getGuildExpGained() * multiplier);
            guild.queueUpdate();
        }
        //WEAPONS / ITEMS
        List<AbstractWeapon> weaponsFound = gamePlayerPvE.getWeaponsFound();
        List<AbstractItem<?, ?, ?>> itemsFound = gamePlayerPvE.getItemsFound();
        if (multiplier > 0) {
            int maxWeaponInventorySize = currentlyPatreon ? WeaponManagerMenu.MAX_WEAPONS_PATREON : WeaponManagerMenu.MAX_WEAPONS;
            int currentWeaponInventorySize = (int) weaponInventory.stream().filter(abstractWeapon -> !(abstractWeapon instanceof StarterWeapon)).count();
            int weaponsFoundSize = weaponsFound.size();
            int newWeaponInventorySize = currentWeaponInventorySize + weaponsFoundSize;
            if (newWeaponInventorySize >= maxWeaponInventorySize) {
                List<AbstractWeapon> weaponsToKeep = weaponsFound.subList(0, Math.max(0, maxWeaponInventorySize - currentWeaponInventorySize));
                List<AbstractWeapon> weaponsToSalvage = weaponsFound.subList(Math.max(0, maxWeaponInventorySize - currentWeaponInventorySize),
                        weaponsFoundSize
                );
                weaponInventory.addAll(weaponsToKeep);
                for (AbstractWeapon weapon : weaponsToSalvage) {
                    if (weapon instanceof Salvageable) {
                        Salvageable salvageable = (Salvageable) weapon;
                        AtomicInteger randomSalvageAmount = new AtomicInteger(salvageable.getSalvageAmount());
                        Bukkit.getPluginManager().callEvent(new PreWeaponSalvageEvent(randomSalvageAmount));
                        addCurrency(Currencies.SYNTHETIC_SHARD, randomSalvageAmount.get() / 2);
                    }
                }
            } else {
                weaponInventory.addAll(weaponsFound);
            }

            itemsManager.getItemInventory().addAll(itemsFound);

            //QUESTS
            for (Quests quests : gamePlayerPvE.getQuestsCompleted()) {
                if (quests.time == playersCollection || playersCollection == PlayersCollections.LIFETIME) {
                    questsCompleted.merge(quests, 1L, Long::sum);
                    quests.rewards.forEach(this::addCurrency);
                }
            }
        } else {
            if (playersCollection == PlayersCollections.LIFETIME) {
                //need to search by uuid incase weapon got upgraded or changed
                for (AbstractWeapon weapon : weaponsFound) {
                    boolean removed = weaponInventory.removeIf(abstractWeapon -> abstractWeapon.getUUID().equals(weapon.getUUID()));
                    if (!removed && weapon instanceof Salvageable) {
                        MasterworksFair fair = MasterworksFairManager.currentFair;
                        if (!fair.getCommonPlayerEntries().removeIf(entry -> entry.getWeapon().getUUID().equals(weapon.getUUID())) &&
                                !fair.getRarePlayerEntries().removeIf(entry -> entry.getWeapon().getUUID().equals(weapon.getUUID())) &&
                                !fair.getEpicPlayerEntries().removeIf(entry -> entry.getWeapon().getUUID().equals(weapon.getUUID()))
                        ) {
                            ChatChannels.sendDebugMessage((CommandIssuer) null, gamePlayer.getName() + " - Subtracted currency", true);
                            subtractCurrency(Currencies.SYNTHETIC_SHARD,
                                    (((Salvageable) weapon).getMaxSalvageAmount() + ((Salvageable) weapon).getMinSalvageAmount()) / 2
                            );
                        } else {
                            ChatChannels.sendDebugMessage((CommandIssuer) null, gamePlayer.getName() + " - Removed weapon from fair", true);
                        }
                    } else {
                        ChatChannels.sendDebugMessage((CommandIssuer) null, gamePlayer.getName() + " - Removed weapon from inventory", true);
                    }
                }

                for (AbstractItem<?, ?, ?> item : itemsFound) {
                    itemsManager.getItemInventory().removeIf(abstractItem -> abstractItem.getUUID().equals(item.getUUID()));
                }
            }

            //QUESTS
            for (Quests quests : gamePlayerPvE.getQuestsCompleted()) {
                if (quests.time == playersCollection || playersCollection == PlayersCollections.LIFETIME) {
                    questsCompleted.merge(quests, -1L, Long::sum);
                    quests.rewards.forEach(this::subtractCurrency);
                }
            }
        }
        getItemsManager().addBlessingsFound(gamePlayerPvE.getBlessingsFound() * multiplier);

        //SPENDABLE
        addCurrency(Currencies.LEGEND_FRAGMENTS, gamePlayerPvE.getLegendFragmentsGained() * multiplier);
        addCurrency(Currencies.ILLUSION_SHARD, gamePlayerPvE.getIllusionShardGained() * multiplier);
        gamePlayerPvE.getMobDropsGained().forEach((mob, integer) -> addMobDrops(mob, integer * multiplier));


        //UPDATE UNIVERSAL EXPERIENCE
        this.experience += gamePlayer.getExperienceEarnedUniversal() * multiplier;

        //UPDATE CLASS, SPEC
        this.getClass(Specializations.getClass(gamePlayer.getSpec())).updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        this.getSpec(gamePlayer.getSpec()).updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);

        //UPDATE GAME MODE STATS
        if (databaseGame instanceof DatabaseGamePvEEvent) {
            assert gamePlayer instanceof DatabaseGamePlayerPvEEvent;
            eventStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
            addCurrency(
                    ((DatabaseGamePvEEvent) databaseGame).getEvent().currency,
                    Math.min(((DatabaseGamePlayerPvEEvent) gamePlayer).getPoints(), ((DatabaseGamePvEEvent) databaseGame).getPointLimit()) * multiplier
            );
        } else {
            if (GameMode.isWaveDefense(gameMode)) {
                super.updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
                PvEDatabaseStatInformation difficultyStats = getDifficultyStats(((DatabaseGamePvEWaveDefense) databaseGame).getDifficulty());
                if (difficultyStats != null) {
                    difficultyStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
                } else {
                    ChatChannels.sendDebugMessage((CommandIssuer) null, ChatColor.RED + "Error: Difficulty stats is null", true);
                }
            } else if (gameMode == GameMode.ONSLAUGHT) {

            }
        }
    }

    public PvEDatabaseStatInformation getDifficultyStats(DifficultyIndex difficultyIndex) {
        switch (difficultyIndex) {
            case EASY:
                return easyStats;
            case NORMAL:
                return normalStats;
            case HARD:
                return hardStats;
            case ENDLESS:
                return endlessStats;
        }
        return null;
    }

    public void subtractCurrency(Currencies currency, int amount) {
        this.subtractCurrency(currency, (long) amount);
    }

    public DatabasePlayerPvEDifficultyStats getEasyStats() {
        return easyStats;
    }

    public DatabasePlayerPvEDifficultyStats getNormalStats() {
        return normalStats;
    }

    public DatabasePlayerPvEDifficultyStats getHardStats() {
        return hardStats;
    }

    public DatabasePlayerPvEDifficultyStats getEndlessStats() {
        return endlessStats;
    }

    public List<AbstractWeapon> getWeaponInventory() {
        return weaponInventory;
    }

    public ItemsManager getItemsManager() {
        return itemsManager;
    }

    public List<MasterworksFairEntry> getMasterworksFairEntries() {
        return masterworksFairEntries;
    }

    public void addMasterworksFairEntry(MasterworksFairEntry entry) {
        this.masterworksFairEntries.add(entry);
    }

    public List<SupplyDropEntry> getSupplyDropEntries() {
        return supplyDropEntries;
    }

    public void addSupplyDropEntry(SupplyDropEntry entry) {
        if (supplyDropEntries.size() > 25) {
            this.supplyDropEntries = supplyDropEntries.subList(Math.max(supplyDropEntries.size() - 25, 0), supplyDropEntries.size());
        }
        this.supplyDropEntries.add(entry);
    }

    public List<MasterworksFairReward> getMasterworksFairRewards() {
        return masterworksFairRewards;
    }

    public void addReward(MasterworksFairReward reward) {
        this.masterworksFairRewards.add(reward);
    }

    public Long getCurrencyValue(Currencies currency) {
        if (AdminCommand.BYPASSED_PLAYER_CURRENCIES.contains(this)) {
            return Long.MAX_VALUE;
        }
        return this.currencies.getOrDefault(currency, 0L);
    }

    public void addCurrency(Currencies currency, int amount) {
        this.addCurrency(currency, (long) amount);
    }

    public void addOneCurrency(Currencies currency) {
        this.addCurrency(currency, 1L);
    }

    public void addCurrency(Currencies currency, Long amount) {
        if (AdminCommand.BYPASSED_PLAYER_CURRENCIES.contains(this)) {
            return;
        }
        if (!currencies.containsKey(currency)) {
            currencies.put(currency, 0L);
        }
        if (currency == Currencies.MYSTERIOUS_TOKEN) {
            this.currencies.put(currency, Math.min(100, this.currencies.get(currency) + amount));
        } else {
            this.currencies.put(currency, this.currencies.get(currency) + amount);
        }
        CustomScoreboard.reloadPvEScoreboard(this);

        if (amount >= 0) {
            return;
        }
        DatabaseGameEvent gameEvent = null;
        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
        if (currentGameEvent != null && currentGameEvent.getEvent().currency == currency) {
            gameEvent = currentGameEvent;
        } else {
            for (GameEvents events : GameEvents.VALUES) {
                if (events.currency == currency) {
                    if (DatabaseGameEvent.PREVIOUS_GAME_EVENTS.containsKey(events)) {
                        gameEvent = DatabaseGameEvent.PREVIOUS_GAME_EVENTS.get(events);
                    }
                    break;
                }
            }
        }
        if (gameEvent == null) {
            return;
        }
        GameEvents event = gameEvent.getEvent();
        Map<Long, ? extends EventMode> eventsStats = event.eventsStatsFunction.apply(eventStats);
        long epochSecond = gameEvent.getStartDateSecond();
        EventMode eventMode = eventsStats.get(epochSecond);
        if (eventMode == null) {
            ChatUtils.MessageTypes.GAME_EVENTS.sendMessage("Unable to add currency: " + currency.name + ". No event mode found for " + event.name + "(" + epochSecond + ")");
            return;
        }
        //event
        this.eventStats.addEventPointsSpent(-amount);
        //event mode
        EventMode generalEventMode = event.generalEventFunction.apply(eventStats);
        generalEventMode.addEventPointsSpent(-amount);
        //event in event mode
        eventMode.addEventPointsSpent(-amount);
    }

    public void subtractOneCurrency(Currencies currency) {
        this.subtractCurrency(currency, 1L);
    }

    public void subtractCurrency(Currencies currency, Long amount) {
        this.addCurrency(currency, -amount);
    }

    public boolean isCompletedTutorial() {
        return completedTutorial;
    }

    public void setCompletedTutorial(boolean completedTutorial) {
        this.completedTutorial = completedTutorial;
    }

    public Map<Quests, Long> getQuestsCompleted() {
        return questsCompleted;
    }

    public List<PatreonReward> getPatreonRewards() {
        return patreonRewards;
    }

    public List<CompensationReward> getCompensationRewards() {
        return compensationRewards;
    }

    public List<GameEventReward> getGameEventRewards() {
        return gameEventRewards;
    }

    public List<PouchReward> getPouchRewards() {
        return pouchRewards;
    }

    public Map<Specializations, List<AutoUpgradeProfile>> getAutoUpgradeProfiles() {
        return autoUpgradeProfiles;
    }

    public boolean isAutoSaveUpgradeProfile() {
        return autoSaveUpgradeProfile;
    }

    public void setAutoSaveUpgradeProfile(boolean autoSaveUpgradeProfile) {
        this.autoSaveUpgradeProfile = autoSaveUpgradeProfile;
    }

    public DatabasePlayerPvEEventStats getEventStats() {
        return eventStats;
    }

    public long getMobDrops(MobDrops mobDrops) {
        if (AdminCommand.BYPASSED_PLAYER_CURRENCIES.contains(this)) {
            return Long.MAX_VALUE;
        }
        return this.mobDrops.getOrDefault(mobDrops, 0L);
    }

    public Map<MobDrops, Long> getMobDrops() {
        return mobDrops;
    }

    public void addMobDrops(MobDrops mobDrops, long amount) {
        if (AdminCommand.BYPASSED_PLAYER_CURRENCIES.contains(this)) {
            return;
        }
        if (!this.mobDrops.containsKey(mobDrops)) {
            this.mobDrops.put(mobDrops, amount);
        } else {
            this.mobDrops.put(mobDrops, this.mobDrops.get(mobDrops) + amount);
        }
    }

    public boolean isCurrentlyPatreon() {
        return currentlyPatreon;
    }

    public void setCurrentlyPatreon(boolean currentlyPatreon) {
        this.currentlyPatreon = currentlyPatreon;
    }

    public Map<String, Long> getIllusionVendorRewardsPurchased() {
        return illusionVendorRewardsPurchased;
    }
}
