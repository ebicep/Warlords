package com.ebicep.warlords.player.ingame.instances.type;

import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;

import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <p>FromSelf - used on self cooldowns to others</p>
 * <p>FromOther - used on others cooldowns for others</p>
 *
 * <p>Ability authors — three patterns:</p>
 * <ol>
 *   <li><b>Static text</b> — {@code new PlayerNameData(text, predicate)}; dirty on cooldown add/remove only.</li>
 *   <li><b>Shield HP</b> — {@link PlayerNameData#shieldHealth}; dirty is auto-wired when the cooldown
 *       {@link #changesPlayerName()}.</li>
 *   <li><b>Other live values</b> — {@link PlayerNameData#dynamic} plus
 *       {@code CooldownManager.markNameDisplayDirtyIfChanged(before, after)} (or
 *       {@code markNameDisplayDirty()}) when the displayed value changes.</li>
 * </ol>
 *
 * <p>Dirty which entity:</p>
 * <ul>
 *   <li><b>FromOther</b> — dirty the entity that owns the cooldown (their name row changes).</li>
 *   <li><b>FromSelf</b> — dirty the <em>affected target rows</em> (victims whose predicate result
 *       changed), not only the owner. FromSelf overlays are painted onto those target rows for
 *       every viewer.</li>
 * </ul>
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

    record PlayerNameData(
            @Nullable TextComponent staticText,
            @Nullable Supplier<TextComponent> dynamicText,
            Predicate<WarlordsEntity> displayPredicate
    ) {

        private PlayerNameData(Supplier<TextComponent> textSupplier, Predicate<WarlordsEntity> displayPredicate) {
            this(null, textSupplier, displayPredicate);
        }

        public PlayerNameData(TextComponent text, Predicate<WarlordsEntity> displayPredicate) {
            this(text, null, displayPredicate);
        }

        public PlayerNameData(TextComponent text, WarlordsEntity target) {
            this(text, entity -> entity == target);
        }

        public static PlayerNameData dynamic(Supplier<TextComponent> textSupplier, Predicate<WarlordsEntity> predicate) {
            return new PlayerNameData(textSupplier, predicate);
        }

        public static PlayerNameData shieldHealth(Shield shield, Predicate<WarlordsEntity> predicate, TextColor color) {
            return dynamic(() -> Component.text((int) shield.getShieldHealth(), color), predicate);
        }

        public TextComponent resolveText() {
            if (staticText != null) {
                return staticText;
            }
            if (dynamicText != null) {
                return dynamicText.get();
            }
            ChatUtils.MessageType.GAME.sendErrorMessage(new Throwable("PlayerNameData has no text to resolve!"));
            return Component.empty();
        }
    }

}
