package com.ebicep.warlords.database.repositories.player.pojos.cache;

import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.CTFStatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.duel.DuelStatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.interception.InterceptionStatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.siege.SiegeStatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.tdm.TDMStatsWarlordsClasses;

/**
 * Resolves {@link StatsWarlordsClasses} vs {@link CachedGeneralStats} defaults in favor of the push-up cache.
 * Mode aggregators implement the nested mode bridge (not raw {@code StatsWarlordsClasses} + this type),
 * which avoids typed/raw {@code StatsWarlordsClasses} inheritance clashes.
 */
public interface PushedStatsWarlordsClasses extends CachedGeneralStats {

    @Override
    default int getKills() {
        return CachedGeneralStats.super.getKills();
    }

    @Override
    default int getAssists() {
        return CachedGeneralStats.super.getAssists();
    }

    @Override
    default int getDeaths() {
        return CachedGeneralStats.super.getDeaths();
    }

    @Override
    default int getWins() {
        return CachedGeneralStats.super.getWins();
    }

    @Override
    default int getLosses() {
        return CachedGeneralStats.super.getLosses();
    }

    @Override
    default int getPlays() {
        return CachedGeneralStats.super.getPlays();
    }

    @Override
    default long getDamage() {
        return CachedGeneralStats.super.getDamage();
    }

    @Override
    default long getHealing() {
        return CachedGeneralStats.super.getHealing();
    }

    @Override
    default long getAbsorbed() {
        return CachedGeneralStats.super.getAbsorbed();
    }

    @Override
    default long getExperience() {
        return CachedGeneralStats.super.getExperience();
    }

    interface CTF extends CTFStatsWarlordsClasses, PushedStatsWarlordsClasses {
        @Override
        default int getKills() {
            return PushedStatsWarlordsClasses.super.getKills();
        }

        @Override
        default int getAssists() {
            return PushedStatsWarlordsClasses.super.getAssists();
        }

        @Override
        default int getDeaths() {
            return PushedStatsWarlordsClasses.super.getDeaths();
        }

        @Override
        default int getWins() {
            return PushedStatsWarlordsClasses.super.getWins();
        }

        @Override
        default int getLosses() {
            return PushedStatsWarlordsClasses.super.getLosses();
        }

        @Override
        default int getPlays() {
            return PushedStatsWarlordsClasses.super.getPlays();
        }

        @Override
        default long getDamage() {
            return PushedStatsWarlordsClasses.super.getDamage();
        }

        @Override
        default long getHealing() {
            return PushedStatsWarlordsClasses.super.getHealing();
        }

        @Override
        default long getAbsorbed() {
            return PushedStatsWarlordsClasses.super.getAbsorbed();
        }

        @Override
        default long getExperience() {
            return PushedStatsWarlordsClasses.super.getExperience();
        }

        @Override
        default void warmPushedStats() {
            pushedStats().warm(() -> pushedStats().fillGeneral(
                    CTFStatsWarlordsClasses.super.getKills(),
                    CTFStatsWarlordsClasses.super.getAssists(),
                    CTFStatsWarlordsClasses.super.getDeaths(),
                    CTFStatsWarlordsClasses.super.getWins(),
                    CTFStatsWarlordsClasses.super.getLosses(),
                    CTFStatsWarlordsClasses.super.getPlays(),
                    CTFStatsWarlordsClasses.super.getDamage(),
                    CTFStatsWarlordsClasses.super.getHealing(),
                    CTFStatsWarlordsClasses.super.getAbsorbed(),
                    CTFStatsWarlordsClasses.super.getExperience()
            ));
        }

        default int treeWalkKills() {
            return CTFStatsWarlordsClasses.super.getKills();
        }
    }

    interface TDM extends TDMStatsWarlordsClasses, PushedStatsWarlordsClasses {
        @Override
        default int getKills() {
            return PushedStatsWarlordsClasses.super.getKills();
        }

        @Override
        default int getAssists() {
            return PushedStatsWarlordsClasses.super.getAssists();
        }

        @Override
        default int getDeaths() {
            return PushedStatsWarlordsClasses.super.getDeaths();
        }

        @Override
        default int getWins() {
            return PushedStatsWarlordsClasses.super.getWins();
        }

        @Override
        default int getLosses() {
            return PushedStatsWarlordsClasses.super.getLosses();
        }

        @Override
        default int getPlays() {
            return PushedStatsWarlordsClasses.super.getPlays();
        }

        @Override
        default long getDamage() {
            return PushedStatsWarlordsClasses.super.getDamage();
        }

        @Override
        default long getHealing() {
            return PushedStatsWarlordsClasses.super.getHealing();
        }

        @Override
        default long getAbsorbed() {
            return PushedStatsWarlordsClasses.super.getAbsorbed();
        }

        @Override
        default long getExperience() {
            return PushedStatsWarlordsClasses.super.getExperience();
        }

        @Override
        default void warmPushedStats() {
            pushedStats().warm(() -> pushedStats().fillGeneral(
                    TDMStatsWarlordsClasses.super.getKills(),
                    TDMStatsWarlordsClasses.super.getAssists(),
                    TDMStatsWarlordsClasses.super.getDeaths(),
                    TDMStatsWarlordsClasses.super.getWins(),
                    TDMStatsWarlordsClasses.super.getLosses(),
                    TDMStatsWarlordsClasses.super.getPlays(),
                    TDMStatsWarlordsClasses.super.getDamage(),
                    TDMStatsWarlordsClasses.super.getHealing(),
                    TDMStatsWarlordsClasses.super.getAbsorbed(),
                    TDMStatsWarlordsClasses.super.getExperience()
            ));
        }

        default int treeWalkKills() {
            return TDMStatsWarlordsClasses.super.getKills();
        }
    }

    interface Interception extends InterceptionStatsWarlordsClasses, PushedStatsWarlordsClasses {
        @Override
        default int getKills() {
            return PushedStatsWarlordsClasses.super.getKills();
        }

        @Override
        default int getAssists() {
            return PushedStatsWarlordsClasses.super.getAssists();
        }

        @Override
        default int getDeaths() {
            return PushedStatsWarlordsClasses.super.getDeaths();
        }

        @Override
        default int getWins() {
            return PushedStatsWarlordsClasses.super.getWins();
        }

        @Override
        default int getLosses() {
            return PushedStatsWarlordsClasses.super.getLosses();
        }

        @Override
        default int getPlays() {
            return PushedStatsWarlordsClasses.super.getPlays();
        }

        @Override
        default long getDamage() {
            return PushedStatsWarlordsClasses.super.getDamage();
        }

        @Override
        default long getHealing() {
            return PushedStatsWarlordsClasses.super.getHealing();
        }

        @Override
        default long getAbsorbed() {
            return PushedStatsWarlordsClasses.super.getAbsorbed();
        }

        @Override
        default long getExperience() {
            return PushedStatsWarlordsClasses.super.getExperience();
        }

        @Override
        default void warmPushedStats() {
            pushedStats().warm(() -> pushedStats().fillGeneral(
                    InterceptionStatsWarlordsClasses.super.getKills(),
                    InterceptionStatsWarlordsClasses.super.getAssists(),
                    InterceptionStatsWarlordsClasses.super.getDeaths(),
                    InterceptionStatsWarlordsClasses.super.getWins(),
                    InterceptionStatsWarlordsClasses.super.getLosses(),
                    InterceptionStatsWarlordsClasses.super.getPlays(),
                    InterceptionStatsWarlordsClasses.super.getDamage(),
                    InterceptionStatsWarlordsClasses.super.getHealing(),
                    InterceptionStatsWarlordsClasses.super.getAbsorbed(),
                    InterceptionStatsWarlordsClasses.super.getExperience()
            ));
        }
    }

    interface Duel extends DuelStatsWarlordsClasses, PushedStatsWarlordsClasses {
        @Override
        default int getKills() {
            return PushedStatsWarlordsClasses.super.getKills();
        }

        @Override
        default int getAssists() {
            return PushedStatsWarlordsClasses.super.getAssists();
        }

        @Override
        default int getDeaths() {
            return PushedStatsWarlordsClasses.super.getDeaths();
        }

        @Override
        default int getWins() {
            return PushedStatsWarlordsClasses.super.getWins();
        }

        @Override
        default int getLosses() {
            return PushedStatsWarlordsClasses.super.getLosses();
        }

        @Override
        default int getPlays() {
            return PushedStatsWarlordsClasses.super.getPlays();
        }

        @Override
        default long getDamage() {
            return PushedStatsWarlordsClasses.super.getDamage();
        }

        @Override
        default long getHealing() {
            return PushedStatsWarlordsClasses.super.getHealing();
        }

        @Override
        default long getAbsorbed() {
            return PushedStatsWarlordsClasses.super.getAbsorbed();
        }

        @Override
        default long getExperience() {
            return PushedStatsWarlordsClasses.super.getExperience();
        }

        @Override
        default void warmPushedStats() {
            pushedStats().warm(() -> pushedStats().fillGeneral(
                    DuelStatsWarlordsClasses.super.getKills(),
                    DuelStatsWarlordsClasses.super.getAssists(),
                    DuelStatsWarlordsClasses.super.getDeaths(),
                    DuelStatsWarlordsClasses.super.getWins(),
                    DuelStatsWarlordsClasses.super.getLosses(),
                    DuelStatsWarlordsClasses.super.getPlays(),
                    DuelStatsWarlordsClasses.super.getDamage(),
                    DuelStatsWarlordsClasses.super.getHealing(),
                    DuelStatsWarlordsClasses.super.getAbsorbed(),
                    DuelStatsWarlordsClasses.super.getExperience()
            ));
        }
    }

    interface Siege extends SiegeStatsWarlordsClasses, PushedStatsWarlordsClasses {
        @Override
        default int getKills() {
            return PushedStatsWarlordsClasses.super.getKills();
        }

        @Override
        default int getAssists() {
            return PushedStatsWarlordsClasses.super.getAssists();
        }

        @Override
        default int getDeaths() {
            return PushedStatsWarlordsClasses.super.getDeaths();
        }

        @Override
        default int getWins() {
            return PushedStatsWarlordsClasses.super.getWins();
        }

        @Override
        default int getLosses() {
            return PushedStatsWarlordsClasses.super.getLosses();
        }

        @Override
        default int getPlays() {
            return PushedStatsWarlordsClasses.super.getPlays();
        }

        @Override
        default long getDamage() {
            return PushedStatsWarlordsClasses.super.getDamage();
        }

        @Override
        default long getHealing() {
            return PushedStatsWarlordsClasses.super.getHealing();
        }

        @Override
        default long getAbsorbed() {
            return PushedStatsWarlordsClasses.super.getAbsorbed();
        }

        @Override
        default long getExperience() {
            return PushedStatsWarlordsClasses.super.getExperience();
        }

        @Override
        default void warmPushedStats() {
            pushedStats().warm(() -> pushedStats().fillGeneral(
                    SiegeStatsWarlordsClasses.super.getKills(),
                    SiegeStatsWarlordsClasses.super.getAssists(),
                    SiegeStatsWarlordsClasses.super.getDeaths(),
                    SiegeStatsWarlordsClasses.super.getWins(),
                    SiegeStatsWarlordsClasses.super.getLosses(),
                    SiegeStatsWarlordsClasses.super.getPlays(),
                    SiegeStatsWarlordsClasses.super.getDamage(),
                    SiegeStatsWarlordsClasses.super.getHealing(),
                    SiegeStatsWarlordsClasses.super.getAbsorbed(),
                    SiegeStatsWarlordsClasses.super.getExperience()
            ));
        }
    }
}
