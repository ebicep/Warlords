package com.ebicep.warlords.game.option.pve.rewards;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.DifficultyIndex;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public abstract class PveRewards<T extends PveOption> {

    protected final HashMap<UUID, PlayerPveRewards> playerRewards = new HashMap<>();
    protected final HashMap<String, Long> mobsKilled = new HashMap<>();

    protected final T pveOption;
    protected final DifficultyIndex difficulty;
    protected final CoinGainOption coinGainOption;

    public PveRewards(T pveOption) {
        this.pveOption = pveOption;
        this.difficulty = pveOption.getDifficulty();
        this.coinGainOption = pveOption.getGame()
                                       .getOptions()
                                       .stream()
                                       .filter(CoinGainOption.class::isInstance)
                                       .map(CoinGainOption.class::cast)
                                       .findAny()
                                       .orElse(null);
    }


    public void storeRewards() {
        storeBaseCoinSummary();
        storeWeaponFragmentGain();
        storeIllusionShardGain();
    }

    public void storeBaseCoinSummary() {
        if (coinGainOption == null) {
            return;
        }
        LinkedHashMap<String, Long> cachedBaseCoinSummary = new LinkedHashMap<>();

        storeCustomBaseCoinSummary(cachedBaseCoinSummary);

        for (Map.Entry<String, LinkedHashMap<String, Long>> stringLinkedHashMapEntry : coinGainOption.getMobCoinValues().entrySet()) {
            cachedBaseCoinSummary.put(stringLinkedHashMapEntry.getKey(), 0L);
            for (Map.Entry<String, Long> stringLongEntry : stringLinkedHashMapEntry.getValue().entrySet()) {
                if (mobsKilled.containsKey(stringLongEntry.getKey())) {
                    cachedBaseCoinSummary.merge(stringLinkedHashMapEntry.getKey(),
                            (long) (mobsKilled.get(stringLongEntry.getKey()) * stringLongEntry.getValue() * difficulty
                                    .getRewardsMultiplier()),
                            Long::sum
                    );
                }
            }
        }
        if (coinGainOption.getPlayerCoinPerKill() != 0) {
            int totalKills = pveOption
                    .getGame()
                    .warlordsPlayers()
                    .mapToInt(wp -> wp.getMinuteStats().total().getKills()).sum();
            cachedBaseCoinSummary.put("Kills", coinGainOption.getPlayerCoinPerKill() * totalKills);
        }
        long convertBonus = coinGainOption.getGuildCoinInsigniaConvertBonus();
        if (convertBonus != 0 && shouldStoreInsigniaConverted()) {
            pveOption.getGame()
                     .warlordsPlayers()
                     .forEach(warlordsPlayer -> cachedBaseCoinSummary.put(
                             "Excess Insignia Converted",
                             Math.min(warlordsPlayer.getCurrency() / 100, convertBonus)
                     ));
        }

        pveOption.getGame()
                 .warlordsPlayers()
                 .forEach(warlordsPlayer -> getPlayerRewards(warlordsPlayer.getUuid()).setCachedBaseCoinSummary(cachedBaseCoinSummary));

    }

    protected abstract void storeCustomBaseCoinSummary(LinkedHashMap<String, Long> cachedBaseCoinSummary);

    protected abstract boolean shouldStoreInsigniaConverted();

    public PlayerPveRewards getPlayerRewards(UUID uuid) {
        return playerRewards.computeIfAbsent(uuid, k -> new PlayerPveRewards());
    }

    public void storeWeaponFragmentGain() {
        if (difficulty == DifficultyIndex.EVENT) {
            return;
        }

        storeWeaponFragmentGainInternal();
    }

    protected abstract void storeWeaponFragmentGainInternal();

    protected void addExtraFragmentGain(int per5, Specializations currentSpec, DatabasePlayer databasePlayer, AtomicLong legendFragmentGain) {
        legendFragmentGain.updateAndGet(v -> (long) (v * difficulty.getRewardsMultiplier()));
        //warlordsPlayer.sendMessage("Legend Fragment Gain After Rewards Multiplier: " + legendFragmentGain.get());
        int specPrestigeBonus = databasePlayer.getSpec(currentSpec).getPrestige() * 5;
        int otherSpecPrestigeBonus = 0;
        for (Specializations value : Specializations.VALUES) {
            if (value != currentSpec) {
                otherSpecPrestigeBonus += databasePlayer.getSpec(value).getPrestige() * 2;
            }
        }
        legendFragmentGain.addAndGet((long) ((specPrestigeBonus + otherSpecPrestigeBonus) * difficulty.getRewardsMultiplier() * (per5 / 25)));
        //warlordsPlayer.sendMessage("Legend Fragment Gain After Prestiges: " + legendFragmentGain.get());
    }

    public void storeIllusionShardGain() {
        if (difficulty == DifficultyIndex.EVENT) {
            return;
        }

        storeIllusionShardGainInternal();
    }

    protected abstract void storeIllusionShardGainInternal();

    public HashMap<String, Long> getMobsKilled() {
        return mobsKilled;
    }

}
