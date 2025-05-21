package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.PlayerNameInstance;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public record WoundingData(float amount) {

    public static void applyNewWoundingInit(WarlordsEntity target) {
        if (!(target.getCooldownManager().hasCooldown(WoundingData.class))) {
            target.sendMessage(Component.text("You are ", NamedTextColor.GRAY)
                                        .append(Component.text("wounded", NamedTextColor.RED))
                                        .append(Component.text(".", NamedTextColor.GRAY)));
        }
        target.getCooldownManager().removePreviousWounding();
    }

    public static void sendWoundExpired(WarlordsEntity target) {
        CooldownManager cooldownManager = target.getCooldownManager();
        if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterCooldownClass(WoundingData.class).stream().count() == 1) {
            target.sendMessage(Component.text("You are no longer ", NamedTextColor.GRAY)
                                        .append(Component.text("wounded", NamedTextColor.RED))
                                        .append(Component.text(".", NamedTextColor.GRAY)));
        }
    }

    public static PlayerNameInstance.PlayerNameData getSuffixFromOther(WarlordsEntity from, WarlordsEntity to) {
        return new PlayerNameInstance.PlayerNameData(
                Component.text("WND", NamedTextColor.RED),
                we -> we == from || (we.isTeammate(to) && we.getSpecClass().specType == SpecType.HEALER)
        );
    }

}
