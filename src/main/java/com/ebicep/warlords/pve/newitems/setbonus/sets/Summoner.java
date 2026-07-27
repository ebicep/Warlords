package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Aspect;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Summoner extends BaseSet {

    private int minionDamage;
    private int minionSpeed;

    public static void applyToSummon(AbstractMob mob, WarlordsPlayer owner) {
        if (!owner.getCooldownManager().hasCooldown(Summoner.class)) {
            return;
        }
        new GameRunnable(owner.getGame()) {

            @Override
            public void run() {
                WarlordsNPC npc = mob.getWarlordsNPC();
                if (npc == null) {
                    return;
                }
                Summoner summoner = (Summoner) NewItemsSetBonus.SUMMONER.getSetBonus();
                summoner.applyBonuses(owner, mob, npc);
            }

        }.runTaskLater(1);
    }

    @Override
    public void init() {
        super.init();
        this.minionDamage = getValue("minionDamage", int.class);
        this.minionSpeed = getValue("minionSpeed", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "summoner";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(minionDamage, minionSpeed);
    }

    private void applyBonuses(WarlordsPlayer owner, AbstractMob mob, WarlordsNPC npc) {
        if (mob.getAspect() == null) {
            Aspect[] aspects = Aspect.values();
            Aspect aspect = aspects[ThreadLocalRandom.current().nextInt(aspects.length)];
            mob.setAspect(aspect);
            aspect.apply(npc);
        }
        npc.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName() + " - Damage",
                null,
                Summoner.class,
                null,
                npc,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                false
        ).addModifier(
                Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,
                (damageEvent, currentDamageValue) -> currentDamageValue.addModifier(
                        FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                        getName() + " - Damage",
                        1 + minionDamage / 100f
                )
        ));
        npc.addSpeedModifier(owner, getName() + " - Speed", minionSpeed, Integer.MAX_VALUE);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Summoner.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ));
        }

    }

}
