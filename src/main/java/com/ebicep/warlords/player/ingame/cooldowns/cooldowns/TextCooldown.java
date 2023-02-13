package com.ebicep.warlords.player.ingame.cooldowns.cooldowns;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import org.bukkit.ChatColor;

import java.util.function.Consumer;

/**
 * This type of cooldown is used for text that should be displayed on the action bar and only gets removed on a specified condition
 */
public class TextCooldown<T> extends AbstractCooldown<T> {

    protected boolean remove = false;
    protected String text;

    public TextCooldown(
            String name,
            String nameAbbreviation,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            String text
    ) {
        super(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, onRemove);
        this.text = text;
    }

    @Override
    public String getNameAbbreviation() {
        return ChatColor.GREEN + nameAbbreviation + ChatColor.GRAY + ":" + ChatColor.GOLD + text;
    }

    @Override
    public void onTick(WarlordsEntity from) {

    }

    @Override
    public boolean removeCheck() {
        return remove;
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
