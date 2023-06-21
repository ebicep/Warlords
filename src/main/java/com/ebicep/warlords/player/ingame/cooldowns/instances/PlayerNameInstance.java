package com.ebicep.warlords.player.ingame.cooldowns.instances;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.kyori.adventure.text.TextComponent;

import java.util.Collection;

/**
 * <p>FromSelf - used on self cooldowns to others</p>
 * <p>FromEnemy - used on others cooldowns for others</p>
 */
public interface PlayerNameInstance {

    default PlayerNameData addPrefixFromSelf() {
        return null;
    }

    default PlayerNameData addSuffixFromSelf() {
        return null;
    }

    default PlayerNameData addPrefixFromEnemy() {
        return null;
    }

    default PlayerNameData addSuffixFromEnemy() {
        return null;
    }

    record PlayerNameData(TextComponent text, Collection<WarlordsEntity> targets) {
    }

}
