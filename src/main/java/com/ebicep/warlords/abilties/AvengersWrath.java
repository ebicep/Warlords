package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.ingame.AbstractWarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AvengersWrath extends AbstractAbility {
    protected int extraPlayersStruck = 0;

    private int duration = 12;

    public AvengersWrath() {
        super("Avenger's Wrath", 0, 0, 52.85f, 0, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Burst with incredible holy power,\n" +
                "§7causing your Avenger's Strikes to\n" +
                "§7hit up to §e2 §7additional enemies\n" +
                "§7that are within §e5 §7blocks of your\n" +
                "§7target. Your energy per second is\n" +
                "§7increased by §e20 §7for the duration\n" +
                "§7of the effect. Lasts §6" + duration + " §7seconds.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Extra Players Struck", "" + extraPlayersStruck));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull AbstractWarlordsEntity wp, @Nonnull Player player) {
        Utils.playGlobalSound(wp.getLocation(), "paladin.avengerswrath.activation", 2, 1);

        AvengersWrath tempAvengersWrath = new AvengersWrath();
        wp.getCooldownManager().addRegularCooldown(
                name,
                "WRATH",
                AvengersWrath.class,
                tempAvengersWrath,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                duration * 20,
                (cooldown, ticksLeft, counter) -> {
                    if (counter % 4 == 0) {
                        ParticleEffect.SPELL.display(
                                0.3F,
                                0.1F,
                                0.3F,
                                0.2F,
                                6,
                                wp.getLocation().add(0, 1.2, 0),
                                500
                        );
                    }
                }
        );

        return true;
    }

    public void addExtraPlayersStruck() {
        extraPlayersStruck++;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
