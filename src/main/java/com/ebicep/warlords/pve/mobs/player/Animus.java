package com.ebicep.warlords.pve.mobs.player;

import com.ebicep.warlords.abilities.JudgementStrike;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.flags.Untargetable;
import com.ebicep.warlords.pve.mobs.tiers.PlayerMob;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Animus extends AbstractMob implements PlayerMob, Untargetable {

    @Nullable
    private WarlordsEntity owner;
    @Nullable
    private WarlordsEntity inherited;

    public Animus(Location spawnLocation) {
        this(spawnLocation, "Animus", 5000, 0, 0, 0, 0);
    }

    public Animus(
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

    public Animus(Location spawnLocation, WarlordsEntity owner, WarlordsEntity inherited) {
        this(
                spawnLocation,
                owner.getName() + "'s Animus",
                (int) inherited.getMaxHealth(),
                owner.getSpeed().getLastSpeed() * owner.getSpeed().getBaseSpeedToWalkingSpeed(),
                0,
                150,
                250
        ); //TODO test speed
        this.owner = owner;
        this.inherited = inherited;
        this.equipment = new Utils.SimpleEntityEquipment(
                owner.getHelmet(),
                owner.getChestplate(),
                owner.getLeggings(),
                owner.getBoots(),
                owner.getWeaponItem()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ANIMUS;
    }

    @Override
    public void onNPCCreate() {
        super.onNPCCreate();
        if (owner != null) {
            SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
            skinTrait.setSkinName(owner.getName());
        }
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        if (owner != null) {
            // remove other animus
            List<AbstractMob> toDespawn = new ArrayList<>();
            for (AbstractMob mob : option.getMobs()) {
                if (mob instanceof Animus animus && Objects.equals(animus.getOwner(), owner)) {
                    toDespawn.add(mob);
                }
            }
            toDespawn.forEach(option::despawnMob);

            // copy strike stats
            for (JudgementStrike judgementStrike : owner.getAbilitiesMatching(JudgementStrike.class)) {
                playerClass.addAbility(new JudgementStrike() {{
                    getCooldown().setBaseValue(2);
                    setMinDamageHeal(new FloatModifiable(judgementStrike.getMinDamageHeal()));
                    setMaxDamageHeal(new FloatModifiable(judgementStrike.getMaxDamageHeal()));
                    getHealValues().getStrikeHealing().value().setBaseValue(judgementStrike.getHealValues().getStrikeHealing().value().getBaseValue());
                    setInPve(judgementStrike.isInPve());
                    setPveMasterUpgrade(judgementStrike.isPveMasterUpgrade());
                    setPveMasterUpgrade2(judgementStrike.isPveMasterUpgrade2());
                }});
                break;
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        if (event.getCause().equals("Judgement Strike")) {
            double speed = attacker.getSpeed()
                                   .getModifiers()
                                   .stream()
                                   .filter(modifier -> modifier.getModifier() > 0)
                                   .mapToDouble(value -> value.getModifier())
                                   .sum();
            float damageBoost = (float) (1 + speed / 100);
            event.setMin(event.getMin() * damageBoost);
            event.setMax(event.getMax() * damageBoost);
        }
    }

    @Nullable
    public WarlordsEntity getOwner() {
        return owner;
    }
}
