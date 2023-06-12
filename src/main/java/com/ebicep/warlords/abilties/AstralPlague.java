package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class AstralPlague extends AbstractAbility implements Duration {

    private int energyIncrease = 5;
    private int tickDuration = 240;
    private int fieldRange = 6;
    private int damageIncrease = 15;

    public AstralPlague() {
        super("Astral Plague", 0, 0, 50, 50, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
//        description = Component.text("Grant yourself Astral Energy, causing your attacks to inflict 3 stacks of Poisonous Hex instantly and increase your energy per second by ")
//                               .append(Component.text(energyIncrease, NamedTextColor.YELLOW))
//                               .append(Component.text(". Lasts "))
//                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
//                               .append(Component.text(" seconds. "))
//                               .append(Component.text(
//                                       "\n\nAdditionally, conjure a field of " + fieldRange + " blocks around the Conjurer. Enemies within range take "))
//                               .append(Component.text(damageIncrease + "%", NamedTextColor.RED))
//                               .append(Component.text(" more damage from all sources."))
//                               .append(Component.text(format(shieldTickDuration / 20f), NamedTextColor.GOLD))
//                               .append(Component.text(" seconds. Not reactivating the ability will instead make all enemies around you in a " +
//                                       vulnerableRange + " block range around you vulnerable to Poisonous Hex. Increasing the damage it deals by "))
//                               .append(Component.text(damageIncrease + "%", NamedTextColor.RED))
//                               .append(Component.text(" for "))
//                               .append(Component.text(format(damageIncreaseDuration / 20f), NamedTextColor.GOLD))
//                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
        return false;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }
}
