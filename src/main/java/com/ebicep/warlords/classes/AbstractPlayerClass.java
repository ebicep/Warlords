package com.ebicep.warlords.classes;

import com.ebicep.warlords.abilties.EarthenSpike;
import com.ebicep.warlords.abilties.SoulShackle;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractPlayerClass {

    protected int maxHealth;
    protected int maxEnergy;
    protected float energyPerSec;
    protected float energyOnHit;
    protected int damageResistance;
    protected AbstractAbility weapon;
    protected AbstractAbility red;
    protected AbstractAbility purple;
    protected AbstractAbility blue;
    protected AbstractAbility orange;
    protected boolean abilityCD = true;
    protected boolean secondaryAbilityCD = true;
    protected String name;
    protected String className;
    protected String classNameShort;

    public AbstractPlayerClass(
            String name,
            int maxHealth,
            int maxEnergy,
            int energyPerSec,
            int energyOnHit,
            int damageResistance,
            AbstractAbility weapon,
            AbstractAbility red,
            AbstractAbility purple,
            AbstractAbility blue,
            AbstractAbility orange
    ) {
        this.maxHealth = maxHealth;
        this.maxEnergy = maxEnergy;
        this.energyPerSec = energyPerSec;
        this.energyOnHit = energyOnHit;
        this.damageResistance = damageResistance;
        this.weapon = weapon;
        this.red = red;
        this.purple = purple;
        this.blue = blue;
        this.orange = orange;
        this.name = name;

        updateCustomStats();
    }

    public void updateCustomStats() {
        for (AbstractAbility ability : getAbilities()) {
            ability.updateCustomStats(this);
        }
    }

    public void setUpgradeBranches(WarlordsPlayer wp) {

    }

    public List<BaseComponent[]> getFormattedData() {
        List<BaseComponent[]> baseComponents = new ArrayList<>();
        ChatColor[] chatColors = {
                ChatColor.GREEN,
                ChatColor.RED,
                ChatColor.LIGHT_PURPLE,
                ChatColor.AQUA,
                ChatColor.GOLD,
                ChatColor.GRAY,
                ChatColor.GRAY,
                ChatColor.GRAY,
                ChatColor.GRAY
        };
        for (int i = 0; i < getAbilities().length; i++) {
            AbstractAbility ability = getAbilities()[i];
            baseComponents.add(new ComponentBuilder()
                    .appendHoverText(chatColors[i] + ability.getName(), ability.getAbilityInfo()
                            .stream()
                            .map(stringStringPair -> ChatColor.WHITE + stringStringPair.getA() + ": " + ChatColor.GOLD + stringStringPair.getB())
                            .collect(Collectors.joining("\n")))
                    .create()
            );
        }

        return baseComponents;
    }

    public AbstractAbility[] getAbilities() {
        return new AbstractAbility[]{weapon, red, purple, blue, orange};
    }

    public void onRightClick(@Nonnull WarlordsEntity wp, @Nonnull Player player, int slot, boolean hotkeyMode) {
        // Makes it so abilities cannot be used when the game is over
        if (!wp.isActive()) {
            return;
        }

        if (wp.isDead()) {
            return;
        }

        if (!wp.getGame().isFrozen()) {

            AbstractAbility ability = null;

            switch (slot) {
                case 0:
                    ability = weapon;
                    break;
                case 1:
                    ability = red;
                    break;
                case 2:
                    ability = purple;
                    break;
                case 3:
                    ability = blue;
                    break;
                case 4:
                    ability = orange;
                    break;
            }

            if (ability == null) {
                return;
            }

            if (slot == 0) {
                if (wp.getCooldownManager().hasCooldown(SoulShackle.class)) {
                    player.sendMessage(ChatColor.RED + "You have been silenced!");
                    player.playSound(player.getLocation(), "notreadyalert", 1, 1);
                } else {
                    if (player.getLevel() >= weapon.getEnergyCost() * wp.getEnergyModifier() && abilityCD) {
                        WarlordsAbilityActivateEvent event = new WarlordsAbilityActivateEvent(wp, ability);
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            return;
                        }
                        weapon.onActivate(wp, player);
                        if (!(weapon instanceof AbstractStrikeBase) && !(weapon instanceof EarthenSpike)) {
                            weapon.addTimesUsed();
                            sendRightClickPacket(player);
                        }
                        resetAbilityCD(wp);
                    } else {
                        player.playSound(player.getLocation(), "notreadyalert", 1, 1);
                    }
                }
            } else {
                onRightClickAbility(ability, wp, player);
            }

            if (player.getVehicle() != null) {
                player.getVehicle().remove();
            }

        }

        if (hotkeyMode) {
            player.getInventory().setHeldItemSlot(0);
        }

    }

    public static void sendRightClickPacket(Player player) {
        if (player == null) {
            return;
        }
        PacketPlayOutAnimation playOutAnimation = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 0);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(playOutAnimation);
    }

    private void resetAbilityCD(WarlordsEntity we) {
        abilityCD = false;
        new GameRunnable(we.getGame()) {

            @Override
            public void run() {
                abilityCD = true;
            }
        }.runTaskLater(1);
    }

    public void onRightClickAbility(AbstractAbility ability, WarlordsEntity wp, Player player) {
        if (ability.getCurrentCooldown() != 0) {
            if (secondaryAbilityCD) {
                ability.runSecondAbilities();
                resetSecondaryAbilityCD(wp);
            }
            return;
        }
        if (player.getLevel() >= ability.getEnergyCost() * wp.getEnergyModifier() && abilityCD) {
            WarlordsAbilityActivateEvent event = new WarlordsAbilityActivateEvent(wp, ability);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            boolean shouldApplyCooldown = ability.onActivate(wp, player);
            if (shouldApplyCooldown) {
                ability.addTimesUsed();
                if (!wp.isDisableCooldowns()) {
                    ability.setCurrentCooldown((float) (ability.getCooldown() * wp.getCooldownModifier()));
                }
                sendRightClickPacket(player);
            }
            resetAbilityCD(wp);
        }

    }

    private void resetSecondaryAbilityCD(WarlordsEntity we) {
        secondaryAbilityCD = false;
        new GameRunnable(we.getGame()) {

            @Override
            public void run() {
                secondaryAbilityCD = true;
            }
        }.runTaskLater(5);
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public float getEnergyPerSec() {
        return energyPerSec;
    }

    public void setEnergyPerSec(float energyPerSec) {
        this.energyPerSec = energyPerSec;
    }

    public float getEnergyOnHit() {
        return energyOnHit;
    }

    public void setEnergyOnHit(float energyOnHit) {
        this.energyOnHit = energyOnHit;
    }

    public int getDamageResistance() {
        return damageResistance;
    }

    public void setDamageResistance(int damageResistance) {
        this.damageResistance = Math.max(0, damageResistance);
    }

    public AbstractAbility getWeapon() {
        return weapon;
    }

    public void setWeapon(AbstractAbility weapon) {
        this.weapon = weapon;
    }

    public AbstractAbility getRed() {
        return red;
    }

    public void setRed(AbstractAbility red) {
        this.red = red;
    }

    public AbstractAbility getPurple() {
        return purple;
    }

    public void setPurple(AbstractAbility purple) {
        this.purple = purple;
    }

    public AbstractAbility getBlue() {
        return blue;
    }

    public void setBlue(AbstractAbility blue) {
        this.blue = blue;
    }

    public AbstractAbility getOrange() {
        return orange;
    }

    public void setOrange(AbstractAbility orange) {
        this.orange = orange;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public String getClassNameShort() {
        return classNameShort;
    }

    public String getClassNameShortWithBrackets() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + this.classNameShort + ChatColor.DARK_GRAY + "]";
    }

    public void runEverySecond() {
        this.red.runEverySecond();
        this.blue.runEverySecond();
        this.orange.runEverySecond();
        this.purple.runEverySecond();
        this.weapon.runEverySecond();
    }

    public void runEveryTick() {
        this.red.runEveryTick();
        this.blue.runEveryTick();
        this.orange.runEveryTick();
        this.purple.runEveryTick();
        this.weapon.runEveryTick();
    }

    public void increaseAllCooldownTimersBy(float amount) {
        this.red.addCooldown(amount);
        this.purple.addCooldown(amount);
        this.blue.addCooldown(amount);
        this.orange.addCooldown(amount);
    }

    public void decreaseAllCooldownTimersBy(float amount) {
        this.red.subtractCooldown(amount);
        this.purple.subtractCooldown(amount);
        this.blue.subtractCooldown(amount);
        this.orange.subtractCooldown(amount);
    }
}
