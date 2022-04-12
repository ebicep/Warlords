package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class IceBarrier extends AbstractAbility {

    private final int duration = 6;
    private int damageReductionPercent = 50;

    public float getDamageReduction() {
        return (100 - damageReductionPercent) / 100f;
    }

    public IceBarrier() {
        super("Ice Barrier", 0, 0, 46.98f, 0, 0, 0);
    }

    public IceBarrier(int damageReductionPercent) {
        super("Ice Barrier", 0, 0, 46.98f, 0, 0, 0);
        this.damageReductionPercent = damageReductionPercent;
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Surround yourself with a layer of\n" +
                "§7of cold air, reducing damage taken by\n" +
                "§c" + damageReductionPercent + "%§7, While active, taking melee\n" +
                "§7damage reduces the attacker's movement\n" +
                "§7speed by §e20% §7for §62 §7seconds. Lasts\n" +
                "§6" + duration + " §7seconds.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));

        return info;
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        Utils.playGlobalSound(player.getLocation(), "mage.icebarrier.activation", 2, 1);

        IceBarrier tempIceBarrier = new IceBarrier(damageReductionPercent);
        wp.getCooldownManager().addCooldown(new RegularCooldown<IceBarrier>(
                name,
                "ICE",
                IceBarrier.class,
                tempIceBarrier,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                duration * 20,
                (cooldown, ticksLeft) -> {
                    if (ticksLeft % 5 == 0) {
                        Location particleLoc = wp.getLocation().add(0, 1.5, 0);
                        ParticleEffect.CLOUD.display(
                                0.2f,
                                0.2f,
                                0.2f,
                                0.001f,
                                1,
                                particleLoc,
                                500
                        );
                        ParticleEffect.FIREWORKS_SPARK.display(
                                0.3f,
                                0.2f,
                                0.3f,
                                0.0001f,
                                1,
                                particleLoc,
                                500
                        );
                    }
                }
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                float newDamageValue = currentDamageValue * getDamageReduction();
                event.getPlayer().addAbsorbed(Math.abs(currentDamageValue - newDamageValue));
                return newDamageValue;
            }
        });

        return true;
    }

    public int getDamageReductionPercent() {
        return damageReductionPercent;
    }

    public void setDamageReductionPercent(int damageReductionPercent) {
        this.damageReductionPercent = damageReductionPercent;
    }
}
