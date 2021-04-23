package com.ebicep.warlords;

import com.ebicep.warlords.classes.PlayerClass;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;

import java.util.ArrayList;
import java.util.UUID;

public class WarlordsPlayer {

    private Player player;
    private String name;
    private UUID uuid;
    private PlayerClass spec;
    private int health;
    private int maxHealth;
    private int respawnTimer;
    private int energy;
    private int maxEnergy;

    private int hitCooldown;

    private int wrath;

    public WarlordsPlayer(Player player, String name, UUID uuid, PlayerClass spec) {
        this.player = player;
        this.name = name;
        this.uuid = uuid;
        this.spec = spec;
        this.health = spec.getMaxHealth();
        this.maxHealth = spec.getMaxHealth();
        this.respawnTimer = -1;
        this.energy = spec.getMaxEnergy();
        this.maxEnergy = spec.getMaxEnergy();
        this.hitCooldown = 20;
    }

    public void assignItemLore() {
        ItemStack[] inventoryContents = new ItemStack[9];

        Dye redDye = new Dye();
        redDye.setColor(DyeColor.RED);
        Dye limeDye = new Dye();
        limeDye.setColor(DyeColor.LIME);
        Dye orangeDye = new Dye();
        orangeDye.setColor(DyeColor.ORANGE);

        //§
        ItemStack weapon = new ItemStack(Material.GOLD_PICKAXE);
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
        weaponLore.add("§7Max Energy: §a+25");
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

        ItemStack red = new ItemStack(redDye.toItemStack(1));
        ItemMeta redMeta = weapon.getItemMeta();
        redMeta.setDisplayName("§6" + spec.getRed().getName());
        ArrayList<String> redLore = new ArrayList<>();
        redLore.add("§7Cooldown: §b" + spec.getRed().getCooldown());
        redLore.add("§7Energy Cost: §e" + spec.getRed().getEnergyCost());
        redLore.add("§7Crit Chance: §c" + spec.getRed().getCritChance() + "%");
        redLore.add("§7Crit Multiplier: §c" + spec.getRed().getCritMultiplier() + "%");
        redLore.add("");
        redLore.add(spec.getRed().getDescription());
        redMeta.setLore(redLore);
        red.setItemMeta(redMeta);
        redMeta.spigot().setUnbreakable(true);

        ItemStack purple = new ItemStack(Material.GLOWSTONE_DUST);
        ItemMeta purpleMeta = purple.getItemMeta();
        purpleMeta.setDisplayName("§6" + spec.getPurple().getName());
        ArrayList<String> purpleLore = new ArrayList<>();
        purpleLore.add("§7Cooldown: §b" + spec.getPurple().getCooldown());
        purpleLore.add("§7Energy Cost: §e" + spec.getPurple().getEnergyCost());
        purpleLore.add("§7Crit Chance: §c" + spec.getPurple().getCritChance() + "%");
        purpleLore.add("§7Crit Multiplier: §c" + spec.getPurple().getCritMultiplier() + "%");
        purpleLore.add("");
        purpleLore.add(spec.getPurple().getDescription());
        purpleMeta.setLore(purpleLore);
        purple.setItemMeta(purpleMeta);
        purpleMeta.spigot().setUnbreakable(true);

        ItemStack blue = new ItemStack(limeDye.toItemStack(1));
        ItemMeta blueMeta = blue.getItemMeta();
        blueMeta.setDisplayName("§6" + spec.getBlue().getName());
        ArrayList<String> blueLore = new ArrayList<>();
        blueLore.add("§7Cooldown: §b" + spec.getBlue().getCooldown());
        blueLore.add("§7Energy Cost: §e" + spec.getBlue().getEnergyCost());
        blueLore.add("§7Crit Chance: §c" + spec.getBlue().getCritChance() + "%");
        blueLore.add("§7Crit Multiplier: §c" + spec.getBlue().getCritMultiplier() + "%");
        blueLore.add("");
        blueLore.add(spec.getBlue().getDescription());
        blueMeta.setLore(blueLore);
        blue.setItemMeta(blueMeta);
        blueMeta.spigot().setUnbreakable(true);

        ItemStack orange = new ItemStack(orangeDye.toItemStack(1));
        ItemMeta orangeMeta = orange.getItemMeta();
        orangeMeta.setDisplayName("§6" + spec.getOrange().getName());
        ArrayList<String> orangeLore = new ArrayList<>();
        orangeLore.add("§7Cooldown: §b" + spec.getOrange().getCooldown());
        orangeLore.add("§7Energy Cost: §e" + spec.getOrange().getEnergyCost());
        orangeLore.add("§7Crit Chance: §c" + spec.getOrange().getCritChance() + "%");
        orangeLore.add("§7Crit Multiplier: §c" + spec.getOrange().getCritMultiplier() + "%");
        orangeLore.add("");
        orangeLore.add(spec.getOrange().getDescription());
        orangeMeta.setLore(orangeLore);
        orange.setItemMeta(orangeMeta);
        orangeMeta.spigot().setUnbreakable(true);

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

        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName("§aFlag Finder");
        compass.setItemMeta(compassMeta);
        compassMeta.spigot().setUnbreakable(true);

        inventoryContents[0] = weapon;
        inventoryContents[1] = red;
        inventoryContents[2] = purple;
        inventoryContents[3] = blue;
        inventoryContents[4] = orange;
        inventoryContents[7] = horse;
        inventoryContents[8] = compass;

        player.getInventory().setContents(inventoryContents);
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

    public void addHealth(int min, int max, int critChance, int critMultiplier) {
        double random = (int) ((Math.random() * (max - min)) + min);
        int crit = (int) ((Math.random() * (100)));
        if (crit <= critChance) {
            random *= critMultiplier / 100f;
        }
        if (this.health + random > this.maxHealth) {
            this.health = maxHealth;
        } else {
            this.health += Math.round(random);
        }
    }

    public void respawn() {
        this.health = this.maxHealth;
    }

    public int getRespawnTimer() {
        return respawnTimer;
    }

    public void setRespawnTimer(int respawnTimer) {
        this.respawnTimer = respawnTimer;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void subtractEnergy(int amount) {
        if (energy - amount > maxEnergy) {
            energy = maxEnergy;
        } else {
            this.energy -= amount;
        }
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public int getHitCooldown() {
        return hitCooldown;
    }

    public void setHitCooldown(int hitCooldown) {
        this.hitCooldown = hitCooldown;
    }

    public int getWrath() {
        return wrath;
    }

    public void setWrath(int wrath) {
        this.wrath = wrath;
    }
}
