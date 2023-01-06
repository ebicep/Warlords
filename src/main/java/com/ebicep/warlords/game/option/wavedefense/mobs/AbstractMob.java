package com.ebicep.warlords.game.option.wavedefense.mobs;

import com.ebicep.customentities.nms.pve.CustomEntity;
import com.ebicep.warlords.abilties.Fireball;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsDropWeaponEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveWeaponEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.UnaryOperator;

public abstract class AbstractMob<T extends CustomEntity<?>> implements Mob {

    protected final T entity;
    protected final EntityInsentient entityInsentient;
    protected final LivingEntity livingEntity;
    protected final Location spawnLocation;
    protected final String name;
    protected final MobTier mobTier;
    protected final EntityEquipment ee;
    protected final int maxHealth;
    protected final float walkSpeed;
    protected final int damageResistance;
    protected final float minMeleeDamage;
    protected final float maxMeleeDamage;

    protected WarlordsNPC warlordsNPC;

    public AbstractMob(
            T entity,
            Location spawnLocation,
            String name,
            MobTier mobTier,
            EntityEquipment ee,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        this.entity = entity;
        this.spawnLocation = spawnLocation;
        this.name = name;
        this.mobTier = mobTier;
        this.ee = ee;
        this.maxHealth = maxHealth;
        this.walkSpeed = walkSpeed;
        this.damageResistance = damageResistance;
        this.minMeleeDamage = minMeleeDamage;
        this.maxMeleeDamage = maxMeleeDamage;

        entity.spawn(spawnLocation);

        this.entityInsentient = entity.get();
        this.entityInsentient.persistent = true;

        this.livingEntity = (LivingEntity) entityInsentient.getBukkitEntity();
        if (ee != null) {
            livingEntity.getEquipment().setBoots(ee.getBoots());
            livingEntity.getEquipment().setLeggings(ee.getLeggings());
            livingEntity.getEquipment().setChestplate(ee.getChestplate());
            livingEntity.getEquipment().setHelmet(ee.getHelmet());
            livingEntity.getEquipment().setItemInHand(ee.getItemInHand());
        } else {
            livingEntity.getEquipment().setHelmet(new ItemStack(Material.BARRIER));
        }
    }

    public WarlordsNPC toNPC(Game game, Team team, UUID uuid) {
        this.warlordsNPC = new WarlordsNPC(
                uuid,
                name,
                Weapons.ABBADON,
                livingEntity,
                game,
                team,
                Specializations.PYROMANCER,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                this
        );
        AbstractAbility weapon = warlordsNPC.getSpec().getWeapon();
        if (weapon instanceof Fireball) {
            ((Fireball) weapon).setMaxDistance(150);
        }

        Optional<Option> optional = game.getOptions()
                                        .stream()
                                        .filter(option -> option instanceof WaveDefenseOption)
                                        .findFirst();
        if (optional.isPresent()) {
            WaveDefenseOption option = (WaveDefenseOption) optional.get();

            onSpawn(option);
            game.addNPC(warlordsNPC);

            boolean isEndless = option.getDifficulty() == DifficultyIndex.ENDLESS;
            /*
             * Base scale of 600
             *
             * The higher the scale is the longer it takes to increase per interval.
             */
            double scale = isEndless ? 1200.0 : 600.0;
            long playerCount = 6;//game.warlordsPlayers().count();
            // Flag check whether mob is a boss.
            boolean bossFlagCheck = playerCount > 1 && warlordsNPC.getMobTier() == MobTier.BOSS;
            // Reduce base scale by 100 for each player after 2 or more players in game instance.
            double modifiedScale = scale - (playerCount > 1 ? 100 * playerCount : 0);
            // Divide scale based on wave count.
            double modifier = option.getWaveCounter() / modifiedScale + 1;

            // Multiply health & min/max melee damage by waveCounter + 1 ^ base damage.
            int minMeleeDamage = (int) Math.pow(warlordsNPC.getMinMeleeDamage(), modifier);
            int maxMeleeDamage = (int) Math.pow(warlordsNPC.getMaxMeleeDamage(), modifier);
            float health = (float) Math.pow(warlordsNPC.getMaxBaseHealth(), modifier);
            // Increase boss health by 25% for each player in game instance.
            float bossMultiplier = 1 + (0.25f * playerCount);

            // Multiply damage/health by given difficulty.
            float difficultyMultiplier;
            switch (option.getDifficulty()) {
                case EASY:
                    difficultyMultiplier = 0.75f;
                    break;
                case HARD:
                    difficultyMultiplier = 1.5f;
                    break;
                default:
                    difficultyMultiplier = 1;
                    break;
            }

            // Final health value after applying all modifiers.
            float finalHealth = (health * difficultyMultiplier) * (bossFlagCheck ? bossMultiplier : 1);
            warlordsNPC.setMaxBaseHealth(finalHealth);
            warlordsNPC.setMaxHealth(finalHealth);
            warlordsNPC.setHealth(finalHealth);

            int endlessFlagCheckMin = isEndless ? minMeleeDamage : (int) (warlordsNPC.getMinMeleeDamage() * difficultyMultiplier);
            int endlessFlagCheckMax = isEndless ? maxMeleeDamage : (int) (warlordsNPC.getMaxMeleeDamage() * difficultyMultiplier);
            warlordsNPC.setMinMeleeDamage(endlessFlagCheckMin);
            warlordsNPC.setMaxMeleeDamage(endlessFlagCheckMax);
        }

        return warlordsNPC;
    }

    public abstract void onSpawn(WaveDefenseOption option);

    public AbstractMob<T> prependOperation(UnaryOperator<WarlordsNPC> mapper) {
        mapper.apply(this.warlordsNPC);
        return this;
    }

    public abstract void whileAlive(int ticksElapsed, WaveDefenseOption option);

    public abstract void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event);

    public abstract void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event);

    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption option) {
        dropWeapon(killer);
    }

    public void dropWeapon(WarlordsEntity killer) {
        if (DatabaseManager.playerService == null || !(killer instanceof WarlordsPlayer)) {
            return;
        }
        dropWeapon(killer, 100);
        PlayerFilter.playingGame(killer.getGame())
                    .teammatesOfExcludingSelf(killer)
                    .forEach(teammate -> dropWeapon(teammate, 200));
    }

    private void dropWeapon(WarlordsEntity killer, int bound) {
        AtomicDouble dropRate = new AtomicDouble(dropRate());
        Bukkit.getPluginManager().callEvent(new WarlordsDropWeaponEvent(killer, dropRate));
        if (ThreadLocalRandom.current().nextDouble(0, bound) < dropRate.get()) {
            AbstractWeapon weapon = generateWeapon((WarlordsPlayer) killer);
            Bukkit.getPluginManager().callEvent(new WarlordsGiveWeaponEvent(killer, weapon));

            killer.getGame().forEachOnlinePlayer((player, team) -> {
                player.spigot().sendMessage(new ComponentBuilder(ChatColor.AQUA + killer.getName() + ChatColor.GRAY + " got lucky and found ")
                        .appendHoverItem(weapon.getName(), weapon.generateItemStack(false))
                        .append(ChatColor.GRAY + "!")
                        .create()
                );
            });
            killer.playSound(killer.getLocation(), Sound.LEVEL_UP, 500, 2);
        }
    }

    public EntityLiving getTarget() {
        return this.entity.getTarget();
    }

    public void setTarget(WarlordsEntity target) {
        this.entity.setTarget(((CraftPlayer) target.getEntity()).getHandle());
    }

    public void setTarget(LivingEntity target) {
        this.entity.setTarget((((EntityLiving) ((CraftEntity) target).getHandle())));
    }

    public void removeTarget() {
        this.entity.removeTarget();
    }

    public T getEntity() {
        return entity;
    }

    public EntityInsentient getEntityInsentient() {
        return entityInsentient;
    }

    public LivingEntity getLivingEntity() {
        return livingEntity;
    }

    public WarlordsNPC getWarlordsNPC() {
        return warlordsNPC;
    }

    public String getName() {
        return name;
    }

    public MobTier getMobTier() {
        return mobTier;
    }

    public EntityEquipment getEe() {
        return ee;
    }
}
