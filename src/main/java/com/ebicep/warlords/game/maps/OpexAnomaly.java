package com.ebicep.warlords.game.maps;

public class OpexAnomaly extends AbstractAnomalyMap {

    public OpexAnomaly() {
        super(
                "Opex Anomaly",
                "OpexAnomaly",
                new double[]{0.5, 90, 0.5},
                new double[][]{
                        {18.5, 90, 0.5},
                        {0.5, 90, 18.5},
                        {-17.5, 90, 0.5}
                },
                new double[][][]{
                        {{32.5, 90, 0.5}, {27.5, 90, 12.5}, {27.5, 90, -11.5}},
                        {{0.5, 90, 32.5}, {12.5, 90, 27.5}, {-11.5, 90, 27.5}},
                        {{-31.5, 90, 0.5}, {-26.5, 90, 12.5}, {-26.5, 90, -11.5}}
                }
        );
    }
}