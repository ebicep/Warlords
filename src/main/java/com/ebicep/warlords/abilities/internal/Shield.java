package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
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
        AbstractCooldown<?> cooldown = event.getAbstractCooldown();
        if (!(cooldown.getCooldownObject() instanceof Shield shield)) {
            return;
        }
        Consumer<CooldownManager> oldRemoveForce = cooldown.getOnRemoveForce();
        cooldown.setOnRemoveForce(cooldownManager -> {
            oldRemoveForce.accept(cooldownManager);
            if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterCooldownClass(Shield.class).stream().count() == 1) {
                warlordsEntity.giveAbsorption(0);
            } else {
                double totalShieldHealth = new CooldownFilter<>(warlordsEntity, RegularCooldown.class)
                        .filter(RegularCooldown::hasTicksLeft)
                        .filterCooldownClassAndMapToObjectsOfClass(Shield.class)
                        .mapToDouble(Shield::getShieldHealth)
                        .sum();
                warlordsEntity.giveAbsorption((float) (totalShieldHealth / warlordsEntity.getMaxHealth() * 40));
            }
        });
        double totalShieldHealth = new CooldownFilter<>(warlordsEntity, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(Shield.class)
                .mapToDouble(Shield::getShieldHealth)
                .sum();
        totalShieldHealth += shield.getShieldHealth();
        warlordsEntity.giveAbsorption((float) (totalShieldHealth / warlordsEntity.getMaxHealth() * 40));
    }

    public float getShieldHealth() {
        return shieldHealth;
    }

    public void setShieldHealth(float shieldHealth) {
        this.shieldHealth = shieldHealth;
    }

    public float getMaxShieldHealth() {
        return maxShieldHealth;
    }

    public void setMaxShieldHealth(float maxShieldHealth) {
        this.maxShieldHealth = maxShieldHealth;
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
