package com.ebicep.warlords.game.option.marker;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface WeaponDisplayMarker extends GameMarker {

    int weaponDisplayPriority();

    @Nullable
    default List<Component> leftClickDescription(WarlordsPlayer wp, Player player) {
        return null;
    }

}
