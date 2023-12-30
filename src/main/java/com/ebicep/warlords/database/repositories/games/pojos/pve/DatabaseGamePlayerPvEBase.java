package com.ebicep.warlords.database.repositories.games.pojos.pve;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PlayerPveRewards;
import com.ebicep.warlords.guilds.GuildExperienceUtils;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.quests.Quests;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

public abstract class DatabaseGamePlayerPvEBase extends DatabaseGamePlayerBase {

    private int prestige;
    private int level;
    private AbstractWeapon weapon;
    @Field("upgrade_log")
    private List<AbilityTree.UpgradeLog> upgradeLog;
    @Field("mob_kills")
    private Map<String, Long> mobKills = new LinkedHashMap<>();
    @Field("mob_assists")
    private Map<String, Long> mobAssists = new LinkedHashMap<>();
    @Field("mob_deaths")
    private Map<String, Long> mobDeaths = new LinkedHashMap<>();
    @Field("coins_gained")
    private long coinsGained;
    @Field("guild_coins_gained")
    private long guildCoinsGained;
    @Field("guild_exp_gained")
    private long guildExpGained;
    @Field("weapons_found")
    private List<AbstractWeapon> weaponsFound = new ArrayList<>();
    @Field("legend_fragments_gain")
    private long legendFragmentsGained;
    @Field("illusion_shard_gain")
    private long illusionShardGained;
    @Field("blessings_found")
    private int blessingsFound;
    @Field("mob_drops_gained")
    private Map<MobDrop, Long> mobDropsGained = new HashMap<>();
    @Field("items_found")
    private List<AbstractItem> itemsFound = new ArrayList<>();
    @Deprecated
    @Field("quests_completed")
    private List<Quests> questsCompleted = null;
    @Field("bounties_completed")
    private List<Bounty> bountiesCompleted = null;

    public DatabaseGamePlayerPvEBase() {
    }

    public DatabaseGamePlayerPvEBase(WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent, PveOption pveOption, boolean counted) {
        super(warlordsPlayer, gameWinEvent, counted);
        //ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("DatabaseGamePlayerPvE - " + warlordsPlayer.getName());
        UUID uuid = warlordsPlayer.getUuid();
        PlayerPveRewards playerPveRewards = pveOption.getRewards().getPlayerRewards(uuid);
        DatabaseManager.getPlayer(uuid, databasePlayer -> {
            this.prestige = databasePlayer.getSpec(warlordsPlayer.getSpecClass()).getPrestige();
        });
        this.level = ExperienceManager.getLevelForSpec(uuid, warlordsPlayer.getSpecClass());
        this.weapon = warlordsPlayer.getWeapon();
        this.upgradeLog = warlordsPlayer.getAbilityTree().getUpgradeLog();
        this.mobKills = warlordsPlayer.getMinuteStats().total().getMobKills();
        this.mobAssists = warlordsPlayer.getMinuteStats().total().getMobAssists();
        this.mobDeaths = warlordsPlayer.getMinuteStats().total().getMobDeaths();
        Currencies.PvECoinSummary coinGainFromGameStats = Currencies.getCoinGainFromGameStats(warlordsPlayer, pveOption, true);
        this.coinsGained = coinGainFromGameStats.getTotalCoinsGained();
        this.guildCoinsGained = coinGainFromGameStats.getTotalGuildCoinsGained();
        this.guildExpGained = GuildExperienceUtils.getExpFromPvE(warlordsPlayer, pveOption, true)
                                                  .values()
                                                  .stream()
                                                  .mapToLong(aLong -> aLong)
                                                  .sum();
        this.weaponsFound.addAll(playerPveRewards.getWeaponsFound());
        this.itemsFound.addAll(playerPveRewards.getItemsFound());
        this.legendFragmentsGained = playerPveRewards.getLegendFragmentGain();
        this.illusionShardGained = playerPveRewards.getIllusionShardGain();
        this.blessingsFound = playerPveRewards.getBlessingsFound();
        this.mobDropsGained = new HashMap<>(playerPveRewards.getMobDropsGained());
        if (!counted) {
            return;
        }
        for (PlayersCollections activeCollection : PlayersCollections.ACTIVE_COLLECTIONS) {
            BountyUtils.BountyInfo bountyInfo = BountyUtils.BOUNTY_COLLECTION_INFO.get(activeCollection.name);
            if (bountyInfo == null) {
                continue;
            }
            DatabaseManager.getPlayer(uuid, activeCollection, databasePlayer -> {
                List<AbstractBounty> trackableBounties = databasePlayer.getPveStats().getTrackableBounties();
                try {
                    for (AbstractBounty bounty : trackableBounties) {
                        if (bounty instanceof TracksPostGame tracksPostGame) {
                            tracksPostGame.onGameEnd(pveOption.getGame(), warlordsPlayer, gameWinEvent);
                        } else if (bounty instanceof TracksDuringGame tracksDuringGame) {
                            tracksDuringGame.apply(bounty);
                        }
                    }
                } catch (Exception e) {
                    ChatUtils.MessageType.BOUNTIES.sendErrorMessage(e);
                }
            });
        }
        //ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("DatabaseGamePlayerPvE - " + warlordsPlayer.getName() + " DONE");
    }

    public int getPrestige() {
        return prestige;
    }

    public int getLevel() {
        return level;
    }

    public AbstractWeapon getWeapon() {
        return weapon;
    }

    public List<AbilityTree.UpgradeLog> getUpgradeLog() {
        return upgradeLog;
    }

    public Map<String, Long> getMobKills() {
        return mobKills;
    }

    public Map<String, Long> getMobAssists() {
        return mobAssists;
    }

    public Map<String, Long> getMobDeaths() {
        return mobDeaths;
    }

    public long getCoinsGained() {
        return coinsGained;
    }

    public long getGuildCoinsGained() {
        return guildCoinsGained;
    }

    public long getGuildExpGained() {
        return guildExpGained;
    }

    public List<AbstractWeapon> getWeaponsFound() {
        return weaponsFound;
    }

    public long getLegendFragmentsGained() {
        return legendFragmentsGained;
    }

    public long getIllusionShardGained() {
        return illusionShardGained;
    }

    public int getBlessingsFound() {
        return blessingsFound;
    }

    public Map<MobDrop, Long> getMobDropsGained() {
        return mobDropsGained;
    }

    public List<AbstractItem> getItemsFound() {
        return itemsFound;
    }

}
