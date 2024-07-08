package com.ebicep.warlords.game.option.towerdefense.mobs;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.towerdefense.mobs.attributes.blocking.Blockable;
import com.ebicep.warlords.game.option.towerdefense.mobs.attributes.blocking.TDBlockingMode;
import com.ebicep.warlords.game.option.towerdefense.mobs.attributes.type.GroundType;
import com.ebicep.warlords.game.option.towerdefense.mobs.attributes.type.TDMobType;
import com.ebicep.warlords.player.ingame.MobHologram;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.CustomAttackStrategy;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.citizensnpcs.api.ai.NavigatorParameters;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

public abstract class TowerDefenseMob extends AbstractMob {

    // resistances in decimal, .1 = 10% res
    private final FloatModifiable physicalResistance = new FloatModifiable(0);
    private final FloatModifiable magicResistance = new FloatModifiable(0);
    @Nullable
    private WarlordsEntity spawner;

    // air/ground
    // blocking state
    // list<pathfinding modifying>
    private TDMobType mobType = GroundType.DEFAULT;
    private TDBlockingMode blockingMode = Blockable.DEFAULT;

    public TowerDefenseMob(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractAbility... abilities
    ) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, abilities);
    }

    @Override
    public WarlordsNPC toNPC(Game game, Team team, Consumer<WarlordsNPC> modifyStats) {
        EntityType entityType = getMobRegistry().entityType;
        this.npc = NPCManager.NPC_REGISTRY.createNPC(entityType, name);

        NavigatorParameters defaultParameters = this.npc.getNavigator().getDefaultParameters();
        defaultParameters.attackStrategy(CustomAttackStrategy.ATTACK_STRATEGY);
        defaultParameters.attackRange(getDefaultAttackRange())
                         .stuckAction(null) // disable tping to player if too far away
                         .updatePathRate(5)
                         .distanceMargin(.75)
                         .speedModifier(.9f)
                         .range(100);

        npc.data().set(NPC.Metadata.COLLIDABLE, false);

        giveGoals();
        onNPCCreate();
        updateEquipment();

        this.npc.spawn(spawnLocation);

        if (npc.getEntity() instanceof Player player) {
            player.setNoDamageTicks(0);
        }

        this.warlordsNPC = new WarlordsNPC(
                name,
                npc,
                game,
                team,
                maxHealth,
                walkSpeed,
                minMeleeDamage,
                maxMeleeDamage,
                meleeCritChance,
                meleeCritMutiplier,
                this,
                playerClass,
                new MobHologram.TextDisplayHologram(.2f) {
                    @Nullable
                    @Override
                    public Entity getEntity() {
                        if (warlordsNPC == null) {
                            return null;
                        }
                        return warlordsNPC.getEntity();
                    }

                }
        );

        modifyStats.accept(warlordsNPC);

        if (spawner instanceof WarlordsPlayer) {
            warlordsNPC.getMobHologram().getCustomHologramLines().add(new MobHologram.CustomHologramLine(spawner.getColoredName()));
        }

        for (AbstractAbility ability : warlordsNPC.getAbilities()) {
            if (ability.getCurrentCooldown() < ability.getCooldownValue()) {
                warlordsNPC.setEnergy(warlordsNPC.getEnergy() + ability.getEnergyCostValue());
            }
        }

        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Resistances",
                null,
                TowerDefenseMob.class,
                null,
                warlordsNPC,
                CooldownTypes.INTERNAL,
                cooldownManager -> {},
                false,
                (cooldown, ticksElapsed) -> {
                    physicalResistance.tick();
                    magicResistance.tick();
                }
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                EnumSet<InstanceFlags> flags = event.getFlags();
                List<CustomInstanceFlags> customFlags = event.getCustomFlags();
                for (CustomInstanceFlags customFlag : customFlags) {
                    if (customFlag instanceof CustomInstanceFlags.Valued valued) {
                        switch (valued.flag()) {
                            case TD_PHYSICAL_RES_REDUCTION -> valued.floatModifiableConsumer().accept(physicalResistance);
                            case TD_MAGIC_RES_REDUCTION -> valued.floatModifiableConsumer().accept(magicResistance);
                        }
                    }
                }
                if (flags.contains(InstanceFlags.TD_PHYSICAL)) {
                    return currentDamageValue * (1 - physicalResistance.getCalculatedValue());
                } else if (flags.contains(InstanceFlags.TD_MAGIC)) {
                    return currentDamageValue * (1 - magicResistance.getCalculatedValue());
                }
                physicalResistance.tick();
                magicResistance.tick();
                return currentDamageValue;
            }
        });

        return warlordsNPC;
    }

    @Override
    protected void handleAspects(PveOption option) {
        // disable aspects
    }

    @Override
    public void giveGoals() {
    }

    public void setSpawner(@Nullable WarlordsEntity spawner) {
        this.spawner = spawner;
    }

    public FloatModifiable getPhysicalResistance() {
        return physicalResistance;
    }

    public FloatModifiable getMagicResistance() {
        return magicResistance;
    }

    public TDMobType getMobType() {
        return mobType;
    }

    public void setMobType(TDMobType mobType) {
        this.mobType = mobType;
    }

    public TDBlockingMode getBlockingMode() {
        return blockingMode;
    }

    public void setBlockingMode(TDBlockingMode blockingMode) {
        this.blockingMode = blockingMode;
    }

}
