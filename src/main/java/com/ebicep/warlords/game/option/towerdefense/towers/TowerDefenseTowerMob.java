package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.towerdefense.towers.trooptype.TDTroopType;
import com.ebicep.warlords.player.ingame.MobHologram;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.CustomAttackStrategy;
import com.ebicep.warlords.pve.mobs.tiers.Mob;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.citizensnpcs.api.ai.NavigatorParameters;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public abstract class TowerDefenseTowerMob extends AbstractMob implements Mob {

    private WarlordsTower spawner;
    protected FloatModifiable armor = new FloatModifiable(0);
    private TDTroopType troopType = TDTroopType.DEFAULT;

    public TowerDefenseTowerMob(
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
        defaultParameters.attackRange(2)
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

        for (AbstractAbility ability : warlordsNPC.getAbilities()) {
            if (ability.getCurrentCooldown() < ability.getCooldownValue()) {
                warlordsNPC.setCurrentEnergy(warlordsNPC.getCurrentEnergy() + ability.getEnergyCostValue());
            }
        }

        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Armor",
                null,
                TowerDefenseTowerMob.class,
                null,
                warlordsNPC,
                CooldownTypes.INTERNAL,
                cooldownManager -> {},
                false,
                (cooldown, ticksElapsed) -> {
                    armor.tick();
                }
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                List<CustomInstanceFlags> customFlags = event.getCustomFlags();
                for (CustomInstanceFlags customFlag : customFlags) {
                    if (customFlag instanceof CustomInstanceFlags.Valued valued && valued.flag() == CustomInstanceFlags.Valued.Flag.TD_DEFENDER_ARMOR) {
                        valued.floatModifiableConsumer().accept(armor);
                    }
                }
                currentDamageValue *= (1 - armor.getCalculatedValue());
                armor.tick();
                return currentDamageValue;
            }
        });
        return warlordsNPC;
    }

    @Override
    public void giveGoals() {
        npc.getDefaultGoalController().addGoal(new NPCTowerDefenseDefenderGoal(this, 3), 2);
    }

    public void setSpawner(@Nonnull WarlordsTower spawner) {
        this.spawner = spawner;
    }

    public WarlordsTower getSpawner() {
        return spawner;
    }

    @Override
    public double weaponDropRate() {
        return 0;
    }

    @Override
    public int commonWeaponDropChance() {
        return 0;
    }

    @Override
    public int rareWeaponDropChance() {
        return 0;
    }

    @Override
    public int epicWeaponDropChance() {
        return 0;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public TextColor getTextColor() {
        return NamedTextColor.WHITE;
    }

    public TDTroopType getTroopType() {
        return troopType;
    }

    public void setTroopType(TDTroopType troopType) {
        this.troopType = troopType;
    }

    public FloatModifiable getArmor() {
        return armor;
    }

}
