package com.ebicep.warlords.player.cooldowns.cooldowns;

import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownManager;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import org.bukkit.ChatColor;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * This type of cooldown is used for any cooldown thats objects need to persist and should be removed based on the objects condition,
 * ex. Soulbinding people or orbs produced right before the cooldown is about to expire
 */
public class PersistentCooldown<T> extends RegularCooldown<T> {

    protected Predicate<T> objectCheck;
    protected boolean hidden = false;

    public PersistentCooldown(String name, String nameAbbreviation, Class<T> cooldownClass, T cooldownObject, WarlordsPlayer from, CooldownTypes cooldownType, Consumer<CooldownManager> onRemove, int ticksLeft, Predicate<T> objectCheck) {
        super(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, onRemove, ticksLeft);
        this.objectCheck = objectCheck;
    }

    @Override
    public String getNameAbbreviation() {
        if (hidden) return "";
        return ChatColor.GREEN + nameAbbreviation + ChatColor.GRAY + ":" + ChatColor.GOLD + (ticksLeft / 20 + 1) + " ";
    }

    @Override
    public boolean removeCheck() {
        if (ticksLeft <= 0) {
            if (objectCheck.test(cooldownObject)) {
                return true;
            } else {
                hidden = true;
            }
        }
        return false;
    }

    public boolean isShown() {
        return !hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
