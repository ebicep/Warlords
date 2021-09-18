package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ArcaneShield extends AbstractAbility {

    public int maxShieldHealth;
    private float shieldHealth = 0;

    private final int duration = 6;

    public ArcaneShield() {
        super("Arcane Shield", 0, 0, 31.32f, 40, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Surround yourself with arcane\n" +
                "§7energy, creating a shield that will\n" +
                "§7absorb up to §e" + maxShieldHealth + " §7(§e50% §7of your maximum\n" +
                "§7health) incoming damage. Lasts §6" + duration + " §7seconds.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        wp.getCooldownManager().addCooldown(name, ArcaneShield.this.getClass(), new ArcaneShield(), "ARCA", duration, wp, CooldownTypes.ABILITY);
        ((EntityLiving) ((CraftPlayer) player).getHandle()).setAbsorptionHearts(20);
        shieldHealth = maxShieldHealth;

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.arcaneshield.activation", 2, 1);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!wp.getCooldownManager().getCooldown(ArcaneShield.class).isEmpty()) {
                    Location location = player.getLocation();
                    location.add(0, 1.5, 0);
                    ParticleEffect.CLOUD.display(0.15F, 0.3F, 0.15F, 0.01F, 2, location, 500);
                    ParticleEffect.FIREWORKS_SPARK.display(0.3F, 0.3F, 0.3F, 0.0001F, 1, location, 500);
                    ParticleEffect.SPELL_WITCH.display(0.3F, 0.3F, 0.3F, 0, 1, location, 500);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 3);
    }

    public float getShieldHealth() {
        return shieldHealth;
    }

    public void addShieldHealth(float amount) {
        this.shieldHealth += amount;
    }

}
