package com.ebicep.warlords.classes;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.ArcaneShield;
import com.ebicep.warlords.abilties.EarthenSpike;
import com.ebicep.warlords.abilties.SoulShackle;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.TextComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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

    public AbstractPlayerClass(String name, int maxHealth, int maxEnergy, int energyPerSec, int energyOnHit, int damageResistance, AbstractAbility weapon, AbstractAbility red, AbstractAbility purple, AbstractAbility blue, AbstractAbility orange) {
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

        if (blue instanceof ArcaneShield) {
            ArcaneShield arcaneShield = ((ArcaneShield) blue);
            arcaneShield.setMaxShieldHealth((int) (maxHealth * (arcaneShield.getShieldPercentage() / 100f)));
            blue.updateDescription(null); // Arcaneshield does not use the player in its description
        }
    }

    public void setUpgradeBranches(WarlordsPlayer wp) {

    }

    public List<TextComponent> getFormattedData() {
        List<TextComponent> textComponentList = new ArrayList<>();
        ChatColor[] chatColors = {ChatColor.GREEN, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.AQUA, ChatColor.GOLD, ChatColor.GRAY, ChatColor.GRAY, ChatColor.GRAY, ChatColor.GRAY};
        for (int i = 0; i < getAbilities().length; i++) {
            AbstractAbility ability = getAbilities()[i];
            textComponentList.add(new TextComponentBuilder(chatColors[i] + ability.getName())
                    .setHoverText(ability.getAbilityInfo().stream()
                            .map(stringStringPair -> ChatColor.WHITE + stringStringPair.getA() + ": " + ChatColor.GOLD + stringStringPair.getB())
                            .collect(Collectors.joining("\n"))
                    )
                    .getTextComponent());
        }

        return textComponentList;
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

            switch (slot) {
                case 0:
                    if (wp.getCooldownManager().hasCooldown(SoulShackle.class)) {
                        player.sendMessage(ChatColor.RED + "You have been silenced!");
                        player.playSound(player.getLocation(), "notreadyalert", 1, 1);
                        break;
                    }
                    if (player.getLevel() >= weapon.getEnergyCost() * wp.getEnergyModifier() && abilityCD) {
                        weapon.onActivate(wp, player);
                        if (!(weapon instanceof AbstractStrikeBase) && !(weapon instanceof EarthenSpike)) {
                            weapon.addTimesUsed();
                            sendRightClickPacket(player);
                        }
                        resetAbilityCD();
                    } else {
                        player.playSound(player.getLocation(), "notreadyalert", 1, 1);
                    }
                    break;
                case 1:
                    onRightClickAbility(red, wp, player);
                    break;
                case 2:
                    onRightClickAbility(purple, wp, player);
                    break;
                case 3:
                    onRightClickAbility(blue, wp, player);
                    break;
                case 4:
                    onRightClickAbility(orange, wp, player);
                    break;
            }

            if (slot == 0 || slot == 1 || slot == 2 || slot == 3 || slot == 4) {
                if (player.getVehicle() != null) {
                    player.getVehicle().remove();
                }
            }

        }

        if (hotkeyMode) {
            player.getInventory().setHeldItemSlot(0);
        }

    }

    private void onRightClickAbility(AbstractAbility ability, WarlordsEntity wp, Player player) {
        if (ability.getCurrentCooldown() != 0) {
            if (secondaryAbilityCD) {
                ability.runSecondAbilities();
                resetSecondaryAbilityCD();
            }
            return;
        }
        if (player.getLevel() >= ability.getEnergyCost() * wp.getEnergyModifier() && abilityCD) {
            boolean shouldApplyCooldown = ability.onActivate(wp, player);
            if (shouldApplyCooldown) {
                ability.addTimesUsed();
                ability.setCurrentCooldown((float) (ability.getCooldown() * wp.getCooldownModifier()));
                sendRightClickPacket(player);
            }
            resetAbilityCD();
        }

    }

    private void resetAbilityCD() {
        abilityCD = false;
        new BukkitRunnable() {

            @Override
            public void run() {
                abilityCD = true;
            }
        }.runTaskLater(Warlords.getInstance(), 1);
    }

    private void resetSecondaryAbilityCD() {
        secondaryAbilityCD = false;
        new BukkitRunnable() {

            @Override
            public void run() {
                secondaryAbilityCD = true;
            }
        }.runTaskLater(Warlords.getInstance(), 5);
    }

    public static void sendRightClickPacket(Player player) {
        PacketPlayOutAnimation playOutAnimation = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 0);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(playOutAnimation);
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
        if (damageResistance < 0) {
            damageResistance = 0;
        }
        return damageResistance;
    }

    public void setDamageResistance(int damageResistance) {
        this.damageResistance = damageResistance;
    }

    public AbstractAbility[] getAbilities() {
        return new AbstractAbility[]{weapon, red, purple, blue, orange};
    }

    public AbstractAbility getWeapon() {
        return weapon;
    }

    public AbstractAbility getRed() {
        return red;
    }

    public AbstractAbility getPurple() {
        return purple;
    }

    public AbstractAbility getBlue() {
        return blue;
    }

    public AbstractAbility getOrange() {
        return orange;
    }

    public void setWeapon(AbstractAbility weapon) {
        this.weapon = weapon;
    }

    public void setRed(AbstractAbility red) {
        this.red = red;
    }

    public void setPurple(AbstractAbility purple) {
        this.purple = purple;
    }

    public void setBlue(AbstractAbility blue) {
        this.blue = blue;
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
}
