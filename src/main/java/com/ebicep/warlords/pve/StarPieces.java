package com.ebicep.warlords.pve;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

public enum StarPieces {

    COMMON(Currencies.COMMON_STAR_PIECE,
            20,
            new LinkedHashMap<>()
    ),
    RARE(Currencies.RARE_STAR_PIECE,
            30,
            new LinkedHashMap<>() {{
                put(Currencies.COMMON_STAR_PIECE, 5L);
                put(Currencies.COIN, 50_000L);
            }}
    ),
    EPIC(Currencies.EPIC_STAR_PIECE,
            40,
            new LinkedHashMap<>() {{
                put(Currencies.RARE_STAR_PIECE, 5L);
                put(Currencies.COIN, 250_000L);
            }}
    ),
    LEGENDARY(Currencies.LEGENDARY_STAR_PIECE,
            50,
            new LinkedHashMap<>() {{
                put(Currencies.EPIC_STAR_PIECE, 5L);
                put(Currencies.COIN, 1_000_000L);
            }}
    );

    public static final StarPieces[] VALUES = values();

    public static void openStarPieceSynthesizerMenu(Player player) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            Menu menu = new Menu("Star Piece Synthesizer", 9 * 4);

            int row = 1;
            int col = 2;
            for (StarPieces starPiece : VALUES) {
                if (starPiece.synthesisCosts.isEmpty()) {
                    continue;
                }
                List<String> costLore = starPiece.getCostLore();
                menu.setItem(col, row,
                        new ItemBuilder(Material.NETHER_STAR)
                                .name(ChatColor.GREEN + "Synthesize: " + starPiece.currency.getColoredName())
                                .lore(WordWrap.wrapWithNewline(ChatColor.GRAY + "Combines lower tier star pieces to create a higher one.", 140))
                                .addLore(costLore)
                                .get(),
                        (m, e) -> {
                            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                            for (Map.Entry<Currencies, Long> currenciesLongEntry : starPiece.synthesisCosts.entrySet()) {
                                Currencies currency = currenciesLongEntry.getKey();
                                long value = currenciesLongEntry.getValue();
                                if (pveStats.getCurrencyValue(currency) < value) {
                                    player.sendMessage(ChatColor.RED + "You need " + currency.getCostColoredName(value) +
                                            ChatColor.RED + " to synthesize this star piece.");
                                    return;
                                }
                            }
                            List<String> confirmLore = new ArrayList<>();
                            confirmLore.add(ChatColor.GRAY + "Synthesize: " + starPiece.currency.getColoredName());
                            confirmLore.addAll(costLore);
                            Menu.openConfirmationMenu(player,
                                    "Confirm Synthesis",
                                    3,
                                    confirmLore,
                                    Collections.singletonList(ChatColor.GRAY + "Go back"),
                                    (m2, e2) -> {
                                        starPiece.synthesisCosts.forEach(pveStats::subtractCurrency);
                                        pveStats.addCurrency(starPiece.currency, 1);
                                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

                                        player.spigot().sendMessage(
                                                new ComponentBuilder(ChatColor.GREEN + "Synthesized " + starPiece.currency.getCostColoredName(1) + ChatColor.GRAY + "!")
                                                        .create()
                                        );
                                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 500, 2);

                                        openStarPieceSynthesizerMenu(player);
                                    },
                                    (m2, e2) -> openStarPieceSynthesizerMenu(player),
                                    (m2) -> {
                                    }
                            );

                        }
                );
                col += 2;
            }

            menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        });
    }

    public final Currencies currency;
    public final int starPieceBonusValue;
    public final LinkedHashMap<Currencies, Long> synthesisCosts;

    StarPieces(Currencies currency, int starPieceBonusValue, LinkedHashMap<Currencies, Long> synthesisCosts) {
        this.currency = currency;
        this.starPieceBonusValue = starPieceBonusValue;
        this.synthesisCosts = synthesisCosts;
    }

    public StarPieces next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    public List<String> getCostLore() {
        return PvEUtils.getCostLore(synthesisCosts, true);
    }

}
