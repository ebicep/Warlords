package com.ebicep.warlords.classes;

import com.ebicep.warlords.abilties.EarthenSpike;
import com.ebicep.warlords.abilties.SoulShackle;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPlayerClass {

    protected int maxHealth;
    protected int maxEnergy;
    protected float energyPerSec;
    protected float energyPerHit;
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
            int energyPerHit,
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
        this.energyPerHit = energyPerHit;
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

    public AbstractAbility[] getAbilities() {
        return new AbstractAbility[]{weapon, red, purple, blue, orange};
    }

    public void setUpgradeBranches(WarlordsPlayer wp) {

    }

    public List<Component> getFormattedData() {
        NamedTextColor[] textColors = {
                NamedTextColor.GREEN,
                NamedTextColor.RED,
                NamedTextColor.LIGHT_PURPLE,
                NamedTextColor.AQUA,
                NamedTextColor.GOLD
        };
        List<Component> components = new ArrayList<>();
        for (int i = 0; i < getAbilities().length; i++) {
            AbstractAbility ability = getAbilities()[i];
            TextComponent.Builder abilityInfo = Component.text();
            ability.getAbilityInfo().forEach(stringStringPair -> {
                abilityInfo.append(Component.text(stringStringPair.getA() + ": ", NamedTextColor.WHITE))
                           .append(Component.text(stringStringPair.getB(), NamedTextColor.GOLD));
                abilityInfo.append(Component.newline());
            });
            components.add(Component.text(textColors[i] + ability.getName())
                                    .hoverEvent(HoverEvent.showText(abilityInfo))
            );
        }

        return components;
    }

    public AbstractAbility[] getAbilitiesExcludingWeapon() {
        return new AbstractAbility[]{red, purple, blue, orange};
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

            AbstractAbility ability = switch (slot) {
                case 0 -> weapon;
                case 1 -> red;
                case 2 -> purple;
                case 3 -> blue;
                case 4 -> orange;
                default -> null;
            };

            if (ability == null) {
                return;
            }

            if (slot == 0) {
                if (wp.getCooldownManager().hasCooldown(SoulShackle.class)) {
                    player.sendMessage(Component.text("You have been silenced!", NamedTextColor.RED));
                    player.playSound(player.getLocation(), "notreadyalert", 1, 1);
                } else {
                    if (player.getLevel() >= weapon.getEnergyCost() * wp.getEnergyModifier() && abilityCD) {
                        WarlordsAbilityActivateEvent event = new WarlordsAbilityActivateEvent(wp, player, ability);
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
        PacketUtils.playRightClickAnimationForPlayer(((CraftPlayer) player).getHandle(), player);
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
            WarlordsAbilityActivateEvent event = new WarlordsAbilityActivateEvent(wp, player, ability);
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

    public float getEnergyPerHit() {
        return energyPerHit;
    }

    public void setEnergyPerHit(float energyPerHit) {
        this.energyPerHit = energyPerHit;
    }

    public int getDamageResistance() {
        return damageResistance;
    }

    public void setDamageResistance(int damageResistance) {
        this.damageResistance = damageResistance;
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

    public Component getClassNameShortWithBrackets() {
        return Component.text("[", NamedTextColor.DARK_GRAY)
                        .append(Component.text(this.classNameShort, NamedTextColor.GOLD))
                        .append(Component.text("]", NamedTextColor.DARK_GRAY));
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
