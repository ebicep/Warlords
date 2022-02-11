package com.ebicep.warlords.game.option;

import com.ebicep.warlords.game.Game;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Adds an item in the lobby screen
 */
public class PreGameItemOption implements Option {
    private int slot;
    @Nonnull
    private BiFunction<Game, Player, ItemStack> item;
    @Nullable
    private BiConsumer<Game, Player> onClick;

    public PreGameItemOption(int slot, @Nullable ItemStack item) {
        this(slot, item, null);
    }

    public PreGameItemOption(int slot, @Nullable ItemStack item, @Nullable BiConsumer<Game, Player> onClick) {
        this(slot, (g, p) -> item, onClick);
    }

    public PreGameItemOption(int slot, @Nonnull BiFunction<Game, Player, ItemStack> item) {
        this(slot, item, null);
    }

    public PreGameItemOption(int slot, @Nonnull BiFunction<Game, Player, ItemStack> item, @Nullable BiConsumer<Game, Player> onClick) {
        this.slot = slot;
        this.item = Objects.requireNonNull(item, "item");
        this.onClick = onClick;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public ItemStack getItem(Game game, Player player) {
        return item.apply(game, player);
    }

    public BiFunction<Game, Player, ItemStack> getItem() {
        return item;
    }

    public void setItem(BiFunction<Game, Player, ItemStack> item) {
        this.item = item;
    }

    public BiConsumer<Game, Player> getOnClick() {
        return onClick;
    }

    public void setOnClick(BiConsumer<Game, Player> onClick) {
        this.onClick = onClick;
    }

    public void runOnClick(Game game, Player player) {
        if (this.onClick != null) {
            this.onClick.accept(game, player);
        }
    }

}
