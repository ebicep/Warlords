package com.ebicep.warlords.game.maps;

public class WhatOnceWas extends AbstractAnomalyMap {

    public WhatOnceWas() {
        super(
                "What Once Was",
                "WhatOnceWas",
                new double[]{0.5, 70, 0.5},
                new double[][]{
                        {16.5, 70, 16.5},
                        {-15.5, 70, 16.5},
                        {0.5, 70, -19.5}
                },
                new double[][][]{
                        {{31.5, 70, 31.5}, {34.5, 70, 14.5}, {14.5, 70, 34.5}},
                        {{-30.5, 70, 31.5}, {-33.5, 70, 14.5}, {-13.5, 70, 34.5}},
                        {{0.5, 70, -36.5}, {16.5, 70, -32.5}, {-15.5, 70, -32.5}}
                }
        );
    }
}