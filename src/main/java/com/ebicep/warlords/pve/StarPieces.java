package com.ebicep.warlords.pve;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.weapons.events.StarPieceSynthesizedEvent;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public enum StarPieces {

    COMMON(Currencies.COMMON_STAR_PIECE,
            20,
            new LinkedHashMap<>()
    ),
    RARE(Currencies.RARE_STAR_PIECE,
            30,
            new LinkedHashMap<>() {{
                put(Currencies.COMMON_STAR_PIECE, 3L);
                put(Currencies.COIN, 50_000L);
            }}
    ),
    EPIC(Currencies.EPIC_STAR_PIECE,
            40,
            new LinkedHashMap<>() {{
                put(Currencies.RARE_STAR_PIECE, 3L);
                put(Currencies.COIN, 250_000L);
            }}
    ),
    LEGENDARY(Currencies.LEGENDARY_STAR_PIECE,
            50,
            new LinkedHashMap<>() {{
                put(Currencies.EPIC_STAR_PIECE, 3L);
                put(Currencies.COIN, 1_000_000L);
            }}
    ),
    ASCENDANT(Currencies.ASCENDANT_STAR_PIECE,
            60,
            new LinkedHashMap<>()
//            new LinkedHashMap<>() {{
//                put(Currencies.LEGENDARY_STAR_PIECE, 3L);
//                put(Currencies.COIN, 2_000_000L);
//            }}
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
                List<Component> costLore = starPiece.getCostLore();
                menu.setItem(col, row,
                        new ItemBuilder(Material.NETHER_STAR)
                                .name(Component.textOfChildren(Component.text("Synthesize: ", NamedTextColor.GREEN), starPiece.currency.getColoredName()))
                                .lore(WordWrap.wrap(Component.text("Combines lower tier star pieces to create a higher one.", NamedTextColor.GRAY), 140))
                                .addLore(costLore)
                                .get(),
                        (m, e) -> {
                            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                            for (Map.Entry<Currencies, Long> currenciesLongEntry : starPiece.synthesisCosts.entrySet()) {
                                Currencies currency = currenciesLongEntry.getKey();
                                long value = currenciesLongEntry.getValue();
                                if (pveStats.getCurrencyValue(currency) < value) {
                                    player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                                                .append(currency.getCostColoredName(value))
                                                                .append(Component.text(" to synthesize this star piece."))
                                    );
                                    return;
                                }
                            }
                            List<Component> confirmLore = new ArrayList<>();
                            confirmLore.add(Component.textOfChildren(Component.text("Synthesize: ", NamedTextColor.GRAY), starPiece.currency.getColoredName()));
                            confirmLore.addAll(costLore);
                            Menu.openConfirmationMenu(player,
                                    "Confirm Synthesis",
                                    3,
                                    confirmLore,
                                    Menu.GO_BACK,
                                    (m2, e2) -> {
                                        starPiece.synthesisCosts.forEach(pveStats::subtractCurrency);
                                        pveStats.addCurrency(starPiece.currency, 1);
                                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

                                        Bukkit.getPluginManager().callEvent(new StarPieceSynthesizedEvent(player.getUniqueId(), starPiece));

                                        player.sendMessage(Component.textOfChildren(
                                                Component.text("Synthesized ", NamedTextColor.GREEN),
                                                starPiece.currency.getCostColoredName(1),
                                                Component.text("!", NamedTextColor.GRAY)
                                        ));
                                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);

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
    @Nonnull
    public final LinkedHashMap<Currencies, Long> synthesisCosts;

    StarPieces(Currencies currency, int starPieceBonusValue, @Nonnull LinkedHashMap<Currencies, Long> synthesisCosts) {
        this.currency = currency;
        this.starPieceBonusValue = starPieceBonusValue;
        this.synthesisCosts = synthesisCosts;
    }

    public StarPieces next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    public List<Component> getCostLore() {
        return PvEUtils.getCostLore(synthesisCosts, true);
    }

}
