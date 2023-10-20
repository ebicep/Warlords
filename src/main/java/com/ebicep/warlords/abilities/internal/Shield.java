package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.function.Consumer;

public class Shield implements Listener {

    private String name;
    private float maxShieldHealth;
    private float shieldHealth;

    public Shield() {
    }

    public Shield(String name, float maxShieldHealth) {
        this.name = name;
        this.maxShieldHealth = maxShieldHealth;
        this.shieldHealth = maxShieldHealth;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAddCooldown(WarlordsAddCooldownEvent event) {
        WarlordsEntity warlordsEntity = event.getWarlordsEntity();
        Entity entity = warlordsEntity.getEntity();
        AbstractCooldown<?> cooldown = event.getAbstractCooldown();
        if (!(cooldown.getCooldownObject() instanceof Shield shield)) {
            return;
        }
        Consumer<CooldownManager> oldRemoveForce = cooldown.getOnRemoveForce();
        cooldown.setOnRemoveForce(cooldownManager -> {
            WarlordsEntity we = cooldownManager.getWarlordsEntity();
            if (entity instanceof Player player) {
                if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterCooldownClass(Shield.class).stream().count() == 1) {
                    ((CraftPlayer) player).getHandle().setAbsorptionAmount(0);
                } else {
                    double totalShieldHealth = new CooldownFilter<>(we, RegularCooldown.class)
                            .filter(RegularCooldown::hasTicksLeft)
                            .filterCooldownClassAndMapToObjectsOfClass(Shield.class)
                            .mapToDouble(Shield::getShieldHealth)
                            .sum();
                    ((CraftPlayer) player).getHandle().setAbsorptionAmount((float) (totalShieldHealth / we.getMaxHealth() * 40));
                }
            }
            oldRemoveForce.accept(cooldownManager);
        });
        if (entity instanceof Player player) {
            double totalShieldHealth = new CooldownFilter<>(warlordsEntity, RegularCooldown.class)
                    .filterCooldownClassAndMapToObjectsOfClass(Shield.class)
                    .mapToDouble(Shield::getShieldHealth)
                    .sum();
            totalShieldHealth += shield.getShieldHealth();
            ((CraftPlayer) player).getHandle().setAbsorptionAmount((float) (totalShieldHealth / warlordsEntity.getMaxHealth() * 40));
        }
    }

    public float getMaxShieldHealth() {
        return maxShieldHealth;
    }

    public void setMaxShieldHealth(float maxShieldHealth) {
        this.maxShieldHealth = maxShieldHealth;
    }

    public float getShieldHealth() {
        return shieldHealth;
    }

    public void setShieldHealth(float shieldHealth) {
        this.shieldHealth = shieldHealth;
    }

    public String getName() {
        return name;
    }

    public void addShieldHealth(float damage) {
        shieldHealth += damage;
        if (shieldHealth > maxShieldHealth) {
            shieldHealth = maxShieldHealth;
        }
    }
}
