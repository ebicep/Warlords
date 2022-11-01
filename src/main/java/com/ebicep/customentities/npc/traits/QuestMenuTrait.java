package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.pve.quests.QuestsMenu;
import net.citizensnpcs.api.event.NPCRightClickEvent;

public class QuestMenuTrait extends WarlordsTrait {

    public QuestMenuTrait() {
        super("QuestMenuTrait");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        QuestsMenu.openQuestMenu(event.getClicker());
    }

}
