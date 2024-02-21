package com.ebicep.warlords.database.repositories.player.pojos.pve;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.commands.debugcommands.misc.AdminCommand;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.events.pojos.GameEventReward;
import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePlayerPvEOnslaught;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePvEOnslaught;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.MultiStats;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.DatabasePlayerPvEEventStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.DatabasePlayerOnslaughtStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.DatabasePlayerPvEWaveDefenseDifficultyStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.DatabasePlayerWaveDefenseStats;
import com.ebicep.warlords.events.player.AddCurrencyEvent;
import com.ebicep.warlords.events.player.WeaponSalvageEvent;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.option.pve.onslaught.PouchReward;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairEntry;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager;
import com.ebicep.warlords.pve.events.supplydrop.SupplyDropEntry;
import com.ebicep.warlords.pve.items.ItemsManager;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.quests.Quests;
import com.ebicep.warlords.pve.rewards.types.BountyReward;
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
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabasePlayerPvE implements MultiStats<DatabaseGamePvEBase, DatabaseGamePlayerPvEBase> {

    @Transient
    private DatabasePlayer databasePlayer;

    @Field("wave_defense_stats")
    private DatabasePlayerWaveDefenseStats waveDefenseStats = new DatabasePlayerWaveDefenseStats();
    @Field("onslaught_stats")
    private DatabasePlayerOnslaughtStats onslaughtStats = new DatabasePlayerOnslaughtStats();
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
    @Field("patreon_rewards")
    private List<PatreonReward> patreonRewards = new ArrayList<>();
    @Field("compensation_rewards")
    private List<CompensationReward> compensationRewards = new ArrayList<>();
    @Field("game_event_rewards")
    private List<GameEventReward> gameEventRewards = new ArrayList<>();
    @Field("pouch_rewards")
    private List<PouchReward> pouchRewards = new ArrayList<>();
    @Field("bounty_rewards")
    private List<BountyReward> bountyRewards = new ArrayList<>();
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
    private Map<MobDrop, Long> mobDrops = new LinkedHashMap<>();
    @Field("completed_tutorial")
    private boolean completedTutorial = false;
    //OLD
    @Deprecated
    @Field("quests_completed")
    private Map<Quests, Long> questsCompleted = new HashMap<>();
    //NEW BOUNTIES
    @Field("completed_bounties")
    private Map<Bounty, Long> completedBounties = new HashMap<>();
    @Field("bounties_completed")
    private int bountiesCompleted = 0; // can only get 2 extra for daily/weekly
    @Field("active_bounties")
    private List<AbstractBounty> activeBounties = new ArrayList<>();

    @Field("auto_upgrade_profiles")
    private Map<Specializations, List<AutoUpgradeProfile>> autoUpgradeProfiles = new HashMap<>();
    @Field("illusion_vendor_rewards_purchased")
    private Map<String, Long> illusionVendorRewardsPurchased = new HashMap<>();
    //MAGE
    //0=fireball
    //1=flameburst
    //etc
    @Field("alternative_masteries_unlocked")
    private Map<Specializations, Map<Integer, Instant>> alternativeMasteriesUnlocked = new HashMap<>();
    @Transient
    private EnumSet<Ability> alternativeMasteriesUnlockedAbilities = EnumSet.noneOf(Ability.class);

    public void loadInCollection(PlayersCollections collection) {
        if (activeBounties.isEmpty()) {
            activeBounties.addAll(BountyUtils.getNewBounties(collection.name));
        }
        updateLocalAlternativeMasteriesUnlocked();
    }

    public void updateLocalAlternativeMasteriesUnlocked() {
        alternativeMasteriesUnlocked.forEach((specializations, integerInstantMap) -> {
            Ability[] abilities = Ability.SPEC_ABILITIES.get(specializations);
            for (int i = 0; i < 5; i++) {
                if (integerInstantMap.containsKey(i)) {
                    alternativeMasteriesUnlockedAbilities.add(abilities[i]);
                }
            }
        });
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        //COINS
        addCurrency(Currencies.COIN, gamePlayer.getCoinsGained() * multiplier);
        //GUILDS
        Pair<Guild, GuildPlayer> guildGuildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(gamePlayer.getUuid());
        if (playersCollection == PlayersCollections.LIFETIME && guildGuildPlayerPair != null) {
            Guild guild = guildGuildPlayerPair.getA();
            GuildPlayer guildPlayer = guildGuildPlayerPair.getB();
            guild.addCurrentCoins(gamePlayer.getGuildCoinsGained() * multiplier);
            guild.addExperience(gamePlayer.getGuildExpGained() * multiplier);
            guildPlayer.addCoins(gamePlayer.getGuildCoinsGained() * multiplier);
            guildPlayer.addExperience(gamePlayer.getGuildExpGained() * multiplier);
            guild.queueUpdate();
        }
        //WEAPONS / ITEMS
        List<AbstractWeapon> weaponsFound = gamePlayer.getWeaponsFound();
        List<AbstractItem> itemsFound = gamePlayer.getItemsFound();
        if (playersCollection == PlayersCollections.LIFETIME) {
            if (multiplier > 0) {
                int maxWeaponInventorySize = databasePlayer.isPatreon() ? WeaponManagerMenu.MAX_WEAPONS_PATREON : WeaponManagerMenu.MAX_WEAPONS;
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
                        if (weapon instanceof Salvageable salvageable) {
                            AtomicInteger randomSalvageAmount = new AtomicInteger(salvageable.getSalvageAmount());
                            Bukkit.getPluginManager().callEvent(new WeaponSalvageEvent.Pre(databasePlayer.getUuid(), weapon, randomSalvageAmount));
                            addCurrency(Currencies.SYNTHETIC_SHARD, randomSalvageAmount.get() / 2);
                        }
                    }
                } else {
                    weaponInventory.addAll(weaponsFound);
                }
                itemsManager.getItemInventory().addAll(itemsFound);
            } else {
                //need to search by uuid incase weapon got upgraded or changed
                for (AbstractWeapon weapon : weaponsFound) {
                    boolean removed = weaponInventory.removeIf(abstractWeapon -> abstractWeapon.getUUID().equals(weapon.getUUID()));
                    if (!removed && weapon instanceof Salvageable) {
                        MasterworksFair fair = MasterworksFairManager.currentFair;
                        if (!fair.getCommonPlayerEntries().removeIf(entry -> entry.getWeapon().getUUID().equals(weapon.getUUID())) &&
                                !fair.getRarePlayerEntries().removeIf(entry -> entry.getWeapon().getUUID().equals(weapon.getUUID())) &&
                                !fair.getEpicPlayerEntries().removeIf(entry -> entry.getWeapon().getUUID().equals(weapon.getUUID()))
                        ) {
                            ChatChannels.sendDebugMessage((CommandIssuer) null, gamePlayer.getName() + " - Subtracted currency");
                            subtractCurrency(Currencies.SYNTHETIC_SHARD,
                                    (((Salvageable) weapon).getMaxSalvageAmount() + ((Salvageable) weapon).getMinSalvageAmount()) / 2
                            );
                        } else {
                            ChatChannels.sendDebugMessage((CommandIssuer) null, gamePlayer.getName() + " - Removed weapon from fair");
                        }
                    } else {
                        ChatChannels.sendDebugMessage((CommandIssuer) null, gamePlayer.getName() + " - Removed weapon from inventory");
                    }
                }
                for (AbstractItem item : itemsFound) {
                    itemsManager.getItemInventory().removeIf(abstractItem -> abstractItem.getUUID().equals(item.getUUID()));
                }
            }
        }

        getItemsManager().addBlessingsFound(gamePlayer.getBlessingsFound() * multiplier);
        //SPENDABLE
        addCurrency(Currencies.LEGEND_FRAGMENTS, gamePlayer.getLegendFragmentsGained() * multiplier);
        addCurrency(Currencies.ILLUSION_SHARD, gamePlayer.getIllusionShardGained() * multiplier);
        gamePlayer.getMobDropsGained().forEach((mob, integer) -> addMobDrops(mob, integer * multiplier));

        // TODO
        //UPDATE GAME MODE STATS
        if (databaseGame instanceof DatabaseGamePvEEvent gamePvEEvent && gamePlayer instanceof DatabaseGamePlayerPvEEvent gamePlayerPvEEvent) {
            eventStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
            addCurrency(
                    ((DatabaseGamePvEEvent) databaseGame).getEvent().currency,
                    Math.min(((DatabaseGamePlayerPvEEvent) gamePlayer).getPoints(), ((DatabaseGamePvEEvent) databaseGame).getPointLimit()) * multiplier
            );
        } else {
            if (GameMode.isWaveDefense(gameMode) && databaseGame instanceof DatabaseGamePvEWaveDefense gamePvEWaveDefense && gamePlayer instanceof DatabaseGamePlayerPvEWaveDefense gamePlayerPvEWaveDefense) {
                waveDefenseStats.updateStats(databasePlayer, gamePvEWaveDefense, gamePlayerPvEWaveDefense, multiplier, playersCollection);
            } else if (gameMode == GameMode.ONSLAUGHT && databaseGame instanceof DatabaseGamePvEOnslaught gamePvEOnslaught && gamePlayer instanceof DatabaseGamePlayerPvEOnslaught gamePlayerPvEOnslaught) {
                onslaughtStats.updateStats(databasePlayer, gamePvEOnslaught, gamePlayerPvEOnslaught, multiplier, playersCollection);
            } else {
                ChatUtils.MessageType.GAME.sendErrorMessage("Unable to update stats for " + databaseGame.getClass().getSimpleName() + " and " + gamePlayer.getClass()
                                                                                                                                                          .getSimpleName());
            }
        }
    }


    public void addCurrency(Currencies currency, Long amount) {
        if (AdminCommand.BYPASSED_PLAYER_CURRENCIES.contains(this)) {
            return;
        }
        if (!currencies.containsKey(currency)) {
            currencies.put(currency, 0L);
        }
        this.currencies.put(currency, this.currencies.get(currency) + amount);

        Bukkit.getServer().getPluginManager().callEvent(new AddCurrencyEvent(this, currency, amount));

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
            ChatUtils.MessageType.GAME_EVENTS.sendMessage("Unable to add currency: " + currency.name + ". No event mode found for " + event.name + "(" + epochSecond + ")");
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

    public void addCurrency(Currencies currency, int amount) {
        this.addCurrency(currency, (long) amount);
    }

    public void subtractCurrency(Currencies currency, int amount) {
        this.subtractCurrency(currency, (long) amount);
    }

    public ItemsManager getItemsManager() {
        return itemsManager;
    }

    public void addMobDrops(MobDrop mobDrop, long amount) {
        if (AdminCommand.BYPASSED_PLAYER_CURRENCIES.contains(this)) {
            return;
        }
        if (!this.mobDrops.containsKey(mobDrop)) {
            this.mobDrops.put(mobDrop, amount);
        } else {
            this.mobDrops.put(mobDrop, this.mobDrops.get(mobDrop) + amount);
        }
    }

    public void subtractCurrency(Currencies currency, Long amount) {
        this.addCurrency(currency, -amount);
    }

    public DatabasePlayer getDatabasePlayer() {
        return databasePlayer;
    }

    public void setDatabasePlayer(DatabasePlayer databasePlayer) {
        this.databasePlayer = databasePlayer;
    }

    public void setCurrency(Currencies currency, Long amount) {
        if (AdminCommand.BYPASSED_PLAYER_CURRENCIES.contains(this)) {
            return;
        }
        this.currencies.put(currency, amount);
    }

    public DatabasePlayerPvEWaveDefenseDifficultyStats getEasyStats() {
        return waveDefenseStats.getEasyStats();
    }

    public DatabasePlayerPvEWaveDefenseDifficultyStats getNormalStats() {
        return waveDefenseStats.getNormalStats();
    }

    public DatabasePlayerPvEWaveDefenseDifficultyStats getHardStats() {
        return waveDefenseStats.getHardStats();
    }

    public DatabasePlayerPvEWaveDefenseDifficultyStats getExtremeStats() {
        return waveDefenseStats.getExtremeStats();
    }

    public DatabasePlayerPvEWaveDefenseDifficultyStats getEndlessStats() {
        return waveDefenseStats.getEndlessStats();
    }

    public List<AbstractWeapon> getWeaponInventory() {
        return weaponInventory;
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

    public void addOneCurrency(Currencies currency) {
        this.addCurrency(currency, 1L);
    }

    public void subtractOneCurrency(Currencies currency) {
        this.subtractCurrency(currency, 1L);
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

    public Map<Bounty, Long> getCompletedBounties() {
        return completedBounties;
    }

    public int getBountiesCompleted() {
        return bountiesCompleted;
    }

    public void addBountiesCompleted() {
        this.bountiesCompleted++;
    }

    public List<AbstractBounty> getActiveBounties() {
        return activeBounties;
    }

    public List<AbstractBounty> getTrackableBounties() {
        return activeBounties.stream()
                             .filter(abstractBounty -> abstractBounty != null && abstractBounty.isStarted() && abstractBounty.getProgress() != null)
                             .toList();
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

    public List<BountyReward> getBountyRewards() {
        return bountyRewards;
    }

    public Map<Specializations, List<AutoUpgradeProfile>> getAutoUpgradeProfiles() {
        return autoUpgradeProfiles;
    }

    public DatabasePlayerPvEEventStats getEventStats() {
        return eventStats;
    }

    public long getMobDrops(MobDrop mobDrop) {
        if (AdminCommand.BYPASSED_PLAYER_CURRENCIES.contains(this)) {
            return Long.MAX_VALUE;
        }
        return this.mobDrops.getOrDefault(mobDrop, 0L);
    }

    public Map<MobDrop, Long> getMobDrops() {
        return mobDrops;
    }

    public Map<String, Long> getIllusionVendorRewardsPurchased() {
        return illusionVendorRewardsPurchased;
    }

    public DatabasePlayerWaveDefenseStats getWaveDefenseStats() {
        return waveDefenseStats;
    }

    public DatabasePlayerOnslaughtStats getOnslaughtStats() {
        return onslaughtStats;
    }

    public Map<Specializations, Map<Integer, Instant>> getAlternativeMasteriesUnlocked() {
        return alternativeMasteriesUnlocked;
    }

    public EnumSet<Ability> getAlternativeMasteriesUnlockedAbilities() {
        return alternativeMasteriesUnlockedAbilities;
    }


}
