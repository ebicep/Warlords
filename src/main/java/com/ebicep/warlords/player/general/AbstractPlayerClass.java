package com.ebicep.warlords.player.general;

import com.ebicep.warlords.abilities.internal.AbilityStats;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsPlayerClassRightClickEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.util.bukkit.packets.PacketUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractPlayerClass {

    public static void sendRightClickPacket(WarlordsEntity warlordsEntity) {
        if (!(warlordsEntity.getEntity() instanceof Player player)) {
            return;
        }
        PacketUtils.playRightClickAnimationForPlayer(((CraftPlayer) player).getHandle(), player);
    }

    protected int maxHealth;
    protected int maxEnergy;
    protected int energyPerSec;
    protected int energyPerHit;
    protected float damageResistance;
    protected int speed;
    protected List<AbstractAbility> abilities;
    protected int abilityGroup = 0; // each group is 4 abilities, excluding weapon
    protected boolean abilityCD = true;
    protected boolean secondaryAbilityCD = true;
    protected String name;
    protected String className = "";
    protected String classNameShort = "";

    public AbstractPlayerClass(
            String className,
            String name,
            int maxHealth,
            int maxEnergy,
            int energyPerSec,
            int energyPerHit,
            float damageResistance,
            int speed,
            List<AbstractAbility> abilities
    ) {
        this.className = className;
        this.classNameShort = className.substring(0, 3).toUpperCase();
        this.name = name;
        this.maxHealth = maxHealth;
        this.maxEnergy = maxEnergy;
        this.energyPerSec = energyPerSec;
        this.energyPerHit = energyPerHit;
        this.damageResistance = damageResistance;
        this.speed = speed;
        this.abilities = new ArrayList<>(abilities);
        this.abilities.forEach(abstractAbility -> abstractAbility.init(abstractAbility.getBuilder()));
    }

    public float getSpeed() {
        return speed;
    }

    public void updateCustomStats(WarlordsEntity warlordsEntity) {
        for (AbstractAbility ability : getAbilities()) {
            ability.updateCustomStats(warlordsEntity);
        }
    }

    public List<AbstractAbility> getAbilities() {
        return abilities;
    }

    public void setUpgradeBranches(WarlordsPlayer wp) {
        AbilityTree abilityTree = wp.getAbilityTree();
        List<AbstractUpgradeBranch<?>> branch = abilityTree.getUpgradeBranches();
        abilities.stream()
                 .map((AbstractAbility ability) -> ability.getUpgradeBranch(abilityTree))
                 .filter(Objects::nonNull)
                 .forEach(branch::add);
    }

    public List<Component> getFormattedData() {
        List<Component> components = new ArrayList<>();
        for (AbstractAbility ability : abilities) {
            if (!(ability instanceof AbilityStats<?, ?> abilityStats)) {
                continue;
            }
            components.add(abilityStats.getFormattedData(ability.getAbilityColor()));
        }
        return components;
    }

    public List<AbstractAbility> getAbilitiesExcludingWeapon() {
        return abilities.subList(1, abilities.size());
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

            if (slot > 4) {
                return;
            }

            AbstractAbility ability;
            if (slot == 0) {
                ability = abilities.get(0);
            } else {
                int abilityIndex = abilityGroup * 4 + slot;
                if (abilityIndex >= abilities.size()) {
                    return;
                }
                ability = abilities.get(abilityIndex);
            }

            if (ability == null) {
                return;
            }

            onRightClickAbility(ability, wp, player, slot);

            Bukkit.getPluginManager().callEvent(new WarlordsPlayerClassRightClickEvent(wp));
        }
        if (hotkeyMode) {
            player.getInventory().setHeldItemSlot(0);
        }
    }

    public void onRightClickAbility(AbstractAbility ability, WarlordsEntity wp, Player player, int slot) {
        if (!ability.anyCharges()) {
            if (secondaryAbilityCD && ability.hasActiveSecondaryAbilities()) {
                ability.runSecondAbilities(wp);
                resetSecondaryAbilityCD(wp);
                if (wp.isDisableCooldowns() && ability.getSecondaryAbilities().isEmpty()) {
                    ability.setCurrentCooldown(0);
                }
            } else {
                player.playSound(player.getLocation(), "notreadyalert", 1, 1);
            }
            return;
        }
        if (player.getLevel() >= ability.getEnergyCostValue() * wp.getEnergyModifier() && abilityCD) {
            WarlordsAbilityActivateEvent.Pre pre = new WarlordsAbilityActivateEvent.Pre(wp, player, ability, slot);
            Bukkit.getPluginManager().callEvent(pre);
            if (pre.isCancelled()) {
                return;
            }
            boolean shouldApplyCooldown = ability.onActivate(wp);
            if (shouldApplyCooldown) {
                WarlordsAbilityActivateEvent.Post post = new WarlordsAbilityActivateEvent.Post(wp, player, ability, slot);
                Bukkit.getPluginManager().callEvent(post);

                wp.subtractEnergy(ability.getName(), ability.getEnergyCostValue(), false);
                if (ability instanceof AbilityStats<?, ?> abilityStats) {
                    abilityStats.getAbilityStats().addTimesUsed();
                }
                if (!wp.isDisableCooldowns() || !ability.getSecondaryAbilities().isEmpty()) {
                    ability.useAbility();
                }
                sendRightClickPacket(player);
                WarlordsAbilityActivateEvent.PostApply postApply = new WarlordsAbilityActivateEvent.PostApply(wp, player, ability, slot);
                Bukkit.getPluginManager().callEvent(postApply);
            }
            resetAbilityCD(wp);
        } else {
            player.playSound(player.getLocation(), "notreadyalert", 1, 1);
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

    public static void sendRightClickPacket(Player player) {
        if (player == null) {
            return;
        }
        PacketUtils.playRightClickAnimationForPlayer(((CraftPlayer) player).getHandle(), player);
    }

    public void resetAbilityCD(WarlordsEntity we) {
        abilityCD = false;
        new GameRunnable(we.getGame()) {

            @Override
            public void run() {
                abilityCD = true;
            }
        }.runTaskLater(1);
    }

    /**
     * https://www.spigotmc.org/attachments/23c935453df410b299e4aee3c8cca21ff94ea98d-png.474751/
     *
     * @param ability
     *
     * @return
     */
    public Integer getInventoryAbilityIndex(AbstractAbility ability) {
        int index = abilities.indexOf(ability);
        if (index == 0) {
            return index;
        }
        return switch (abilityGroup) {
            case 0 -> index;
            case 1 -> 22 + index;
            case 2 -> 9 + index;
            case 3 -> -4 + index;
            default -> null;
        };
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public int getEnergyPerSec() {
        return energyPerSec;
    }

    public int getEnergyPerHit() {
        return energyPerHit;
    }

    public float getDamageResistance() {
        return damageResistance;
    }

    public void setDamageResistance(float damageResistance) {
        this.damageResistance = damageResistance;
    }

    public AbstractAbility getWeapon() {
        return abilities.get(0);
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
        return getClassNameShortWithBrackets(NamedTextColor.GOLD);
    }

    public Component getClassNameShortWithBrackets(TextColor classNameColor) {
        return Component.text("[", NamedTextColor.DARK_GRAY)
                        .append(Component.text(this.classNameShort, classNameColor))
                        .append(Component.text("] ", NamedTextColor.DARK_GRAY));
    }

    public void runEverySecond(@Nullable WarlordsEntity warlordsEntity) {
        abilities.forEach(ability -> ability.runEverySecond(warlordsEntity));
    }

    public void runEveryTick(@Nullable WarlordsEntity warlordsEntity) {
        abilities.forEach(ability -> ability.runEveryTick(warlordsEntity));
    }

    public void increaseAllCooldownTimersBy(float amount) {
        abilities.forEach(ability -> {
            if (ability instanceof WeaponAbilityIcon && ability.getCooldownValue() == 0) {
                return;
            }
            ability.addCurrentCooldown(amount);
        });
    }

    public void decreaseAllCooldownTimersBy(float amount) {
        abilities.forEach(ability -> ability.subtractCurrentCooldown(amount));
    }

}
