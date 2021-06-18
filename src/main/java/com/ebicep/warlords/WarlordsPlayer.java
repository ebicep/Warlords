package com.ebicep.warlords;

import com.ebicep.warlords.classes.ActionBarStats;
import com.ebicep.warlords.classes.PlayerClass;
import com.ebicep.warlords.classes.abilties.HammerOfLight;
import com.ebicep.warlords.classes.abilties.OrbsOfLife;
import com.ebicep.warlords.classes.abilties.Soulbinding;
import com.ebicep.warlords.classes.abilties.Totem;
import com.ebicep.warlords.events.WarlordsDeathEvent;
import com.ebicep.warlords.maps.FlagManager;
import com.ebicep.warlords.util.*;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

public class WarlordsPlayer {

    private Player player;
    private String name;
    private UUID uuid;
    private PlayerClass spec;
    private final Weapons weapon;
    private boolean hotKeyMode = true;
    private int health;
    private int maxHealth;
    private int regenTimer;
    private int respawnTimer;
    private boolean dead = false;
    private float energy;
    private float maxEnergy;
    private int horseCooldown;
    private int flagCooldown;
    private int hitCooldown;
    private int spawnProtection;
    private int spawnDamage = 0;
    private int flagsCaptured = 0;
    private int flagsReturned = 0;

    private final int[] kills = new int[Warlords.game.getMap().getGameTimerInTicks() / 20 / 60];
    private final int[] assists = new int[Warlords.game.getMap().getGameTimerInTicks() / 20 / 60];
    private List<WarlordsPlayer> hitBy = new ArrayList<>();
    private final int[] deaths = new int[Warlords.game.getMap().getGameTimerInTicks() / 20 / 60];
    private final float[] damage = new float[Warlords.game.getMap().getGameTimerInTicks() / 20 / 60];
    private final float[] healing = new float[Warlords.game.getMap().getGameTimerInTicks() / 20 / 60];
    private final float[] absorbed = new float[Warlords.game.getMap().getGameTimerInTicks() / 20 / 60];

    private final CalculateSpeed speed;

    private final List<ActionBarStats> actionBarStats = new ArrayList<>();

    public List<ActionBarStats> getActionBarStats() {
        return actionBarStats;
    }

    public void displayActionBar() {
        StringBuilder actionBarMessage = new StringBuilder(ChatColor.GOLD + "§lHP: ");
        float healthRatio = (float) health / maxHealth;
        if (healthRatio >= .75) {
            actionBarMessage.append(ChatColor.DARK_GREEN);

        } else if (healthRatio >= .25) {
            actionBarMessage.append(ChatColor.YELLOW);

        } else {
            actionBarMessage.append(ChatColor.RED);

        }
        actionBarMessage.append("§l").append(health).append(ChatColor.GOLD).append("§l/§l").append(maxHealth).append("    ");
        if (Warlords.game.getTeamBlueProtected().contains(player)) {
            actionBarMessage.append(ChatColor.BLUE).append("§lBLU TEAM  ");
        } else if (Warlords.game.getTeamRedProtected().contains(player)) {
            actionBarMessage.append(ChatColor.RED).append("§lRED TEAM  ");
        }
        for (int i = 0; i < actionBarStats.size(); i++) {
            ActionBarStats actionBarStat = actionBarStats.get(i);
            actionBarMessage.append(ChatColor.GREEN).append(actionBarStat.getName()).append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(actionBarStat.getTimeLeft()).append(" ");
            if (actionBarStat.subtractTime()) {
                actionBarStats.remove(i);
                i--;
            }
        }
        switch (spec.getClassName()) {
            case "Paladin":
                if (infusionDuration != 0) {
                    actionBarMessage.append(ChatColor.GREEN).append("LINF").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(infusionDuration / 20 + 1).append(" ");
                }
                if (wrathDuration != 0) {
                    actionBarMessage.append(ChatColor.GREEN).append("WRAT").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(wrathDuration).append(" ");
                }
                if (presenceDuration != 0) {
                    actionBarMessage.append(ChatColor.GREEN).append("INSP").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(presenceDuration / 20).append(" ");
                }
                break;
            case "Warrior":
                if (bloodLustDuration != 0) {
                    actionBarMessage.append(ChatColor.GREEN).append("BLOO").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(bloodLustDuration).append(" ");
                }
                if (berserkDuration != 0) {
                    actionBarMessage.append(ChatColor.GREEN).append("BERS").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(berserkDuration / 20).append(" ");
                }
                if (interveneDuration != 0) {
                    actionBarMessage.append(ChatColor.GREEN).append("VENE").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(interveneDuration).append(" ");
                }
                if (lastStandDuration != 0) {
                    actionBarMessage.append(ChatColor.GREEN).append("LAST").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(lastStandDuration).append(" ");
                }
                if (orbsOfLifeDuration != 0) {
                    actionBarMessage.append(ChatColor.GREEN).append("ORBS").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(orbsOfLifeDuration).append(" ");
                }
                if (undyingArmyDuration != 0) {
                    actionBarMessage.append(ChatColor.GREEN).append("ARMY").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(undyingArmyDuration).append(" ");
                }
                break;
            case "Mage":
                // WARP/RAIN IN actionBarStats
                if (arcaneShield - 1 > 0) {
                    actionBarMessage.append(ChatColor.GREEN).append("ARCA").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(arcaneShield - 1).append(" ");
                }
                if (iceBarrierDuration != 0) {
                    actionBarMessage.append(ChatColor.GREEN).append("ICEB").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(iceBarrierDuration / 20 + 1).append(" ");
                }
                if (inferno != 0) {
                    actionBarMessage.append(ChatColor.GREEN).append("INFR").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(inferno).append(" ");
                }
                break;
            case "Shaman":
                // TOTEMS IN actionBarStats
                if (chainLightningCooldown != 0) {
                    actionBarMessage.append(ChatColor.GREEN).append("CHAIN").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(chainLightningCooldown).append(" ");
                }
                if (windfuryDuration != 0) {
                    actionBarMessage.append(ChatColor.GREEN).append("FURY").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(windfuryDuration).append(" ");
                }
                if (spiritLinkDuration != 0) {
                    actionBarMessage.append(ChatColor.GREEN).append("LINK").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(spiritLinkDuration).append(" ");
                }
                if (earthlivingDuration != 0) {
                    actionBarMessage.append(ChatColor.GREEN).append("EARTH").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(earthlivingDuration).append(" ");
                }
                if (soulBindCooldown != 0) {
                    actionBarMessage.append(ChatColor.GREEN).append("SOUL").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(soulBindCooldown).append(" ");
                }
                if (repentanceDuration != 0) {
                    actionBarMessage.append(ChatColor.GREEN).append("REPE").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(repentanceDuration).append(" ");
                }
                break;
        }
        if (powerUpDamage != 0) {
            actionBarMessage.append(ChatColor.GREEN).append("DAMAGE").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(powerUpDamage).append(" ");
        }
        if (powerUpEnergy != 0) {
            actionBarMessage.append(ChatColor.GREEN).append("ENERGY").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(powerUpEnergy).append(" ");
        }
        if (powerUpSpeed != 0) {
            actionBarMessage.append(ChatColor.GREEN).append("SPEED").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(powerUpSpeed).append(" ");
        }
        if (spawnDamage != 0) {
            actionBarMessage.append(ChatColor.GREEN).append("DAMAGE").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(spawnDamage).append(" ");
        }
        PacketUtils.sendActionBar(player, actionBarMessage.toString());
    }

    private boolean teamFlagCompass = true;

    public void displayFlagActionBar() {
        FlagManager.FlagInfo blueFlag = Warlords.game.getFlags().getBlue();
        double blueFlagDistance = Math.round(blueFlag.getFlag().getLocation().distance(player.getLocation()) * 10) / 10.0;
        FlagManager.FlagInfo redFlag = Warlords.game.getFlags().getRed();
        double redFlagDistance = Math.round(redFlag.getFlag().getLocation().distance(player.getLocation()) * 10) / 10.0;

        if (Warlords.game.isBlueTeam(player)) {
            if (teamFlagCompass) {
                if (blueFlag.getFlag() instanceof FlagManager.PlayerFlagLocation) {
                    PacketUtils.sendActionBar(player, "" + ChatColor.BLUE + ChatColor.BOLD + "YOUR Flag " + ChatColor.WHITE + "is stolen " + ChatColor.RED + blueFlagDistance + "m " + ChatColor.WHITE + "away!");
                } else if (blueFlag.getFlag() instanceof FlagManager.GroundFlagLocation) {
                    PacketUtils.sendActionBar(player, "" + ChatColor.BLUE + ChatColor.BOLD + "YOUR Flag " + ChatColor.GOLD + "is dropped " + ChatColor.RED + blueFlagDistance + "m " + ChatColor.WHITE + "away!");
                } else {
                    PacketUtils.sendActionBar(player, "" + ChatColor.BLUE + ChatColor.BOLD + "YOUR " + ChatColor.GREEN + "Flag is safe");
                }
            } else {
                if (redFlag.getFlag() instanceof FlagManager.PlayerFlagLocation) {
                    PacketUtils.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "ENEMY Flag " + ChatColor.WHITE + "is stolen " + ChatColor.RED + redFlagDistance + "m " + ChatColor.WHITE + "away!");
                } else if (redFlag.getFlag() instanceof FlagManager.GroundFlagLocation) {
                    PacketUtils.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "ENEMY Flag " + ChatColor.GOLD + "is dropped " + ChatColor.RED + redFlagDistance + "m " + ChatColor.WHITE + "away!");
                } else {
                    PacketUtils.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "ENEMY " + ChatColor.GREEN + "Flag is safe");
                }
            }
        } else {
            if (teamFlagCompass) {
                if (redFlag.getFlag() instanceof FlagManager.PlayerFlagLocation) {
                    PacketUtils.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "YOUR Flag " + ChatColor.WHITE + "is stolen " + ChatColor.RED + redFlagDistance + "m " + ChatColor.WHITE + "away!");
                } else if (redFlag.getFlag() instanceof FlagManager.GroundFlagLocation) {
                    PacketUtils.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "YOUR Flag " + ChatColor.GOLD + "is dropped " + ChatColor.RED + redFlagDistance + "m " + ChatColor.WHITE + "away!");
                } else {
                    PacketUtils.sendActionBar(player, "" + ChatColor.RED + ChatColor.BOLD + "YOUR " + ChatColor.GREEN + "Flag is safe");
                }
            } else {
                if (blueFlag.getFlag() instanceof FlagManager.PlayerFlagLocation) {
                    PacketUtils.sendActionBar(player, "" + ChatColor.BLUE + ChatColor.BOLD + "ENEMY Flag " + ChatColor.WHITE + "is stolen " + ChatColor.RED + blueFlagDistance + "m " + ChatColor.WHITE + "away!");
                } else if (blueFlag.getFlag() instanceof FlagManager.GroundFlagLocation) {
                    PacketUtils.sendActionBar(player, "" + ChatColor.BLUE + ChatColor.BOLD + "ENEMY Flag " + ChatColor.GOLD + "is dropped " + ChatColor.RED + blueFlagDistance + "m " + ChatColor.WHITE + "away!");
                } else {
                    PacketUtils.sendActionBar(player, "" + ChatColor.BLUE + ChatColor.BOLD + "ENEMY " + ChatColor.GREEN + "Flag is safe");
                }
            }
        }
    }

    // PALADIN
    private int infusionDuration = 0;
    private int wrathDuration = 0;
    private int presenceDuration = 0;

    // WARRIOR
    private int berserkerWounded = 0;
    private int defenderWounded = 0;
    private int crippled = 0;
    private int bloodLustDuration = 0;
    private int berserkDuration = 0;
    private int interveneDuration = 0;
    private int interveneDamage = 0;
    private WarlordsPlayer intervened;
    private WarlordsPlayer intervenedBy;
    private int lastStandDuration = 0;
    private WarlordsPlayer lastStandedBy;
    private int charged = 0;
    private Location chargeLocation;
    private int orbsOfLifeDuration = 0;
    private int undyingArmyDuration = 0;
    private boolean undyingArmyDead = false;
    private WarlordsPlayer undyingArmyBy;

    //SHAMAN
    private int chainLightning = 0;
    private int chainLightningCooldown = 0;
    private int spiritLinkDuration = 0;
    private List<Soulbinding.SoulBoundPlayer> soulBindedPlayers = new ArrayList<>();
    private int soulBindCooldown = 0;
    private int windfuryDuration = 0;
    private int earthlivingDuration = 0;
    private boolean firstProc = false;
    private int repentanceDuration = 0;
    private int repentanceCounter = 0;

    //MAGE
    private int breathSlownessDuration = 0;
    private int frostboltDuration = 0;
    private int arcaneShield = 0;
    private int arcaneShieldHealth = 0;
    private int inferno = 0;
    private int iceBarrierDuration = 0;
    private int iceBarrierSlownessDuration = 0;

    //POWERUPS
    private int powerUpDamage = 0;
    private int powerUpEnergy = 0;
    private boolean powerUpHeal = false;
    private int powerUpSpeed = 0;

    private static final Dye grayDye = new Dye();

    private CustomScoreboard scoreboard;

    public CustomScoreboard getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(CustomScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    private final boolean energyPowerup;

    public boolean isEnergyPowerup() {
        return energyPowerup;
    }

    private Location deathLocation;
    private ArmorStand deathStand;

    public WarlordsPlayer(Player player, String name, UUID uuid, PlayerClass spec, Weapons weapon, boolean energyPowerup) {
        this.player = player;
        this.name = name;
        this.uuid = uuid;
        this.spec = spec;
        this.weapon = weapon;
        this.health = spec.getMaxHealth();
        this.maxHealth = spec.getMaxHealth();
        this.respawnTimer = -1;
        this.energy = 0;
        this.maxEnergy = spec.getMaxEnergy();
        this.horseCooldown = 0;
        this.flagCooldown = 0;
        this.hitCooldown = 20;
        this.spawnProtection = 0;
        this.speed = new CalculateSpeed(player::setWalkSpeed, 13);
        grayDye.setColor(DyeColor.GRAY);
        this.energyPowerup = energyPowerup;
    }

    public void applySkillBoost() {
        ClassesSkillBoosts selectedBoost = Classes.getSelectedBoost(player);
        if (spec.getWeapon().getClass() == selectedBoost.ability) {
            spec.getWeapon().boostSkill();
            spec.getWeapon().updateDescription();
        } else if (spec.getRed().getClass() == selectedBoost.ability) {
            spec.getRed().boostSkill();
            spec.getRed().updateDescription();
        } else if (spec.getPurple().getClass() == selectedBoost.ability) {
            spec.getPurple().boostSkill();
            spec.getPurple().updateDescription();
        } else if (spec.getBlue().getClass() == selectedBoost.ability) {
            spec.getBlue().boostSkill();
            spec.getBlue().updateDescription();
        } else if (spec.getOrange().getClass() == selectedBoost.ability) {
            spec.getOrange().boostOrange();
            spec.getOrange().updateDescription();
        }
    }

    public void assignItemLore() {
        weaponLeftClick();

        updateRedItem();
        updatePurpleItem();
        updateBlueItem();
        updateOrangeItem();
        updateHorseItem();

        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName(ChatColor.GREEN + "Flag Finder");
        compass.setItemMeta(compassMeta);
        compassMeta.spigot().setUnbreakable(true);
        player.getInventory().setItem(8, compass);
    }

    public void weaponLeftClick() {
        player.getInventory().setItem(
                0,
                new ItemBuilder(weapon.item)
                        .name(ChatColor.GOLD + "Warlord's " + weapon.name + " of the " + spec.getClass().getSimpleName())
                        .lore(ChatColor.GRAY + "Damage: " + ChatColor.RED + "132 " + ChatColor.GRAY + "- " + ChatColor.RED + "179",
                                ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + "25%",
                                ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + "200%",
                                "",
                                ChatColor.GREEN + spec.getClassName() + " (" + spec.getClass().getSimpleName() + "):",
                                Classes.getSelectedBoost(player).selectedDescription,
                                "",
                                ChatColor.GRAY + "Health: " + ChatColor.GREEN + "+800",
                                ChatColor.GRAY + "Max Energy: " + ChatColor.GREEN + "+35",
                                ChatColor.GRAY + "Cooldown Reduction: " + ChatColor.GREEN + "+13%",
                                ChatColor.GRAY + "Speed: " + ChatColor.GREEN + "+13%",
                                "",
                                ChatColor.GOLD + "Skill Boost Unlocked",
                                ChatColor.DARK_AQUA + "Crafted",
                                ChatColor.LIGHT_PURPLE + "Void Forged [4/4]",
                                ChatColor.GREEN + "EQUIPPED",
                                ChatColor.AQUA + "BOUND",
                                "",
                                ChatColor.YELLOW + ChatColor.BOLD.toString() + "RIGHT-CLICK " + ChatColor.GREEN + "to view " + ChatColor.YELLOW + spec.getWeapon().getName(),
                                ChatColor.GREEN + "stats!")
                        .unbreakable()
                        .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                        .get());
    }

    public void weaponRightClick() {
        player.getInventory().setItem(
                0,
                new ItemBuilder(weapon.item)
                        .name(ChatColor.GREEN + spec.getWeapon().getName() + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Right-Click!")
                        .lore(ChatColor.GRAY + "Energy Cost: " + ChatColor.YELLOW + spec.getWeapon().getEnergyCost(),
                                ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + spec.getWeapon().getCritChance() + "%",
                                ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + spec.getWeapon().getCritMultiplier() + "%",
                                "",
                                spec.getWeapon().getDescription(),
                                "",
                                ChatColor.YELLOW + ChatColor.BOLD.toString() + "LEFT-CLICK " + ChatColor.GREEN + "to view weapon stats!")
                        .unbreakable()
                        .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                        .get());
    }

    public void updateRedItem() {
        if (spec.getRed().getCurrentCooldown() != 0) {
            ItemStack cooldown = new ItemStack(grayDye.toItemStack(spec.getRed().getCurrentCooldownItem()));
            player.getInventory().setItem(1, cooldown);
        } else {
            Dye redDye = new Dye();
            redDye.setColor(DyeColor.RED);
            player.getInventory().setItem(
                    1,
                    new ItemBuilder(redDye.toItemStack(1))
                            .name(ChatColor.GOLD + spec.getRed().getName())
                            .lore(ChatColor.GRAY + "Cooldown: " + ChatColor.AQUA + spec.getRed().getCooldown() + " seconds",
                                    spec.getRed().getEnergyCost() != 0
                                            ? ChatColor.GRAY + "Energy Cost: " + ChatColor.YELLOW + spec.getRed().getEnergyCost() + "\n" +
                                            (spec.getRed().getCritChance() != 0 && spec.getRed().getCritChance() != -1 && spec.getRed().getCritMultiplier() != 100
                                                    ? ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + spec.getRed().getCritChance() + "%" + "\n"
                                                    + ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + spec.getRed().getCritMultiplier() + "%" + "\n\n" + spec.getRed().getDescription()
                                                    : "\n" + spec.getRed().getDescription())
                                            : "\n" + spec.getRed().getDescription()
                            )
                            .unbreakable()
                            .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                            .get());
        }
    }

    public void updatePurpleItem() {
        if (spec.getPurple().getCurrentCooldown() != 0) {
            ItemStack cooldown = new ItemStack(grayDye.toItemStack(spec.getPurple().getCurrentCooldownItem()));
            player.getInventory().setItem(2, cooldown);
        } else {
            player.getInventory().setItem(
                    2,
                    new ItemBuilder(Material.GLOWSTONE_DUST)
                            .name(ChatColor.GOLD + spec.getPurple().getName())
                            .lore(ChatColor.GRAY + "Cooldown: " + ChatColor.AQUA + spec.getPurple().getCooldown() + " seconds",
                                    spec.getPurple().getEnergyCost() != 0
                                            ? ChatColor.GRAY + "Energy Cost: " + ChatColor.YELLOW + spec.getPurple().getEnergyCost() + "\n" +
                                            (spec.getPurple().getCritChance() != 0 && spec.getPurple().getCritChance() != -1 && spec.getPurple().getCritMultiplier() != 100
                                                    ? ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + spec.getPurple().getCritChance() + "%" + "\n"
                                                    + ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + spec.getPurple().getCritMultiplier() + "%" + "\n\n" + spec.getPurple().getDescription()
                                                    : "\n" + spec.getPurple().getDescription())
                                            : "\n" + spec.getPurple().getDescription()
                            )
                            .unbreakable()
                            .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                            .get());
        }
    }

    public void updateBlueItem() {
        if (spec.getBlue().getCurrentCooldown() != 0) {
            ItemStack cooldown = new ItemStack(grayDye.toItemStack(spec.getBlue().getCurrentCooldownItem()));
            player.getInventory().setItem(3, cooldown);
        } else {
            Dye limeDye = new Dye();
            limeDye.setColor(DyeColor.LIME);
            player.getInventory().setItem(
                    3,
                    new ItemBuilder(limeDye.toItemStack(1))
                            .name(ChatColor.GOLD + spec.getBlue().getName())
                            .lore(ChatColor.GRAY + "Cooldown: " + ChatColor.AQUA + spec.getBlue().getCooldown() + " seconds",
                                    spec.getBlue().getEnergyCost() != 0
                                            ? ChatColor.GRAY + "Energy Cost: " + ChatColor.YELLOW + spec.getBlue().getEnergyCost() + "\n" +
                                            (spec.getBlue().getCritChance() != 0 && spec.getBlue().getCritChance() != -1 && spec.getBlue().getCritMultiplier() != 100
                                                    ? ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + spec.getBlue().getCritChance() + "%" + "\n"
                                                    + ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + spec.getBlue().getCritMultiplier() + "%" + "\n\n" + spec.getBlue().getDescription()
                                                    : "\n" + spec.getBlue().getDescription())
                                            : "\n" + spec.getBlue().getDescription()
                            )
                            .unbreakable()
                            .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                            .get());
        }
    }

    public void updateOrangeItem() {
        if (spec.getOrange().getCurrentCooldown() != 0) {
            ItemStack cooldown = new ItemStack(grayDye.toItemStack(spec.getOrange().getCurrentCooldownItem()));
            player.getInventory().setItem(4, cooldown);
        } else {
            Dye orangeDye = new Dye();
            orangeDye.setColor(DyeColor.ORANGE);
            player.getInventory().setItem(
                    4,
                    new ItemBuilder(orangeDye.toItemStack(1))
                            .name(ChatColor.GOLD + spec.getOrange().getName())
                            .lore(ChatColor.GRAY + "Cooldown: " + ChatColor.AQUA + spec.getOrange().getCooldown() + " seconds",
                                    spec.getOrange().getEnergyCost() != 0
                                            ? ChatColor.GRAY + "Energy Cost: " + ChatColor.YELLOW + spec.getOrange().getEnergyCost() + "\n" +
                                            (spec.getOrange().getCritChance() != 0 && spec.getOrange().getCritChance() != -1 && spec.getOrange().getCritMultiplier() != 100
                                                    ? ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + spec.getOrange().getCritChance() + "%" + "\n"
                                                    + ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + spec.getOrange().getCritMultiplier() + "%" + "\n\n" + spec.getOrange().getDescription()
                                                    : "\n" + spec.getOrange().getDescription())
                                            : "\n" + spec.getOrange().getDescription()
                            )
                            .unbreakable()
                            .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                            .get());
        }
    }

    public void updateHorseItem() {
        if (horseCooldown != 0) {
            ItemStack cooldown = new ItemStack(Material.IRON_BARDING, horseCooldown);
            player.getInventory().setItem(7, cooldown);
        } else {
            ItemStack horse = new ItemStack(Material.GOLD_BARDING);
            ItemMeta horseMeta = horse.getItemMeta();
            horseMeta.setDisplayName(ChatColor.GREEN + "Mount " + ChatColor.GRAY + "- §eRight-Click!");
            ArrayList<String> horseLore = new ArrayList<>();
            horseLore.add(ChatColor.GRAY + "Cooldown: §b15 seconds");
            horseLore.add("");
            horseLore.add(ChatColor.GRAY + "Call your steed to assists you in battle");
            horseMeta.setLore(horseLore);
            horse.setItemMeta(horseMeta);
            horseMeta.spigot().setUnbreakable(true);
            player.getInventory().setItem(7, horse);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public PlayerClass getSpec() {
        return spec;
    }

    public void setSpec(PlayerClass spec) {
        this.spec = spec;
    }

    public boolean isHotKeyMode() {
        return hotKeyMode;
    }

    public void setHotKeyMode(boolean hotKeyMode) {
        this.hotKeyMode = hotKeyMode;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void addHealth(WarlordsPlayer attacker, String ability, float min, float max, int critChance, int critMultiplier) {
        if (spawnProtection != 0 || (dead && !undyingArmyDead)) return;
        if (attacker == this && (ability.equals("Fall") || ability.isEmpty())) {
            if (ability.isEmpty()) {
                player.sendMessage("" + ChatColor.RED + "\u00AB" + ChatColor.GRAY + " You took " + ChatColor.RED + min * -1 + ChatColor.GRAY + " melee damage.");
                regenTimer = 10;
                if (health + min <= 0) {
                    dead = true;
                    powerUpDamage = 0;
                    powerUpEnergy = 0;
                    powerUpSpeed = 0;
                    addGrave();

                    Zombie zombie = player.getWorld().spawn(player.getLocation(), Zombie.class);
                    zombie.getEquipment().setBoots(player.getInventory().getBoots());
                    zombie.getEquipment().setLeggings(player.getInventory().getLeggings());
                    zombie.getEquipment().setChestplate(player.getInventory().getChestplate());
                    zombie.getEquipment().setHelmet(player.getInventory().getHelmet());
                    zombie.getEquipment().setItemInHand(player.getInventory().getItemInHand());
                    zombie.damage(2000);

                    hitBy.remove(attacker);
                    hitBy.add(0, attacker);

                    this.addDeath();
                    this.scoreboard.updateKillsAssists();
                    Bukkit.getPluginManager().callEvent(new WarlordsDeathEvent(this));

                    if (Warlords.game.getTeamBlueProtected().contains(player)) {
                        Warlords.redKills++;
                        Warlords.game.addRedPoints(SCORE_KILL_POINTS);
                    } else {
                        Warlords.blueKills++;
                        Warlords.game.addBluePoints(SCORE_KILL_POINTS);
                    }

                    PacketUtils.sendTitle(player, ChatColor.RED + "YOU DIED!", ChatColor.GRAY + "You took " + ChatColor.RED + (min * -1) + ChatColor.GRAY + " melee damage and died.", 0, 40, 0);

                    for (WarlordsPlayer value : Warlords.getPlayers().values()) {
                        value.getScoreboard().updatePoints();
                    }
                    health = 0;
                } else {
                    health += min;
                }

                if (min < 0) {
                    player.playEffect(EntityEffect.HURT);
                }
            } else {
                //TODO FIX FIX IT JUST GETS MORE MESSY LETS GOOOOOOOOOOOOOOO
                player.sendMessage("" + ChatColor.RED + "\u00AB" + ChatColor.GRAY + " You took " + ChatColor.RED + Math.round(min * -1) + ChatColor.GRAY + " fall damage.");
                regenTimer = 10;
                if (health + min < 0) {
                    dead = true;
                    powerUpDamage = 0;
                    powerUpEnergy = 0;
                    powerUpSpeed = 0;
                    addGrave();

                    hitBy.remove(attacker);
                    hitBy.add(0, attacker);

                    this.addDeath();
                    this.scoreboard.updateKillsAssists();
                    Bukkit.getPluginManager().callEvent(new WarlordsDeathEvent(this));

                    if (Warlords.game.getTeamBlueProtected().contains(player)) {
                        Warlords.redKills++;
                        Warlords.game.addRedPoints(SCORE_KILL_POINTS);
                    } else {
                        Warlords.blueKills++;
                        Warlords.game.addBluePoints(SCORE_KILL_POINTS);
                    }

                    Zombie zombie = player.getWorld().spawn(player.getLocation(), Zombie.class);
                    zombie.getEquipment().setBoots(player.getInventory().getBoots());
                    zombie.getEquipment().setLeggings(player.getInventory().getLeggings());
                    zombie.getEquipment().setChestplate(player.getInventory().getChestplate());
                    zombie.getEquipment().setHelmet(player.getInventory().getHelmet());
                    zombie.getEquipment().setItemInHand(player.getInventory().getItemInHand());
                    zombie.damage(2000);

                    //title YOU DIED
                    PacketUtils.sendTitle(player, ChatColor.RED + "YOU DIED!", ChatColor.GRAY + "You took " + ChatColor.RED + (min * -1) + ChatColor.GRAY + " fall damage and died.", 0, 40, 0);

                    for (WarlordsPlayer value : Warlords.getPlayers().values()) {
                        value.getScoreboard().updatePoints();
                    }
                    health = 0;
                } else {
                    health += min;
                }
                player.playEffect(EntityEffect.HURT);
            }
        } else {
            if (attacker.getInferno() != 0 && (!ability.isEmpty() && !ability.equals("Time Warp"))) {
                critChance += attacker.getSpec().getOrange().getCritChance();
                critMultiplier += attacker.getSpec().getOrange().getCritMultiplier();
            }
            //crit
            float damageHealValue = (int) ((Math.random() * (max - min)) + min);
            int crit = (int) ((Math.random() * (100)));
            boolean isCrit = false;
            if (crit <= critChance) {
                isCrit = true;
                damageHealValue *= critMultiplier / 100f;
            }

            // Flag carriers take more damage
            for (MetadataValue metadata : this.getPlayer().getMetadata(FlagManager.FLAG_DAMAGE_MULTIPLIER)) {
                damageHealValue *= damageHealValue > 0 ? 1 : metadata.asDouble();
            }

            //TODO check if totaldmgreduc works
            //reduction begining with base resistance
            float totalReduction = 1;
            if (min < 0 && !HammerOfLight.standingInHammer(attacker.getPlayer(), player)) {
                //base
                totalReduction = 1 - spec.getDamageResistance() / 100f;
                //add damage
                if (attacker.getPowerUpDamage() != 0) {
                    totalReduction += .3;
                } else if (attacker.getSpawnDamage() != 0) {
                    totalReduction += .2;
                }
                if (attacker.getBerserk() != 0) {
                    totalReduction += .25;
                }
                if (berserkDuration != 0) {
                    totalReduction += .1;
                }

                //reduce damage
                if (iceBarrierDuration != 0) {
                    totalReduction -= .5;
                }
                if (chainLightningCooldown != 0) {
                    totalReduction -= chainLightning * .1;
                }
                if (spiritLinkDuration != 0) {
                    totalReduction -= .2;
                }
                //TODO maybe change to hypixel warlords where crippling effects hammer
                if (attacker.getCrippled() != 0) {
                    totalReduction -= .125;
                }
            }
            if (interveneDuration != 0 && !HammerOfLight.standingInHammer(attacker.getPlayer(), player)) {
                //TODO check teammate heal
                if (!Warlords.game.onSameTeam(this, attacker)) {
                    damageHealValue *= totalReduction;
                    damageHealValue *= .5;
                    if (isCrit) {
                        intervenedBy.getPlayer().sendMessage("" + ChatColor.RED + "\u00AB" + ChatColor.GRAY + " " + attacker.getName() + "'s Intervene hit you for " + ChatColor.RED + "§l" + Math.round(damageHealValue) * -1 + "! " + ChatColor.GRAY + "critical damage.");
                        attacker.getPlayer().sendMessage(ChatColor.GREEN + "\u00BB" + ChatColor.GRAY + " Your Intervene hit " + intervenedBy.getName() + " for " + ChatColor.RED + "§l" + Math.round(damageHealValue) * -1 + "! " + ChatColor.GRAY + "critical damage.");

                        for (Player player1 : player.getWorld().getPlayers()) {
                            player1.playSound(player.getLocation(), "warrior.intervene.block.3", 2, 1);
                        }
                    } else {
                        intervenedBy.getPlayer().sendMessage("" + ChatColor.RED + "\u00AB" + ChatColor.GRAY + " " + attacker.getName() + "'s Intervene hit you for " + ChatColor.RED + Math.round(damageHealValue) * -1 + ChatColor.GRAY + " damage.");
                        attacker.getPlayer().sendMessage(ChatColor.GREEN + "\u00BB" + ChatColor.GRAY + " Your Intervene hit " + intervenedBy.getName() + " for " + ChatColor.RED + Math.round(damageHealValue) * -1 + ChatColor.GRAY + " damage.");

                        for (Player player1 : player.getWorld().getPlayers()) {
                            player1.playSound(player.getLocation(), "warrior.intervene.block.1", 2, 1);
                        }
                    }
                    intervenedBy.setHealth((int) (intervenedBy.getHealth() + damageHealValue));
                    interveneDamage += damageHealValue;

                    this.addAbsorbed(-damageHealValue);
                    attacker.addDamage(-damageHealValue);
                }
            } else if (arcaneShield != 0 && !Warlords.game.onSameTeam(this, attacker) && !HammerOfLight.standingInHammer(attacker.getPlayer(), player)) {
                damageHealValue *= totalReduction;
                //TODO check teammate heal
                if (arcaneShieldHealth + damageHealValue < 0) {
                    arcaneShield = 0;
                    addHealth(attacker, ability, (arcaneShieldHealth + damageHealValue), (arcaneShieldHealth + damageHealValue), isCrit ? 100 : -1, 100);

                    addAbsorbed(-(arcaneShield + damageHealValue));
                } else {
                    if (ability.isEmpty()) {
                        player.sendMessage("" + ChatColor.RED + "\u00AB" + ChatColor.GRAY + " You absorbed " + attacker.getName() + "'s melee " + ChatColor.GRAY + "hit.");
                        attacker.getPlayer().sendMessage(ChatColor.GREEN + "\u00BB" + ChatColor.GRAY + " Your melee hit was absorbed by " + name);
                    } else {
                        player.sendMessage("" + ChatColor.RED + "\u00AB" + ChatColor.GRAY + " You absorbed " + attacker.getName() + "'s " + ability + " " + ChatColor.GRAY + "hit.");
                        attacker.getPlayer().sendMessage(ChatColor.GREEN + "\u00BB" + ChatColor.GRAY + " Your " + ability + " was absorbed by " + name + ChatColor.GRAY + ".");
                    }

                    addAbsorbed(-damageHealValue);
                }
                arcaneShieldHealth += damageHealValue;
                ((EntityLiving) ((CraftPlayer) player).getHandle()).setAbsorptionHearts((float) ((float) arcaneShieldHealth / (maxHealth * .5) * 20));
                player.playEffect(EntityEffect.HURT);
            } else {
                System.out.println(attacker.getName() + " hit " + name + " for " + damageHealValue);
                boolean debt = false;

                //Self heal
                if (this == attacker) {
                    if (berserkerWounded != 0) {
                        damageHealValue *= .65;
                    } else if (defenderWounded != 0) {
                        damageHealValue *= .75;
                    }
                    if (this.health + damageHealValue > this.maxHealth) {
                        damageHealValue = this.maxHealth - this.health;
                    }
                    damageHealValue = Math.round(damageHealValue);
                    if (damageHealValue != 0) {
                        if (isCrit) {
                            player.sendMessage(ChatColor.GREEN + "\u00AB" + ChatColor.GRAY + " Your " + ability + " critically healed you for " + ChatColor.GREEN + "§l" + Math.round(damageHealValue) + "! " + ChatColor.GRAY + "health.");
                        } else {
                            player.sendMessage(ChatColor.GREEN + "\u00AB" + ChatColor.GRAY + " Your " + ability + " healed you for " + ChatColor.GREEN + "" + Math.round(damageHealValue) + " " + ChatColor.GRAY + "health.");
                        }
                    }
                    addHealing(damageHealValue);

                } else {
                    damageHealValue *= totalReduction;
                    //DAMAGE
                    if (damageHealValue < 0 && !Warlords.game.onSameTeam(this, attacker)) {
                        if (!hitBy.contains(attacker)) {
                            hitBy.add(attacker);
                        }
                        if (powerUpHeal) {
                            powerUpHeal = false;
                            player.sendMessage("heal cancelled");
                        }
                        if (player.getVehicle() != null) {
                            player.getVehicle().remove();
                        }
                        regenTimer = 10;
                        if (lastStandDuration != 0 && !HammerOfLight.standingInHammer(attacker.getPlayer(), player)) {
                            if (lastStandedBy == this) {
                                damageHealValue *= .5;
                            } else {
                                damageHealValue *= .4;
                            }
                            //TODO multiple last stands? lastest person that last stands will over ride other dude
                            //HEALING FROM LASTSTAND
                            if (lastStandedBy != this && lastStandedBy.getLastStandDuration() != 0) {
                                System.out.println("===" + -damageHealValue);
                                float healValue = damageHealValue * -1;
                                if (isCrit)
                                    lastStandedBy.addHealth(lastStandedBy, "Last Stand", Math.round(healValue), Math.round(healValue), 100, 100);
                                else
                                    lastStandedBy.addHealth(lastStandedBy, "Last Stand", Math.round(healValue), Math.round(healValue), -1, 100);
                            }
                            addAbsorbed(-damageHealValue);
                        }

                        // this metadata is only active on the sg class
                        // the cooldown of the ability prevents multiple from being active at the same time
                        Optional<MetadataValue> totem = player.getMetadata("TOTEM").stream()
                                .filter(e -> e.value() instanceof Totem.TotemSpiritguard)
                                .findAny();
                        if (totem.isPresent()) {
                            Totem.TotemSpiritguard t = (Totem.TotemSpiritguard) totem.get().value();
                            t.addDelayedDamage(damageHealValue);
                            debt = true;
                        }

                        if (isCrit) {
                            if (ability.isEmpty()) {
                                player.sendMessage(ChatColor.RED + "\u00AB" + ChatColor.GRAY + " " + attacker.getName() + " hit you for " + ChatColor.RED + "§l" + Math.round(damageHealValue) * -1 + "! " + ChatColor.GRAY + "critical melee damage.");
                                attacker.getPlayer().sendMessage(ChatColor.GREEN + "\u00BB" + ChatColor.GRAY + " " + "You hit " + name + " for " + ChatColor.RED + "§l" + Math.round(damageHealValue) * -1 + "! " + ChatColor.GRAY + "critical melee damage.");
                            } else {
                                player.sendMessage(ChatColor.RED + "\u00AB" + ChatColor.GRAY + " " + attacker.getName() + "'s " + ability + " hit you for " + ChatColor.RED + "§l" + Math.round(damageHealValue) * -1 + "! " + ChatColor.GRAY + "critical damage.");
                                attacker.getPlayer().sendMessage(ChatColor.GREEN + "\u00BB" + ChatColor.GRAY + " " + "Your " + ability + " hit " + name + " for " + ChatColor.RED + "§l" + Math.round(damageHealValue) * -1 + "! " + ChatColor.GRAY + "critical damage.");
                            }
                        } else {
                            if (ability.isEmpty()) {
                                player.sendMessage(ChatColor.RED + "\u00AB" + ChatColor.GRAY + " " + attacker.getName() + " hit you for " + ChatColor.RED + Math.round(damageHealValue) * -1 + " " + ChatColor.GRAY + "melee damage.");
                                attacker.getPlayer().sendMessage(ChatColor.GREEN + "\u00BB" + ChatColor.GRAY + " " + "You hit " + name + " for " + ChatColor.RED + Math.round(damageHealValue) * -1 + " " + ChatColor.GRAY + "melee damage.");
                            } else {
                                player.sendMessage(ChatColor.RED + "\u00AB" + ChatColor.GRAY + " " + attacker.getName() + "'s " + ability + " hit you for " + ChatColor.RED + Math.round(damageHealValue) * -1 + " " + ChatColor.GRAY + "damage.");
                                attacker.getPlayer().sendMessage(ChatColor.GREEN + "\u00BB" + ChatColor.GRAY + " " + "Your " + ability + " hit " + name + " for " + ChatColor.RED + Math.round(damageHealValue) * -1 + " " + ChatColor.GRAY + "damage.");
                            }
                        }
                        //REPENTANCE
                        if (spec.getBlue().getName().equals("Repentance")) {
                            repentanceCounter += damageHealValue * -1;
                        }
                        if (attacker.getSpec().getBlue().getName().equals("Repentance")) {
                            if (attacker.getRepentanceDuration() != 0) {
                                int healthToAdd = (int) (attacker.getRepentanceCounter() * .1) + 11;
                                attacker.addHealth(attacker, "Repentance", healthToAdd, healthToAdd, -1, 100);
                                attacker.setRepentanceCounter((int) (attacker.getRepentanceCounter() * .5));
                                attacker.addEnergy(attacker, "Repentance", (int) (healthToAdd * .035));
                            }
                        }

                        //ORBS
                        if (attacker.getOrbsOfLifeDuration() != 0 && !ability.isEmpty()) {
                            Location location = player.getLocation();
                            OrbsOfLife.Orb orb = new OrbsOfLife.Orb(((CraftWorld) player.getWorld()).getHandle(), location, attacker);
                            //TODO Add team whitelist
                            ArmorStand orbStand = (ArmorStand) location.getWorld().spawnEntity(location.add(Math.random() * 5 - 2.5, 0, Math.random() * 5 - 2.5), EntityType.ARMOR_STAND);
                            orbStand.setVisible(false);
                            //WOW need to set passenger to orb or else the orb will move   like ???
                            orbStand.setPassenger(orb.spawn(location).getBukkitEntity());
                            orb.setArmorStand(orbStand);
                            Warlords.getOrbs().add(orb);
                        }

                        //prot strike
                        if (ability.equals("Protector's Strike")) {
                            //SELF HEAL
                            int tempNewCritChance;
                            if (isCrit) {
                                tempNewCritChance = 100;
                            } else {
                                tempNewCritChance = -1;
                            }
                            attacker.addHealth(attacker, name, -damageHealValue / 2, -damageHealValue / 2, tempNewCritChance, 100);

                            int counter = 0;
                            //reloops near players to give health to
                            List<Entity> nearNearPlayers = attacker.getPlayer().getNearbyEntities(5.0D, 5.0D, 5.0D);
                            nearNearPlayers.remove(attacker.getPlayer());
                            nearNearPlayers = Utils.filterOnlyTeammates(nearNearPlayers, attacker.getPlayer());
                            for (Entity nearEntity2 : nearNearPlayers) {
                                if (nearEntity2 instanceof Player) {
                                    Player nearTeamPlayer = (Player) nearEntity2;
                                    Warlords.getPlayer(nearTeamPlayer).addHealth(attacker, name, -damageHealValue, -damageHealValue, tempNewCritChance, 100);
                                    counter++;
                                    if (counter == 2)
                                        break;
                                }
                            }
                        }
                    }
                    //HEALING
                    else {
                        if (Warlords.game.onSameTeam(this, attacker)) {
                            if (berserkerWounded != 0) {
                                damageHealValue *= .65;
                            } else if (defenderWounded != 0) {
                                damageHealValue *= .75;
                            }
                            if (this.health + damageHealValue > maxHealth) {
                                damageHealValue = maxHealth - this.health;
                            }
                            if (damageHealValue != 0) {
                                if (isCrit) {
                                    player.sendMessage(ChatColor.GREEN + "\u00AB" + ChatColor.GRAY + " " + attacker.getName() + "'s " + ability + " critically healed you for " + ChatColor.GREEN + "§l" + Math.round(damageHealValue) + "! " + ChatColor.GRAY + "health.");
                                    attacker.getPlayer().sendMessage(ChatColor.GREEN + "\u00BB" + ChatColor.GRAY + " " + "Your " + ability + " critically healed " + name + " for " + ChatColor.GREEN + "§l" + Math.round(damageHealValue) + "! " + ChatColor.GRAY + "health.");
                                } else {
                                    player.sendMessage(ChatColor.GREEN + "\u00AB" + ChatColor.GRAY + " " + attacker.getName() + "'s " + ability + " healed for " + ChatColor.GREEN + "" + Math.round(damageHealValue) + " " + ChatColor.GRAY + "health.");
                                    attacker.getPlayer().sendMessage(ChatColor.GREEN + "\u00BB" + ChatColor.GRAY + " " + "Your " + ability + " healed " + name + " for " + ChatColor.GREEN + "" + Math.round(damageHealValue) + " " + ChatColor.GRAY + "health.");
                                }
                            }
                        }
                    }
                    if (attacker.getBloodLustDuration() != 0 && damageHealValue < 0) {
                        attacker.addHealth(attacker, "Blood Lust", Math.round(damageHealValue * -.65f), Math.round(damageHealValue * -.65f), -1, 100);
                    }
                }

                // adding/subtracing health
                //debt and healing
                if (!(debt && damageHealValue < 0)) {
                    this.health += Math.round(damageHealValue);
                }
                if (damageHealValue < 0) {
                    player.playEffect(EntityEffect.HURT);
                }
                attacker.addDamage(-damageHealValue);
                if (this.health <= 0 && undyingArmyDuration == 0) {
                    dead = true;
                    powerUpDamage = 0;
                    powerUpEnergy = 0;
                    powerUpSpeed = 0;
                    addGrave();

                    Zombie zombie = player.getWorld().spawn(player.getLocation(), Zombie.class);
                    zombie.getEquipment().setBoots(player.getInventory().getBoots());
                    zombie.getEquipment().setLeggings(player.getInventory().getLeggings());
                    zombie.getEquipment().setChestplate(player.getInventory().getChestplate());
                    zombie.getEquipment().setHelmet(player.getInventory().getHelmet());
                    zombie.getEquipment().setItemInHand(player.getInventory().getItemInHand());
                    zombie.damage(2000);

                    attacker.getPlayer().playSound(attacker.getPlayer().getLocation(), Sound.ORB_PICKUP, 500f, 0.3f);

                    hitBy.remove(attacker);
                    hitBy.add(0, attacker);

                    attacker.addKill();
                    attacker.scoreboard.updateKillsAssists();
                    this.addDeath();
                    this.scoreboard.updateKillsAssists();
                    Bukkit.getPluginManager().callEvent(new WarlordsDeathEvent(this));

                    if (Warlords.game.isBlueTeam(attacker.player)) {
                        player.sendMessage(ChatColor.GRAY + "You were killed by " + ChatColor.BLUE + attacker.getName());
                        attacker.getPlayer().sendMessage(ChatColor.GRAY + "You killed " + ChatColor.RED + name);
                        for (Player gamePlayer : Warlords.getPlayers().keySet()) {
                            if (gamePlayer != this.player && gamePlayer != attacker.player)
                                gamePlayer.sendMessage(ChatColor.RED + name + ChatColor.GRAY + " was killed by " + ChatColor.BLUE + attacker.getName());
                        }
                    } else {
                        player.sendMessage(ChatColor.GRAY + "You were killed by " + ChatColor.RED + attacker.getName());
                        attacker.getPlayer().sendMessage(ChatColor.GRAY + "You killed " + ChatColor.BLUE + name);
                        for (Player gamePlayer : Warlords.getPlayers().keySet()) {
                            if (gamePlayer != this.player && gamePlayer != attacker.player)
                                gamePlayer.sendMessage(ChatColor.BLUE + name + ChatColor.GRAY + " was killed by " + ChatColor.RED + attacker.getName());
                        }
                    }

                    if (Warlords.game.getTeamBlueProtected().contains(player)) {
                        Warlords.redKills++;
                        Warlords.game.addRedPoints(SCORE_KILL_POINTS);
                    } else {
                        Warlords.blueKills++;
                        Warlords.game.addBluePoints(SCORE_KILL_POINTS);
                    }

                    //title YOU DIED
                    PacketUtils.sendTitle(player, ChatColor.RED + "YOU DIED!", ChatColor.GRAY + attacker.getName() + " killed you.", 0, 40, 0);

                    for (WarlordsPlayer value : Warlords.getPlayers().values()) {
                        value.getScoreboard().updatePoints();
                    }
                } else {
                    if (!ability.isEmpty() && !ability.equals("Time Warp") && !ability.equals("Healing Rain") && !ability.equals("Hammer of Light")) {
                        attacker.getPlayer().playSound(attacker.getPlayer().getLocation(), Sound.ORB_PICKUP, 0.8f, 1f);
                    }
                }

            }

            if (ability.equals("")) {
                if (attacker.getWindfuryDuration() != 0) {
                    int windfuryActivate = (int) (Math.random() * 100);
                    if (attacker.isFirstProc()) {
                        attacker.setFirstProc(false);
                        windfuryActivate = 0;
                    }
                    if (windfuryActivate < 35) {
                        for (Player player1 : Bukkit.getOnlinePlayers()) {
                            player1.playSound(player.getLocation(), "shaman.windfuryweapon.impact", 2, 1);
                        }
                        addHealth(attacker, "Windfury Weapon", min, max, 25, 235);
                        if (health > 0)
                            addHealth(attacker, "Windfury Weapon", min, max, 25, 235);
                    }
                } else if (attacker.getEarthlivingDuration() != 0) {
                    int earthlivingActivate = (int) (Math.random() * 100);
                    if (attacker.isFirstProc()) {
                        if (isCrit) {
                            attacker.addHealth(attacker, "Earthliving Weapon", (damageHealValue * -2.4f), (damageHealValue * -2.4f), 100, 100);
                        } else {
                            attacker.addHealth(attacker, "Earthliving Weapon", (damageHealValue * -2.4f), (damageHealValue * -2.4f), -1, 100);
                        }

                        for (Player player1 : player.getWorld().getPlayers()) {
                            player1.playSound(player.getLocation(), "shaman.earthlivingweapon.impact", 2, 1);
                        }

                        attacker.setFirstProc(false);
                        List<Entity> near = attacker.getPlayer().getNearbyEntities(3.0D, 3.0D, 3.0D);
                        near = Utils.filterOnlyTeammates(near, attacker.getPlayer());
                        int counter = 0;
                        for (Entity entity : near) {
                            if (entity instanceof Player && ((Player) entity).getGameMode() != GameMode.SPECTATOR) {
                                if (earthlivingActivate < 40) {
                                    Warlords.getPlayer((Player) near.get(0)).addHealth(attacker, "Earthliving Weapon", (damageHealValue * -2.4f), (damageHealValue * -2.4f), 25, 100);

                                    counter++;
                                }
                            }
                            if (counter == 2)
                                break;
                        }
                    } else if (earthlivingActivate < 40) {
                        if (isCrit) {
                            attacker.addHealth(attacker, "Earthliving Weapon", (damageHealValue * -2.4f), (damageHealValue * -2.4f), 100, 100);
                        } else {
                            attacker.addHealth(attacker, "Earthliving Weapon", (damageHealValue * -2.4f), (damageHealValue * -2.4f), -1, 100);
                        }

                        for (Player player1 : player.getWorld().getPlayers()) {
                            player1.playSound(player.getLocation(), "shaman.earthlivingweapon.impact", 2, 1);
                        }

                        List<Entity> near = attacker.getPlayer().getNearbyEntities(3.0D, 3.0D, 3.0D);
                        near = Utils.filterOnlyTeammates(near, attacker.getPlayer());
                        int counter = 0;
                        for (Entity entity : near) {
                            if (entity instanceof Player && ((Player) entity).getGameMode() != GameMode.SPECTATOR) {
                                Warlords.getPlayer((Player) near.get(0)).addHealth(attacker, "Earthliving Weapon", (damageHealValue * -2.4f), (damageHealValue * -2.4f), 25, 440);
                                counter++;
                            }
                            if (counter == 2)
                                break;
                        }
                    }
                }
            }
            System.out.println(attacker.name + " - " + attacker.getTotalDamage());
        }
    }

    private static final int SCORE_KILL_POINTS = 5;

    public void addGrave() {
        Location deathLocation = player.getLocation();
        Block bestGraveCandidate = null;
        boolean isFlagCarrier = !player.getMetadata(FlagManager.FLAG_DAMAGE_MULTIPLIER).isEmpty();
        for (int x = -1; x <= 1; x++) {
            //Bukkit.broadcastMessage("For 1:" + x);
            for (int z = -1; z <= 1; z++) {
                //Bukkit.broadcastMessage("For 2:" + z);
                if (isFlagCarrier && x == 0 && z == 0) {
                    // This player is a flag carrier, prevent placing the grave at the direct location of the player
                    continue;
                }

                Location toTest = deathLocation.clone().add(x, 2, z);
                Block lastBlock = toTest.getBlock();

                if (lastBlock.getType() == Material.AIR) {
                    toTest.subtract(0, 1, 0);
                    for (; toTest.getY() > 0; toTest.subtract(0, 1, 0)) {
                        //Bukkit.broadcastMessage("For 3:" + toTest.getY());
                        Block underTest = toTest.getBlock();
                        if (underTest.getType() != Material.AIR) {
                            if (underTest.getType().isTransparent()) {
                                // We have hit a sappling, fence, torch or other non-solid
                                break;
                            }
                            // We have hit a solid block. Go back 1 tile
                            toTest.add(0, 1, 0);
                            // Check if we found a better tile for the grave
                            if (bestGraveCandidate != null) {
                                double newDistance = toTest.distanceSquared(deathLocation);
                                double existingDistance = bestGraveCandidate.getLocation(toTest).distanceSquared(deathLocation);
                                if (newDistance >= existingDistance) {
                                    // Our new candidate is not closer, skip
                                    break;
                                }
                            }
                            bestGraveCandidate = lastBlock;
                            //
                            break;
                        }
                        lastBlock = underTest;
                    }
                }
            }
        }

        if (bestGraveCandidate != null) {

            //spawn grave
            bestGraveCandidate.setType(Material.SAPLING);
            bestGraveCandidate.setData((byte) 5);

            this.deathLocation = bestGraveCandidate.getLocation();

            this.deathStand = (ArmorStand) player.getWorld().spawnEntity(bestGraveCandidate.getLocation().add(.5, -1.5, .5), EntityType.ARMOR_STAND);
            if (Warlords.game.isBlueTeam(player)) {
                this.deathStand.setCustomName(ChatColor.BLUE + name + ChatColor.GRAY + " - " + ChatColor.YELLOW + "DEAD");
            } else {
                this.deathStand.setCustomName(ChatColor.RED + name + ChatColor.GRAY + " - " + ChatColor.YELLOW + "DEAD");
            }
            this.deathStand.setCustomNameVisible(true);
            this.deathStand.setGravity(false);
            this.deathStand.setVisible(false);
        }
    }

    public void respawn() {
        this.health = this.maxHealth;
    }

    public int getRegenTimer() {
        return regenTimer;
    }

    public void setRegenTimer(int regenTimer) {
        this.regenTimer = regenTimer;
    }

    public int getRespawnTimer() {
        return respawnTimer;
    }

    public void setRespawnTimer(int respawnTimer) {
        this.respawnTimer = respawnTimer;
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public void addEnergy(WarlordsPlayer giver, String ability, int amount) {
        if (energy + amount > maxEnergy) {
            this.energy = maxEnergy;
        } else {
            this.energy += amount;
        }
        if (this == giver) {
            player.sendMessage(ChatColor.GREEN + "\u00AB" + ChatColor.GRAY + " Your " + ability + " gave you " + ChatColor.YELLOW + amount + " " + ChatColor.GRAY + "energy.");
        } else {
            player.sendMessage(ChatColor.GREEN + "\u00AB" + ChatColor.GRAY + " " + giver.getName() + "'s " + ability + " gave you " + ChatColor.YELLOW + amount + " " + ChatColor.GRAY + "energy.");
            giver.getPlayer().sendMessage(ChatColor.GREEN + "\u00BB" + ChatColor.GRAY + " " + "Your " + ability + " gave " + name + " " + ChatColor.YELLOW + amount + " " + ChatColor.GRAY + "energy.");
        }
    }

    public void subtractEnergy(int amount) {
        if (energy - amount > maxEnergy) {
            energy = maxEnergy;
        } else {
            this.energy -= amount;
        }
    }

    public float getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public int getHorseCooldown() {
        return horseCooldown;
    }

    public void setHorseCooldown(int horseCooldown) {
        this.horseCooldown = horseCooldown;
    }

    public int getFlagCooldown() {
        return flagCooldown;
    }

    public void setFlagCooldown(int flagCooldown) {
        this.flagCooldown = flagCooldown;
    }

    public int getHitCooldown() {
        return hitCooldown;
    }

    public void setHitCooldown(int hitCooldown) {
        this.hitCooldown = hitCooldown;
    }

    public int getInfusion() {
        return infusionDuration;
    }

    public void setInfusion(int infusionDuration) {
        this.infusionDuration = infusionDuration;
    }

    public void setBreathSlowness(int breathSlownessDuration) {
        this.breathSlownessDuration = breathSlownessDuration;
    }

    public int getBreathSlowness() {
        return breathSlownessDuration;
    }

    public void setFrostbolt(int frostboltDuration) {
        this.frostboltDuration = frostboltDuration;
    }

    public int getFrostbolt() {
        return frostboltDuration;
    }

    public int getWrathDuration() {
        return wrathDuration;
    }

    public void setWrathDuration(int wrathDuration) {
        this.wrathDuration = wrathDuration;
    }

    public int getPresence() {
        return presenceDuration;
    }

    public void setPresence(int presenceDuration) {
        this.presenceDuration = presenceDuration;
    }

    public int getBloodLustDuration() {
        return bloodLustDuration;
    }

    public void setBloodLustDuration(int bloodLustDuration) {
        this.bloodLustDuration = bloodLustDuration;
    }

    public int getBerserk() {
        return berserkDuration;
    }

    public void setBerserk(int berserkDuration) {
        this.berserkDuration = berserkDuration;
    }

    public int getInterveneDuration() {
        return interveneDuration;
    }

    public void setInterveneDuration(int interveneDuration) {
        this.interveneDuration = interveneDuration;
    }

    public int getInterveneDamage() {
        return interveneDamage;
    }

    public void setInterveneDamage(int interveneDamage) {
        this.interveneDamage = interveneDamage;
    }

    public WarlordsPlayer getIntervened() {
        return intervened;
    }

    public void setIntervened(WarlordsPlayer intervened) {
        this.intervened = intervened;
    }

    public WarlordsPlayer getIntervenedBy() {
        return intervenedBy;
    }

    public void setIntervenedBy(WarlordsPlayer intervenedBy) {
        this.intervenedBy = intervenedBy;
    }

    public int getLastStandDuration() {
        return lastStandDuration;
    }

    public void setLastStandDuration(int lastStandDuration) {
        this.lastStandDuration = lastStandDuration;
    }

    public WarlordsPlayer getLastStandedBy() {
        return lastStandedBy;
    }

    public void setLastStandedBy(WarlordsPlayer lastStandedBy) {
        this.lastStandedBy = lastStandedBy;
    }

    public int getBerserkerWounded() {
        return berserkerWounded;
    }

    public void setBerserkerWounded(int berserkerWounded) {
        this.berserkerWounded = berserkerWounded;
    }

    public int getDefenderWounded() {
        return defenderWounded;
    }

    public void setDefenderWounded(int defenderWounded) {
        this.defenderWounded = defenderWounded;
    }

    public int getCrippled() {
        return crippled;
    }

    public void setCrippled(int crippled) {
        this.crippled = crippled;
    }

    public int getOrbsOfLifeDuration() {
        return orbsOfLifeDuration;
    }

    public void setOrbsOfLifeDuration(int orbsOfLifeDuration) {
        this.orbsOfLifeDuration = orbsOfLifeDuration;
    }

    public int getUndyingArmyDuration() {
        return undyingArmyDuration;
    }

    public void setUndyingArmyDuration(int undyingArmyDuration) {
        this.undyingArmyDuration = undyingArmyDuration;
    }

    public boolean isUndyingArmyDead() {
        return undyingArmyDead;
    }

    public void setUndyingArmyDead(boolean undyingArmyDead) {
        this.undyingArmyDead = undyingArmyDead;
    }

    public WarlordsPlayer getUndyingArmyBy() {
        return undyingArmyBy;
    }

    public void setUndyingArmyBy(WarlordsPlayer undyingArmyBy) {
        this.undyingArmyBy = undyingArmyBy;
    }

    public int getWindfuryDuration() {
        return windfuryDuration;
    }

    public void setWindfuryDuration(int windfuryDuration) {
        this.windfuryDuration = windfuryDuration;
    }

    public int getEarthlivingDuration() {
        return earthlivingDuration;
    }

    public void setEarthlivingDuration(int earthlivingDuration) {
        this.earthlivingDuration = earthlivingDuration;
    }

    public int getRepentanceDuration() {
        return repentanceDuration;
    }

    public void setRepentanceDuration(int repentanceDuration) {
        this.repentanceDuration = repentanceDuration;
    }

    public int getRepentanceCounter() {
        return repentanceCounter;
    }

    public void setRepentanceCounter(int repentanceCounter) {
        this.repentanceCounter = repentanceCounter;
    }

    public int getArcaneShield() {
        return arcaneShield;
    }

    public void setArcaneShield(int arcaneShield) {
        this.arcaneShield = arcaneShield;
    }

    public int getArcaneShieldHealth() {
        return arcaneShieldHealth;
    }

    public void setArcaneShieldHealth(int arcaneShieldHealth) {
        this.arcaneShieldHealth = arcaneShieldHealth;
    }

    public int getInferno() {
        return inferno;
    }

    public void setInferno(int inferno) {
        this.inferno = inferno;
    }

    public int getIceBarrier() {
        return iceBarrierDuration;
    }

    public void setIceBarrier(int iceBarrierDuration) {
        this.iceBarrierDuration = iceBarrierDuration;
    }

    public int getIceBarrierSlowness() {
        return iceBarrierSlownessDuration;
    }

    public void setIceBarrierSlowness(int iceBarrierSlownessDuration) {
        this.iceBarrierSlownessDuration = iceBarrierSlownessDuration;
    }

    public int getPowerUpDamage() {
        return powerUpDamage;
    }

    public void setPowerUpDamage(int powerUpDamage) {
        this.powerUpDamage = powerUpDamage;
    }

    public int getPowerUpEnergy() {
        return powerUpEnergy;
    }

    public void setPowerUpEnergy(int powerUpEnergy) {
        this.powerUpEnergy = powerUpEnergy;
    }

    public boolean isPowerUpHeal() {
        return powerUpHeal;
    }

    public void setPowerUpHeal(boolean powerUpHeal) {
        this.powerUpHeal = powerUpHeal;
    }

    public int getPowerUpSpeed() {
        return powerUpSpeed;
    }

    public void setPowerUpSpeed(int powerUpSpeed) {
        this.powerUpSpeed = powerUpSpeed;
    }

    public int getChainLightning() {
        return chainLightning;
    }

    public void setChainLightning(int chainLightning) {
        this.chainLightning = chainLightning;
    }

    public int getChainLightningCooldown() {
        return chainLightningCooldown;
    }

    public void setChainLightningCooldown(int chainLightningCooldown) {
        this.chainLightningCooldown = chainLightningCooldown;
    }

    public int getSpiritLink() {
        return spiritLinkDuration;
    }

    public void setSpiritLink(int spiritLinkDuration) {
        this.spiritLinkDuration = spiritLinkDuration;
    }

    public List<Soulbinding.SoulBoundPlayer> getSoulBindedPlayers() {
        return soulBindedPlayers;
    }

    public void setSoulBindedPlayers(List<Soulbinding.SoulBoundPlayer> soulBindedPlayers) {
        this.soulBindedPlayers = soulBindedPlayers;
    }

    public boolean hasBoundPlayer(WarlordsPlayer warlordsPlayer) {
        for (Soulbinding.SoulBoundPlayer soulBindedPlayer : soulBindedPlayers) {
            if (soulBindedPlayer.getBoundPlayer() == warlordsPlayer) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBoundPlayerSoul(WarlordsPlayer warlordsPlayer) {
        for (Soulbinding.SoulBoundPlayer soulBindedPlayer : soulBindedPlayers) {
            if (soulBindedPlayer.getBoundPlayer() == warlordsPlayer) {
                if (!soulBindedPlayer.isHitWithSoul()) {
                    soulBindedPlayer.setHitWithSoul(true);
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public boolean hasBoundPlayerLink(WarlordsPlayer warlordsPlayer) {
        for (Soulbinding.SoulBoundPlayer soulBindedPlayer : soulBindedPlayers) {
            if (soulBindedPlayer.getBoundPlayer() == warlordsPlayer) {
                if (!soulBindedPlayer.isHitWithLink()) {
                    soulBindedPlayer.setHitWithLink(true);
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public int getSoulBindCooldown() {
        return soulBindCooldown;
    }

    public void setSoulBindCooldown(int soulBindCooldown) {
        this.soulBindCooldown = soulBindCooldown;
    }

    public int getCharged() {
        return charged;
    }

    public void setCharged(int charged) {
        this.charged = charged;
    }

    public Location getChargeLocation() {
        return chargeLocation;
    }

    public void setChargeLocation(Location chargeLocation) {
        this.chargeLocation = chargeLocation;
    }

    public int[] getKills() {
        return kills;
    }

    public void addKill() {
        this.kills[Warlords.game.getMinute()]++;
    }

    public int getTotalKills() {
        return IntStream.of(kills).sum();
    }

    public int[] getAssists() {
        return assists;
    }

    public void addAssist() {
        this.assists[Warlords.game.getMinute()]++;
    }

    public int getTotalAssists() {
        return IntStream.of(assists).sum();
    }

    public List<WarlordsPlayer> getHitBy() {
        return hitBy;
    }

    public void setHitBy(List<WarlordsPlayer> hitBy) {
        this.hitBy = hitBy;
    }

    public int[] getDeaths() {
        return deaths;
    }

    public int getTotalDeaths() {
        return IntStream.of(deaths).sum();
    }

    public void addDeath() {
        this.deaths[Warlords.game.getMinute()]++;
    }

    public float[] getDamage() {
        return damage;
    }

    public void addDamage(float amount) {
        this.damage[Warlords.game.getMinute()] += amount;
    }

    public float getTotalDamage() {
        return (float) IntStream.range(0, damage.length).mapToDouble(i -> damage[i]).sum();
    }

    public float[] getHealing() {
        return healing;
    }

    public void addHealing(float amount) {
        this.healing[Warlords.game.getMinute()] += amount;
    }

    public float getTotalHealing() {
        return (float) IntStream.range(0, healing.length).mapToDouble(i -> healing[i]).sum();
    }

    public float[] getAbsorbed() {
        return absorbed;
    }

    public void addAbsorbed(float amount) {
        this.absorbed[Warlords.game.getMinute()] += amount;
    }

    public float getTotalAbsorbed() {
        return (float) IntStream.range(0, absorbed.length).mapToDouble(i -> absorbed[i]).sum();
    }

    public ItemStack getStatItemStack(String name) {
        ItemStack itemStack = new ItemStack(Material.STONE);
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        meta.setDisplayName(ChatColor.AQUA + "Stat Breakdown (" + name + "):");
        for (int i = 0; i < damage.length - 1 && i < Warlords.game.getMinute() + 1; i++) {
            if (name.equals("Kills")) {
                lore.add(ChatColor.WHITE + "Minute " + (i + 1) + ": " + ChatColor.GOLD + Utils.addCommaAndRound(kills[i + 1]));
            } else if (name.equals("Assists")) {
                lore.add(ChatColor.WHITE + "Minute " + (i + 1) + ": " + ChatColor.GOLD + Utils.addCommaAndRound(assists[i + 1]));
            } else if (name.equals("Deaths")) {
                lore.add(ChatColor.WHITE + "Minute " + (i + 1) + ": " + ChatColor.GOLD + Utils.addCommaAndRound(deaths[i + 1]));
            } else if (name.equals("Damage")) {
                lore.add(ChatColor.WHITE + "Minute " + (i + 1) + ": " + ChatColor.GOLD + Utils.addCommaAndRound(damage[i + 1]));
            } else if (name.equals("Healing")) {
                lore.add(ChatColor.WHITE + "Minute " + (i + 1) + ": " + ChatColor.GOLD + Utils.addCommaAndRound(healing[i + 1]));
            } else if (name.equals("Absorbed")) {
                lore.add(ChatColor.WHITE + "Minute " + (i + 1) + ": " + ChatColor.GOLD + Utils.addCommaAndRound(absorbed[i + 1]));
            }
        }
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public boolean isFirstProc() {
        return firstProc;
    }

    public void setFirstProc(boolean firstProc) {
        this.firstProc = firstProc;
    }

    public boolean isTeamFlagCompass() {
        return teamFlagCompass;
    }

    public void setTeamFlagCompass(boolean teamFlagCompass) {
        this.teamFlagCompass = teamFlagCompass;
    }

    public CalculateSpeed getSpeed() {
        return speed;
    }

    public Location getDeathLocation() {
        return deathLocation;
    }

    public void setDeathLocation(Location deathLocation) {
        this.deathLocation = deathLocation;
    }

    public ArmorStand getDeathStand() {
        return deathStand;
    }

    public void setDeathStand(ArmorStand deathStand) {
        this.deathStand = deathStand;
    }

    public int getFlagsCaptured() {
        return flagsCaptured;
    }

    public void addFlagCap() {
        this.flagsCaptured++;
    }

    public int getFlagsReturned() {
        return flagsReturned;
    }

    public void addFlagReturn() {
        this.flagsReturned++;
    }

    public int getTotalCapsAndReturns() {
        return this.flagsCaptured + this.flagsReturned;
    }

    public int getSpawnProtection() {
        return spawnProtection;
    }

    public void setSpawnProtection(int spawnProtection) {
        this.spawnProtection = spawnProtection;
    }

    public void setSpawnDamage(int spawnDamage) {
        this.spawnDamage = spawnDamage;
    }

    public int getSpawnDamage() {
        return spawnDamage;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }
}
