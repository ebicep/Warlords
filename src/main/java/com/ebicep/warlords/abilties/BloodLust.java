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

public class BloodLust extends AbstractAbility {

    private final int duration = 15;
    private int damageConvertPercent = 65;

    public BloodLust() {
        super("Blood Lust", 0, 0, 31.32f, 20, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7You lust for blood, healing yourself\n" +
                "§7for §a" + damageConvertPercent + "% §7of all the damage you deal.\n" +
                "§7Lasts §6" + duration + " §7seconds.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player p) {
        wp.subtractEnergy(energyCost);
        BloodLust tempBloodLust = new BloodLust();
        wp.getCooldownManager().addCooldown(new RegularCooldown<BloodLust>(
                name,
                "LUST",
                BloodLust.class,
                tempBloodLust,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                duration * 20
        ) {
            @Override
            public boolean distinct() {
                return true;
            }

            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                WarlordsPlayer attacker = event.getAttacker();
                BloodLust bloodLust = (BloodLust) attacker.getSpec().getBlue();
                attacker.addHealingInstance(attacker, "Blood Lust", currentDamageValue * (bloodLust.getDamageConvertPercent() / 100f), currentDamageValue * (bloodLust.getDamageConvertPercent() / 100f), -1, 100, false, false);
            }
        });

        Utils.playGlobalSound(p.getLocation(), "warrior.bloodlust.activation", 2, 1);

        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                if (wp.getCooldownManager().hasCooldown(tempBloodLust)) {
                    Location location = wp.getLocation();
                    location.add((Math.random() - 0.5) * 1, 1.2, (Math.random() - 0.5) * 1);
                    ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(255, 0, 0), location, 500);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 4);

        return true;
    }

    public int getDamageConvertPercent() {
        return damageConvertPercent;
    }

    public void setDamageConvertPercent(int damageConvertPercent) {
        this.damageConvertPercent = damageConvertPercent;
    }
}
