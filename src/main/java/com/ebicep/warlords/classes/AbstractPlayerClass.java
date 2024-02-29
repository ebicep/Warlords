package com.ebicep.warlords.classes;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractPlayerClass {

    protected int maxHealth;
    protected int maxEnergy;
    protected float energyPerSec;
    protected float energyPerHit;
    protected float damageResistance;
    protected List<AbstractAbility> abilities;
    protected int abilityGroup = 0; // each group is 4 abilities, excluding weapon
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
            AbstractAbility... abilities
    ) {
        this.maxHealth = maxHealth;
        this.maxEnergy = maxEnergy;
        this.energyPerSec = energyPerSec;
        this.energyPerHit = energyPerHit;
        this.damageResistance = damageResistance;
        this.abilities = new ArrayList<>(List.of(abilities));
        this.name = name;

        updateCustomStats();
    }

    public void updateCustomStats() {
        for (AbstractAbility ability : getAbilities()) {
            ability.updateCustomStats(this);
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
        NamedTextColor[] textColors = {
                NamedTextColor.GREEN,
                NamedTextColor.RED,
                NamedTextColor.LIGHT_PURPLE,
                NamedTextColor.AQUA,
                NamedTextColor.GOLD
        };
        List<Component> components = new ArrayList<>();
        for (int i = 0; i < abilities.size(); i++) {
            AbstractAbility ability = abilities.get(i);
            TextComponent.Builder abilityInfo = Component.text();
            List<Pair<String, String>> info = ability.getAbilityInfo();
            if (info != null) {
                info.forEach(stringStringPair -> {
                    abilityInfo.append(Component.text(stringStringPair.getA() + ": ", NamedTextColor.WHITE))
                               .append(Component.text(stringStringPair.getB(), NamedTextColor.GOLD));
                    abilityInfo.append(Component.newline());
                });
            }
            components.add(Component.text(ability.getName(), textColors[i])
                                    .hoverEvent(HoverEvent.showText(abilityInfo))
            );
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

            if (player.getVehicle() != null) {
                player.getVehicle().remove();
            }

        }
        if (hotkeyMode) {
            player.getInventory().setHeldItemSlot(0);
        }
    }

    public void onRightClickAbility(AbstractAbility ability, WarlordsEntity wp, Player player, int slot) {
        if (ability.getCurrentCooldown() != 0) {
            if (secondaryAbilityCD) {
                ability.runSecondAbilities();
                resetSecondaryAbilityCD(wp);
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

                wp.subtractEnergy(name, ability.getEnergyCost(), false);
                ability.addTimesUsed();
                if (!wp.isDisableCooldowns()) {
                    ability.setCurrentCooldown(ability.getCooldownValue());
                }
                sendRightClickPacket(player);
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

    public static void sendRightClickPacket(WarlordsEntity warlordsEntity) {
        if (!(warlordsEntity.getEntity() instanceof Player player)) {
            return;
        }
        PacketUtils.playRightClickAnimationForPlayer(((CraftPlayer) player).getHandle(), player);
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

    /**
     * https://www.spigotmc.org/attachments/23c935453df410b299e4aee3c8cca21ff94ea98d-png.474751/
     *
     * @param ability
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
        return Component.text("[", NamedTextColor.DARK_GRAY)
                        .append(Component.text(this.classNameShort, NamedTextColor.GOLD))
                        .append(Component.text("]", NamedTextColor.DARK_GRAY));
    }

    public void runEverySecond(@Nullable WarlordsEntity warlordsEntity) {
        abilities.forEach(ability -> ability.runEverySecond(warlordsEntity));
    }

    public void runEveryTick(@Nullable WarlordsEntity warlordsEntity) {
        abilities.forEach(ability -> ability.runEveryTick(warlordsEntity));
    }

    public void increaseAllCooldownTimersBy(float amount) {
        abilities.forEach(ability -> ability.addCurrentCooldown(amount));
    }

    public void decreaseAllCooldownTimersBy(float amount) {
        abilities.forEach(ability -> ability.subtractCurrentCooldown(amount));
    }
}
