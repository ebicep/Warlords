package com.ebicep.warlords.database.repositories.player.pojos.general.classes;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.ArmorManager;

public abstract class DatabaseBaseGeneral extends AbstractDatabaseStatInformation {

    protected ArmorManager.Helmets helmet = ArmorManager.Helmets.SIMPLE_MAGE_HELMET;
    protected ArmorManager.ArmorSets armor = ArmorManager.ArmorSets.SIMPLE_CHESTPLATE;

    public DatabaseBaseGeneral() {
    }

    @Override
    public void updateCustomStats(
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        //UPDATE SPEC EXPERIENCE
        this.experience += gamePlayer.getExperienceEarnedSpec() * multiplier;
    }

    public ArmorManager.Helmets getHelmet() {
        return helmet;
    }

    public ArmorManager.ArmorSets getArmor() {
        return armor;
    }

    public void setHelmet(ArmorManager.Helmets helmet) {
        this.helmet = helmet;
    }

    public void setArmor(ArmorManager.ArmorSets armor) {
        this.armor = armor;
    }
}
