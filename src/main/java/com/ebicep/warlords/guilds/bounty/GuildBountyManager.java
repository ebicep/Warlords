package com.ebicep.warlords.guilds.bounty;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.guilds.GuildLeaderboardManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.rewards.types.BountyReward;
import com.ebicep.warlords.util.java.DateUtil;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class GuildBountyManager {

    public static final int MAX_SLOTS = 2;
    public static final long[] SLOT_COSTS = {500_000, 1_000_000};

    private GuildBountyManager() {
    }

    public static GuildBountyData getOrCreateData(Guild guild) {
        GuildBountyData data = findData(guild).orElseGet(() -> {
            GuildBountyData newData = new GuildBountyData();
            guild.addUpgrade(newData);
            guild.queueUpdate();
            return newData;
        });
        validateWeek(guild, data);
        return data;
    }

    public static Optional<GuildBountyData> findData(Guild guild) {
        return guild.getUpgrades().stream().filter(GuildBountyData.class::isInstance).map(GuildBountyData.class::cast).findFirst();
    }

    public static void validateWeek(Guild guild, GuildBountyData data) {
        long currentWeek = DateUtil.getCurrentWeekStartEpochDay();
        boolean changed = false;
        if (data.getWeekStartEpochDay() != currentWeek) {
            data.setWeekStartEpochDay(currentWeek);
            data.getActiveBounties().clear();
            changed = true;
        }

        int unlockedSlots = data.getUnlockedSlots();
        while (data.getActiveBounties().size() > unlockedSlots) {
            data.getActiveBounties().remove(data.getActiveBounties().size() - 1);
            changed = true;
        }
        while (data.getActiveBounties().size() < unlockedSlots) {
            GuildBounty nextBounty = getRandomBounty(data.getActiveBounties());
            if (nextBounty == null) {
                break;
            }
            data.getActiveBounties().add(new GuildBountyProgress(nextBounty));
            changed = true;
        }

        if (changed) {
            guild.queueUpdate();
        }
    }

    public static void unlockSlot(Guild guild) {
        GuildBountyData data = getOrCreateData(guild);
        if (data.getUnlockedSlots() >= MAX_SLOTS) {
            return;
        }
        data.setUnlockedSlots(data.getUnlockedSlots() + 1);
        validateWeek(guild, data);
        guild.queueUpdate();
    }

    public static void addProgress(Guild guild, GuildBounty bounty, long amount) {
        if (amount <= 0) {
            return;
        }
        GuildBountyData data = getActiveData(guild);
        if (data == null) {
            return;
        }
        for (GuildBountyProgress progress : data.getActiveBounties()) {
            if (progress.getBounty() != bounty || progress.isCompleted()) {
                continue;
            }
            if (progress.add(amount)) {
                completeBounty(guild, bounty);
            } else {
                guild.queueUpdate();
            }
            return;
        }
    }

    public static void updateMaxProgress(Guild guild, GuildBounty bounty, long value) {
        GuildBountyData data = getActiveData(guild);
        if (data == null) {
            return;
        }
        for (GuildBountyProgress progress : data.getActiveBounties()) {
            if (progress.getBounty() != bounty || progress.isCompleted() || value <= progress.getValue()) {
                continue;
            }
            if (progress.updateMax(value)) {
                completeBounty(guild, bounty);
            } else {
                guild.queueUpdate();
            }
            return;
        }
    }

    private static GuildBountyData getActiveData(Guild guild) {
        Optional<GuildBountyData> optionalData = findData(guild);
        if (optionalData.isEmpty() || optionalData.get().getUnlockedSlots() == 0) {
            return null;
        }
        GuildBountyData data = optionalData.get();
        validateWeek(guild, data);
        return data;
    }

    private static void completeBounty(Guild guild, GuildBounty bounty) {
        LinkedHashMap<Spendable, Long> playerRewards = bounty.getPlayerRewards();
        for (GuildPlayer guildPlayer : guild.getPlayers()) {
            UUID uuid = guildPlayer.getUUID();
            LinkedHashMap<Spendable, Long> rewardsCopy = new LinkedHashMap<>(playerRewards);
            Warlords.newChain()
                    .asyncFirst(() -> DatabaseManager.playerService.findByUUID(uuid))
                    .syncLast(optional -> {
                        if (optional.isEmpty()) {
                            return;
                        }
                        DatabasePlayer databasePlayer = optional.get();
                        databasePlayer.getPveStats().getBountyRewards().add(new BountyReward(rewardsCopy, bounty.getName()));
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    })
                    .execute();
        }

        guild.addCurrentCoins(bounty.getGuildCoins());
        guild.addExperience(bounty.getGuildExperience());
        guild.queueUpdate();
        GuildLeaderboardManager.recalculateAllLeaderboards();

        guild.sendGuildMessageToOnlinePlayers(
                Component.text("Guild bounty completed: ", NamedTextColor.GREEN)
                         .append(Component.text(bounty.getName(), NamedTextColor.GOLD))
                         .append(Component.text("! Every guild member received the bounty rewards. The guild received ", NamedTextColor.GRAY))
                         .append(Component.text(NumberFormat.addCommas(bounty.getGuildCoins()) + " Guild Coins", NamedTextColor.YELLOW))
                         .append(Component.text(" and ", NamedTextColor.GRAY))
                         .append(Component.text(NumberFormat.addCommas(bounty.getGuildExperience()) + " Guild XP", NamedTextColor.AQUA))
                         .append(Component.text(".", NamedTextColor.GRAY)),
                true
        );
    }

    private static GuildBounty getRandomBounty(List<GuildBountyProgress> activeBounties) {
        Set<GuildBounty> excluded = new HashSet<>();
        activeBounties.forEach(progress -> excluded.add(progress.getBounty()));
        List<GuildBounty> available = new ArrayList<>();
        for (GuildBounty bounty : GuildBounty.VALUES) {
            if (!excluded.contains(bounty)) {
                available.add(bounty);
            }
        }
        Collections.shuffle(available);
        return available.isEmpty() ? null : available.get(0);
    }
}
