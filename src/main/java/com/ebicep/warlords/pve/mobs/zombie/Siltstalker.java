package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class Siltstalker extends AbstractMob implements ChampionMob {

    private static final int SUBMERGE_RANGE = 24;
    private static final int SUBMERGE_COOLDOWN_TICKS = 14 * 20;
    private static final int INITIAL_SUBMERGE_DELAY_TICKS = 5 * 20;
    private static final int SUBMERGE_DURATION_TICKS = 2 * 20;

    private static final int BACKSTAB_WINDOW_TICKS = 4 * 20;
    private static final int WEAKEN_DURATION_TICKS = 4 * 20;
    private static final float WEAKEN_DAMAGE_MULTIPLIER = .8f;


    private int submergeCooldownTicks = INITIAL_SUBMERGE_DELAY_TICKS;
    private int submergeTicksLeft = 0;
    private boolean submerged = false;

    private WarlordsEntity ambushTarget;
    private WarlordsEntity backstabTarget;
    private int backstabTicksLeft = 0;
    private static final ItemStack AIR = new ItemStack(Material.AIR);

    public Siltstalker(Location spawnLocation) {
        super(
                spawnLocation,
                "Siltstalker",
                5800,
                0.28f,
                10,
                375,
                575
        );
    }

    public Siltstalker(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.SILTSTALKER;
    }

    @Override
    public double getDefaultAttackRange() {
        return 2;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_DROWNED_AMBIENT_WATER, 2, 0.8f);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (warlordsNPC == null || warlordsNPC.isDead() || !warlordsNPC.isActive()) {
            return;
        }

        tickBackstabWindow();

        if (submerged) {
            tickSubmerge();
            return;
        }

        if (submergeCooldownTicks > 0) {
            submergeCooldownTicks--;
            return;
        }

        startSubmerge();
    }

    private void startSubmerge() {
        List<WarlordsEntity> targets = PlayerFilter
                .entitiesAround(warlordsNPC, SUBMERGE_RANGE, SUBMERGE_RANGE, SUBMERGE_RANGE)
                .aliveEnemiesOf(warlordsNPC)
                .filter(warlordsEntity -> warlordsEntity instanceof WarlordsPlayer)
                .toList();

        if (targets.isEmpty()) {
            submergeCooldownTicks = 20;
            return;
        }

        Collections.shuffle(targets);

        ambushTarget = targets.getFirst();
        submerged = true;
        submergeTicksLeft = SUBMERGE_DURATION_TICKS;

        removeTarget();
        toggleStun(true);
        applySubmergeVisualState();

        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_MUD_BREAK, 2, 0.6f);
        spawnSubmergeParticles(warlordsNPC.getLocation());

        ambushTarget.sendMessage(Component.text("A Siltstalker sinks beneath the ground and stalks you.", NamedTextColor.DARK_AQUA));
        ambushTarget.playSound(ambushTarget.getLocation(), Sound.ENTITY_DROWNED_AMBIENT_WATER, 1, 0.5f);
    }

    private void tickSubmerge() {
        submergeTicksLeft--;

        if (submergeTicksLeft % 5 == 0) {
            spawnSubmergeParticles(warlordsNPC.getLocation());
        }

        if (!isValidAmbushTarget(ambushTarget)) {
            endSubmerge(false);
            return;
        }

        if (submergeTicksLeft <= 0) {
            emergeBehindTarget();
        }
    }

    private void emergeBehindTarget() {
        if (!isValidAmbushTarget(ambushTarget)) {
            endSubmerge(false);
            return;
        }

        Location emergeLocation = getBehindTarget(ambushTarget);

        warlordsNPC.getEntity().teleport(emergeLocation);
        removeSubmergeVisualState();

        submerged = false;
        toggleStun(false);
        setTarget(ambushTarget);

        backstabTarget = ambushTarget;
        backstabTicksLeft = BACKSTAB_WINDOW_TICKS;

        spawnEmergeParticles(emergeLocation);

        ambushTarget.playSound(ambushTarget.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.7f);
        Utils.playGlobalSound(emergeLocation, Sound.BLOCK_MUD_PLACE, 2, 0.7f);

        ambushTarget = null;
        submergeCooldownTicks = SUBMERGE_COOLDOWN_TICKS;
    }

    private void endSubmerge(boolean resetCooldown) {
        removeSubmergeVisualState();

        submerged = false;
        ambushTarget = null;
        submergeTicksLeft = 0;

        toggleStun(false);

        if (resetCooldown) {
            submergeCooldownTicks = SUBMERGE_COOLDOWN_TICKS;
        } else {
            submergeCooldownTicks = 20;
        }
    }

    private void tickBackstabWindow() {
        if (backstabTarget == null) {
            return;
        }

        if (!isValidBackstabTarget(backstabTarget)) {
            clearBackstab();
            return;
        }

        backstabTicksLeft--;

        if (backstabTicksLeft % 10 == 0) {
            backstabTarget.getWorld().spawnParticle(
                    Particle.SQUID_INK,
                    backstabTarget.getLocation().clone().add(0, 1.1, 0),
                    8,
                    .25,
                    .35,
                    .25,
                    .01
            );
        }

        if (backstabTicksLeft <= 0) {
            clearBackstab();
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        if (attacker != warlordsNPC) {
            return;
        }
        if (receiver != backstabTarget) {
            return;
        }
        if (backstabTicksLeft <= 0) {
            clearBackstab();
            return;
        }

        applyWeakness(receiver);

        Utils.playGlobalSound(receiver.getLocation(), Sound.ENTITY_DROWNED_HURT, 2, 0.6f);

        clearBackstab();
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (self != warlordsNPC) {
            return;
        }
        if (!submerged) {
            return;
        }

        event.setCancelled(true);
    }

    private void applyWeakness(WarlordsEntity target) {
        target.getCooldownManager().removeCooldown(SiltstalkerWeakness.class, false);

        target.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Silt Weakness",
                "SW",
                SiltstalkerWeakness.class,
                new SiltstalkerWeakness(),
                warlordsNPC,
                CooldownTypes.HIGH_LEVEL_DEBUFF,
                cooldownManager -> {
                },
                WEAKEN_DURATION_TICKS
        ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
            currentDamageValue.addModifier(
                    FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                    "Silt Weakness",
                    WEAKEN_DAMAGE_MULTIPLIER
            );
        }));
    }

    private boolean isValidAmbushTarget(WarlordsEntity target) {
        if (target == null) {
            return false;
        }
        if (!target.isAlive() || !target.isActive()) {
            return false;
        }
        if (target.getWorld() != warlordsNPC.getWorld()) {
            return false;
        }
        return warlordsNPC.isEnemyAlive(target);
    }

    private boolean isValidBackstabTarget(WarlordsEntity target) {
        if (!isValidAmbushTarget(target)) {
            return false;
        }
        return target.getLocation().distanceSquared(warlordsNPC.getLocation()) <= 10 * 10;
    }

    private Location getBehindTarget(WarlordsEntity target) {
        Location targetLocation = target.getLocation();
        Vector direction = targetLocation.getDirection();
        direction.setY(0);

        if (direction.lengthSquared() == 0) {
            direction = warlordsNPC.getLocation().toVector().subtract(targetLocation.toVector());
            direction.setY(0);
        }

        if (direction.lengthSquared() == 0) {
            direction = new Vector(1, 0, 0);
        }

        direction.normalize();

        Location behind = targetLocation.clone().subtract(direction.multiply(2.4));
        behind.setY(targetLocation.getY());

        Vector lookDirection = targetLocation.toVector().subtract(behind.toVector());
        if (lookDirection.lengthSquared() > 0) {
            behind.setDirection(lookDirection);
        }

        return behind;
    }

    private void applySubmergeVisualState() {
        if (warlordsNPC != null) {
            warlordsNPC.setHealthLineVisible(false);
        }

        if (npc != null) {
            Equipment equipmentTrait = npc.getOrAddTrait(Equipment.class);
            equipmentTrait.set(Equipment.EquipmentSlot.HAND, AIR);
            equipmentTrait.set(Equipment.EquipmentSlot.OFF_HAND, AIR);
            equipmentTrait.set(Equipment.EquipmentSlot.HELMET, AIR);
            equipmentTrait.set(Equipment.EquipmentSlot.CHESTPLATE, AIR);
            equipmentTrait.set(Equipment.EquipmentSlot.LEGGINGS, AIR);
            equipmentTrait.set(Equipment.EquipmentSlot.BOOTS, AIR);
        }

        if (warlordsNPC.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, SUBMERGE_DURATION_TICKS + 20, 0, false, false));
            livingEntity.setInvulnerable(true);

            org.bukkit.inventory.EntityEquipment entityEquipment = livingEntity.getEquipment();
            if (entityEquipment != null) {
                entityEquipment.setItemInMainHand(AIR);
                entityEquipment.setItemInOffHand(AIR);
                entityEquipment.setHelmet(AIR);
                entityEquipment.setChestplate(AIR);
                entityEquipment.setLeggings(AIR);
                entityEquipment.setBoots(AIR);
            }
        }
    }

    private void removeSubmergeVisualState() {
        if (warlordsNPC != null) {
            warlordsNPC.setHealthLineVisible(true);
        }

        if (npc != null) {
            updateEquipment();
        }

        if (warlordsNPC == null) {
            return;
        }

        if (warlordsNPC.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.removePotionEffect(PotionEffectType.INVISIBILITY);
            livingEntity.setInvulnerable(false);
        }
    }

    private void spawnSubmergeParticles(Location location) {
        EffectUtils.displayParticle(Particle.SQUID_INK, location.clone().add(0, .25, 0), 18, .45, .15, .45, .02);
        EffectUtils.displayParticle(Particle.BUBBLE_POP, location.clone().add(0, .25, 0), 12, .35, .1, .35, .01);
    }

    private void spawnEmergeParticles(Location location) {
        EffectUtils.displayParticle(Particle.SQUID_INK, location.clone().add(0, 1, 0), 24, .45, .5, .45, .03);
        EffectUtils.displayParticle(Particle.SCULK_SOUL, location.clone().add(0, 1, 0), 14, .35, .35, .35, .02);
        Utils.playGlobalSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 2, 0.6f);
    }

    private void clearBackstab() {
        backstabTarget = null;
        backstabTicksLeft = 0;
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_DROWNED_DEATH, 2, 0.5f);
    }

    @Override
    public void cleanup(PveOption pveOption) {
        removeSubmergeVisualState();
        clearBackstab();
        super.cleanup(pveOption);
    }

    private static class SiltstalkerWeakness {
    }

}
