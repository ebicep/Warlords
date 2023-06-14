package com.ebicep.warlords.abilties.internal;

import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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

    @EventHandler
    private void onAddCooldown(WarlordsAddCooldownEvent event) {
        org.bukkit.entity.LivingEntity livingEntity = event.getWarlordsEntity().getEntity();
        AbstractCooldown<?> cooldown = event.getAbstractCooldown();
        if (cooldown.getCooldownClass().equals(Shield.class)) {
            Consumer<CooldownManager> oldRemoveForce = cooldown.getOnRemoveForce();
            cooldown.setOnRemoveForce(cooldownManager -> {
                if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterCooldownClass(Shield.class).stream().count() == 1) {
                    if (livingEntity instanceof Player player) {
                        ((LivingEntity) ((CraftPlayer) player).getHandle()).setAbsorptionAmount(0);
                    }
                }
                oldRemoveForce.accept(cooldownManager);
            });
            if (livingEntity instanceof Player player) {
                ((LivingEntity) ((CraftPlayer) player).getHandle()).setAbsorptionAmount(20);
            }
        }
    }

    public String getName() {
        return name;
    }

    public float getShieldHealth() {
        return shieldHealth;
    }

    public void addShieldHealth(float damage) {
        shieldHealth += damage;
        if (shieldHealth > maxShieldHealth) {
            shieldHealth = maxShieldHealth;
        }
    }

    public void setShieldHealth(float shieldHealth) {
        this.shieldHealth = shieldHealth;
    }
}
