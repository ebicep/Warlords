package com.ebicep.warlords.pve;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsCoinSummaryEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveGuildCoinEvent;
import com.ebicep.warlords.game.option.RecordTimeElapsedOption;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.CoinGainOption;
import com.ebicep.warlords.game.option.pve.rewards.PlayerPveRewards;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.Pair;
import com.google.common.util.concurrent.AtomicDouble;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public enum Currencies implements Spendable {

    SYNTHETIC_SHARD(
            "Synthetic Shard",
            NamedTextColor.WHITE,
            new ItemStack(Material.GRAY_STAINED_GLASS)
    ),
    LEGEND_FRAGMENTS(
            "Legend Fragment",
            TextColor.color(255, 139, 0),
            new ItemStack(Material.BLAZE_POWDER)
    ),
    FAIRY_ESSENCE(
            "Fairy Essence",
            NamedTextColor.LIGHT_PURPLE,
            new ItemStack(Material.MAGENTA_DYE)
    ) {
        @Override
        public boolean pluralIncludeS() {
            return false;
        }
    },
    COMMON_STAR_PIECE(
            "Common Star Piece",
            NamedTextColor.GREEN,
            new ItemStack(Material.NETHER_STAR)
    ),
    RARE_STAR_PIECE(
            "Rare Star Piece",
            NamedTextColor.BLUE,
            new ItemStack(Material.NETHER_STAR)
    ),
    EPIC_STAR_PIECE(
            "Epic Star Piece",
            NamedTextColor.DARK_PURPLE,
            new ItemStack(Material.NETHER_STAR)
    ),
    LEGENDARY_STAR_PIECE(
            "Legendary Star Piece",
            NamedTextColor.GOLD,
            new ItemStack(Material.NETHER_STAR)
    ),
    ASCENDANT_STAR_PIECE(
            "Ascendant Star Piece",
            NamedTextColor.RED,
            new ItemStack(Material.NETHER_STAR)
    ),
    SUPPLY_DROP_TOKEN(
            "Supply Drop Token",
            NamedTextColor.GOLD,
            new ItemStack(Material.FIREWORK_STAR)
    ),
    COIN(
            "Coin",
            NamedTextColor.YELLOW,
            new ItemStack(Material.GOLD_NUGGET)
    ),
    SKILL_BOOST_MODIFIER(
            "Skill Boost Modifier",
            NamedTextColor.DARK_GRAY,
            new ItemStack(Material.BOOKSHELF)
    ),
    EVENT_POINTS_BOLTARO(
            "Boltaro Event Point",
            NamedTextColor.YELLOW,
            new ItemStack(Material.SUNFLOWER)
    ),
    EVENT_POINTS_NARMER(
            "Narmer Event Point",
            NamedTextColor.YELLOW,
            new ItemStack(Material.SUNFLOWER)
    ),
    EVENT_POINTS_MITHRA(
            "Mithra Event Point",
            NamedTextColor.YELLOW,
            new ItemStack(Material.SUNFLOWER)
    ),
    EVENT_POINTS_ILLUIMINA( //TODO FIX
            "Illumina Event Point",
            NamedTextColor.YELLOW,
            new ItemStack(Material.SUNFLOWER)
    ),
    EVENT_POINTS_GARDEN_OF_HESPERIDES(
            "Garden of Hesperides Event Point",
            NamedTextColor.YELLOW,
            new ItemStack(Material.SUNFLOWER)
    ),
    EVENT_POINTS_LIBRARY_ARCHIVES(
            "Library Archives Event Point",
            NamedTextColor.YELLOW,
            new ItemStack(Material.SUNFLOWER)
    ),
    TITLE_TOKEN_JUGGERNAUT(
            "Juggernaut Title Token",
            NamedTextColor.YELLOW,
            new ItemStack(Material.SNOWBALL)
    ),
    TITLE_TOKEN_PHARAOHS_REVENGE(
            "Pharaoh's Revenge Title Token",
            NamedTextColor.YELLOW,
            new ItemStack(Material.SNOWBALL)
    ),
    TITLE_TOKEN_SPIDERS_BURROW(
            "Spiders Burrow Title Token",
            NamedTextColor.YELLOW,
            new ItemStack(Material.SNOWBALL)
    ),
    TITLE_TOKEN_BANE_OF_IMPURITIES(
            "Bane of Impurities Title Token",
            NamedTextColor.YELLOW,
            new ItemStack(Material.SNOWBALL)
    ),
    TITLE_TOKEN_GARDEN_OF_HESPERIDES(
            "Garden of Hesperides Title Token",
            NamedTextColor.YELLOW,
            new ItemStack(Material.SNOWBALL)
    ),
    TITLE_TOKEN_LIBRARY_ARCHIVES(
            "Library Archives Title Token",
            NamedTextColor.YELLOW,
            new ItemStack(Material.SNOWBALL)
    ),
    LIMIT_BREAKER(
            "Limit Breaker",
            NamedTextColor.DARK_GRAY,
            new ItemStack(Material.CLOCK)
    ),
    MYSTERIOUS_TOKEN(
            "Mysterious Token",
            NamedTextColor.DARK_GRAY,
            new ItemStack(Material.BEDROCK)
    ),
    ILLUSION_SHARD(
            "Illusion Shard",
            NamedTextColor.DARK_PURPLE,
            new ItemStack(Material.GRAY_STAINED_GLASS_PANE)
    ),
    CELESTIAL_BRONZE(
            "Celestial Bronze",
            NamedTextColor.GOLD,
            new ItemStack(Material.RABBIT_SPAWN_EGG)
    ) {
        @Override
        public boolean pluralIncludeS() {
            return false;
        }
    },
    SCRAP_METAL(
            "Scrap Metal",
            NamedTextColor.GRAY,
            new ItemStack(Material.MELON_SEEDS)
    ) {
        @Override
        public boolean pluralIncludeS() {
            return false;
        }
    },
    ASCENDANT_SHARD(
            "Ascendant Shard",
            TextColor.color(220, 20, 60),
            new ItemStack(Material.ECHO_SHARD)
    ),


    ;

    public static final List<Currencies> STAR_PIECES = Arrays.asList(
            COMMON_STAR_PIECE,
            RARE_STAR_PIECE,
            EPIC_STAR_PIECE,
            LEGENDARY_STAR_PIECE
    );
    public static final Currencies[] VALUES = values();
    public static final HashMap<UUID, PvECoinSummary> CACHED_PLAYER_COIN_STATS = new HashMap<>();

    public static PvECoinSummary getCoinGainFromGameStats(
            WarlordsEntity warlordsPlayer,
            PveOption pveOption,
            boolean recalculate
    ) {
        if (!recalculate &&
                CACHED_PLAYER_COIN_STATS.containsKey(warlordsPlayer.getUuid()) &&
                CACHED_PLAYER_COIN_STATS.get(warlordsPlayer.getUuid()) != null
        ) {
            return CACHED_PLAYER_COIN_STATS.get(warlordsPlayer.getUuid());
        }

        PlayerPveRewards playerPveRewards = pveOption
                .getRewards()
                .getPlayerRewards(warlordsPlayer.getUuid());
        LinkedHashMap<String, Long> coinSummary = new LinkedHashMap<>(playerPveRewards.getCachedBaseCoinSummary());

        long totalCoinsEarned = 0;
        for (Long value : coinSummary.values()) {
            totalCoinsEarned += value;
        }

        long guildCoinsEarned = 0;
        CoinGainOption coinGainOption = pveOption
                .getGame()
                .getOptions()
                .stream()
                .filter(CoinGainOption.class::isInstance)
                .map(CoinGainOption.class::cast)
                .findAny()
                .orElse(null);

        if (coinGainOption != null) {
            Pair<Long, Integer> guildCoinPerXSec = coinGainOption.getGuildCoinPerXSec();
            if (guildCoinPerXSec != null) {
                RecordTimeElapsedOption recordTimeElapsedOption = pveOption
                        .getGame()
                        .getOptions()
                        .stream()
                        .filter(option -> option instanceof RecordTimeElapsedOption)
                        .map(RecordTimeElapsedOption.class::cast)
                        .findAny()
                        .orElse(null);
                if (recordTimeElapsedOption != null) {
                    int secondsElapsed = recordTimeElapsedOption.getTicksElapsed() / 20;
                    Pair<Long, Integer> coinsPerXSec = coinGainOption.getGuildCoinPerXSec();
                    guildCoinsEarned = secondsElapsed / coinsPerXSec.getB() * coinsPerXSec.getA();
                }
            }
            if (coinGainOption.getGuildCoinInsigniaConvertBonus() != 0) {
                AtomicDouble guildCoinConversionRate = new AtomicDouble(.05);
                if (!coinGainOption.isDisableCoinConversionUpgrade()) {
                    Bukkit.getPluginManager().callEvent(new WarlordsGiveGuildCoinEvent(warlordsPlayer, guildCoinConversionRate));
                }
                guildCoinsEarned = Math.min(1000, Math.round(totalCoinsEarned * guildCoinConversionRate.get()));
            }
        }

        Bukkit.getPluginManager().callEvent(new WarlordsCoinSummaryEvent(warlordsPlayer, coinSummary));

        totalCoinsEarned = 0;
        for (Long value : coinSummary.values()) {
            totalCoinsEarned += value;
        }

        if (CACHED_PLAYER_COIN_STATS.containsKey(warlordsPlayer.getUuid())) {
            return CACHED_PLAYER_COIN_STATS
                    .get(warlordsPlayer.getUuid())
                    .setCoinSummary(coinSummary)
                    .setTotalCoinsGained(totalCoinsEarned)
                    .setTotalGuildCoinsGained(guildCoinsEarned);
        } else {
            PvECoinSummary pvECoinSummary = new PvECoinSummary(coinSummary, totalCoinsEarned, guildCoinsEarned);
            CACHED_PLAYER_COIN_STATS.put(warlordsPlayer.getUuid(), pvECoinSummary);
            return pvECoinSummary;
        }
    }

    public final String name;
    public final TextColor textColor;
    public final ItemStack item;

    Currencies(String name, TextColor textColor, ItemStack item) {
        this.name = name;
        this.textColor = textColor;
        this.item = item;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TextColor getTextColor() {
        return textColor;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public void addToPlayer(DatabasePlayer databasePlayer, long amount) {
        databasePlayer.getPveStats().addCurrency(this, amount);
    }

    @Override
    public Long getFromPlayer(DatabasePlayer databasePlayer) {
        return databasePlayer.getPveStats().getCurrencyValue(this);
    }

    public Component getColoredName() {
        return Component.text(name, textColor);
    }

    public static class PvECoinSummary {
        private LinkedHashMap<String, Long> coinSummary;
        private long totalCoinsGained;
        private long totalGuildCoinsGained;

        public PvECoinSummary(LinkedHashMap<String, Long> coinSummary, long totalCoinsGained, long totalGuildCoinsGained) {
            this.coinSummary = coinSummary;
            this.totalCoinsGained = totalCoinsGained;
            this.totalGuildCoinsGained = totalGuildCoinsGained;
        }

        public LinkedHashMap<String, Long> getCoinSummary() {
            return coinSummary;
        }

        public PvECoinSummary setCoinSummary(LinkedHashMap<String, Long> coinSummary) {
            this.coinSummary = coinSummary;
            return this;
        }

        public long getTotalCoinsGained() {
            return totalCoinsGained;
        }

        public PvECoinSummary setTotalCoinsGained(long totalCoinsGained) {
            this.totalCoinsGained = totalCoinsGained;
            return this;
        }

        public long getTotalGuildCoinsGained() {
            return totalGuildCoinsGained;
        }

        public PvECoinSummary setTotalGuildCoinsGained(long totalGuildCoinsGained) {
            this.totalGuildCoinsGained = totalGuildCoinsGained;
            return this;
        }
    }
}
