package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LightInfusion extends AbstractAbility {
    private boolean pveUpgrade = false;

    private int duration = 3;
    private int speedBuff = 40;
    private int energyGiven = 120;
    private int strikesUsed = 0;

    public LightInfusion(float cooldown) {
        super("Light Infusion", 0, 0, cooldown, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "You become infused with light, restoring §a" + energyGiven +
                " §7energy and increasing your movement speed by §e" + speedBuff +
                "% " + "§7for §6" + duration + " §7seconds";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        strikesUsed = 0;
        wp.addEnergy(wp, name, energyGiven);
        Utils.playGlobalSound(player.getLocation(), "paladin.infusionoflight.activation", 2, 1);

        Runnable cancelSpeed = wp.getSpeed().addSpeedModifier("Infusion", speedBuff, duration * 20, "BASE");

        LightInfusion tempLightInfusion = new LightInfusion(cooldown);
        wp.getCooldownManager().addCooldown(new RegularCooldown<LightInfusion>(
                name,
                "INF",
                LightInfusion.class,
                tempLightInfusion,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    cancelSpeed.run();

                    if (pveUpgrade) {
                        wp.addEnergy(wp, name, 30 * strikesUsed);
                    }
                },
                duration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
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
                })
        ) {
            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (pveUpgrade) {
                    if (event.getAbility().equals("Avenger's Strike")) {
                        strikesUsed++;
                    }
                }
            }
        });

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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }
}
