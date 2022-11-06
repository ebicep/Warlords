package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.pve.StarPieces;
import net.citizensnpcs.api.event.NPCRightClickEvent;

public class StarPieceSynthesizerTrait extends WarlordsTrait {

    public StarPieceSynthesizerTrait() {
        super("StarPieceSynthesizerTrait");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        StarPieces.openStarPieceSynthesizerMenu(event.getClicker());
    }

}

