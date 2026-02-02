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
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Summoner extends BaseSet {

    private int minionDamage;
    private int minionSpeed;

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
                    private void onMobSpawn(WarlordsMobSpawnEvent event) {
                    AbstractMob mob = event.getMob();

                    if (mob.getWarlordsNPC() == null) {
                        return;
                    }

                    WarlordsNPC mobNPC = mob.getWarlordsNPC();
                    if (!mobNPC.getTeam().equals(warlordsPlayer.getTeam())) {
                        return;
                    }

                    if (mob.getAspect() == null) {
                        Aspect[] aspects = Aspect.values();
                        Aspect randomAspect = aspects[ThreadLocalRandom.current().nextInt(aspects.length)];
                        mob.setAspect(randomAspect);
                        randomAspect.apply(mobNPC);
                    }

                    mobNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                            getName() + " - Damage",
                            null,
                            Summoner.class,
                            null,
                            mobNPC,
                            CooldownTypes.BUFF,
                            cooldownManager -> {},
                            false
                    ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event2, currentDamageValue) -> {
                        currentDamageValue.addModifier(
                                FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                getName() + " - Damage",
                                1 + minionDamage / 100f
                        );
                    }));

                    mobNPC.addSpeedModifier(warlordsPlayer, getName() + " - Speed", minionSpeed, Integer.MAX_VALUE);
                }
            });

        }

    }

}