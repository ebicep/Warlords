package com.ebicep.warlords.pve.bountysystem;

import com.ebicep.warlords.pve.bountysystem.bounties.Kill500;

import java.util.function.Supplier;

public enum Bounties {

    KILL500(Kill500::new),

    ;

    public final Supplier<AbstractBounty> create;

    Bounties(Supplier<AbstractBounty> create) {
        this.create = create;
    }
}
