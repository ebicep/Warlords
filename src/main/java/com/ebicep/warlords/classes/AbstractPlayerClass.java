package com.ebicep.warlords.classes;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.ArcaneShield;
import com.ebicep.warlords.abilties.EarthenSpike;
import com.ebicep.warlords.abilties.SoulShackle;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.player.WarlordsPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

public abstract class AbstractPlayerClass {

    private final int abilityCooldownDelay = 1;
    private final int secondaryAbilityCooldownDelay = 5;
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
        if (red.getName().contains("Consecrate")) {
            className = "Paladin";
            classNameShort = "PAL";
        } else if (purple.getName().contains("Time")) {
            className = "Mage";
            classNameShort = "MAG";
        } else if (purple.getName().contains("Ground")) {
            className = "Warrior";
            classNameShort = "WAR";
        } else if (red.getName().contains("Curse") || red.getName().contains("Shackle") || red.getName().contains("Puddle")) {
            className = "Rogue";
            classNameShort = "ROG";
        } else {
            className = "Shaman";
            classNameShort = "SHA";
        }

        if (blue instanceof ArcaneShield) {
            ArcaneShield arcaneShield = ((ArcaneShield) blue);
            arcaneShield.setMaxShieldHealth((int) (maxHealth * (arcaneShield.getShieldPercentage() / 100f)));
            blue.updateDescription(null); // Arcaneshield does not use the player in its description
        }
    }

    public void onRightClick(@Nonnull WarlordsPlayer wp, @Nonnull Player player, int slot, boolean hotkeyMode) {
        // Makes it so abilities cannot be used when the game is over
        if (wp.getGameState() != wp.getGame().getState()) {
            return;
        }

        if (wp.isDead()) {
            return;
        }

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
                        sendRightClickPacket(player);
                    }
                    resetAbilityCD();
                } else {
                    player.playSound(player.getLocation(), "notreadyalert", 1, 1);
                }
                break;
            case 1:
                if (red.getCurrentCooldown() != 0) {
                    if (secondaryAbilityCD) {
                        red.runSecondAbilities();
                        resetSecondaryAbilityCD();
                    }
                    break;
                }
                if (player.getLevel() >= red.getEnergyCost() * wp.getEnergyModifier() && abilityCD) {
                    boolean shouldApplyCooldown = red.onActivate(wp, player);
                    if (shouldApplyCooldown) {
                        red.setCurrentCooldown((float) (red.getCooldown() * wp.getCooldownModifier()));
                        sendRightClickPacket(player);
                    }
                    resetAbilityCD();
                }
                break;
            case 2:
                if (purple.getCurrentCooldown() != 0) {
                    if (secondaryAbilityCD) {
                        purple.runSecondAbilities();
                        resetSecondaryAbilityCD();
                    }
                    break;
                }
                if (player.getLevel() >= purple.getEnergyCost() * wp.getEnergyModifier() && abilityCD) {
                    boolean shouldApplyCooldown = purple.onActivate(wp, player);
                    if (shouldApplyCooldown) {
                        purple.setCurrentCooldown((float) (purple.getCooldown() * wp.getCooldownModifier()));
                        sendRightClickPacket(player);
                    }
                    resetAbilityCD();
                }

                break;
            case 3:
                if (blue.getCurrentCooldown() != 0) {
                    if (secondaryAbilityCD) {
                        blue.runSecondAbilities();
                        resetSecondaryAbilityCD();
                    }
                    break;
                }
                if (player.getLevel() >= blue.getEnergyCost() * wp.getEnergyModifier() && abilityCD) {
                    boolean shouldApplyCooldown = blue.onActivate(wp, player);
                    if (shouldApplyCooldown) {
                        blue.setCurrentCooldown((float) (blue.getCooldown() * wp.getCooldownModifier()));
                        sendRightClickPacket(player);
                    }
                    resetAbilityCD();
                }

                break;
            case 4:
                if (orange.getCurrentCooldown() != 0) {
                    if (secondaryAbilityCD) {
                        orange.runSecondAbilities();
                        resetSecondaryAbilityCD();
                    }
                    break;
                }
                if (player.getLevel() >= orange.getEnergyCost() * wp.getEnergyModifier() && abilityCD) {
                    boolean shouldApplyCooldown = orange.onActivate(wp, player);
                    if (shouldApplyCooldown) {
                        orange.setCurrentCooldown((float) (orange.getCooldown() * wp.getCooldownModifier()));
                        sendRightClickPacket(player);
                    }
                    resetAbilityCD();
                }
                break;
        }

        if (hotkeyMode) {
            player.getInventory().setHeldItemSlot(0);
        }

        if (slot == 0 || slot == 1 || slot == 2 || slot == 3 || slot == 4) {
            if (player.getVehicle() != null) {
                player.getVehicle().remove();
            }
        }
    }

    private void resetAbilityCD() {
        abilityCD = false;
        new BukkitRunnable() {

            @Override
            public void run() {
                abilityCD = true;
            }
        }.runTaskLater(Warlords.getInstance(), abilityCooldownDelay);
    }

    private void resetSecondaryAbilityCD() {
        secondaryAbilityCD = false;
        new BukkitRunnable() {

            @Override
            public void run() {
                secondaryAbilityCD = true;
            }
        }.runTaskLater(Warlords.getInstance(), secondaryAbilityCooldownDelay);
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

    public AbstractAbility[] getAbilities() {
        return new AbstractAbility[]{weapon, red, purple, blue, orange};
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
}
