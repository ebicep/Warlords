package com.ebicep.warlords.database.repositories.games.pojos.pve;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

public class DatabaseGamePlayerPvE extends DatabaseGamePlayerBase {

    private int prestige;
    private int level;
    private AbstractWeapon weapon;
    @Field("upgrade_log")
    private List<AbilityTree.UpgradeLog> upgradeLog;

    public DatabaseGamePlayerPvE(WarlordsEntity warlordsEntity) {
        super(warlordsEntity);
        if (warlordsEntity instanceof WarlordsPlayer) {
            WarlordsPlayer warlordsPlayer = (WarlordsPlayer) warlordsEntity;
            this.prestige = warlordsPlayer.getPrestige();
            this.level = warlordsPlayer.getLevel();
            this.weapon = warlordsPlayer.getAbstractWeapon();
            this.upgradeLog = warlordsPlayer.getAbilityTree().getUpgradeLog();
        }
    }

    public int getPrestige() {
        return prestige;
    }

    public int getLevel() {
        return level;
    }

    public AbstractWeapon getWeapon() {
        return weapon;
    }

    public List<AbilityTree.UpgradeLog> getUpgradeLog() {
        return upgradeLog;
    }
}
