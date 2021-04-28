package com.ebicep.warlords;

import com.ebicep.warlords.classes.PlayerClass;
import com.ebicep.warlords.classes.abilties.Orb;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarlordsPlayer {

    private Player player;
    private String name;
    private UUID uuid;
    private PlayerClass spec;
    private int health;
    private int maxHealth;
    private int regenTimer;
    private int respawnTimer;
    private float energy;
    private float maxEnergy;
    public static final float defaultSpeed = (float) (.2825);
    public static final float infusionSpeed = (float) (.35);
    public static final float presenceSpeed = (float) (.325);

    private int horseCooldown;
    private int hitCooldown;

    private int infusion = 0;
    private int wrath = 0;
    private int presence = 0;
    private int bloodLust = 0;
    private int berserk = 0;
    private int intervene = 0;
    private int interveneDamage = 0;
    private WarlordsPlayer intervened;
    private WarlordsPlayer intervenedBy;
    private int lastStand = 0;
    private WarlordsPlayer lastStandedBy;
    private int orbOfLife = 0;
    private int undyingArmy = 0;
    private boolean undyingArmyDead = false;
    private WarlordsPlayer undyingArmyBy;
    private int windfury = 0;
    private int earthliving = 0;


    private int berserkerWounded = 0;
    private int defenderWounded = 0;
    private int crippled = 0;
    private final Dye grayDye = new Dye();

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
        player.setWalkSpeed(defaultSpeed);
        this.horseCooldown = 0;
        this.hitCooldown = 20;
        grayDye.setColor(DyeColor.GRAY);
    }

    public void assignItemLore() {
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
            ItemStack cooldown = new ItemStack(grayDye.toItemStack(spec.getRed().getCurrentCooldown()));
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
            redLore.add("§7Crit Chance: §c" + spec.getRed().getCritChance() + "%");
            redLore.add("§7Crit Multiplier: §c" + spec.getRed().getCritMultiplier() + "%");
            redLore.add("");
            redLore.add(spec.getRed().getDescription());
            redMeta.setLore(redLore);
            red.setItemMeta(redMeta);
            redMeta.spigot().setUnbreakable(true);
            player.getInventory().setItem(1, red);
        }
    }

    public void updatePurpleItem() {
        if (spec.getPurple().getCurrentCooldown() != 0) {
            ItemStack cooldown = new ItemStack(grayDye.toItemStack(spec.getPurple().getCurrentCooldown()));
            player.getInventory().setItem(2, cooldown);
        } else {
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
            player.getInventory().setItem(2, purple);
        }
    }

    public void updateBlueItem() {
        if (spec.getBlue().getCurrentCooldown() != 0) {
            ItemStack cooldown = new ItemStack(grayDye.toItemStack(spec.getBlue().getCurrentCooldown()));
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
            blueLore.add("§7Crit Chance: §c" + spec.getBlue().getCritChance() + "%");
            blueLore.add("§7Crit Multiplier: §c" + spec.getBlue().getCritMultiplier() + "%");
            blueLore.add("");
            blueLore.add(spec.getBlue().getDescription());
            blueMeta.setLore(blueLore);
            blue.setItemMeta(blueMeta);
            blueMeta.spigot().setUnbreakable(true);
            player.getInventory().setItem(3, blue);
        }
    }

    public void updateOrangeItem() {
        if (spec.getOrange().getCurrentCooldown() != 0) {
            ItemStack cooldown = new ItemStack(grayDye.toItemStack(spec.getOrange().getCurrentCooldown()));
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
            orangeLore.add("§7Crit Chance: §c" + spec.getOrange().getCritChance() + "%");
            orangeLore.add("§7Crit Multiplier: §c" + spec.getOrange().getCritMultiplier() + "%");
            orangeLore.add("");
            orangeLore.add(spec.getOrange().getDescription());
            orangeMeta.setLore(orangeLore);
            orange.setItemMeta(orangeMeta);
            orangeMeta.spigot().setUnbreakable(true);
            player.getInventory().setItem(4, orange);
        }
    }

    public void updateHorseItem() {
        if (horseCooldown != 0) {
            ItemStack cooldown = new ItemStack(grayDye.toItemStack(horseCooldown));
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
        //crit
        float damageHealValue = (int) ((Math.random() * (max - min)) + min);
        int crit = (int) ((Math.random() * (100)));
        boolean isCrit = false;
        if (crit <= critChance) {
            isCrit = true;
            damageHealValue *= critMultiplier / 100f;
        }
        //resistance
        damageHealValue *= 1 - spec.getDamageResistance() / 100f;
        //TODO fix calcualtions should be (.25 + .1 + ....) then multiply
        //berserk
        if (attacker.getBerserk() != 0) {
            damageHealValue *= 1.25;
        }
        if (berserk != 0) {
            damageHealValue *= 1.1;
        }
        if (intervene != 0) {
            damageHealValue *= .5;
            if (isCrit) {
                intervenedBy.getPlayer().sendMessage("§c\u00AB§7 " + attacker.getName() + "'s Intervene hit you for §c§l" + (int) damageHealValue * -1 + "! §7critical damage.");
                attacker.getPlayer().sendMessage("§a\u00BB§7 Your Intervene hit " + intervenedBy.getName() + " for §c§l" + (int) damageHealValue * -1 + "! §7critical damage.");
            } else {
                intervenedBy.getPlayer().sendMessage("§c\u00AB§7 " + attacker.getName() + "'s Intervene hit you for §c" + (int) damageHealValue * -1 + "§7 damage.");
                attacker.getPlayer().sendMessage("§a\u00BB§7 Your Intervene hit " + intervenedBy.getName() + " for §c" + (int) damageHealValue * -1 + "§7 damage.");
            }
            intervenedBy.setHealth((int) (intervenedBy.getHealth() + damageHealValue));
            interveneDamage += damageHealValue;
        } else {
            System.out.println(attacker.getName() + " hit " + name);
            System.out.println(damageHealValue);
            //Prevent overheal
            if (this.health + damageHealValue > this.maxHealth) {
                damageHealValue = this.maxHealth - this.health;
                this.health = maxHealth;
            } else {
                this.health += Math.round(damageHealValue);
            }
            //Self heal
            if (attacker.getName().equals(name)) {
                if (isCrit) {
                    player.sendMessage("§a\u00AB§7 Your " + ability + " critically healed you for §a§l" + (int) damageHealValue + "! §7health.");
                } else {
                    player.sendMessage("§a\u00AB§7 Your " + ability + " healed for §a" + (int) damageHealValue + " §7health.");

                }
            } else {
                //DAMAGE
                if (damageHealValue < 0) {
                    regenTimer = 10;
                    float tempDamageHealValue = Math.abs(damageHealValue);
                    if (lastStand != 0) {
                        if (spec.getOrange().getName().equals("Last Stand")) {
                            tempDamageHealValue *= .5;
                        } else {
                            tempDamageHealValue *= .4;
                            //TODO multiple last stands? lastest person that last stands will over ride other dude
                            if (lastStandedBy.getLastStand() != 0) {
                                if (isCrit)
                                    lastStandedBy.addHealth(lastStandedBy, "Last Stand", (int) (tempDamageHealValue), (int) (tempDamageHealValue), 100, 100);
                                else
                                    lastStandedBy.addHealth(lastStandedBy, "Last Stand", (int) (tempDamageHealValue), (int) (tempDamageHealValue), -1, 100);
                            }
                        }
                    }
                    if (attacker.getCrippled() != 0) {
                        tempDamageHealValue *= .875;
                    }
                    if (isCrit) {
                        if (ability.isEmpty()) {
                            player.sendMessage("§c\u00AB§7 " + attacker.getName() + " hit you for §c§l" + (int) tempDamageHealValue + "! §7critical melee damage.");
                            attacker.getPlayer().sendMessage("§a\u00BB§7 " + "You hit " + name + " for §c§l" + (int) tempDamageHealValue + "! §7critical melee damage.");
                        } else {
                            player.sendMessage("§c\u00AB§7 " + attacker.getName() + "'s " + ability + " hit you for §c§l" + (int) tempDamageHealValue + "! §7critical damage.");
                            attacker.getPlayer().sendMessage("§a\u00BB§7 " + "Your " + ability + " hit " + name + " for §c§l" + (int) tempDamageHealValue + "! §7critical damage.");
                        }
                    } else {
                        if (ability.isEmpty()) {
                            player.sendMessage("§c\u00AB§7 " + attacker.getName() + " hit you for §c" + (int) tempDamageHealValue + " §7damage.");
                            attacker.getPlayer().sendMessage("§a\u00BB§7 " + "You hit " + name + " for §c" + (int) tempDamageHealValue + " §7damage.");
                        } else {
                            player.sendMessage("§c\u00AB§7 " + attacker.getName() + "'s " + ability + " hit you for §c" + (int) tempDamageHealValue + " §7damage.");
                            attacker.getPlayer().sendMessage("§a\u00BB§7 " + "Your " + ability + " hit " + name + " for §c" + (int) tempDamageHealValue + " §7damage.");
                        }
                    }

                    if (attacker.getOrbOfLife() != 0 && !ability.isEmpty()) {
                        Location location = player.getLocation();
                        Orb orb = new Orb(((CraftWorld) player.getWorld()).getHandle(), location);
                        //TODO Add team whitelist
                        ArmorStand orbStand = (ArmorStand) location.getWorld().spawnEntity(location.add(Math.random() * 3 - 1.5, 0, Math.random() * 3 - 1.5), EntityType.ARMOR_STAND);
                        orbStand.setVisible(false);
                        //WOW need to set passenger to orb or else the orb will move   like ???
                        orbStand.setPassenger(orb.spawn(location).getBukkitEntity());
                        orb.setArmorStand(orbStand);
                        Warlords.getOrbs().add(orb);
                    }
                }
                //HEALING
                else {
                    if (berserkerWounded != 0) {
                        damageHealValue *= .65;
                    } else if (defenderWounded != 0) {
                        damageHealValue *= .75;
                    }
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
        if (attacker.getBloodLust() != 0 && damageHealValue < 0) {
            attacker.addHealth(attacker, "Blood Lust", Math.round(damageHealValue * -.65f), Math.round(damageHealValue * -.65f), 0, 0);
        }
        //TODO make inital windfury hit proc
        if (ability.equals("")) {
            if (attacker.getWindfury() != 0) {
                int windfuryActivate = (int) (Math.random() * 100);
                if (windfuryActivate < 35) {
                    addHealth(attacker, "Windfury Weapon", min, max, 25, 235);
                    addHealth(attacker, "Windfury Weapon", min, max, 25, 235);
                }
            } else if (attacker.getEarthliving() != 0) {
                //TODO heal attackers teamamtes
                List<Entity> near = attacker.getPlayer().getNearbyEntities(3.0D, 3.0D, 3.0D);
                near.remove(attacker.getPlayer());
                int counter = 0;
                for (Entity entity : near) {
                    if (entity instanceof Player) {
                        int earthlivingActivate = (int) (Math.random() * 100);
                        if (earthlivingActivate < 40) {
                            Warlords.getPlayer((Player) near.get(0)).addHealth(attacker, "Earthliving Weapon", min * -1, max * -1, 25, 440);
                            counter++;
                        }
                    }
                    if (counter == 2)
                        break;
                }

            }
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
        return infusion;
    }

    public void setInfusion(int infusion) {
        this.infusion = infusion;
    }

    public int getWrath() {
        return wrath;
    }

    public void setWrath(int wrath) {
        this.wrath = wrath;
    }

    public int getPresence() {
        return presence;
    }

    public void setPresence(int presence) {
        this.presence = presence;
    }

    public int getBloodLust() {
        return bloodLust;
    }

    public void setBloodLust(int bloodLust) {
        this.bloodLust = bloodLust;
    }

    public int getBerserk() {
        return berserk;
    }

    public void setBerserk(int berserk) {
        this.berserk = berserk;
    }

    public int getIntervene() {
        return intervene;
    }

    public void setIntervene(int intervene) {
        this.intervene = intervene;
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

    public int getLastStand() {
        return lastStand;
    }

    public void setLastStand(int lastStand) {
        this.lastStand = lastStand;
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

    public int getOrbOfLife() {
        return orbOfLife;
    }

    public void setOrbOfLife(int orbOfLife) {
        this.orbOfLife = orbOfLife;
    }

    public int getUndyingArmy() {
        return undyingArmy;
    }

    public void setUndyingArmy(int undyingArmy) {
        this.undyingArmy = undyingArmy;
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

    public int getWindfury() {
        return windfury;
    }

    public void setWindfury(int windfury) {
        this.windfury = windfury;
    }

    public int getEarthliving() {
        return earthliving;
    }

    public void setEarthliving(int earthliving) {
        this.earthliving = earthliving;
    }
}
