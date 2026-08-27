package com.ebicep.warlords.database.repositories.player.pojos.general;

import com.ebicep.warlords.database.repositories.events.pojos.GameEventReward;
import com.ebicep.warlords.database.repositories.items.pojos.WeeklyBlessings;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.ItemsManager;
import com.ebicep.warlords.pve.items.menu.ItemMichaelMenu;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.NewItemsUtils;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.pve.rewards.types.CompensationReward;
import com.ebicep.warlords.pve.rewards.types.LevelUpReward;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public enum DatabasePlayerPatches {

    EOD_ITEMS {
        @Override
        public boolean run(UUID uuid, DatabasePlayer databasePlayer) {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            pveStats.getItemsManager().getItemInventory().forEach(item -> item.applyRandomModifier());
            return true;
        }
    },
    EOD_ASCENDANT_SHARD_2 {
        @Override
        public boolean run(UUID uuid, DatabasePlayer databasePlayer) {
            if (!databasePlayer.getPatchesApplied().contains(EOD_ASCENDANT_SHARD)) {
                return true;
            }
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            for (CompensationReward compensationReward : pveStats.getCompensationRewards()) {
                if (compensationReward instanceof CompensationReward.AscendantShardPrestigePatch prestigePatch) {
                    Long previousValue = prestigePatch.getRewards().get(Currencies.ASCENDANT_SHARD);
                    if (previousValue == null) {
                        ChatUtils.MessageType.WARLORDS.sendErrorMessage("EOD_ASCENDANT_SHARD_2: previousValue is null");
                        ChatUtils.MessageType.WARLORDS.sendErrorMessage(String.valueOf(prestigePatch));
                        return true;
                    }
                    pveStats.getCompensationRewards().add(new CompensationReward.AscendantShardPrestigePatch(previousValue * 2L));
                    return true;
                }
            }
            return true;
        }
    },
    EOD_ASCENDANT_SHARD {
        @Override
        public boolean run(UUID uuid, DatabasePlayer databasePlayer) {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            int totalPrestige = Arrays.stream(Specializations.VALUES)
                                      .mapToInt(spec -> databasePlayer.getSpec(spec).getPrestige())
                                      .sum();
            if (totalPrestige > 0) {
                pveStats.getCompensationRewards().add(new CompensationReward.AscendantShardPrestigePatch(totalPrestige * 3L));
            }
            return true;
        }
    },
    EOD_CELESTIAL_BRONZE {
        @Override
        public boolean run(UUID uuid, DatabasePlayer databasePlayer) {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            Long celestialBronze = pveStats.getCurrencyValue(Currencies.CELESTIAL_BRONZE);
            if (celestialBronze > 0) {
                pveStats.getCompensationRewards().add(new CompensationReward.CelestialBronzePatch(celestialBronze));
            }
            pveStats.setCurrency(Currencies.CELESTIAL_BRONZE, 0L);
            return true;
        }
    },
    EOD_BLESSINGS {
        @Override
        public boolean run(UUID uuid, DatabasePlayer databasePlayer) {
            List<WeeklyBlessings> weeklyBlessings = WeeklyBlessings.allWeeklyBlessings;
            if (weeklyBlessings.isEmpty()) {
                return true;
            }
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();

            Map<Integer, Integer> blessingsBought = new HashMap<>();
            for (WeeklyBlessings weeklyBlessing : weeklyBlessings) {
                Map<Integer, Integer> bought = weeklyBlessing.getPlayerOrders().get(uuid);
                if (bought == null) {
                    continue;
                }
                for (Map.Entry<Integer, Integer> entry : bought.entrySet()) {
                    blessingsBought.merge(entry.getKey(), entry.getValue(), Integer::sum);
                }
            }
            if (!blessingsBought.isEmpty()) {
                LinkedHashMap<Spendable, Long> rewards = new LinkedHashMap<>();
                blessingsBought.forEach((tier, amount) -> {
                    LinkedHashMap<Spendable, Long> cost = ItemMichaelMenu.BuyABlessingMenu.COSTS.get(tier);
                    cost.forEach((spendable, aLong) -> rewards.merge(spendable, aLong * amount, Long::sum));
                });
                ItemsManager itemsManager = pveStats.getItemsManager();
                rewards.merge(Currencies.LEGEND_FRAGMENTS, itemsManager.getBlessingsFound() * 15L, Long::sum);

                LinkedHashMap<Spendable, Long> sortedRewards = new LinkedHashMap<>();
                rewards.entrySet().stream()
                       .sorted(Map.Entry.<Spendable, Long>comparingByValue().reversed())
                       .forEachOrdered(x -> sortedRewards.put(x.getKey(), x.getValue()));

                pveStats.getCompensationRewards().add(new CompensationReward.BlessingPatch(sortedRewards));
                itemsManager.setBlessingsFound(0);
                itemsManager.getBlessingsBought().clear();
            }
            return true;
        }
    },
    EOD_ASCENDANT_SHARD_3 {
        @Override
        public boolean run(UUID uuid, DatabasePlayer databasePlayer) {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            AtomicInteger masteriesUnlocked = new AtomicInteger();
            pveStats.getAlternativeMasteriesUnlocked().forEach((specializations, integerInstantMap) -> masteriesUnlocked.addAndGet(integerInstantMap.keySet().size()));
            pveStats.subtractCurrency(Currencies.ASCENDANT_SHARD, masteriesUnlocked.get());
            return true;
        }
    },
    EOD_ASCENDANT_SHARD_4 {
        @Override
        public boolean run(UUID uuid, DatabasePlayer databasePlayer) {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            AtomicInteger masteriesUnlocked = new AtomicInteger();
            Map<Specializations, Map<Integer, Instant>> unlocked = pveStats.getAlternativeMasteriesUnlocked();
            unlocked.forEach((specializations, integerInstantMap) -> masteriesUnlocked.addAndGet(integerInstantMap.keySet().size()));
            unlocked.clear();
            pveStats.getAlternativeMasteriesUnlockedAbilities().clear();
            pveStats.addCurrency(Currencies.ASCENDANT_SHARD, masteriesUnlocked.get());
            return true;
        }
    },
    EVENT_BOLTARO_DOUBLE_REWARDS_1749204000 {
        @Override
        public boolean run(UUID uuid, DatabasePlayer databasePlayer) {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            long target = 1749204000;
            List<GameEventReward> gameEventRewards = pveStats.getGameEventRewards();
            List<GameEventReward> rewards = gameEventRewards
                    .stream()
                    .filter(gameEventReward -> gameEventReward.getEvent() == target)
                    .toList();
            if (rewards.size() == 2) {
                GameEventReward reward1 = rewards.get(0);
                GameEventReward reward2 = rewards.get(1);
                if (reward1.claimed() && !reward2.claimed()) {
                    gameEventRewards.remove(reward2);
                } else if (!reward1.claimed() && reward2.claimed()) {
                    gameEventRewards.remove(reward1);
                } else if (!reward1.claimed() && !reward2.claimed()) {
                    gameEventRewards.remove(reward2);
                } else if (reward1.claimed() && reward2.claimed()) {
                    reward2.unGiveToPlayer(databasePlayer);
                    gameEventRewards.remove(reward2);
                }
            }

            return true;
        }
    },
    LEVEL_UP_REWARDS {
        @Override
        public boolean run(UUID uuid, DatabasePlayer databasePlayer) {
            LinkedHashMap<Spendable, Long> rewards = new LinkedHashMap<>();
            for (Specializations spec : Specializations.VALUES) {
                DatabaseSpecialization databaseSpec = databasePlayer.getSpec(spec);
                int maxClaimable = databaseSpec.getMaxLevelUpRewardsClaimable();
                for (int index = 1; index <= maxClaimable; index++) {
                    int level = ((index - 1) % 100) + 1;
                    int prestige = (index - 1) / 100;
                    if (databaseSpec.hasLevelUpReward(level, prestige)) {
                        continue;
                    }
                    LevelUpReward.getRewardForLevel(level)
                                 .forEach((spendable, amount) -> rewards.merge(spendable, amount, Long::sum));
                }
                databaseSpec.getLevelUpRewards().clear();
                databaseSpec.setLevelUpRewardsClaimed(maxClaimable);
            }
            if (!rewards.isEmpty()) {
                databasePlayer.getPveStats().getCompensationRewards().add(new CompensationReward.LevelUpPatch(rewards));
                CompensationReward.LevelUpPatch.giveLevelUpPatchFutureMessage(databasePlayer);
            }
            return true;
        }
    },
    LEGACY_ITEM_COMPENSATION_2026 {
        @Override
        public boolean run(UUID uuid, DatabasePlayer databasePlayer) {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            EnumMap<ItemTier, Integer> tierCounts = new EnumMap<>(ItemTier.class);
            pveStats.getItemsManager().getItemInventory().forEach(item -> {
                if (item != null && item.getTier() != null) {
                    tierCounts.merge(item.getTier(), 1, Integer::sum);
                }
            });

            long alpha = tierCounts.getOrDefault(ItemTier.ALPHA, 0);
            long beta = tierCounts.getOrDefault(ItemTier.BETA, 0);
            long gamma = tierCounts.getOrDefault(ItemTier.GAMMA, 0);
            long delta = tierCounts.getOrDefault(ItemTier.DELTA, 0);
            long omega = tierCounts.getOrDefault(ItemTier.OMEGA, 0);

            long zenithStars = omega * 2L;
            long syntheticShards = omega * 5_000L + delta * 1_000L;
            long legendFragments = delta * 500L;
            long scrapMetal = (alpha + beta) * 25L;
            long legendaryItems = omega / 3L;
            long sovereignItems = delta / 7L;
            long epicItems = gamma / 10L;

            List<NewItem> generatedItems = new ArrayList<>();
            try {
                for (long i = 0; i < legendaryItems; i++) {
                    generatedItems.add(NewItemsUtils.generateRandomItem(NewItemTier.LEGENDARY));
                }
                for (long i = 0; i < sovereignItems; i++) {
                    generatedItems.add(NewItemsUtils.generateRandomItem(NewItemTier.SOVEREIGN));
                }
                for (long i = 0; i < epicItems; i++) {
                    generatedItems.add(NewItemsUtils.generateRandomItem(NewItemTier.EPIC));
                }
            } catch (RuntimeException exception) {
                ChatUtils.MessageType.WARLORDS.sendErrorMessage("Failed to generate legacy item compensation for " + uuid);
                ChatUtils.MessageType.WARLORDS.sendErrorMessage(exception);
                return false;
            }

            LinkedHashMap<Spendable, Long> rewards = new LinkedHashMap<>();
            if (zenithStars > 0) {
                rewards.put(MobDrop.ZENITH_STAR, zenithStars);
            }
            if (syntheticShards > 0) {
                rewards.put(Currencies.SYNTHETIC_SHARD, syntheticShards);
            }
            if (legendFragments > 0) {
                rewards.put(Currencies.LEGEND_FRAGMENTS, legendFragments);
            }
            if (scrapMetal > 0) {
                rewards.put(Currencies.SCRAP_METAL, scrapMetal);
            }

            boolean hasRewards = !rewards.isEmpty() || !generatedItems.isEmpty();
            if (hasRewards) {
                pveStats.getCompensationRewards().add(new CompensationReward.LegacyItemPatch(rewards, generatedItems));
            }

            List<Component> summary = new ArrayList<>();
            summary.add(Component.text("------------------------------------------------", NamedTextColor.DARK_AQUA));
            summary.add(Component.text("Legacy Item Compensation", NamedTextColor.GOLD));
            summary.add(Component.text("This is your compensation for the removal of the old item system.", NamedTextColor.GRAY));
            summary.add(Component.text("You will only receive these rewards once.", NamedTextColor.GRAY));

            if (!hasRewards) {
                summary.add(Component.text("No compensation rewards were generated from your legacy item totals.", NamedTextColor.YELLOW));
            } else {
                if (zenithStars > 0) {
                    summary.add(Component.text(" • ", NamedTextColor.DARK_GRAY)
                            .append(MobDrop.ZENITH_STAR.getCostColoredName(zenithStars)));
                }
                if (syntheticShards > 0) {
                    summary.add(Component.text(" • ", NamedTextColor.DARK_GRAY)
                            .append(Currencies.SYNTHETIC_SHARD.getCostColoredName(syntheticShards)));
                }
                if (legendFragments > 0) {
                    summary.add(Component.text(" • ", NamedTextColor.DARK_GRAY)
                            .append(Currencies.LEGEND_FRAGMENTS.getCostColoredName(legendFragments)));
                }
                if (scrapMetal > 0) {
                    summary.add(Component.text(" • ", NamedTextColor.DARK_GRAY)
                            .append(Currencies.SCRAP_METAL.getCostColoredName(scrapMetal)));
                }
                if (legendaryItems > 0) {
                    summary.add(Component.text(
                            " • " + legendaryItems + " Random Legendary Item" + (legendaryItems == 1 ? "" : "s"),
                            NewItemTier.LEGENDARY.getTextColor()
                    ));
                }
                if (sovereignItems > 0) {
                    summary.add(Component.text(
                            " • " + sovereignItems + " Random Sovereign Item" + (sovereignItems == 1 ? "" : "s"),
                            NewItemTier.SOVEREIGN.getTextColor()
                    ));
                }
                if (epicItems > 0) {
                    summary.add(Component.text(
                            " • " + epicItems + " Random Epic Item" + (epicItems == 1 ? "" : "s"),
                            NewItemTier.EPIC.getTextColor()
                    ));
                }
                summary.add(Component.text("Claim them in your Rewards Inventory", NamedTextColor.GREEN));
            }
            summary.add(Component.text("------------------------------------------------", NamedTextColor.DARK_AQUA));
            databasePlayer.addFutureMessage(FutureMessage.create(summary, true));

            ChatUtils.MessageType.WARLORDS.sendMessage(
                    "Legacy item compensation for " + uuid + ": alpha=" + alpha +
                            ", beta=" + beta +
                            ", gamma=" + gamma +
                            ", delta=" + delta +
                            ", omega=" + omega
            );
            return true;
        }
    },
    REMOVE_RESET_PLAYER_STAT_REWARD {
        @Override
        public boolean run(UUID uuid, DatabasePlayer databasePlayer) {
            List<CompensationReward> compensationRewards = databasePlayer.getPveStats().getCompensationRewards();
            compensationRewards.removeIf(reward -> {
                String from = reward.getFrom();
                if (!"Reset Player stat".equals(from) && !"Reset Player stat Reward".equals(from)) {
                    return false;
                }
                if (reward.claimed()) {
                    reward.unGiveToPlayer(databasePlayer);
                }
                return true;
            });
            return true;
        }
    },

    ;

    public static final DatabasePlayerPatches[] VALUES = values();

    public boolean run(UUID uuid, DatabasePlayer databasePlayer) {
        return false;
    }
}
