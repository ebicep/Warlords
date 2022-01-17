package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.ParticleEffect;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ArcaneShield extends AbstractAbility {

    private final int duration = 6;
    public int maxShieldHealth;
    public int shieldPercentage = 50;
    private float shieldHealth = 0;

    public ArcaneShield() {
        super("Arcane Shield", 0, 0, 31.32f, 40, 0, 0);
    }

    public ArcaneShield(int shieldHealth) {
        super("Arcane Shield", 0, 0, 31.32f, 40, 0, 0);
        this.shieldHealth = shieldHealth;
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Surround yourself with arcane\n" +
                "§7energy, creating a shield that will\n" +
                "§7absorb up to §e" + maxShieldHealth + " §7(§e" + shieldPercentage + "% §7of your maximum\n" +
                "§7health) incoming damage. Lasts §6" + duration + " §7seconds.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player p) {
        wp.subtractEnergy(energyCost);
        ArcaneShield tempArcaneShield = new ArcaneShield(maxShieldHealth);
        wp.getCooldownManager().addRegularCooldown(name, "ARCA", ArcaneShield.class, tempArcaneShield, wp, CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterCooldownClass(ArcaneShield.class).getStream().count() == 1) {
                        ((EntityLiving) ((CraftPlayer) wp.getEntity()).getHandle()).setAbsorptionHearts(0);
                    }
                }, duration * 20);
        ((EntityLiving) ((CraftPlayer) p).getHandle()).setAbsorptionHearts(20);

        for (Player player1 : p.getWorld().getPlayers()) {
            player1.playSound(p.getLocation(), "mage.arcaneshield.activation", 2, 1);
        }

        wp.getGame().getGameTasks().put(
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (wp.getCooldownManager().hasCooldown(tempArcaneShield)) {
                            Location location = p.getLocation();
                            location.add(0, 1.5, 0);
                            ParticleEffect.CLOUD.display(0.15F, 0.3F, 0.15F, 0.01F, 2, location, 500);
                            ParticleEffect.FIREWORKS_SPARK.display(0.3F, 0.3F, 0.3F, 0.0001F, 1, location, 500);
                            ParticleEffect.SPELL_WITCH.display(0.3F, 0.3F, 0.3F, 0, 1, location, 500);
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Warlords.getInstance(), 0, 3),
                System.currentTimeMillis()
        );

        return true;
    }

    public float getShieldHealth() {
        return shieldHealth;
    }

    public void addShieldHealth(float amount) {
        this.shieldHealth += amount;
    }

    public void setMaxShieldHealth(int maxShieldHealth) {
        this.maxShieldHealth = maxShieldHealth;
    }

    public int getShieldPercentage() {
        return shieldPercentage;
    }

    public void setShieldPercentage(int shieldPercentage) {
        this.shieldPercentage = shieldPercentage;
    }
}
