package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LightInfusion extends AbstractAbility {

    private int duration = 3;
    private final int speedBuff = 40;

    public LightInfusion(float cooldown, int energyCost) {
        super("Light Infusion", 0, 0, cooldown, energyCost, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7You become infused with light,\n" +
                "§7restoring §a120 §7energy and\n" +
                "§7increasing your movement speed by\n" +
                "§e" + speedBuff + "% §7for §6" + duration + " §7seconds";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));

        return info;
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        Utils.playGlobalSound(player.getLocation(), "paladin.infusionoflight.activation", 2, 1);

        Runnable cancelSpeed = wp.getSpeed().addSpeedModifier("Infusion", speedBuff, duration * 20, "BASE");

        LightInfusion tempLightInfusion = new LightInfusion(cooldown, energyCost);
        wp.getCooldownManager().addRegularCooldown(
                name,
                "INF",
                LightInfusion.class,
                tempLightInfusion,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    cancelSpeed.run();
                },
                duration * 20,
                (cooldown, ticksLeft) -> {
                    if (ticksLeft % 4 == 0) {
                        ParticleEffect.SPELL.display(
                                0.3f,
                                0.1f,
                                0.3f,
                                0.2f,
                                2,
                                wp.getLocation().add(0, 1.2, 0),
                                500
                        );
                    }
                }
        );

        for (int i = 0; i < 10; i++) {
            ParticleEffect.SPELL.display(
                    1,
                    0,
                    1,
                    0.3f,
                    3,
                    wp.getLocation().add(0, 1.5, 0),
                    500
            );
        }


        return true;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
