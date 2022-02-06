package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        wp.getSpeed().addSpeedModifier("Infusion", speedBuff, duration * 20, "BASE");
        LightInfusion tempLightInfusion = new LightInfusion(cooldown, energyCost);
        wp.getCooldownManager().addRegularCooldown(name, "INF", LightInfusion.class, tempLightInfusion, wp, CooldownTypes.BUFF, cooldownManager -> {
        }, duration * 20);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.infusionoflight.activation", 2, 1);
        }

        for (int i = 0; i < 10; i++) {
            Location particleLoc = player.getLocation().add(0, 1.5, 0);
            ParticleEffect.SPELL.display(1, 0F, 1, 0.3F, 3, particleLoc, 500);
        }
        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                if (wp.getCooldownManager().hasCooldown(tempLightInfusion)) {
                    Location location = player.getLocation();
                    location.add(0, 1.2, 0);
                    ParticleEffect.SPELL.display(0.3F, 0.1F, 0.3F, 0.2F, 2, location, 500);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 4);

        return true;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
