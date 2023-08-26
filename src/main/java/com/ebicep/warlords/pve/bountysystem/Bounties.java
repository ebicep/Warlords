package com.ebicep.warlords.pve.bountysystem;

import com.ebicep.warlords.pve.bountysystem.bounties.Cast500Rune;
import com.ebicep.warlords.pve.bountysystem.bounties.Defeat20Bosses;
import com.ebicep.warlords.pve.bountysystem.bounties.Kill500;

import java.util.function.Supplier;

public enum Bounties {

    KILL500(Kill500::new),
    DEFEAT20BOSSES(Defeat20Bosses::new),
    CAST500RUNE(Cast500Rune::new),

    ;

    public final Supplier<AbstractBounty> create;

    Bounties(Supplier<AbstractBounty> create) {
        this.create = create;
    }
}
