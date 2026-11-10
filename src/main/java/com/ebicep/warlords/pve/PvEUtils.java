package com.ebicep.warlords.pve;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PvEUtils {

    public static <T extends Spendable> List<Component> getCostLore(Map<T, Long> cost, boolean emptyLine) {
        return new ArrayList<>() {{
            if (emptyLine) {
                add(Component.empty());
            }
            add(Component.text("Cost: ", NamedTextColor.AQUA));
            cost.forEach((spendable, amount) -> add(Component.text(" - ", NamedTextColor.GRAY).append(spendable.getCostColoredName(amount))));
        }};
    }

    /**
     * Checks a full cost before charging any of it, telling the player which part they are short on.
     *
     * @param reason appended to the failure message, e.g. "to unlock this socket"
     */
    public static <T extends Spendable> boolean hasEnough(Player player, DatabasePlayer databasePlayer, Map<T, Long> cost, String reason) {
        for (Map.Entry<T, Long> entry : cost.entrySet()) {
            Spendable spendable = entry.getKey();
            Long amount = entry.getValue();
            if (spendable.getFromPlayer(databasePlayer) < amount) {
                player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                            .append(spendable.getCostColoredName(amount))
                                            .append(Component.text(" " + reason + "!", NamedTextColor.RED))
                );
                return false;
            }
        }
        return true;
    }

    public static <T extends Spendable> List<Component> getCostLore(Map<T, Long> cost, @Nullable String costName, boolean emptyLine) {
        return new ArrayList<>() {{
            if (emptyLine) {
                add(Component.empty());
            }
            if (costName != null) {
                add(Component.text(costName + ": ", NamedTextColor.AQUA));
            }
            cost.forEach((spendable, amount) -> add(Component.text(" - ", NamedTextColor.GRAY).append(spendable.getCostColoredName(amount))));
        }};
    }

}
