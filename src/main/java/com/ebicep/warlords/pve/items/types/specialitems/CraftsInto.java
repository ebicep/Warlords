package com.ebicep.warlords.pve.items.types.specialitems;

import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.delta.*;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta.*;
import com.ebicep.warlords.pve.items.types.specialitems.tome.delta.*;

public interface CraftsInto {

    AbstractItem getCraftsInto();

    interface CraftsPalmOfTheSoothsayer extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto() {
            return new SoothsayersPalms();
        }
    }

    interface CraftsSamsonsFists extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto() {
            return new SamsonsFists();
        }
    }

    interface CraftsPendragonGauntlets extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto() {
            return new PendragonGauntlets();
        }
    }

    interface CraftsGardeningGloves extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto() {
            return new GardeningGloves();
        }
    }

    interface CraftsMultipurposeKnuckles extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto() {
            return new MultipurposeKnuckles();
        }
    }

    interface CraftsFirewaterAlmanac extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto() {
            return new FirewaterAlmanac();
        }
    }

    interface CraftsThePresentTestament extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto() {
            return new ThePresentTestament();
        }
    }

    interface CraftsAGuideToMMA extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto() {
            return new AGuideToMMA();
        }
    }

    interface CraftsPansTome extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto() {
            return new PansTome();
        }
    }

    interface CraftsScrollOfUncertainty extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto() {
            return new ScrollOfUncertainty();
        }
    }

    interface CraftsBucklerPiece extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto() {
            return new BucklerPiece();
        }
    }

    interface CraftsCrossNecklaceCharm extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto() {
            return new CrossNecklaceCharm();
        }
    }

    interface CraftsPridwensBulwark extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto() {
            return new PridwensBulwark();
        }
    }

    interface CraftsAerialAegis extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto() {
            return new AerialAegis();
        }
    }

    interface CraftsShieldOfSnatching extends CraftsInto {
        @Override
        default AbstractItem getCraftsInto() {
            return new ShieldOfSnatching();
        }
    }
}
