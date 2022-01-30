package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.ParticleEffect;
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
    public void onActivate(WarlordsPlayer wp, Player p) {
        wp.subtractEnergy(energyCost);
        wp.getCooldownManager().addCooldown(name, BloodLust.this.getClass(), new BloodLust(), "LUST", duration, wp, CooldownTypes.ABILITY);

        for (Player player1 : p.getWorld().getPlayers()) {
            player1.playSound(p.getLocation(), "warrior.bloodlust.activation", 2, 1);
        }

        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                if (!wp.getCooldownManager().getCooldown(BloodLust.class).isEmpty()) {
                    Location location = p.getLocation();
                    location.add((Math.random() - 0.5) * 1, 1.2, (Math.random() - 0.5) * 1);
                    ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(255, 0, 0), location, 500);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 4);
    }

    public int getDamageConvertPercent() {
        return damageConvertPercent;
    }

    public void setDamageConvertPercent(int damageConvertPercent) {
        this.damageConvertPercent = damageConvertPercent;
    }
}
