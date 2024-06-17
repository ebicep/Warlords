package com.ebicep.warlords.player.ingame.instances.type;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.kyori.adventure.text.TextComponent;

import java.util.function.Predicate;

/**
 * <p>FromSelf - used on self cooldowns to others</p>
 * <p>FromEnemy - used on others cooldowns for others</p>
 */
public interface PlayerNameInstance {

    default boolean changesPlayerName() {
        return addPrefixFromSelf() != null ||
                addSuffixFromSelf() != null ||
                addPrefixFromOther() != null ||
                addSuffixFromOther() != null;
    }

    default PlayerNameData addPrefixFromSelf() {
        return null;
    }

    default PlayerNameData addSuffixFromSelf() {
        return null;
    }

    default PlayerNameData addPrefixFromOther() {
        return null;
    }

    default PlayerNameData addSuffixFromOther() {
        return null;
    }

    record PlayerNameData(TextComponent text, Predicate<WarlordsEntity> displayPredicate) {
        public PlayerNameData(TextComponent text, WarlordsEntity target) {
            this(text, entity -> entity == target);
        }
    }

}
