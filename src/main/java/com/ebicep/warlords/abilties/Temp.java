package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class Temp extends AbstractAbility {

    public Temp() {
        super("Placeholder Ability", 0, 0, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Placeholder Ability");
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        return true;
    }

}