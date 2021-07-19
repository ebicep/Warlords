package com.ebicep.warlords.player;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.shaman.specs.spiritguard.Spiritguard;
import com.ebicep.warlords.events.WarlordsDeathEvent;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.flags.*;
import com.ebicep.warlords.maps.state.PlayingState;
import com.ebicep.warlords.powerups.DamagePowerUp;
import com.ebicep.warlords.util.ItemBuilder;
import com.ebicep.warlords.util.PacketUtils;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

public final class WarlordsPlayer {

    private final String name;
    private final UUID uuid;
    private final PlayingState gameState;
    private final Team team;
    private AbstractPlayerClass spec;
    private final Classes specClass;
    private final Weapons weapon;
    private boolean hotKeyMode;
    private int health;
    private int maxHealth;
    private int regenTimer;
    private int timeInCombat = 0;
    private int respawnTimer;
    private int respawnTimeSpent = 0;
    private boolean dead = false;
    private float energy;
    private float maxEnergy;
    private float horseCooldown;
    private int flagCooldown;
    private int hitCooldown;
    private int spawnProtection;
    private int spawnDamage = 0;
    private int flagsCaptured = 0;
    private int flagsReturned = 0;
    // We have to store these in here as the new player might logout midgame
    private float walkspeed = 1;
    private int blocksTravelledCM = 0;
    private boolean infiniteEnergy;
    private boolean disableCooldowns;
    private double energyModifier = 1;
    private double cooldownModifier = 1;
    private boolean takeDamage = true;

    private final int[] kills = new int[Warlords.game.getMap().getGameTimerInTicks() / 20 / 60];
    private final int[] assists = new int[Warlords.game.getMap().getGameTimerInTicks() / 20 / 60];
    private List<WarlordsPlayer> hitBy = new ArrayList<>();
    private final int[] deaths = new int[Warlords.game.getMap().getGameTimerInTicks() / 20 / 60];
    private final float[] damage = new float[Warlords.game.getMap().getGameTimerInTicks() / 20 / 60];
    private final float[] healing = new float[Warlords.game.getMap().getGameTimerInTicks() / 20 / 60];
    private final float[] absorbed = new float[Warlords.game.getMap().getGameTimerInTicks() / 20 / 60];

    private final List<Location> trail = new ArrayList<>();

    private final CalculateSpeed speed;

    private boolean teamFlagCompass = true;

    //POWERUPS
    private boolean powerUpHeal = false;

    @Nonnull
    private final CustomScoreboard scoreboard;

    private Location deathLocation = null;
    private ArmorStand deathStand = null;
    private LivingEntity entity = null;

    private double flagDamageMultipler = 0;

    private CooldownManager cooldownManager = new CooldownManager(this);

    public WarlordsPlayer(
            @Nonnull OfflinePlayer player,
            @Nonnull PlayingState gameState,
            @Nonnull Team team,
            @Nonnull PlayerSettings settings
    ) {
        this.name = player.getName();
        this.uuid = player.getUniqueId();
        this.gameState = gameState;
        this.team = team;
        this.specClass = settings.selectedClass();
        this.spec = specClass.create.get();
        this.maxHealth = (int) (this.spec.getMaxHealth() * (gameState.getGame().getCooldownMode()? 1.5 : 1));
        this.health = this.maxHealth;
        this.respawnTimer = -1;
        this.energy = this.spec.getMaxEnergy();
        this.energyModifier = gameState.getGame().getCooldownMode() ? 0.5 : 1;
        this.maxEnergy = this.spec.getMaxEnergy();
        this.horseCooldown = 0;
        this.flagCooldown = 0;
        this.cooldownModifier = gameState.getGame().getCooldownMode() ? 0.5 : 1;
        this.hitCooldown = 20;
        this.spawnProtection = 0;
        this.speed = new CalculateSpeed(this::setWalkSpeed, 13);
        this.scoreboard = new CustomScoreboard(this, gameState);
        Player p = player.getPlayer();
        this.entity = spawnJimmy(p == null ? Warlords.getRejoinPoint(uuid) : p.getLocation(), null);
        this.weapon = settings.weapon();
        this.hotKeyMode = settings.hotKeyMode();
        updatePlayerReference(p);
    }

    public Zombie spawnJimmy(@Nonnull Location loc, @Nullable PlayerInventory inv) {
        Zombie jimmy = loc.getWorld().spawn(loc, Zombie.class);
        jimmy.setBaby(false);
        jimmy.setCustomNameVisible(true);
        jimmy.setCustomName(this.getSpec().getClassNameShortWithBrackets() + " " + this.getColoredName() + " " + ChatColor.RED + this.health + "❤"); // TODO add level and class into the name of this jimmy
        jimmy.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
        ((EntityLiving) ((CraftEntity) jimmy).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0);
        ((EntityLiving) ((CraftEntity) jimmy).getHandle()).getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(0);
        //prevents jimmy from moving
        net.minecraft.server.v1_8_R3.Entity nmsEn = ((CraftEntity) jimmy).getHandle();
        NBTTagCompound compound = new NBTTagCompound();
        nmsEn.c(compound);
        compound.setByte("NoAI", (byte) 1);
        nmsEn.f(compound);

        if (inv != null) {
            jimmy.getEquipment().setBoots(inv.getBoots());
            jimmy.getEquipment().setLeggings(inv.getLeggings());
            jimmy.getEquipment().setChestplate(inv.getChestplate());
            jimmy.getEquipment().setHelmet(inv.getHelmet());
            jimmy.getEquipment().setItemInHand(inv.getItemInHand());
        }
        return jimmy;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public CustomScoreboard getScoreboard() {
        return scoreboard;
    }

    private void setWalkSpeed(float walkspeed) {
        this.walkspeed = walkspeed;
        Player player = Bukkit.getPlayer(uuid);
        if(player != null) player.setWalkSpeed(this.walkspeed);
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
        actionBarMessage.append(team.boldColoredPrefix()).append(" TEAM  ");
        for (Cooldown cooldown : cooldownManager.getCooldowns()) {
            if (!cooldown.isHidden()) {
                if (cooldown.getName().equals("WND") || cooldown.getName().equals("CRIP")) {
                    actionBarMessage.append(ChatColor.RED);
                } else {
                    actionBarMessage.append(ChatColor.GREEN);
                }
                actionBarMessage.append(cooldown.getName()).append(ChatColor.GRAY).append(":").append(ChatColor.GOLD).append((int) cooldown.getTimeLeft() + 1).append(" ");
            }
        }
        if (entity instanceof Player) {
            PacketUtils.sendActionBar((Player) entity, actionBarMessage.toString());
        }
    }


    public void displayFlagActionBar(@Nonnull Player player) {
        FlagManager flags = this.gameState.flags();

        FlagInfo blueFlag = flags.getBlue();
        double blueFlagDistance = Math.round(blueFlag.getFlag().getLocation().distance(player.getLocation()) * 10) / 10.0;
        FlagInfo redFlag = flags.getRed();
        double redFlagDistance = Math.round(redFlag.getFlag().getLocation().distance(player.getLocation()) * 10) / 10.0;

        if (teamFlagCompass) {
            FlagLocation flag = flags.get(team).getFlag();
            if (flag instanceof PlayerFlagLocation) {
                PacketUtils.sendActionBar(player, "" + team.teamColor() + ChatColor.BOLD + "YOUR Flag " + ChatColor.WHITE + "is stolen " + ChatColor.RED + blueFlagDistance + "m " + ChatColor.WHITE + "away!");
            } else if (flag instanceof GroundFlagLocation) {
                PacketUtils.sendActionBar(player, "" + team.teamColor() + ChatColor.BOLD + "YOUR Flag " + ChatColor.GOLD + "is dropped " + ChatColor.RED + blueFlagDistance + "m " + ChatColor.WHITE + "away!");
            } else {
                PacketUtils.sendActionBar(player, "" + team.teamColor() + ChatColor.BOLD + "YOUR " + ChatColor.GREEN + "Flag is safe");
            }
        } else {
            FlagLocation flag = flags.get(team.enemy()).getFlag();
            String start = "" + team.enemy().teamColor() + ChatColor.BOLD + "ENEMY ";
            if (flag instanceof PlayerFlagLocation) {
                PacketUtils.sendActionBar(player, start + "Flag " + ChatColor.WHITE + "is stolen " + ChatColor.RED + redFlagDistance + "m " + ChatColor.WHITE + "away!");
            } else if (flag instanceof GroundFlagLocation) {
                PacketUtils.sendActionBar(player, start + "ENEMY Flag " + ChatColor.GOLD + "is dropped " + ChatColor.RED + redFlagDistance + "m " + ChatColor.WHITE + "away!");
            } else {
                PacketUtils.sendActionBar(player, start + ChatColor.GREEN + "Flag is safe");
            }
        }
    }

    public void applySkillBoost(Player player) {
        ClassesSkillBoosts selectedBoost = Classes.getSelectedBoost(Bukkit.getOfflinePlayer(uuid));
        if (spec.getWeapon().getClass() == selectedBoost.ability) {
            if (selectedBoost != ClassesSkillBoosts.PROTECTOR_STRIKE) {
                spec.getWeapon().boostSkill();
            }
            spec.getWeapon().updateDescription(player);
        } else if (spec.getRed().getClass() == selectedBoost.ability) {
            spec.getRed().boostSkill();
            spec.getRed().updateDescription(player);
        } else if (spec.getPurple().getClass() == selectedBoost.ability) {
            spec.getPurple().boostSkill();
            spec.getPurple().updateDescription(player);
        } else if (spec.getBlue().getClass() == selectedBoost.ability) {
            spec.getBlue().boostSkill();
            spec.getBlue().updateDescription(player);
        } else if (spec.getOrange().getClass() == selectedBoost.ability) {
            spec.getOrange().boostOrange();
            spec.getOrange().updateDescription(player);
        }
    }

    public void assignItemLore(Player player) {
        //§
        ItemStack weapon = new ItemStack(this.weapon.item);
        ItemMeta weaponMeta = weapon.getItemMeta();
        weaponMeta.setDisplayName("§cWarlord's Felflame of the " + spec.getWeapon().getName());
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
        weaponLeftClick(player);

        updateRedItem(player);
        updatePurpleItem(player);
        updateBlueItem(player);
        updateOrangeItem(player);
        updateHorseItem(player);

        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName(ChatColor.GREEN + "Flag Finder");
        compass.setItemMeta(compassMeta);
        compassMeta.spigot().setUnbreakable(true);
        player.getInventory().setItem(8, compass);
    }

    public void weaponLeftClick(Player player) {
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

    public void weaponRightClick(Player player) {
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

    public void updateItem(Player player, int slot, AbstractAbility ability, ItemStack item) {
        if (ability.getCurrentCooldown() > 0) {
            ItemStack cooldown = new ItemStack(Material.INK_SACK, ability.getCurrentCooldownItem(), (byte) 8);
            player.getInventory().setItem(slot, cooldown);
        } else {
            player.getInventory().setItem(
                    slot,
                    new ItemBuilder(item)
                            .name(ChatColor.GOLD + ability.getName())
                            .lore(ChatColor.GRAY + "Cooldown: " + ChatColor.AQUA + ability.getCooldown() + " seconds",
                                    ability.getEnergyCost() != 0 && ability.getEnergyCost() != -120
                                            ? ChatColor.GRAY + "Energy Cost: " + ChatColor.YELLOW + ability.getEnergyCost() + "\n" +
                                            (ability.getCritChance() != 0 && ability.getCritChance() != -1 && ability.getCritMultiplier() != 100
                                                    ? ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + ability.getCritChance() + "%" + "\n"
                                                    + ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + ability.getCritMultiplier() + "%" + "\n\n" + ability.getDescription()
                                                    : "\n" + ability.getDescription())
                                            : "\n" + ability.getDescription()
                            )
                            .unbreakable()
                            .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                            .get());
        }
    }
    public void updateRedItem() {
        if (entity instanceof Player) {
            updateRedItem((Player)entity);
        }
    }

    public void updateRedItem(Player player) {
        updateItem(player, 1, spec.getRed(), new ItemStack(Material.INK_SACK, 1, (byte)1));
    }

    public void updatePurpleItem() {
        if (entity instanceof Player) {
            updatePurpleItem((Player) entity);
        }
    }

    public void updatePurpleItem(Player player) {
        updateItem(player, 2, spec.getPurple(), new ItemStack(Material.GLOWSTONE_DUST));
    }

    public void updateBlueItem() {
        if (entity instanceof Player) {
            updateBlueItem((Player) entity);
        }
    }
    public void updateBlueItem(Player player) {
        updateItem(player, 3, spec.getBlue(), new ItemStack(Material.INK_SACK, 1, (byte)10));
    }

    public void updateOrangeItem() {
        if (entity instanceof Player) {
            updateOrangeItem((Player) entity);
        }
    }
    public void updateOrangeItem(Player player) {
        updateItem(player, 4, spec.getOrange(), new ItemStack(Material.INK_SACK, 1, (byte)14));
    }

    public void updateHorseItem(Player player) {
        if (horseCooldown > 0) {
            ItemStack cooldown = new ItemStack(Material.IRON_BARDING, (int) horseCooldown + 1);
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

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public AbstractPlayerClass getSpec() {
        return spec;
    }

    public void setSpec(AbstractPlayerClass spec) {
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

    public void showDeathAnimation() {
        if(this.entity instanceof Zombie) {
            this.entity.damage(200);
        } else {
            Player player = (Player)this.entity;
            Zombie zombie = player.getWorld().spawn(player.getLocation(), Zombie.class);
            zombie.getEquipment().setBoots(player.getInventory().getBoots());
            zombie.getEquipment().setLeggings(player.getInventory().getLeggings());
            zombie.getEquipment().setChestplate(player.getInventory().getChestplate());
            zombie.getEquipment().setHelmet(player.getInventory().getHelmet());
            zombie.getEquipment().setItemInHand(player.getInventory().getItemInHand());
            zombie.damage(2000);
        }
    }

    public void addHealth(WarlordsPlayer attacker, String ability, float min, float max, int critChance, int critMultiplier) {
        if (spawnProtection != 0 || (dead && !cooldownManager.checkUndyingArmy(false))) return;
        if (attacker == this && (ability.equals("Fall") || ability.isEmpty())) {
            if (ability.isEmpty()) {
                sendMessage("" + ChatColor.RED + "\u00AB" + ChatColor.GRAY + " You took " + ChatColor.RED + Math.round(min * -1) + ChatColor.GRAY + " melee damage.");
                regenTimer = 10;
                if (health + min <= 0) {
                    die(attacker);
                    gameState.addKill(team, false);
                    if (entity instanceof Player)
                        PacketUtils.sendTitle((Player) entity, ChatColor.RED + "YOU DIED!", ChatColor.GRAY + "You took " + ChatColor.RED + Math.round(min * -1) + ChatColor.GRAY + " melee damage and died.", 0, 40, 0);
                    health = 0;
                } else {
                    health += min;
                }

                if (min < 0) {
                    this.entity.playEffect(EntityEffect.HURT);
                    for (Player player1 : attacker.getWorld().getPlayers()) {
                        player1.playSound(entity.getLocation(), Sound.HURT_FLESH, 1, 1);
                    }
                }
            } else {
                //TODO FIX FIX IT JUST GETS MORE MESSY LETS GOOOOOOOOOOOOOOO
                sendMessage("" + ChatColor.RED + "\u00AB" + ChatColor.GRAY + " You took " + ChatColor.RED + Math.round(min * -1) + ChatColor.GRAY + " fall damage.");
                regenTimer = 10;
                if (health + min < 0) {
                    die(attacker);
                    gameState.addKill(team, false); // TODO, fall damage is only a suicide if it happens more than 5 seconds after the last damage
                    //title YOU DIED
                    if (entity instanceof Player) {
                        PacketUtils.sendTitle((Player) entity, ChatColor.RED + "YOU DIED!", ChatColor.GRAY + "You took " + ChatColor.RED + Math.round(min * -1) + ChatColor.GRAY + " fall damage and died.", 0, 40, 0);
                    }

                    health = 0;
                } else {
                    health += min;
                }
                entity.playEffect(EntityEffect.HURT);
                for (Player player1 : attacker.getWorld().getPlayers()) {
                    player1.playSound(entity.getLocation(), Sound.HURT_FLESH, 1, 1);
                }
            }
        } else {
            if (!attacker.getCooldownManager().getCooldown(Inferno.class).isEmpty() && (!ability.isEmpty() && !ability.equals("Time Warp"))) {
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
            damageHealValue *= damageHealValue > 0 || flagDamageMultipler == 0 ? 1 : flagDamageMultipler;

            //reduction begining with base resistance
            float totalReduction = 1;
            if (min < 0 && !HammerOfLight.standingInHammer(attacker, entity)) {
                //base
                totalReduction = 1 - spec.getDamageResistance() / 100f;
                //add damage
                if (!attacker.getCooldownManager().getCooldown(DamagePowerUp.class).isEmpty()) {
                    totalReduction *= 1.2;
                    //totalReduction += .2;
                } else if (attacker.getSpawnDamage() > 0) {
                    totalReduction *= 1.2;
                    //totalReduction += .2;
                }

                for (Cooldown cooldown : attacker.getCooldownManager().getCooldown(Berserk.class)) {
                    totalReduction *= 1.25;
                }

                for (Cooldown cooldown : cooldownManager.getCooldown(Berserk.class)) {
                    totalReduction *= 1.1;
                }

                //reduce damage
                for (Cooldown cooldown : cooldownManager.getCooldown(IceBarrier.class)) {
                    totalReduction *= .5;
                }

                for (Cooldown cooldown : cooldownManager.getCooldown(ChainLightning.class)) {
                    String chainName = cooldown.getName();
                    totalReduction *= 1 - Integer.parseInt(chainName.charAt(chainName.indexOf("(") + 1) + "") * .1;
                }

                for (Cooldown cooldown : cooldownManager.getCooldown(SpiritLink.class)) {
                    totalReduction *= .8;
                }

                //TODO maybe change to hypixel warlords where crippling effects hammer
                if (!attacker.getCooldownManager().getCooldown(CripplingStrike.class).isEmpty()) {
                    totalReduction *= .875;
                    //totalReduction -= .125;
                }
            }
            if (!cooldownManager.getCooldown(Intervene.class).isEmpty() && cooldownManager.getCooldown(Intervene.class).get(0).getFrom() != this && !HammerOfLight.standingInHammer(attacker, entity)) {
                if (this.isEnemyAlive(attacker)) {
                    damageHealValue *= totalReduction;
                    damageHealValue *= .5;
                    WarlordsPlayer intervenedBy = cooldownManager.getCooldown(Intervene.class).get(0).getFrom();

                    Location loc = getLocation();
                    gameState.getGame().forEachOnlinePlayer((player1, t) -> {
                        player1.playSound(loc, "warrior.intervene.block", 2, 1);
                    });

                    intervenedBy.getEntity().playEffect(EntityEffect.HURT);
                    intervenedBy.setRegenTimer(10);
                    ((Intervene) cooldownManager.getCooldown(Intervene.class).get(0).getCooldownObject()).addDamagePrevented(-damageHealValue);

                    removeHorse();
                    intervenedBy.removeHorse();

                    //removing intervene if out damaged
                    if (((Intervene) cooldownManager.getCooldown(Intervene.class).get(0).getCooldownObject()).getDamagePrevented() >= 3600) {
                        //remove from intervener
                        intervenedBy.sendMessage("§c\u00AB§7 " + "Your " + ChatColor.YELLOW + "Intervene " + ChatColor.GRAY + "has expired!");
                        intervenedBy.getCooldownManager().getCooldowns().remove(cooldownManager.getCooldown(Intervene.class).get(0));
                        //remove from intervened
                        sendMessage("§c\u00AB§7 " + intervenedBy.getName() + "'s " + ChatColor.YELLOW + "Intervene " + ChatColor.GRAY + "has expired!");
                        cooldownManager.getCooldowns().remove(cooldownManager.getCooldown(Intervene.class).get(0));
                    }

                    intervenedBy.addHealth(attacker, "Intervene", damageHealValue, damageHealValue, isCrit ? 100 : -1, 100);

                    this.addAbsorbed(-damageHealValue);
                    attacker.addAbsorbed(-damageHealValue);
                }
            } else if (!cooldownManager.getCooldown(ArcaneShield.class).isEmpty() && this.isEnemyAlive(attacker) && !HammerOfLight.standingInHammer(attacker, entity)) {
                damageHealValue *= totalReduction;
                if (((ArcaneShield) spec.getBlue()).getShieldHealth() + damageHealValue < 0) {
                    if (entity instanceof Player) {
                        ((EntityLiving) ((CraftPlayer) entity).getHandle()).setAbsorptionHearts(0);
                    }

                    cooldownManager.removeCooldown(ArcaneShield.class);
                    addHealth(attacker, ability, (((ArcaneShield) spec.getBlue()).getShieldHealth() + damageHealValue), (((ArcaneShield) spec.getBlue()).getShieldHealth() + damageHealValue), isCrit ? 100 : -1, 100);

                    addAbsorbed(-(((ArcaneShield) spec.getBlue()).getShieldHealth()));
                    attacker.addAbsorbed(-(((ArcaneShield) spec.getBlue()).getShieldHealth()));
                } else {
                    ((ArcaneShield) spec.getBlue()).addShieldHealth(damageHealValue);

                    if (entity instanceof Player) {
                        ((EntityLiving) ((CraftPlayer) entity).getHandle()).setAbsorptionHearts((float) (((ArcaneShield) spec.getBlue()).getShieldHealth() / (maxHealth * .5) * 20));
                    }

                    if (ability.isEmpty()) {
                        sendMessage("" + ChatColor.RED + "\u00AB" + ChatColor.GRAY + " You absorbed " + attacker.getName() + "'s melee " + ChatColor.GRAY + "hit.");
                        attacker.sendMessage(ChatColor.GREEN + "\u00BB" + ChatColor.GRAY + " Your melee hit was absorbed by " + name);
                    } else {
                        sendMessage("" + ChatColor.RED + "\u00AB" + ChatColor.GRAY + " You absorbed " + attacker.getName() + "'s " + ability + " " + ChatColor.GRAY + "hit.");
                        attacker.sendMessage(ChatColor.GREEN + "\u00BB" + ChatColor.GRAY + " Your " + ability + " was absorbed by " + name + ChatColor.GRAY + ".");
                    }

                    addAbsorbed(-damageHealValue);
                    attacker.addAbsorbed(-damageHealValue);
                }
                this.entity.playEffect(EntityEffect.HURT);
                for (Player player1 : attacker.getWorld().getPlayers()) {
                    player1.playSound(entity.getLocation(), Sound.HURT_FLESH, 1, 1);
                }

                removeHorse();
            } else {
                System.out.println(attacker.getName() + " hit " + name + " for " + damageHealValue);
                boolean debt = false;

                //Self heal
                if (this == attacker) {
                    if (!cooldownManager.getCooldown(WoundingStrikeBerserker.class).isEmpty()) {
                        damageHealValue *= .65;
                    } else if (!cooldownManager.getCooldown(WoundingStrikeDefender.class).isEmpty()) {
                        damageHealValue *= .75;
                    }
                    if (this.health + damageHealValue > this.maxHealth) {
                        damageHealValue = this.maxHealth - this.health;
                    }
                    damageHealValue = Math.round(damageHealValue);
                    if (damageHealValue > 0) {
                        if (isCrit) {
                            sendMessage(ChatColor.GREEN + "\u00AB" + ChatColor.GRAY + " Your " + ability + " critically healed you for " + ChatColor.GREEN + "§l" + Math.round(damageHealValue) + "! " + ChatColor.GRAY + "health.");
                        } else {
                            sendMessage(ChatColor.GREEN + "\u00AB" + ChatColor.GRAY + " Your " + ability + " healed you for " + ChatColor.GREEN + "" + Math.round(damageHealValue) + " " + ChatColor.GRAY + "health.");
                        }
                    }
                    addHealing(damageHealValue);
                } else {
                    damageHealValue *= totalReduction;
                    //DAMAGE
                    if (damageHealValue < 0 && isEnemyAlive(attacker)) {
                        if (!hitBy.contains(attacker)) {
                            hitBy.add(attacker);
                        }
                        if (powerUpHeal) {
                            powerUpHeal = false;
                            sendMessage(ChatColor.GRAY + "Your §aHealing Powerup §7has worn off.");
                        }
                        removeHorse();
                        regenTimer = 10;
                        if (!cooldownManager.getCooldown(LastStand.class).isEmpty() && !HammerOfLight.standingInHammer(attacker, entity)) {
                            for (Cooldown cooldown : cooldownManager.getCooldown(LastStand.class)) {
                                WarlordsPlayer lastStandedBy = cooldown.getFrom();
                                if (lastStandedBy == this) {
                                    damageHealValue *= .5;
                                } else {
                                    damageHealValue *= .4;
                                }

                                //HEALING FROM LASTSTAND
                                if (lastStandedBy != this) {
                                    float finalDamageHealValue = damageHealValue;
                                    boolean finalIsCrit = isCrit;
                                    //healing if multiple last stands
                                    lastStandedBy.getCooldownManager().getCooldown(LastStand.class).stream()
                                            .filter(cd -> cd.getCooldownObject() == cooldown.getCooldownObject() && cd.getTimeLeft() > 0)
                                            .forEach(ls -> {
                                                float healValue = finalDamageHealValue * -1;
                                                if (finalIsCrit)
                                                    lastStandedBy.addHealth(lastStandedBy, "Last Stand", Math.round(healValue), Math.round(healValue), 100, 100);
                                                else
                                                    lastStandedBy.addHealth(lastStandedBy, "Last Stand", Math.round(healValue), Math.round(healValue), -1, 100);

                                            });
                                }
                                addAbsorbed(-damageHealValue);
                                attacker.addAbsorbed(-damageHealValue);
                            }
                        }

                        // this metadata is only active on the sg class
                        // the cooldown of the ability prevents multiple from being active at the same time
                        Optional<MetadataValue> totem = entity.getMetadata("TOTEM").stream()
                                .filter(e -> e.value() instanceof Totem.TotemSpiritguard)
                                .findAny();
                        if (totem.isPresent()) {
                            Totem.TotemSpiritguard t = (Totem.TotemSpiritguard) totem.get().value();
                            t.addDelayedDamage(damageHealValue);
                            debt = true;
                        }

                        if (isCrit) {
                            if (ability.isEmpty()) {
                                sendMessage(ChatColor.RED + "\u00AB" + ChatColor.GRAY + " " + attacker.getName() + " hit you for " + ChatColor.RED + "§l" + Math.round(damageHealValue) * -1 + "! " + ChatColor.GRAY + "critical melee damage.");
                                attacker.sendMessage(ChatColor.GREEN + "\u00BB" + ChatColor.GRAY + " " + "You hit " + name + " for " + ChatColor.RED + "§l" + Math.round(damageHealValue) * -1 + "! " + ChatColor.GRAY + "critical melee damage.");
                            } else {
                                sendMessage(ChatColor.RED + "\u00AB" + ChatColor.GRAY + " " + attacker.getName() + "'s " + ability + " hit you for " + ChatColor.RED + "§l" + Math.round(damageHealValue) * -1 + "! " + ChatColor.GRAY + "critical damage.");
                                attacker.sendMessage(ChatColor.GREEN + "\u00BB" + ChatColor.GRAY + " " + "Your " + ability + " hit " + name + " for " + ChatColor.RED + "§l" + Math.round(damageHealValue) * -1 + "! " + ChatColor.GRAY + "critical damage.");
                            }
                        } else {
                            if (ability.isEmpty()) {
                                sendMessage(ChatColor.RED + "\u00AB" + ChatColor.GRAY + " " + attacker.getName() + " hit you for " + ChatColor.RED + Math.round(damageHealValue) * -1 + " " + ChatColor.GRAY + "melee damage.");
                                attacker.sendMessage(ChatColor.GREEN + "\u00BB" + ChatColor.GRAY + " " + "You hit " + name + " for " + ChatColor.RED + Math.round(damageHealValue) * -1 + " " + ChatColor.GRAY + "melee damage.");
                            } else {
                                sendMessage(ChatColor.RED + "\u00AB" + ChatColor.GRAY + " " + attacker.getName() + "'s " + ability + " hit you for " + ChatColor.RED + Math.round(damageHealValue) * -1 + " " + ChatColor.GRAY + "damage.");
                                attacker.sendMessage(ChatColor.GREEN + "\u00BB" + ChatColor.GRAY + " " + "Your " + ability + " hit " + name + " for " + ChatColor.RED + Math.round(damageHealValue) * -1 + " " + ChatColor.GRAY + "damage.");
                            }
                        }
                        //REPENTANCE
                        if (spec instanceof Spiritguard) {
                            ((Repentance) spec.getBlue()).addToPool(damageHealValue * -1);
                        }
                        if (attacker.getSpec() instanceof Spiritguard) {
                            if (!attacker.getCooldownManager().getCooldown(Repentance.class).isEmpty()) {
                                int healthToAdd = (int) (((Repentance) attacker.getSpec().getBlue()).getPool() * .1) + 11;
                                attacker.addHealth(attacker, "Repentance", healthToAdd, healthToAdd, -1, 100);
                                ((Repentance) attacker.getSpec().getBlue()).setPool(((Repentance) attacker.getSpec().getBlue()).getPool() * .5f);
                                attacker.addEnergy(attacker, "Repentance", (float) (healthToAdd * .035));
                            }
                        }

                        //ORBS
                        if (!attacker.getCooldownManager().getCooldown(OrbsOfLife.class).isEmpty() && !ability.isEmpty()) {
                            for (Cooldown cooldown : attacker.getCooldownManager().getCooldown(OrbsOfLife.class)) {
                                OrbsOfLife orbsOfLife = (OrbsOfLife) cooldown.getCooldownObject();
                                Location location = getLocation();
                                Location spawnLocation = orbsOfLife.generateSpawnLocation(location);

                                OrbsOfLife.Orb orb = new OrbsOfLife.Orb(((CraftWorld) location.getWorld()).getHandle(), spawnLocation, attacker);
                                ArmorStand orbStand = (ArmorStand) location.getWorld().spawnEntity(spawnLocation, EntityType.ARMOR_STAND);
                                orbStand.setVisible(false);
                                orbStand.setGravity(false);
                                orbStand.setPassenger(orb.spawn(spawnLocation).getBukkitEntity());
                                orb.setArmorStand(orbStand);

                                orbsOfLife.getSpawnedOrbs().add(orb);

                                // Hacky way
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        for (WarlordsPlayer player : PlayerFilter.playingGame(attacker.getGame()).enemiesOf(attacker)) {
                                            if (player.getEntity() instanceof Player) {
                                                ((CraftPlayer) player.getEntity()).getHandle().playerConnection.sendPacket(
                                                        new PacketPlayOutEntityDestroy(orb.getId())
                                                );
                                            }
                                        }
                                    }
                                }.runTaskLater(Warlords.getInstance(), 1);
                            }
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

                            if (Warlords.getPlayerSettings(attacker.uuid).classesSkillBoosts() == ClassesSkillBoosts.PROTECTOR_STRIKE) {
                                attacker.addHealth(attacker, ability, -damageHealValue / 1.43f, -damageHealValue / 1.43f, tempNewCritChance, 100);
                            } else {
                                attacker.addHealth(attacker, ability, -damageHealValue / 2, -damageHealValue / 2, tempNewCritChance, 100);
                            }

                            //reloops near players to give health to
                            for(WarlordsPlayer nearTeamPlayer : PlayerFilter
                                .entitiesAround(attacker, 6, 6, 6)
                                .aliveTeammatesOfExcludingSelf(attacker)
                                .limit(2)
                            ) {
                                if (Warlords.getPlayerSettings(attacker.uuid).classesSkillBoosts() == ClassesSkillBoosts.PROTECTOR_STRIKE) {
                                    nearTeamPlayer.addHealth(attacker, ability, -damageHealValue * 1.2f, -damageHealValue * 1.2f, tempNewCritChance, 100);
                                } else {
                                    nearTeamPlayer.addHealth(attacker, ability, -damageHealValue, -damageHealValue, tempNewCritChance, 100);
                                }
                            }
                        }
                    }
                    //HEALING
                    else {
                        if (isTeammateAlive(attacker)) {
                            if (!cooldownManager.getCooldown(WoundingStrikeBerserker.class).isEmpty()) {
                                damageHealValue *= .65;
                            } else if (!cooldownManager.getCooldown(WoundingStrikeDefender.class).isEmpty()) {
                                damageHealValue *= .75;
                            }
                            if (this.health + damageHealValue > maxHealth) {
                                damageHealValue = maxHealth - this.health;
                            }
                            if (damageHealValue != 0) {
                                if (isCrit) {
                                    sendMessage(ChatColor.GREEN + "\u00AB" + ChatColor.GRAY + " " + attacker.getName() + "'s " + ability + " critically healed you for " + ChatColor.GREEN + "§l" + Math.round(damageHealValue) + "! " + ChatColor.GRAY + "health.");
                                    attacker.sendMessage(ChatColor.GREEN + "\u00BB" + ChatColor.GRAY + " " + "Your " + ability + " critically healed " + name + " for " + ChatColor.GREEN + "§l" + Math.round(damageHealValue) + "! " + ChatColor.GRAY + "health.");
                                } else {
                                    sendMessage(ChatColor.GREEN + "\u00AB" + ChatColor.GRAY + " " + attacker.getName() + "'s " + ability + " healed for " + ChatColor.GREEN + "" + Math.round(damageHealValue) + " " + ChatColor.GRAY + "health.");
                                    attacker.sendMessage(ChatColor.GREEN + "\u00BB" + ChatColor.GRAY + " " + "Your " + ability + " healed " + name + " for " + ChatColor.GREEN + "" + Math.round(damageHealValue) + " " + ChatColor.GRAY + "health.");
                                }
                            }
                        }
                    }
                    if (!attacker.getCooldownManager().getCooldown(BloodLust.class).isEmpty() && damageHealValue < 0) {
                        attacker.addHealth(attacker, "Blood Lust", Math.round(damageHealValue * -.65f), Math.round(damageHealValue * -.65f), -1, 100);
                    }
                }

                // adding/subtracing health
                //debt and healing
                if (!(debt && damageHealValue < 0) && takeDamage) {
                    this.health += Math.round(damageHealValue);
                }
                if (damageHealValue < 0) {
                    attacker.addDamage(-damageHealValue);
                    this.entity.playEffect(EntityEffect.HURT);
                    for (Player player1 : attacker.getWorld().getPlayers()) {
                        player1.playSound(entity.getLocation(), Sound.HURT_FLESH, 1, 1);
                    }
                }
                if (this.health <= 0 && cooldownManager.getCooldown(UndyingArmy.class).size() == 0) {
                    if (attacker.entity instanceof Player) {
                        ((Player)attacker.entity).playSound(attacker.getLocation(), Sound.ORB_PICKUP, 500f, 0.3f);
                    }

                    die(attacker);

                    attacker.addKill();
                    attacker.scoreboard.updateKillsAssists();

                    sendMessage(ChatColor.GRAY + "You were killed by " + attacker.getColoredName());
                    attacker.sendMessage(ChatColor.GRAY + "You killed " + getColoredName());
                    gameState.getGame().forEachOnlinePlayer((p, t) -> {
                        if(p != this.entity && p != attacker.entity) {
                            p.sendMessage(getColoredName() + ChatColor.GRAY + " was killed by " + attacker.getColoredName());
                        }
                    });
                    gameState.addKill(team, false);


                    //title YOU DIED
                    if (this.entity instanceof Player) {
                        PacketUtils.sendTitle((Player) entity, ChatColor.RED + "YOU DIED!", ChatColor.GRAY + attacker.getName() + " killed you.", 0, 40, 0);
                    }
                } else {
                    if (!ability.isEmpty() && this != attacker && damageHealValue != 0) {
                        if (attacker.entity instanceof Player) {
                            ((Player) attacker.entity).playSound(attacker.getLocation(), Sound.ORB_PICKUP, 1, 1);
                        }
                    }
                }

            }

            if (ability.isEmpty()) {
                if (!attacker.getCooldownManager().getCooldown(Windfury.class).isEmpty()) {
                    int windfuryActivate = (int) (Math.random() * 100);
                    if (((Windfury) attacker.getSpec().getPurple()).isFirstProc()) {
                        ((Windfury) attacker.getSpec().getPurple()).setFirstProc(false);
                        windfuryActivate = 0;
                    }
                    if (windfuryActivate < 35) {
                        final int[] counter = {0};
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                gameState.getGame().forEachOnlinePlayer((player1, t) -> {
                                    player1.playSound(getLocation(), "shaman.windfuryweapon.impact", 2, 1);
                                });
                                addHealth(attacker, "Windfury Weapon", min, max, 25, 235);
                                counter[0]++;
                                if (counter[0] == 2) {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(Warlords.getInstance(), 3, 3);
                    }
                } else if (!attacker.getCooldownManager().getCooldown(Earthliving.class).isEmpty()) {
                    int earthlivingActivate = (int) (Math.random() * 100);
                    if (((Earthliving) attacker.getSpec().getPurple()).isFirstProc() || Utils.getTotemDownAndClose(attacker, attacker.getEntity()) != null) {
                        //self heal
                        attacker.addHealth(attacker, "Earthliving Weapon", (132 * 2.4f), (179 * 2.4f), 25, 200);

                        gameState.getGame().forEachOnlinePlayer((player1, t) -> {
                            player1.playSound(getLocation(), "shaman.earthlivingweapon.impact", 2, 1);
                        });

                        ((Earthliving) attacker.getSpec().getPurple()).setFirstProc(false);
                        for (WarlordsPlayer nearPlayer : PlayerFilter
                                .entitiesAround(attacker, 3, 3, 3)
                            .aliveTeammatesOfExcludingSelf(attacker)
                            .limit(2)
                        ) {
                            nearPlayer.addHealth(attacker, "Earthliving Weapon", 132 * 2.4f, 179 * 2.4f, 25, 200);
                        }
                    } else if (earthlivingActivate < 40) {
                        attacker.addHealth(attacker, "Earthliving Weapon", 132 * 2.4f, 179 * 2.4f, 25, 200);

                        gameState.getGame().forEachOnlinePlayer((p, t) -> {
                            p.playSound(getLocation(), "shaman.earthlivingweapon.impact", 1, 1);
                        });

                        for (WarlordsPlayer nearPlayer : PlayerFilter.entitiesAround(attacker, 3, 3, 3)
                            .aliveTeammatesOfExcludingSelf(attacker)
                                .limit(2)
                        ) {
                            nearPlayer.addHealth(attacker, "Earthliving Weapon", 132 * 2.4f, 179 * 2.4f, 25, 200);
                        }
                    }
                }
            }
            System.out.println(attacker.name + " - " + attacker.getTotalDamage());
        }
    }

    public void removeHorse() {
        if (entity.getVehicle() != null) {
            entity.getVehicle().remove();
        }
    }

    public void die(WarlordsPlayer attacker) {
        dead = true;

        addGrave();

        showDeathAnimation();

        hitBy.remove(attacker);
        hitBy.add(0, attacker);

        this.addDeath();
        this.scoreboard.updateKillsAssists();
        Bukkit.getPluginManager().callEvent(new WarlordsDeathEvent(this));

        cooldownManager.clearCooldowns();

    }

    public void addGrave() {
        LivingEntity player = this.entity;

        Location deathLocation = player.getLocation();
        Block bestGraveCandidate = null;
        boolean isFlagCarrier = this.getFlagDamageMultipler() > 0;
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
            this.deathStand.setCustomName(team.teamColor() + name + ChatColor.GRAY + " - " + ChatColor.YELLOW + "DEAD");
            this.deathStand.setCustomNameVisible(true);
            this.deathStand.setGravity(false);
            this.deathStand.setVisible(false);
        }
    }

    public void heal() {
        this.health = this.maxHealth;
    }

    public void respawn() {
        this.health = this.maxHealth;
        if (deathStand != null) {
            deathStand.remove();
            deathStand = null;
        }
        if (deathLocation != null) {
            Block deathBlock = deathLocation.getBlock();
            if (deathBlock.getType() == Material.SAPLING) {
                deathBlock.setType(Material.AIR);
            }
            deathLocation = null;
        }
        if (entity instanceof Player) {
            ((Player)entity).setGameMode(GameMode.ADVENTURE);
        }
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

    public void giveRespawnTimer() {
        int respawn = gameState.getTimerInSeconds() % 12;
        if (respawn <= 4) {
            respawn += 12;
        }
        setRespawnTimer(respawn);
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public void addEnergy(WarlordsPlayer giver, String ability, float amount) {
        if (energy + amount > maxEnergy) {
            this.energy = maxEnergy;
        } else {
            this.energy += amount;
        }
        if ((int) amount != 0) {
            if (this == giver) {
                sendMessage(ChatColor.GREEN + "\u00AB" + ChatColor.GRAY + " Your " + ability + " gave you " + ChatColor.YELLOW + (int) amount + " " + ChatColor.GRAY + "energy.");
            } else {
                sendMessage(ChatColor.GREEN + "\u00AB" + ChatColor.GRAY + " " + giver.getName() + "'s " + ability + " gave you " + ChatColor.YELLOW + (int) amount + " " + ChatColor.GRAY + "energy.");
                giver.sendMessage(ChatColor.GREEN + "\u00BB" + ChatColor.GRAY + " " + "Your " + ability + " gave " + name + " " + ChatColor.YELLOW + (int) amount + " " + ChatColor.GRAY + "energy.");
            }
        }
    }
    public void sendMessage(String message) {
        if (this.entity instanceof Player) { // TODO check if this if is really needed, we can send a message to any entity??
            this.entity.sendMessage(message);
        }
    }

    public void subtractEnergy(int amount) {
        if (!infiniteEnergy) {
            amount *= energyModifier;
            if (energy - amount > maxEnergy) {
                energy = maxEnergy;
            } else {
                this.energy -= amount;
            }
        }
    }

    public float getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public float getHorseCooldown() {
        return horseCooldown;
    }

    public void setHorseCooldown(float horseCooldown) {
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

    public boolean isPowerUpHeal() {
        return powerUpHeal;
    }

    public void setPowerUpHeal(boolean powerUpHeal) {
        this.powerUpHeal = powerUpHeal;
    }

    public int[] getKills() {
        return kills;
    }

    public void addKill() {
        this.kills[this.gameState.getTimer() / (20 * 60)]++;
    }

    public int getTotalKills() {
        return IntStream.of(kills).sum();
    }

    public int[] getAssists() {
        return assists;
    }

    public void addAssist() {
        this.assists[this.gameState.getTimer() / (20 * 60)]++;
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
        this.deaths[this.gameState.getTimer() / (20 * 60)]++;
    }

    public float[] getDamage() {
        return damage;
    }

    public void addDamage(float amount) {
        this.damage[this.gameState.getTimer() / (20 * 60)] += amount;
    }

    public float getTotalDamage() {
        return (float) IntStream.range(0, damage.length).mapToDouble(i -> damage[i]).sum();
    }

    public float[] getHealing() {
        return healing;
    }

    public void addHealing(float amount) {
        this.healing[this.gameState.getTimer() / (20 * 60)] += amount;
    }

    public float getTotalHealing() {
        return (float) IntStream.range(0, healing.length).mapToDouble(i -> healing[i]).sum();
    }

    public float[] getAbsorbed() {
        return absorbed;
    }

    public void addAbsorbed(float amount) {
        this.absorbed[this.gameState.getTimer() / (20 * 60)] += amount;
    }

    public float getTotalAbsorbed() {
        return (float) IntStream.range(0, absorbed.length).mapToDouble(i -> absorbed[i]).sum();
    }

    public ItemStack getStatItemStack(String name) {
        ItemStack itemStack = new ItemStack(Material.STONE);
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        meta.setDisplayName(ChatColor.AQUA + "Stat Breakdown (" + name + "):");
        int minute = (this.gameState.getGame().getMap().getGameTimerInTicks() - this.gameState.getTimer()) / (20 * 60);
        for (int i = 0; i < damage.length - 1 && i < minute + 1; i++) {
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

    public boolean isTeamFlagCompass() {
        return teamFlagCompass;
    }

    public void toggleTeamFlagCompass() {
        teamFlagCompass = !teamFlagCompass;
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

    public void updatePlayerReference(@Nullable Player player) {
        if (player == this.entity) {
            return;
        }
        Location loc = this.getLocation();

        if (player == null) {
            if (this.entity instanceof Player) {
                this.entity = spawnJimmy(loc, ((Player)this.entity).getInventory());
            }
        } else {
            if (this.entity instanceof Zombie) { // This could happen if there was a problem during the quit event
                this.entity.remove();
            }
            player.teleport(loc);
            this.entity = player;
            player.removeMetadata("WARLORDS_PLAYER", Warlords.getInstance());
            player.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
            player.setWalkSpeed(walkspeed);
            player.setMaxHealth(40);
            player.setLevel((int) this.getMaxEnergy());
            player.getInventory().clear();
            this.spec.getWeapon().updateDescription(player);
            this.spec.getRed().updateDescription(player);
            this.spec.getPurple().updateDescription(player);
            this.spec.getBlue().updateDescription(player);
            this.spec.getOrange().updateDescription(player);
            applySkillBoost(player);
            player.closeInventory();
            ((EntityLiving) ((CraftPlayer) player).getHandle()).setAbsorptionHearts(0);
            this.assignItemLore(player);
            ArmorManager.resetArmor(player, getSpecClass(), getTeam());
            player.setScoreboard(this.getScoreboard().getScoreboard());

            if (isDeath()) {
                player.setGameMode(GameMode.SPECTATOR);
                giveRespawnTimer();
            }
            // TODO Update the inventory based on the status of isUndyingArmyDead here

            //SKILL TREE JUICERS
            /*player.getInventory().setItem(6, new ItemBuilder(Material.FIREWORK_CHARGE)
                    .name(ChatColor.GREEN + "Skill Tree" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Right-Click!")
                    .lore(ChatColor.GRAY + "Opens your Skill Tree to upgrade your class!")
                    .get());*/
        }
    }

    public Classes getSpecClass() {
        return specClass;
    }

    public Team getTeam() {
        return team;
    }

    public Game getGame() {
        return this.gameState.getGame();
    }

    public boolean isDeath() {
        return this.health <= 0 || this.dead || (entity instanceof Player && ((Player) entity).getGameMode() == GameMode.SPECTATOR);
    }

    public boolean isAlive() {
        return !isDeath();
    }

    @Nonnull
    public LivingEntity getEntity() {
        return this.entity;
    }

    @Nonnull
    public Location getLocation() {
        return this.entity.getLocation();
    }

    @Nonnull
    public Location getLocation(@Nonnull Location copyInto) {
        return this.entity.getLocation(copyInto);
    }

    public boolean isEnemyAlive(Entity other) {
        return isEnemyAlive(Warlords.getPlayer(other));
    }

    public boolean isEnemyAlive(@Nullable WarlordsPlayer p) {
        return p != null &&
                p.getGame() == getGame() &&
                !p.isDeath() &&
                p.getTeam() != getTeam();
    }

    public boolean isEnemy(@Nullable WarlordsPlayer p) {
        return p != null &&
                p.getGame() == getGame() &&
                p.getTeam() != getTeam();
    }

    public boolean isTeammateAlive(Entity other) {
        return isEnemyAlive(Warlords.getPlayer(other));
    }

    public boolean isTeammateAlive(@Nullable WarlordsPlayer p) {
        return p != null &&
                p.getGame() == getGame() &&
                !p.isDeath() &&
                p.getTeam() == getTeam();
    }

    public boolean isTeammate(@Nullable WarlordsPlayer p) {
        return p != null &&
                p.getGame() == getGame() &&
                p.getTeam() == getTeam();
    }

    public void teleport(Location location) {
        this.entity.teleport(location);
    }

    public PlayingState getGameState() {
        return this.gameState;
    }

    public double getFlagDamageMultipler() {
        return flagDamageMultipler;
    }

    public void setFlagDamageMultipler(double flagDamageMultipler) {
        this.flagDamageMultipler = flagDamageMultipler;
    }

    public String getColoredName() {
        return getTeam().teamColor() + getName();
    }

    public void setVelocity(org.bukkit.util.Vector v) {
        this.entity.setVelocity(v);
    }

    public World getWorld() {
        return this.entity.getWorld();
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public List<Location> getTrail() {
        return trail;
    }

    public void addTimeInCombat() {
        timeInCombat++;
    }

    public int getTimeInCombat() {
        return timeInCombat;
    }

    public void addTotalRespawnTime() {
        respawnTimeSpent += respawnTimer;
    }

    public int getRespawnTimeSpent() {
        return respawnTimeSpent;
    }

    public boolean getInfiniteEnergy() {
        return infiniteEnergy;
    }

    public void setInfiniteEnergy(boolean infiniteEnergy) {
        this.infiniteEnergy = infiniteEnergy;
    }

    public boolean getDisableCooldowns() {
        return disableCooldowns;
    }

    public void setDisableCooldowns(boolean disableCooldowns) {
        this.disableCooldowns = disableCooldowns;
    }

    public double getEnergyModifier() {
        return energyModifier;
    }

    public void setEnergyModifier(double energyModifier) {
        this.energyModifier = energyModifier;
    }

    public double getCooldownModifier() {
        return cooldownModifier;
    }

    public void setCooldownModifier(double cooldownModifier) {
        this.cooldownModifier = cooldownModifier;
    }

    public boolean isTakeDamage() {
        return takeDamage;
    }

    public void setTakeDamage(boolean takeDamage) {
        this.takeDamage = takeDamage;
    }

    public int getBlocksTravelledCM() {
        return blocksTravelledCM;
    }

    public void setBlocksTravelledCM(int blocksTravelledCM) {
        this.blocksTravelledCM = blocksTravelledCM;
    }
}
