package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLightInfusion extends AbstractAbility implements PurpleAbilityIcon, Duration {

    protected static void playCastEffect(@Nonnull WarlordsEntity wp) {
        for (int i = 0; i < 10; i++) {
            EffectUtils.displayParticle(
                    Particle.SPELL,
                    wp.getLocation().add(0, 1.5, 0),
                    3,
                    1,
                    0,
                    1,
                    .3
            );
        }
    }

    protected int tickDuration = 60;
    protected int speedBuff = 40;
    protected int energyGiven = 120;

    public AbstractLightInfusion(float cooldown) {
        super("Light Infusion", cooldown, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("You become infused with light, restoring ")
                               .append(Component.text(energyGiven, NamedTextColor.YELLOW))
                               .append(Component.text(" energy and increasing your movement speed by "))
                               .append(Component.text(speedBuff + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" for "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));

        return info;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public int getSpeedBuff() {
        return speedBuff;
    }

    public void setSpeedBuff(int speedBuff) {
        this.speedBuff = speedBuff;
    }

    public int getEnergyGiven() {
        return energyGiven;
    }

    public void setEnergyGiven(int energyGiven) {
        this.energyGiven = energyGiven;
    }


}
