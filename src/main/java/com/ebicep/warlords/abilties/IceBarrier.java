package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
    public boolean onActivate(WarlordsPlayer wp, Player player) {
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
                duration * 20
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                float newDamageValue = currentDamageValue * getDamageReduction();
                event.getPlayer().addAbsorbed(Math.abs(currentDamageValue - newDamageValue));
                return newDamageValue;
            }
        });

        Utils.playGlobalSound(player.getLocation(), "mage.icebarrier.activation", 2, 1);

        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                if (wp.getCooldownManager().hasCooldown(tempIceBarrier)) {
                    Location location = wp.getLocation();
                    location.add(0, 1.5, 0);
                    ParticleEffect.CLOUD.display(0.2F, 0.2F, 0.2F, 0.001F, 1, location, 500);
                    ParticleEffect.FIREWORKS_SPARK.display(0.3F, 0.2F, 0.3F, 0.0001F, 1, location, 500);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 5);

        return true;
    }

    public int getDamageReductionPercent() {
        return damageReductionPercent;
    }

    public void setDamageReductionPercent(int damageReductionPercent) {
        this.damageReductionPercent = damageReductionPercent;
    }
}
