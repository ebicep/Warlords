package com.ebicep.warlords.database.repositories.player.pojos.cache.support;

import com.ebicep.warlords.database.repositories.player.pojos.cache.PushedStatTotals;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayerCompStats;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayerPubStats;
import com.ebicep.warlords.database.repositories.player.pojos.general.TournamentStats;
import com.ebicep.warlords.database.repositories.player.pojos.general.classes.DatabaseArcanist;
import com.ebicep.warlords.database.repositories.player.pojos.general.classes.DatabaseMage;
import com.ebicep.warlords.database.repositories.player.pojos.general.classes.DatabasePaladin;
import com.ebicep.warlords.database.repositories.player.pojos.general.classes.DatabaseRogue;
import com.ebicep.warlords.database.repositories.player.pojos.general.classes.DatabaseShaman;
import com.ebicep.warlords.database.repositories.player.pojos.general.classes.DatabaseWarrior;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly.DatabasePlayerAnomalyStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.DatabasePlayerPvEEventStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.DatabasePlayerOnslaughtStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.DatabasePlayerWaveDefenseStats;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.items.ItemsManager;
import com.ebicep.warlords.pve.mobs.MobDrop;
import org.objenesis.ObjenesisStd;

/**
 * Allocates {@link DatabasePlayer} without running constructors that touch armor, weapons, or registries.
 */
public final class HeadlessDatabasePlayers {

    private static final ObjenesisStd OBJENESIS = new ObjenesisStd();

    private HeadlessDatabasePlayers() {
    }

    public static DatabasePlayer newPlayer() {
        DatabasePlayer databasePlayer = OBJENESIS.newInstance(DatabasePlayer.class);
        TestReflection.setField(databasePlayer, "uuid", PushCacheTestFixtures.TEST_UUID);
        TestReflection.setField(databasePlayer, "pushedStats", new PushedStatTotals());
        TestReflection.setField(databasePlayer, "mage", new DatabaseMage());
        TestReflection.setField(databasePlayer, "warrior", new DatabaseWarrior());
        TestReflection.setField(databasePlayer, "paladin", new DatabasePaladin());
        TestReflection.setField(databasePlayer, "shaman", new DatabaseShaman());
        TestReflection.setField(databasePlayer, "rogue", new DatabaseRogue());
        TestReflection.setField(databasePlayer, "arcanist", new DatabaseArcanist());
        TestReflection.setField(databasePlayer, "pubStats", new DatabasePlayerPubStats());
        TestReflection.setField(databasePlayer, "compStats", new DatabasePlayerCompStats());
        TestReflection.setField(databasePlayer, "tournamentStats", new TournamentStats());
        return databasePlayer;
    }

    public static DatabasePlayer newPlayerWithPve() {
        DatabasePlayer databasePlayer = newPlayer();
        DatabasePlayerPvE pveStats = OBJENESIS.newInstance(HeadlessDatabasePlayerPvE.class);
        TestReflection.setField(pveStats, "pushedStats", new PushedStatTotals());
        TestReflection.setField(pveStats, "waveDefenseStats", new DatabasePlayerWaveDefenseStats());
        TestReflection.setField(pveStats, "onslaughtStats", new DatabasePlayerOnslaughtStats());
        TestReflection.setField(pveStats, "anomalyStats", new DatabasePlayerAnomalyStats());
        TestReflection.setField(pveStats, "eventStats", new DatabasePlayerPvEEventStats());
        TestReflection.setField(pveStats, "itemsManager", OBJENESIS.newInstance(ItemsManager.class));
        TestReflection.setField(databasePlayer, "pveStats", pveStats);
        return databasePlayer;
    }

    private static final class HeadlessDatabasePlayerPvE extends DatabasePlayerPvE {

        @Override
        public void addCurrency(Currencies currency, Long amount) {
        }

        @Override
        public void addCurrency(Currencies currency, int amount) {
        }

        @Override
        public void addMobDrops(MobDrop mobDrop, long amount) {
        }
    }
}
