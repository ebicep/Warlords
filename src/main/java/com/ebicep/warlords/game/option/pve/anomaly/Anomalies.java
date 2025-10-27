package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import net.kyori.adventure.text.Component;

import java.util.LinkedHashMap;
import java.util.List;

public enum Anomalies {

    OPEX_ANOMALY("Opex Anomaly",
            List.of(Component.text("Test"))
    ) {
        @Override
        public LinkedHashMap<Spendable, Long> getRewards() {
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 120_000L);
                put(Currencies.ETHEREUM_CRYSTAL, 3L);
                put(Currencies.ASCENDANT_STAR_PIECE, 1L);
                put(Currencies.LIMIT_BREAKER, 1L);
            }};
        }
    },
    PLAINS_OF_DUNESTAR("Plains of Dunestar",
            List.of(
                    Component.text("A new threat emerges along the borders"),
                    Component.text("of Plains of Dunestar. Investigate it!")
            )
    ) {
        @Override
        public LinkedHashMap<Spendable, Long> getRewards() {
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 120_000L);
                put(Currencies.ETHEREUM_CRYSTAL, 3L);
                put(Currencies.ASCENDANT_STAR_PIECE, 1L);
                put(Currencies.LIMIT_BREAKER, 1L);
            }};
        }
    },
    WHAT_ONCE_WAS( "What Once Was",
            List.of(Component.text("Test"))
    ) {
        @Override
        public LinkedHashMap<Spendable, Long> getRewards() {
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 120_000L);
                put(Currencies.ETHEREUM_CRYSTAL, 3L);
                put(Currencies.ASCENDANT_STAR_PIECE, 1L);
                put(Currencies.LIMIT_BREAKER, 1L);
            }};
        }
    },
    ENDLESS_PARADOX( "Endless Paradox",
            List.of(Component.text("Test"))
    ) {
        @Override
        public LinkedHashMap<Spendable, Long> getRewards() {
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 120_000L);
                put(Currencies.ETHEREUM_CRYSTAL, 3L);
                put(Currencies.ASCENDANT_STAR_PIECE, 1L);
                put(Currencies.LIMIT_BREAKER, 1L);
            }};
        }
    }

    ;

    private final String name;
    private final List<Component> description;

    public static final Anomalies[] VALUES = values();

    public abstract LinkedHashMap<Spendable, Long> getRewards();

    Anomalies(String name, List<Component> description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public List<Component> getDescription() {
        return description;
    }
}
