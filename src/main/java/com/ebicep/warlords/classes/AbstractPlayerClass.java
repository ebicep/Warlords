package com.ebicep.warlords.classes;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.internal.AbstractChainBase;
import com.ebicep.warlords.classes.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.WarlordsPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public abstract class AbstractPlayerClass {

    protected int maxHealth;
    protected int maxEnergy;
    protected int energyPerSec;
    protected int energyOnHit;
    protected int damageResistance;
    protected AbstractAbility weapon;
    protected AbstractAbility red;
    protected AbstractAbility purple;
    protected AbstractAbility blue;
    protected AbstractAbility orange;
    protected String className;
    protected String classNameShort;

    protected int weaponUpgrade = 0;
    protected int redUpgrade = 0;
    protected int purpleUpgrade = 0;
    protected int blueUpgrade = 0;
    protected int orangeUpgrade = 0;

    public AbstractPlayerClass(int maxHealth, int maxEnergy, int energyPerSec, int energyOnHit, int damageResistance, AbstractAbility weapon, AbstractAbility red, AbstractAbility purple, AbstractAbility blue, AbstractAbility orange) {
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
        if (red.getName().contains("Consecrate")) {
            className = "Paladin";
            classNameShort = "PAL";
        } else if (purple.getName().contains("Time")) {
            className = "Mage";
            classNameShort = "MAG";
        } else if (purple.getName().contains("Ground")) {
            className = "Warrior";
            classNameShort = "WAR";
        } else {
            className = "Shaman";
            classNameShort = "SHA";
        }
        if (blue instanceof ArcaneShield) {
            ((ArcaneShield) blue).maxShieldHealth = maxHealth / 2;
            blue.updateDescription(null); // Arcaneshield does not use the player in its description
        }
    }

    public void onRightClick(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {

        // Makes it so abilities cannot be used when the game is over
        if (wp.getGameState() != wp.getGame().getState()) {
            return;
        }

        if (wp.isDeath()) {
            return;
        }
        if (player.getInventory().getHeldItemSlot() == 0) {
            if (player.getLevel() >= weapon.getEnergyCost() * wp.getEnergyModifier()) {
                weapon.onActivate(wp, player);
                if (!(weapon instanceof AbstractStrikeBase) && !(weapon instanceof EarthenSpike)) {
                    sendRightClickPacket(player);
                }
            } else {
                player.sendMessage("§cYou can't do that yet!");
                player.playSound(player.getLocation(), "notreadyalert", 1, 1);
            }

        } else if (player.getInventory().getHeldItemSlot() == 1) {
            if (red.getCurrentCooldown() == 0) {
                if (player.getLevel() >= red.getEnergyCost() * wp.getEnergyModifier()) {
                    red.onActivate(wp, player);
                    if (!(red instanceof AbstractChainBase)) {
                        red.setCurrentCooldown((float) (red.cooldown * wp.getCooldownModifier()));
                        sendRightClickPacket(player);
                    }
                }
            }
        } else if (player.getInventory().getHeldItemSlot() == 2) {
            if (purple.getCurrentCooldown() == 0) {
                if (player.getLevel() >= purple.getEnergyCost() * wp.getEnergyModifier()) {
                    purple.onActivate(wp, player);
                    purple.setCurrentCooldown((float) (purple.cooldown * wp.getCooldownModifier()));
                    sendRightClickPacket(player);
                }
            }
        } else if (player.getInventory().getHeldItemSlot() == 3) {
            if (blue.getCurrentCooldown() == 0) {
                if (player.getLevel() >= blue.getEnergyCost() * wp.getEnergyModifier()) {
                    blue.onActivate(wp, player);
                    if (!(blue instanceof AbstractChainBase) && !(blue instanceof Intervene)) {
                        blue.setCurrentCooldown((float) (blue.cooldown * wp.getCooldownModifier()));
                        sendRightClickPacket(player);
                    }
                }
            }
        } else if (player.getInventory().getHeldItemSlot() == 4) {
            if (orange.getCurrentCooldown() == 0 && player.getLevel() >= orange.getEnergyCost() * wp.getEnergyModifier()) {
                orange.onActivate(wp, player);
                if (!(orange instanceof HammerOfLight) && !(orange instanceof HealingRain)) {
                    orange.setCurrentCooldown((float) (orange.cooldown * wp.getCooldownModifier()));
                    sendRightClickPacket(player);
                }
            }
        }
        if (player.getVehicle() != null) {
            player.getVehicle().remove();
        }
    }

    public void onRightClickHotKey(WarlordsPlayer wp, Player player, int slot) {

        // Makes it so abilities cannot be used when the game is over
        if (wp.getGameState() != wp.getGame().getState()) {
            return;
        }

        if (wp.isDeath()) {
            return;
        }
        if (slot == 0) {
            if (player.getLevel() >= weapon.getEnergyCost() * wp.getEnergyModifier()) {
                weapon.onActivate(wp, player);
                if (!(weapon instanceof AbstractStrikeBase) && !(weapon instanceof EarthenSpike))
                    sendRightClickPacket(player);
            } else {
                player.sendMessage("§cYou can't do that yet!");
                player.playSound(player.getLocation(), "notreadyalert", 1, 1);
            }

        } else if (slot == 1) {
            if (red.getCurrentCooldown() == 0) {
                if (player.getLevel() >= red.getEnergyCost() * wp.getEnergyModifier()) {
                    red.onActivate(wp, player);
                    if (!(red instanceof AbstractChainBase)) {
                        red.setCurrentCooldown((float) (red.cooldown * wp.getCooldownModifier()));
                        sendRightClickPacket(player);
                    }
                }

            }
        } else if (slot == 2) {
            if (purple.getCurrentCooldown() == 0) {
                if (player.getLevel() >= purple.getEnergyCost() * wp.getEnergyModifier()) {
                    purple.onActivate(wp, player);
                    purple.setCurrentCooldown((float) (purple.cooldown * wp.getCooldownModifier()));
                    sendRightClickPacket(player);
                }
            }
        } else if (slot == 3) {
            if (blue.getCurrentCooldown() == 0) {
                if (player.getLevel() >= blue.getEnergyCost() * wp.getEnergyModifier()) {
                    blue.onActivate(wp, player);
                    if (!(blue instanceof AbstractChainBase) && !(blue instanceof Intervene)) {
                        blue.setCurrentCooldown((float) (blue.cooldown * wp.getCooldownModifier()));
                        sendRightClickPacket(player);
                    }
                }
            }
        } else if (slot == 4) {
            if (orange.getCurrentCooldown() == 0) {
                if (player.getLevel() >= orange.getEnergyCost() * wp.getEnergyModifier()) {
                    orange.onActivate(wp, player);
                    if (!(orange instanceof HammerOfLight) && !(orange instanceof HealingRain)) {
                        orange.setCurrentCooldown((float) (orange.cooldown * wp.getCooldownModifier()));
                        sendRightClickPacket(player);
                    }
                }
            }
        }
        player.getInventory().setHeldItemSlot(0);

        if (player.getVehicle() != null) {
            player.getVehicle().remove();
        }
    }

    private void sendRightClickPacket(Player player) {
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

    public int getEnergyPerSec() {
        return energyPerSec;
    }

    public void setEnergyPerSec(int energyPerSec) {
        this.energyPerSec = energyPerSec;
    }

    public int getEnergyOnHit() {
        return energyOnHit;
    }

    public void setEnergyOnHit(int energyOnHit) {
        this.energyOnHit = energyOnHit;
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

    public String getClassName() {
        return className;
    }

    public String getClassNameShort() {
        return classNameShort;
    }

    public String getClassNameShortWithBrackets() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + this.classNameShort + ChatColor.DARK_GRAY + "]";
    }

    public int getWeaponUpgrade() {
        return weaponUpgrade;
    }

    public void addWeaponUpgrade() {
        this.weaponUpgrade++;
    }

    public int getRedUpgrade() {
        return redUpgrade;
    }

    public void addRedUpgrade() {
        this.redUpgrade++;
    }

    public int getPurpleUpgrade() {
        return purpleUpgrade;
    }

    public void addPurpleUpgrade() {
        this.purpleUpgrade++;
    }

    public int getBlueUpgrade() {
        return blueUpgrade;
    }

    public void addBlueUpgrade() {
        this.blueUpgrade++;
    }

    public int getOrangeUpgrade() {
        return orangeUpgrade;
    }

    public void addOrangeUpgrade() {
        this.orangeUpgrade++;
    }
}
