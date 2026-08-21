package com.ebicep.warlords.honorifics;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Spendable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class HonorificCost {

    private final LinkedHashMap<Spendable, Long> entries;

    private HonorificCost(LinkedHashMap<Spendable, Long> entries) {
        this.entries = entries;
    }

    public static HonorificCost of(Spendable spendable, long amount) {
        LinkedHashMap<Spendable, Long> entries = new LinkedHashMap<>();
        entries.put(spendable, amount);
        return new HonorificCost(entries);
    }

    public static HonorificCost of(Map<Spendable, Long> costs) {
        return new HonorificCost(new LinkedHashMap<>(costs));
    }

    public boolean canAfford(DatabasePlayer databasePlayer) {
        return entries.entrySet()
                .stream()
                .allMatch(entry -> entry.getKey().getFromPlayer(databasePlayer) >= entry.getValue());
    }

    public void take(DatabasePlayer databasePlayer) {
        entries.forEach((spendable, amount) -> spendable.subtractFromPlayer(databasePlayer, amount));
    }

    public List<Component> getLore() {
        List<Component> lore = new ArrayList<>();
        boolean first = true;
        for (Map.Entry<Spendable, Long> entry : entries.entrySet()) {
            Component prefix = Component.text(first ? "Cost: " : "      ", NamedTextColor.GRAY);
            lore.add(prefix.append(entry.getKey().getCostColoredName(entry.getValue())));
            first = false;
        }
        return lore;
    }

    public Map<Spendable, Long> getEntries() {
        return Map.copyOf(entries);
    }
}
