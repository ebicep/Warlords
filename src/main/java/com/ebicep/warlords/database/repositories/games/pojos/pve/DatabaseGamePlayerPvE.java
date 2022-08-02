package com.ebicep.warlords.database.repositories.games.pojos.pve;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

public class DatabaseGamePlayerPvE extends DatabaseGamePlayerBase {

    @Field("longest_time_in_combat")
    private int longestTimeInCombat;
    @Field("most_damage_in_round")
    private long mostDamageInRound;
    @Field("most_damage_in_wave")
    private long mostDamageInWave;
    private int prestige;
    private int level;
    private AbstractWeapon weapon;
    @Field("upgrade_log")
    private List<AbilityTree.UpgradeLog> upgradeLog;

    public DatabaseGamePlayerPvE(WarlordsEntity warlordsEntity) {
        super(warlordsEntity);
        if (warlordsEntity instanceof WarlordsPlayer) {
            WarlordsPlayer warlordsPlayer = (WarlordsPlayer) warlordsEntity;
            this.prestige = DatabaseManager.playerService.findByUUID(warlordsPlayer.getUuid()).getSpec(warlordsPlayer.getSpecClass()).getPrestige();
            this.level = ExperienceManager.getLevelForSpec(warlordsPlayer.getUuid(), warlordsPlayer.getSpecClass());
            this.weapon = warlordsPlayer.getAbstractWeapon();
            this.upgradeLog = warlordsPlayer.getAbilityTree().getUpgradeLog();
        }
    }

    public int getLongestTimeInCombat() {
        return longestTimeInCombat;
    }

    public long getMostDamageInRound() {
        return mostDamageInRound;
    }

    public long getMostDamageInWave() {
        return mostDamageInWave;
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
