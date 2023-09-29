package com.ebicep.warlords.pve.items.types.specialitems;

import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.delta.*;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta.*;
import com.ebicep.warlords.pve.items.types.specialitems.tome.delta.*;

import java.util.Set;

public interface CraftsInto {

    AbstractItem getCraftsInto(Set<BasicStatPool> statPool);

    interface CraftsPalmOfTheSoothsayer extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
            return new SoothsayersPalms(statPool);
        }
    }

    interface CraftsSamsonsFists extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
            return new SamsonsFists(statPool);
        }
    }

    interface CraftsPendragonGauntlets extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
            return new PendragonGauntlets(statPool);
        }
    }

    interface CraftsGardeningGloves extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
            return new GardeningGloves(statPool);
        }
    }

    interface CraftsMultipurposeKnuckles extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
            return new MultipurposeKnuckles(statPool);
        }
    }

    interface CraftsFirewaterAlmanac extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
            return new FirewaterAlmanac(statPool);
        }
    }

    interface CraftsThePresentTestament extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
            return new ThePresentTestament(statPool);
        }
    }

    interface CraftsAGuideToMMA extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
            return new AGuideToMMA(statPool);
        }
    }

    interface CraftsPansTome extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
            return new PansTome(statPool);
        }
    }

    interface CraftsScrollOfUncertainty extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
            return new ScrollOfUncertainty(statPool);
        }
    }

    interface CraftsBucklerPiece extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
            return new BucklerPiece(statPool);
        }
    }

    interface CraftsCrossNecklaceCharm extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
            return new CrossNecklaceCharm(statPool);
        }
    }

    interface CraftsPridwensBulwark extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
            return new PridwensBulwark(statPool);
        }
    }

    interface CraftsAerialAegis extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
            return new AerialAegis(statPool);
        }
    }

    interface CraftsShieldOfSnatching extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
            return new ShieldOfSnatching(statPool);
        }
    }

    interface CraftsDiabolicalRage extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
            return new DiabolicalRage(statPool);
        }
    }

    interface CraftsBruisedBook extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
            return new BruisedBook(statPool);
        }
    }

    interface CraftsOtherworldlyAmulet extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
            return new OtherworldlyAmulet(statPool);
        }
    }


}
