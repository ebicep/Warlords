package com.ebicep.warlords.database.repositories.player.pojos.pve;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.commands.debugcommands.misc.AdminCommand;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvE;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvE;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.DatabasePlayerPvEEventStats;
import com.ebicep.warlords.game.GameMode;
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
import com.ebicep.warlords.pve.quests.Quests;
import com.ebicep.warlords.pve.rewards.types.CompensationReward;
import com.ebicep.warlords.pve.rewards.types.MasterworksFairReward;
import com.ebicep.warlords.pve.rewards.types.PatreonReward;
import com.ebicep.warlords.pve.upgrades.AutoUpgradeProfile;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weaponaddons.Salvageable;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.java.Pair;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

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
    //EVENTS
    @Field("event_stats")
    private DatabasePlayerPvEEventStats eventStats = new DatabasePlayerPvEEventStats();
    /*
        event_stats
        > total shit
          > event_1
            > date_1
            > date_2
          > event_2
     */
    //GENERAL

    //SUPPLY DROP
    @Field("supply_drop_rewards")
    private List<SupplyDropEntry> supplyDropEntries = new ArrayList<>();
    //MASTERWORKS FAIR
    @Field("masterworks_fair_submissions")
    private List<MasterworksFairEntry> masterworksFairEntries = new ArrayList<>();
    @Field("masterworks_fair_rewards")
    private List<MasterworksFairReward> masterworksFairRewards = new ArrayList<>();
    //PATERON
    @Field("patreon_rewards")
    private List<PatreonReward> patreonRewards = new ArrayList<>();
    //COMPENSATION
    @Field("compensation_rewards")
    private List<CompensationReward> compensationRewards = new ArrayList<>();
    //WEAPONS
    @Field("weapon_inventory")
    private List<AbstractWeapon> weaponInventory = new ArrayList<>();

    //CURRENCIES
    private Map<Currencies, Long> currencies = new LinkedHashMap<>() {{
        for (Currencies value : Currencies.VALUES) {
            put(value, 0L);
        }
    }};
    @Field("completed_tutorial")
    private boolean completedTutorial = false;
    @Field("quests_completed")
    private Map<Quests, Long> questsCompleted = new HashMap<>();
    @Field("auto_upgrade_profiles")
    private Map<Specializations, List<AutoUpgradeProfile>> autoUpgradeProfiles = new HashMap<>();


    @Override
    public void updateCustomStats(
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert databaseGame instanceof DatabaseGamePvE;
        assert gamePlayer instanceof DatabaseGamePlayerPvE;
        super.updateCustomStats(databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        DatabaseGamePlayerPvE gamePlayerPvE = (DatabaseGamePlayerPvE) gamePlayer;

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
        //WEAPONS
        if (multiplier > 0) {
            weaponInventory.addAll(gamePlayerPvE.getWeaponsFound());

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
                List<AbstractWeapon> weaponsFound = gamePlayerPvE.getWeaponsFound();
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
            }

            //QUESTS
            for (Quests quests : gamePlayerPvE.getQuestsCompleted()) {
                if (quests.time == playersCollection || playersCollection == PlayersCollections.LIFETIME) {
                    questsCompleted.merge(quests, -1L, Long::sum);
                    quests.rewards.forEach(this::subtractCurrency);
                }
            }
        }

        //LEGEND FRAGMENTS
        addCurrency(Currencies.LEGEND_FRAGMENTS, gamePlayerPvE.getLegendFragmentsGained() * multiplier);

        //UPDATE UNIVERSAL EXPERIENCE
        this.experience += gamePlayer.getExperienceEarnedUniversal() * multiplier;
        this.experiencePvE += gamePlayer.getExperienceEarnedUniversal() * multiplier;

        //UPDATE CLASS, SPEC
        this.getClass(Specializations.getClass(gamePlayer.getSpec())).updateStats(databaseGame, gamePlayer, multiplier, playersCollection);
        this.getSpec(gamePlayer.getSpec()).updateStats(databaseGame, gamePlayer, multiplier, playersCollection);

        //UPDATE GAME MODE STATS
        switch (((DatabaseGamePvE) databaseGame).getDifficulty()) {
            case EASY:
                easyStats.updateStats(databaseGame, gamePlayer, multiplier, playersCollection);
                break;
            case NORMAL:
                normalStats.updateStats(databaseGame, gamePlayer, multiplier, playersCollection);
                break;
            case HARD:
                hardStats.updateStats(databaseGame, gamePlayer, multiplier, playersCollection);
                break;
            case ENDLESS:
                endlessStats.updateStats(databaseGame, gamePlayer, multiplier, playersCollection);
                break;
        }

    }

    public DatabasePlayerPvEDifficultyStats getDifficultyStats(DifficultyIndex difficultyIndex) {
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

    public void addCurrency(Currencies currency, Long amount) {
        if (AdminCommand.BYPASSED_PLAYER_CURRENCIES.contains(this)) {
            return;
        }
        if (!currencies.containsKey(currency)) {
            currencies.put(currency, 0L);
        }
        this.currencies.put(currency, this.currencies.get(currency) + amount);
        CustomScoreboard.reloadPvEScoreboard(this);
    }

    public void addOneCurrency(Currencies currency) {
        this.addCurrency(currency, 1L);
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

    public Map<Specializations, List<AutoUpgradeProfile>> getAutoUpgradeProfiles() {
        return autoUpgradeProfiles;
    }
}
