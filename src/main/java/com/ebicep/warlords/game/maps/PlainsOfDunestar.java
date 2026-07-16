package com.ebicep.warlords.game.maps;

public class PlainsOfDunestar extends AbstractAnomalyMap {

    public PlainsOfDunestar() {
        super(
                "Plains of Dunestar",
                "PlainsOfDunestar",
                new double[]{0.5, 80, 0.5},
                new double[][]{
                        {22.5, 80, 8.5},
                        {-8.5, 80, 22.5},
                        {-20.5, 80, -10.5}
                },
                new double[][][]{
                        {{38.5, 80, 8.5}, {32.5, 80, 22.5}, {35.5, 80, -7.5}},
                        {{-8.5, 80, 38.5}, {7.5, 80, 34.5}, {-24.5, 80, 34.5}},
                        {{-36.5, 80, -10.5}, {-31.5, 80, 5.5}, {-30.5, 80, -25.5}}
                }
        );
    }
}