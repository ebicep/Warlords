package com.ebicep.warlords;

import com.ebicep.warlords.classes.ActionBarStats;
import com.ebicep.warlords.classes.PlayerClass;
import com.ebicep.warlords.classes.abilties.OrbsOfLife;
import com.ebicep.warlords.classes.abilties.Soulbinding;
import com.ebicep.warlords.events.WarlordsDeathEvent;
import com.ebicep.warlords.maps.FlagManager;
import com.ebicep.warlords.util.CalculateSpeed;
import com.ebicep.warlords.util.CustomScoreboard;
import com.ebicep.warlords.util.Utils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class WarlordsPlayer {

    private Player player;
    private String name;
    private UUID uuid;
    private PlayerClass spec;
    private boolean hotKeyMode = true;
    private int health;
    private int maxHealth;
    private int regenTimer;
    private int respawnTimer;
    private float energy;
    private float maxEnergy;
    private int horseCooldown;
    private int hitCooldown;

    private int kills = 0;
    private int assists = 0;
    private List<WarlordsPlayer> hitBy = new ArrayList<>();
    private int deaths = 0;
    private float damage = 0;
    private float healing = 0;
    private float absorbed = 0;

    private final CalculateSpeed speed;

    private List<ActionBarStats> actionBarStats = new ArrayList<>();

    public List<ActionBarStats> getActionBarStats() {
        return actionBarStats;
    }

    public void displayActionBar() {
        StringBuilder actionBarMessage = new StringBuilder(ChatColor.GOLD + "§lHP: ");
        float healthRatio = (float) health / maxHealth;
        if (healthRatio >= .75) {
            actionBarMessage.append(ChatColor.GREEN);

        } else if (healthRatio >= .25) {
            actionBarMessage.append(ChatColor.YELLOW);

        } else {
            actionBarMessage.append(ChatColor.RED);

        }
        actionBarMessage.append("§l").append(health).append(ChatColor.GOLD).append("§l/§l").append(maxHealth).append("    ");
        if (Warlords.game.getTeamBlue().contains(player)) {
            actionBarMessage.append(ChatColor.BLUE).append("§lBLU TEAM  ");
        } else if (Warlords.game.getTeamRed().contains(player)) {
            actionBarMessage.append(ChatColor.RED).append("§lRED TEAM  ");
        }
        for (int i = 0; i < actionBarStats.size(); i++) {
            ActionBarStats actionBarStat = actionBarStats.get(i);
            actionBarMessage.append(ChatColor.GREEN).append(actionBarStat.getName()).append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(actionBarStat.getTimeLeft()).append("  ");
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
                    actionBarMessage.append(ChatColor.GREEN).append("ICEB").append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append(iceBarrierDuration / 20).append(" ");
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

        Utils.sendActionbar(player, actionBarMessage.toString());
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
                    Utils.sendActionbar(player, "" + ChatColor.BLUE + ChatColor.BOLD + "YOUR Flag " + ChatColor.WHITE + "is stolen " + ChatColor.RED + blueFlagDistance + "m " + ChatColor.WHITE + "away!");
                } else if (blueFlag.getFlag() instanceof FlagManager.GroundFlagLocation) {
                    Utils.sendActionbar(player, "" + ChatColor.BLUE + ChatColor.BOLD + "YOUR Flag " + ChatColor.GOLD + "is dropped " + ChatColor.RED + blueFlagDistance + "m " + ChatColor.WHITE + "away!");
                } else {
                    Utils.sendActionbar(player, "" + ChatColor.BLUE + ChatColor.BOLD + "YOUR " + ChatColor.GREEN + "Flag is safe");
                }
            } else {
                if (redFlag.getFlag() instanceof FlagManager.PlayerFlagLocation) {
                    Utils.sendActionbar(player, "" + ChatColor.RED + ChatColor.BOLD + "ENEMY Flag " + ChatColor.WHITE + "is stolen " + ChatColor.RED + redFlagDistance + "m " + ChatColor.WHITE + "away!");
                } else if (redFlag.getFlag() instanceof FlagManager.GroundFlagLocation) {
                    Utils.sendActionbar(player, "" + ChatColor.RED + ChatColor.BOLD + "ENEMY Flag " + ChatColor.GOLD + "is dropped " + ChatColor.RED + redFlagDistance + "m " + ChatColor.WHITE + "away!");
                } else {
                    Utils.sendActionbar(player, "" + ChatColor.RED + ChatColor.BOLD + "ENEMY " + ChatColor.GREEN + "Flag is safe");
                }
            }
        } else {
            if (teamFlagCompass) {
                if (redFlag.getFlag() instanceof FlagManager.PlayerFlagLocation) {
                    Utils.sendActionbar(player, "" + ChatColor.RED + ChatColor.BOLD + "YOUR Flag " + ChatColor.WHITE + "is stolen " + ChatColor.RED + redFlagDistance + "m " + ChatColor.WHITE + "away!");
                } else if (redFlag.getFlag() instanceof FlagManager.GroundFlagLocation) {
                    Utils.sendActionbar(player, "" + ChatColor.RED + ChatColor.BOLD + "YOUR Flag " + ChatColor.GOLD + "is dropped " + ChatColor.RED + redFlagDistance + "m " + ChatColor.WHITE + "away!");
                } else {
                    Utils.sendActionbar(player, "" + ChatColor.RED + ChatColor.BOLD + "YOUR " + ChatColor.GREEN + "Flag is safe");
                }
            } else {
                if (blueFlag.getFlag() instanceof FlagManager.PlayerFlagLocation) {
                    Utils.sendActionbar(player, "" + ChatColor.BLUE + ChatColor.BOLD + "ENEMY Flag " + ChatColor.WHITE + "is stolen " + ChatColor.RED + blueFlagDistance + "m " + ChatColor.WHITE + "away!");
                } else if (blueFlag.getFlag() instanceof FlagManager.GroundFlagLocation) {
                    Utils.sendActionbar(player, "" + ChatColor.BLUE + ChatColor.BOLD + "ENEMY Flag " + ChatColor.GOLD + "is dropped " + ChatColor.RED + blueFlagDistance + "m " + ChatColor.WHITE + "away!");
                } else {
                    Utils.sendActionbar(player, "" + ChatColor.BLUE + ChatColor.BOLD + "ENEMY " + ChatColor.GREEN + "Flag is safe");
                }
            }
        }
    }


    /*
    cryo 5.95
    cryo + infusion 7.3
    cryo + presence  6.84
    freezing 5.15
    freezing + infusion 6.38
    freezing + presence 5.93
    barrier 6.34
    barrier + infusion 7.36
     */


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

    private boolean energyPowerup;
    public boolean isEnergyPowerup() {
        return energyPowerup;
    }

    private Location deathLocation;
    private ArmorStand deathStand;

    public WarlordsPlayer(Player player, String name, UUID uuid, PlayerClass spec, boolean energyPowerup) {
        this.player = player;
        this.name = name;
        this.uuid = uuid;
        this.spec = spec;
        this.health = spec.getMaxHealth();
        this.maxHealth = spec.getMaxHealth();
        this.respawnTimer = -1;
        this.energy = spec.getMaxEnergy();
        this.maxEnergy = spec.getMaxEnergy();
        this.horseCooldown = 0;
        this.hitCooldown = 20;
        this.speed = new CalculateSpeed(player :: setWalkSpeed, 13);
        grayDye.setColor(DyeColor.GRAY);
        this.energyPowerup = energyPowerup;
    }

    public void assignItemLore() {
        //§
        ItemStack weapon = new ItemStack(Material.STONE_AXE);
        ItemMeta weaponMeta = weapon.getItemMeta();
        weaponMeta.setDisplayName("§6Warlord's Fat Cock of the " + spec.getWeapon().getName());
        ArrayList<String> weaponLore = new ArrayList<>();
        weaponLore.add("§7Damage: §c132 §7- §c179");
        weaponLore.add("§7Crit Chance: §c25%");
        weaponLore.add("§7Crit Multiplier: §c200%");
        weaponLore.add("");
        String classNamePath = spec.getClass().getGenericSuperclass().getTypeName();
        weaponLore.add("§a" + classNamePath.substring(classNamePath.indexOf("Abstract") + 8) + " (" + spec.getClass().getSimpleName() + "):");
        weaponLore.add("§aIncreases the damage you");
        weaponLore.add("§adeal with " + spec.getWeapon().getName() + " by §c20%");
        weaponLore.add("");
        weaponLore.add("§7Health: §a+800");
        weaponLore.add("§7Max Energy: §a+35");
        weaponLore.add("§7Cooldown Reduction: §a+13%");
        weaponLore.add("§7Speed: §a+13%");
        weaponLore.add("");
        weaponLore.add("§6Skill Boost Unlocked");
        weaponLore.add("§3Crafted");
        weaponLore.add("§dVoid Forged [4/4]");
        weaponLore.add("§aEQUIPPED");
        weaponLore.add("§bBOUND");
        weaponMeta.setLore(weaponLore);
        weapon.setItemMeta(weaponMeta);
        weaponMeta.spigot().setUnbreakable(true);
        player.getInventory().setItem(0, weapon);

        updateRedItem();
        updatePurpleItem();
        updateBlueItem();
        updateOrangeItem();
        updateHorseItem();

        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName("§aFlag Finder");
        compass.setItemMeta(compassMeta);
        compassMeta.spigot().setUnbreakable(true);
        player.getInventory().setItem(8, compass);
    }

    public void updateRedItem() {
        if (spec.getRed().getCurrentCooldown() != 0) {
            ItemStack cooldown = new ItemStack(grayDye.toItemStack(Math.round(spec.getRed().getCurrentCooldown())));
            player.getInventory().setItem(1, cooldown);
        } else {
            Dye redDye = new Dye();
            redDye.setColor(DyeColor.RED);
            ItemStack red = new ItemStack(redDye.toItemStack(1));
            ItemMeta redMeta = red.getItemMeta();
            redMeta.setDisplayName("§6" + spec.getRed().getName());
            ArrayList<String> redLore = new ArrayList<>();
            redLore.add("§7Cooldown: §b" + spec.getRed().getCooldown());
            redLore.add("§7Energy Cost: §e" + spec.getRed().getEnergyCost());

            if (spec.getRed().getCritChance()  != 0 || spec.getRed().getCritMultiplier() != 0) {
                redLore.add("§7Crit Chance: §c" + spec.getRed().getCritChance() + "%");
                redLore.add("§7Crit Multiplier: §c" + spec.getRed().getCritMultiplier() + "%");
            }

            redLore.add("");
            redLore.addAll(Arrays.asList(spec.getRed().getDescription().split("\n")));
            redMeta.setLore(redLore);
            red.setItemMeta(redMeta);
            redMeta.spigot().setUnbreakable(true);
            player.getInventory().setItem(1, red);
        }
    }

    public void updatePurpleItem() {
        if (spec.getPurple().getCurrentCooldown() != 0) {
            ItemStack cooldown = new ItemStack(grayDye.toItemStack(Math.round(spec.getPurple().getCurrentCooldown())));
            player.getInventory().setItem(2, cooldown);
        } else {
            ItemStack purple = new ItemStack(Material.GLOWSTONE_DUST);
            ItemMeta purpleMeta = purple.getItemMeta();
            purpleMeta.setDisplayName("§6" + spec.getPurple().getName());
            ArrayList<String> purpleLore = new ArrayList<>();
            purpleLore.add("§7Cooldown: §b" + spec.getPurple().getCooldown());
            purpleLore.add("§7Energy Cost: §e" + spec.getPurple().getEnergyCost());

            if (spec.getPurple().getCritChance()  != 0 || spec.getPurple().getCritMultiplier() != 0) {
                purpleLore.add("§7Crit Chance: §c" + spec.getPurple().getCritChance() + "%");
                purpleLore.add("§7Crit Multiplier: §c" + spec.getPurple().getCritMultiplier() + "%");
            }

            purpleLore.add("");
            purpleLore.addAll(Arrays.asList(spec.getPurple().getDescription().split("\n")));
            purpleMeta.setLore(purpleLore);
            purple.setItemMeta(purpleMeta);
            purpleMeta.spigot().setUnbreakable(true);
            player.getInventory().setItem(2, purple);
        }
    }

    public void updateBlueItem() {
        if (spec.getBlue().getCurrentCooldown() != 0) {
            ItemStack cooldown = new ItemStack(grayDye.toItemStack(Math.round(spec.getBlue().getCurrentCooldown())));
            player.getInventory().setItem(3, cooldown);
        } else {
            Dye limeDye = new Dye();
            limeDye.setColor(DyeColor.LIME);
            ItemStack blue = new ItemStack(limeDye.toItemStack(1));
            ItemMeta blueMeta = blue.getItemMeta();
            blueMeta.setDisplayName("§6" + spec.getBlue().getName());
            ArrayList<String> blueLore = new ArrayList<>();
            blueLore.add("§7Cooldown: §b" + spec.getBlue().getCooldown());
            blueLore.add("§7Energy Cost: §e" + spec.getBlue().getEnergyCost());

            if (spec.getBlue().getCritChance()  != 0 || spec.getBlue().getCritMultiplier() != 0) {
                blueLore.add("§7Crit Chance: §c" + spec.getBlue().getCritChance() + "%");
                blueLore.add("§7Crit Multiplier: §c" + spec.getBlue().getCritMultiplier() + "%");
            }

            blueLore.add("");
            blueLore.addAll(Arrays.asList(spec.getBlue().getDescription().split("\n")));
            blueMeta.setLore(blueLore);
            blue.setItemMeta(blueMeta);
            blueMeta.spigot().setUnbreakable(true);
            player.getInventory().setItem(3, blue);
        }
    }

    public void updateOrangeItem() {
        if (spec.getOrange().getCurrentCooldown() != 0) {
            ItemStack cooldown = new ItemStack(grayDye.toItemStack(Math.round(spec.getOrange().getCurrentCooldown())));
            player.getInventory().setItem(4, cooldown);
        } else {
            Dye orangeDye = new Dye();
            orangeDye.setColor(DyeColor.ORANGE);
            ItemStack orange = new ItemStack(orangeDye.toItemStack(1));
            ItemMeta orangeMeta = orange.getItemMeta();
            orangeMeta.setDisplayName("§6" + spec.getOrange().getName());
            ArrayList<String> orangeLore = new ArrayList<>();
            orangeLore.add("§7Cooldown: §b" + spec.getOrange().getCooldown());
            orangeLore.add("§7Energy Cost: §e" + spec.getOrange().getEnergyCost());

            if (spec.getOrange().getCritChance()  != 0 || spec.getOrange().getCritMultiplier() != 0) {
                orangeLore.add("§7Crit Chance: §c" + spec.getOrange().getCritChance() + "%");
                orangeLore.add("§7Crit Multiplier: §c" + spec.getOrange().getCritMultiplier() + "%");
            }

            orangeLore.add("");
            orangeLore.addAll(Arrays.asList(spec.getOrange().getDescription().split("\n")));
            orangeMeta.setLore(orangeLore);
            orange.setItemMeta(orangeMeta);
            orangeMeta.spigot().setUnbreakable(true);
            player.getInventory().setItem(4, orange);
        }
    }

    public void updateHorseItem() {
        if (horseCooldown != 0) {
            ItemStack cooldown = new ItemStack(Material.IRON_BARDING, horseCooldown);
            player.getInventory().setItem(7, cooldown);
        } else {
            ItemStack horse = new ItemStack(Material.GOLD_BARDING);
            ItemMeta horseMeta = horse.getItemMeta();
            horseMeta.setDisplayName("§aMount §7- §eRight-Click!");
            ArrayList<String> horseLore = new ArrayList<>();
            horseLore.add("§7Cooldown: §b15 seconds");
            horseLore.add("");
            horseLore.add("§7Call your steed to assists you in battle");
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

    public void addHealth(WarlordsPlayer attacker, String ability, int min, int max, int critChance, int critMultiplier) {
        if (attacker == this && ability.isEmpty()) {
            player.sendMessage("§c\u00AB§7 You took §c" + min * -1 + "§7 melee damage.");

            if (health + min < 0) {
                health = 0;
            } else {
                health += min;
            }
        } else {

            if (attacker.getInferno() != 0) {
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
                damageHealValue *= metadata.asDouble();
            }

            //TODO check if totaldmgreduc works
            //reduction begining with base resistance
            float totalReduction = 1;
            if (min < 0) {
                totalReduction = 1 - spec.getDamageResistance() / 100f;
                if (attacker.getPowerUpDamage() != 0) {
                    totalReduction += .3;
                }
                if (attacker.getBerserk() != 0) {
                    totalReduction += .25;
                }
                if (berserkDuration != 0) {
                    totalReduction += .1;
                }
                if (iceBarrierDuration != 0) {
                    totalReduction -= .5;
                }
                if (chainLightningCooldown != 0) {
                    totalReduction -= chainLightning * .1;
                }
                if (spiritLinkDuration != 0) {
                    totalReduction -= .2;
                }
                if (attacker.getCrippled() != 0) {
                    totalReduction -= .125;
                }
            }
            if (interveneDuration != 0) {
                //TODO check teammate heal
                if (!Warlords.game.onSameTeam(this, attacker)) {
                    damageHealValue *= totalReduction;
                    damageHealValue *= .5;
                    if (isCrit) {
                        intervenedBy.getPlayer().sendMessage("§c\u00AB§7 " + attacker.getName() + "'s Intervene hit you for §c§l" + (int) damageHealValue * -1 + "! §7critical damage.");
                        attacker.getPlayer().sendMessage("§a\u00BB§7 Your Intervene hit " + intervenedBy.getName() + " for §c§l" + (int) damageHealValue * -1 + "! §7critical damage.");

                        for (Player player1 : player.getWorld().getPlayers()) {
                            player1.playSound(player.getLocation(), "warrior.intervene.block3", 2, 1);
                        }
                    } else {
                        intervenedBy.getPlayer().sendMessage("§c\u00AB§7 " + attacker.getName() + "'s Intervene hit you for §c" + (int) damageHealValue * -1 + "§7 damage.");
                        attacker.getPlayer().sendMessage("§a\u00BB§7 Your Intervene hit " + intervenedBy.getName() + " for §c" + (int) damageHealValue * -1 + "§7 damage.");

                        for (Player player1 : player.getWorld().getPlayers()) {
                            player1.playSound(player.getLocation(), "warrior.intervene.block.1", 2, 1);
                        }
                    }
                    intervenedBy.setHealth((int) (intervenedBy.getHealth() + damageHealValue));
                    interveneDamage += damageHealValue;

                    this.addAbsorbed(-damageHealValue);
                    attacker.addDamage(-damageHealValue);
                }
            } else if (arcaneShield != 0 && !Warlords.game.onSameTeam(this, attacker)) {
                damageHealValue *= totalReduction;
                //TODO check teammate heal
                if (arcaneShieldHealth + damageHealValue < 0) {
                    Bukkit.broadcastMessage("" + arcaneShieldHealth);
                    Bukkit.broadcastMessage("" + damageHealValue);
                    Bukkit.broadcastMessage("" + (arcaneShieldHealth + damageHealValue));

                    arcaneShield = 0;
                    addHealth(attacker, ability, (int) (arcaneShieldHealth + damageHealValue), (int) (arcaneShieldHealth + damageHealValue), isCrit ? 100 : -1, 100);

                    this.absorbed += -(arcaneShield + damageHealValue);
                } else {
                    if (ability.isEmpty()) {
                        player.sendMessage("§c\u00AB§7 You absorbed " + attacker.getName() + "'s melee §7hit.");
                        attacker.getPlayer().sendMessage("§a\u00BB§7 Your melee hit was absorbed by " + name);
                    } else {
                        player.sendMessage("§c\u00AB§7 You absorbed " + attacker.getName() + "'s " + ability + " §7hit.");
                        attacker.getPlayer().sendMessage("§a\u00BB§7 Your " + ability + " was absorbed by " + name + "§7.");
                    }

                    this.absorbed += -damageHealValue;
                }
                arcaneShieldHealth += damageHealValue;

                //Bukkit.broadcastMessage("" + arcaneShieldHealth);
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
                            player.sendMessage("§a\u00AB§7 Your " + ability + " critically healed you for §a§l" + (int) damageHealValue + "! §7health.");
                        } else {
                            player.sendMessage("§a\u00AB§7 Your " + ability + " healed you for §a" + (int) damageHealValue + " §7health.");
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
                        //TODO rework
//                        if (spec.getOrange() instanceof Totem.TotemSpiritguard) {
//                            for (int i = 0; i < Warlords.totems.size(); i++) {
//                                Totem totem = Warlords.totems.get(i);
//                                if (totem.getOwner() == this) {
//                                    if (totem.getSecondsLeft() != 0) {
//                                        debt = true;
//                                        ((Totem.TotemSpiritguard) totem.getOwner().getSpec().getOrange()).setDelayedDamage((int) (((Totem.TotemSpiritguard) totem.getOwner().getSpec().getOrange()).getDelayedDamage() + damageHealValue));
//                                    }
//                                    intervene = 0;
//                                }
//                            }
//                        }
                        if (lastStandDuration != 0) {
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
                                    lastStandedBy.addHealth(lastStandedBy, "Last Stand", (int) (healValue), (int) (healValue), 100, 100);
                                else
                                    lastStandedBy.addHealth(lastStandedBy, "Last Stand", (int) (healValue), (int) (healValue), -1, 100);
                            }
                            addAbsorbed(-damageHealValue);
                        }

                        if (isCrit) {
                            if (ability.isEmpty()) {
                                player.sendMessage("§c\u00AB§7 " + attacker.getName() + " hit you for §c§l" + (int) damageHealValue * -1 + "! §7critical melee damage.");
                                attacker.getPlayer().sendMessage("§a\u00BB§7 " + "You hit " + name + " for §c§l" + (int) damageHealValue * -1 + "! §7critical melee damage.");
                            } else {
                                player.sendMessage("§c\u00AB§7 " + attacker.getName() + "'s " + ability + " hit you for §c§l" + (int) damageHealValue * -1 + "! §7critical damage.");
                                attacker.getPlayer().sendMessage("§a\u00BB§7 " + "Your " + ability + " hit " + name + " for §c§l" + (int) damageHealValue * -1 + "! §7critical damage.");
                            }
                        } else {
                            if (ability.isEmpty()) {
                                player.sendMessage("§c\u00AB§7 " + attacker.getName() + " hit you for §c" + (int) damageHealValue * -1 + " §7melee damage.");
                                attacker.getPlayer().sendMessage("§a\u00BB§7 " + "You hit " + name + " for §c" + (int) damageHealValue * -1 + " §7melee damage.");
                            } else {
                                player.sendMessage("§c\u00AB§7 " + attacker.getName() + "'s " + ability + " hit you for §c" + (int) damageHealValue * -1 + " §7damage.");
                                attacker.getPlayer().sendMessage("§a\u00BB§7 " + "Your " + ability + " hit " + name + " for §c" + (int) damageHealValue * -1 + " §7damage.");
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
                                    player.sendMessage("§a\u00AB§7 " + attacker.getName() + "'s " + ability + " critically healed you for §a§l" + (int) damageHealValue + "! §7health.");
                                    attacker.getPlayer().sendMessage("§a\u00BB§7 " + "Your " + ability + " critically healed " + name + " for §a§l" + (int) damageHealValue + "! §7health.");
                                } else {
                                    player.sendMessage("§a\u00AB§7 " + attacker.getName() + "'s " + ability + " healed for §a" + (int) damageHealValue + " §7health.");
                                    attacker.getPlayer().sendMessage("§a\u00BB§7 " + "Your " + ability + " healed " + name + " for §a" + (int) damageHealValue + " §7health.");
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
                    player.damage(0);
                }
                attacker.addDamage(-damageHealValue);
                if (this.health <= 0) {
                    //grave
                    Location graveLocation = player.getLocation().clone();
                    for (int i = 0; i < 30; i++) {
                        if (player.getWorld().getBlockAt(graveLocation.clone().add(0, -1, 0)).getType() == Material.AIR) {
                            //get block on floor
                            graveLocation.add(0, -1, 0);
                        } else {
                            //making sure the grave isnt on a block
                            if (player.getWorld().getBlockAt(graveLocation).getType() != Material.AIR) {
                                if (player.getWorld().getBlockAt(graveLocation.clone().add(0, 1, 0)).getType() == Material.AIR) {
                                    graveLocation.add(0, 1, 0);
                                } else if (player.getWorld().getBlockAt(graveLocation.clone().add(1, 0, 0)).getType() == Material.AIR) {
                                    graveLocation.add(1, 0, 0);
                                } else if (player.getWorld().getBlockAt(graveLocation.clone().add(-1, 0, 0)).getType() == Material.AIR) {
                                    graveLocation.add(-1, 0, 0);
                                } else if (player.getWorld().getBlockAt(graveLocation.clone().add(0, 0, 1)).getType() == Material.AIR) {
                                    graveLocation.add(0, 0, 1);
                                } else if (player.getWorld().getBlockAt(graveLocation.clone().add(0, 0, -1)).getType() == Material.AIR) {
                                    graveLocation.add(0, 0, -1);
                                }
                            }

                            deathLocation = graveLocation;

                            //spawn grave
                            player.getWorld().getBlockAt(graveLocation).setType(Material.SAPLING);
                            player.getWorld().getBlockAt(graveLocation).setData((byte) 5);

                            graveLocation.setYaw(0);
                            deathStand = (ArmorStand) player.getWorld().spawnEntity(player.getWorld().getBlockAt(graveLocation).getLocation().add(.5, -1.5, .5), EntityType.ARMOR_STAND);
                            if (Warlords.game.isBlueTeam(player)) {
                                deathStand.setCustomName(ChatColor.BLUE + name + ChatColor.GRAY + " - " + ChatColor.YELLOW + "DEAD");
                            } else {
                                deathStand.setCustomName(ChatColor.RED + name + ChatColor.GRAY + " - " + ChatColor.YELLOW + "DEAD");
                            }
                            deathStand.setCustomNameVisible(true);
                            deathStand.setGravity(false);
                            deathStand.setVisible(false);
                            break;
                        }
                    }

                    attacker.getPlayer().playSound(attacker.getPlayer().getLocation(), Sound.ORB_PICKUP, 500f, 0.3f);

                    hitBy.remove(attacker);
                    hitBy.add(0, attacker);

                    attacker.addKill();
                    attacker.scoreboard.updateKillsAssists();
                    this.addDeath();
                    this.scoreboard.updateKillsAssists();
                    Bukkit.getPluginManager().callEvent(new WarlordsDeathEvent(this));
                    // TODO: add kill messages for everyone, filter out killer/victim
                    if (Warlords.game.isBlueTeam(attacker.player)) {
                        player.sendMessage(ChatColor.GRAY + "You were killed by " + ChatColor.BLUE + attacker.getName());
                        attacker.getPlayer().sendMessage(ChatColor.GRAY + "You killed " + ChatColor.RED + name);
                        for (Player gamePlayer : Warlords.getPlayers().keySet()) {
                            gamePlayer.sendMessage(ChatColor.RED + name + ChatColor.GRAY + " was killed by " + ChatColor.BLUE + attacker.getName());
                        }
                    } else {
                        player.sendMessage(ChatColor.GRAY + "You were killed by " + ChatColor.RED + attacker.getName());
                        attacker.getPlayer().sendMessage(ChatColor.GRAY + "You killed " + ChatColor.BLUE + name);
                        for (Player gamePlayer : Warlords.getPlayers().keySet()) {
                            gamePlayer.sendMessage(ChatColor.BLUE + name + ChatColor.GRAY + " was killed by " + ChatColor.RED + attacker.getName());
                        }
                    }

                    if (scoreboard.getBlueTeam().contains(name)) {
                        Warlords.redKills++;
                        Warlords.game.addRedPoints(SCORE_KILL_POINTS);
                    } else {
                        Warlords.blueKills++;
                        Warlords.game.addBluePoints(SCORE_KILL_POINTS);
                    }

                    for (WarlordsPlayer value : Warlords.getPlayers().values()) {
                        value.getScoreboard().updateKills();
                    }
                } else {

                    if (!ability.isEmpty()) {
                        attacker.getPlayer().playSound(attacker.getPlayer().getLocation(), Sound.ORB_PICKUP, 0.8f, 1f);
                    }
                }

            }

            //TODO make inital windfury hit proc
            if (ability.equals("")) {
                if (attacker.getWindfuryDuration() != 0) {
                    int windfuryActivate = (int) (Math.random() * 100);
                    if (attacker.isFirstProc()) {
                        attacker.setFirstProc(false);
                        windfuryActivate = 0;
                    }
                    if (windfuryActivate < 35) {
                        for (Player player1 : Bukkit.getOnlinePlayers()) {
                            player1.playSound(player.getLocation(), "shaman.windfuryweapon.impact", 1.5F, 1);
                        }
                        addHealth(attacker, "Windfury Weapon", min, max, 25, 235);
                        if (health > 0)
                            addHealth(attacker, "Windfury Weapon", min, max, 25, 235);
                    }
                } else if (attacker.getEarthlivingDuration() != 0) {
                    int earthlivingActivate = (int) (Math.random() * 100);
                    if (attacker.isFirstProc()) {
                        if (isCrit) {
                            attacker.addHealth(attacker, "Earthliving Weapon", (int) (damageHealValue * -2.4), (int) (damageHealValue * -2.4), 100, 100);
                        } else {
                            attacker.addHealth(attacker, "Earthliving Weapon", (int) (damageHealValue * -2.4), (int) (damageHealValue * -2.4), -1, 100);
                        }

                        for (Player player1 : player.getWorld().getPlayers()) {
                            player1.playSound(player.getLocation(), "shaman.earthlivingweapon.impact", 1, 1);
                        }

                        attacker.setFirstProc(false);
                        List<Entity> near = attacker.getPlayer().getNearbyEntities(3.0D, 3.0D, 3.0D);
                        near = Utils.filterOnlyTeammates(near, attacker.getPlayer());
                        int counter = 0;
                        for (Entity entity : near) {
                            if (entity instanceof Player) {
                                if (earthlivingActivate < 40) {
                                    Warlords.getPlayer((Player) near.get(0)).addHealth(attacker, "Earthliving Weapon", (int) (damageHealValue * -2.4), (int) (damageHealValue * -2.4), 25, 100);

                                    counter++;
                                }
                            }
                            if (counter == 2)
                                break;
                        }
                    } else if (earthlivingActivate < 40) {
                        if (isCrit) {
                            attacker.addHealth(attacker, "Earthliving Weapon", (int) (damageHealValue * -2.4), (int) (damageHealValue * -2.4), 100, 100);
                        } else {
                            attacker.addHealth(attacker, "Earthliving Weapon", (int) (damageHealValue * -2.4), (int) (damageHealValue * -2.4), -1, 100);

                        }

                        for (Player player1 : player.getWorld().getPlayers()) {
                            player1.playSound(player.getLocation(), "shaman.earthlivingweapon.impact", 1, 1);
                        }

                        List<Entity> near = attacker.getPlayer().getNearbyEntities(3.0D, 3.0D, 3.0D);
                        near = Utils.filterOnlyTeammates(near, attacker.getPlayer());
                        int counter = 0;
                        for (Entity entity : near) {
                            if (entity instanceof Player) {
                                Warlords.getPlayer((Player) near.get(0)).addHealth(attacker, "Earthliving Weapon", (int) (damageHealValue * -2.4), (int) (damageHealValue * -2.4), 25, 440);
                                counter++;
                            }
                            if (counter == 2)
                                break;
                        }
                    }
                } else if (attacker.getSoulBindCooldown() != 0) {
                    attacker.getPlayer().sendMessage("§a\u00BB§7 " + ChatColor.GRAY + "Your " + ChatColor.LIGHT_PURPLE + "Soulbinding Weapon " + ChatColor.GRAY + "has bound " + player.getName());
                    attacker.getSoulBindedPlayers().add(new Soulbinding.SoulBoundPlayer(this, 2));
                }
            }
        }
    }

    private static final int SCORE_KILL_POINTS = 5;

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
            this.energy += 0;
        } else {
            this.energy += amount;
        }
        if (this == giver) {
            player.sendMessage("§a\u00AB§7 Your " + ability + " gave you §e" + amount + " §7energy.");
        } else {
            player.sendMessage("§a\u00AB§7 " + giver.getName() + "'s " + ability + " gave you §e" + amount + " §7energy.");
            giver.getPlayer().sendMessage("§a\u00BB§7 " + "Your " + ability + " gave " + name + " §e" + amount + " §7energy.");
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

    public boolean hasBoundPlayerSoul(WarlordsPlayer warlordsPlayer) {
        for (Soulbinding.SoulBoundPlayer soulBindedPlayer : soulBindedPlayers) {
            if (soulBindedPlayer.getBoundPlayer() == warlordsPlayer) {
                if (!soulBindedPlayer.isHitWithSoul()) {
                    soulBindedPlayer.setHitWithSoul(true);
                    return true;
                }
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

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void addKill() {
        this.kills++;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public void addAssist() {
        this.assists++;
    }

    public List<WarlordsPlayer> getHitBy() {
        return hitBy;
    }

    public void setHitBy(List<WarlordsPlayer> hitBy) {
        this.hitBy = hitBy;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void addDeath() {
        this.deaths++;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void addDamage(float amount) {
        this.damage += amount;
    }

    public float getHealing() {
        return healing;
    }

    public void setHealing(int healing) {
        this.healing = healing;
    }

    public void addHealing(float amount) {
        this.healing += amount;
    }

    public float getAbsorbed() {
        return absorbed;
    }

    public void setAbsorbed(int absorbed) {
        this.absorbed = absorbed;
    }

    public void addAbsorbed(float amount) {
        this.absorbed += amount;
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
}
