package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Aspect;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Summoner extends BaseSet {

    private static final Map<AbstractMob, UUID> SUMMON_OWNERS = Collections.synchronizedMap(new WeakHashMap<>());

    private int minionDamage;
    private int minionSpeed;

    public static void registerSummon(AbstractMob mob, WarlordsPlayer owner) {
        SUMMON_OWNERS.put(mob, owner.getUuid());
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

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getGame().registerEvents(new Listener() {

                @EventHandler
                public void onSpawn(WarlordsMobSpawnEvent event) {
                    AbstractMob mob = event.getMob();
                    if (!warlordsPlayer.getUuid().equals(SUMMON_OWNERS.get(mob)) || mob.getWarlordsNPC() == null) {
                        return;
                    }
                    WarlordsNPC npc = mob.getWarlordsNPC();
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
                    ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (damageEvent, currentDamageValue) ->
                            currentDamageValue.addModifier(
                                    FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                    getName() + " - Damage",
                                    1 + minionDamage / 100f
                            )
                    ));
                    npc.addSpeedModifier(warlordsPlayer, getName() + " - Speed", minionSpeed, Integer.MAX_VALUE);
                }

            });
        }

    }

}
