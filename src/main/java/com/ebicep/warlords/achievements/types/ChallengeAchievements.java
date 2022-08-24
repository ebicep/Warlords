package com.ebicep.warlords.achievements.types;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.achievements.Achievement;
import com.ebicep.warlords.events.player.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.option.wavedefense.mobs.bosses.Ghoulcaller;
import com.ebicep.warlords.game.option.wavedefense.mobs.bosses.Narmer;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum ChallengeAchievements implements Achievement {

    REJUVENATION("Rejuvenation",
            "Heal your flag carrier from below 1k health to their maximum health capacity or above in 3 seconds.",
            GameMode.CAPTURE_THE_FLAG,
            null,
            warlordsEntity -> {
                List<WarlordsDamageHealingFinalEvent> events = warlordsEntity.getSecondStats().getEventsAsAttackerFromLastSecond(3);
                WarlordsEntity carrier = null;
                int below1000Index = -1;
                int fullHealthIndex = -1;
                for (int i = 0; i < events.size(); i++) {
                    WarlordsDamageHealingFinalEvent event = events.get(i);
                    if (event.isHasFlag() && event.getInitialHealth() <= 1000) {
                        below1000Index = i;
                        carrier = event.getPlayer(); //carrier must be the same person, no repicks
                        break;
                    }
                }
                if (below1000Index != -1) {
                    for (int i = below1000Index; i < events.size(); i++) {
                        WarlordsDamageHealingFinalEvent event = events.get(i);
                        if (event.getPlayer() == carrier && event.isHasFlag() && event.getFinalHealth() >= carrier.getMaxHealth()) {
                            fullHealthIndex = i;
                            break;
                        }
                    }
                } else {
                    return false;
                }
                if (fullHealthIndex != -1) {
                    List<WarlordsDamageHealingFinalEvent> healingEvents = new ArrayList<>();
                    for (int i = below1000Index; i <= fullHealthIndex; i++) {
                        healingEvents.add(events.get(i));
                    }

                    return (float) healingEvents.stream()
                            .filter(WarlordsDamageHealingFinalEvent::isHealingInstance)
                            .map(WarlordsDamageHealingFinalEvent::getValue)
                            .mapToDouble(Float::doubleValue)
                            .sum() >= 2000;
                } else {
                    return false;
                }
            }
    ) {
        @Override
        public boolean checkTeammates() {
            return true;
        }
    },
    BLITZKRIEG("Blitzkrieg",
            "Kill the enemy flag carrier within 2 seconds.",
            GameMode.CAPTURE_THE_FLAG,
            null,
            warlordsEntity -> {
                List<WarlordsDamageHealingFinalEvent> events = warlordsEntity.getSecondStats().getEventsAsAttackerFromLastSecond(2);
                int indexCarrierFull = -1;
                int indexCarrierDead = -1;
                for (int i = 0; i < events.size(); i++) {
                    WarlordsDamageHealingFinalEvent event = events.get(i);
                    if (event.isHasFlag() || event.getValue() >= event.getInitialHealth()) { //for one shots
                        if (event.getInitialHealth() >= event.getPlayer().getMaxHealth()) {
                            indexCarrierFull = i;
                            break;
                        }
                    }
                }
                if (indexCarrierFull != -1) {
                    for (int i = indexCarrierFull; i < events.size(); i++) {
                        WarlordsDamageHealingFinalEvent event = events.get(i);
                        if (event.isDead()) {
                            indexCarrierDead = i;
                            break;
                        }
                    }
                } else {
                    return false;
                }
                if (indexCarrierDead != -1) {
                    List<WarlordsDamageHealingFinalEvent> damageEvents = new ArrayList<>();
                    for (int i = indexCarrierFull; i <= indexCarrierDead; i++) {
                        damageEvents.add(events.get(i));
                    }

                    return (float) damageEvents.stream()
                            .filter(WarlordsDamageHealingFinalEvent::isDamageInstance)
                            .map(WarlordsDamageHealingFinalEvent::getValue)
                            .mapToDouble(Float::doubleValue)
                            .sum() >= 2000;
                } else {
                    return false;
                }
            }
    ) {
        @Override
        public boolean checkTeammates() {
            return true;
        }
    },
    SNIPE_SHOT("Snipe Shot",
            "Kill the enemy flag carrier while being at least 30 blocks away from them.",
            GameMode.CAPTURE_THE_FLAG,
            Specializations.PYROMANCER,
            warlordsEntity -> {
                WarlordsDamageHealingFinalEvent lastEvent = warlordsEntity.getSecondStats().getLastEventAsAttacker();
                return lastEvent.isDead() && lastEvent.isHasFlag() && lastEvent.getPlayer().getLocation().distanceSquared(lastEvent.getAttacker().getLocation()) > 900;
            }
    ),
    DUCK_TANK("Duck Tank",
            "Tank 9000 damage without losing health while holding the flag.",
            GameMode.CAPTURE_THE_FLAG,
            Specializations.CRYOMANCER,
            warlordsEntity -> {
                List<WarlordsDamageHealingFinalEvent> events = warlordsEntity.getSecondStats().getEventsAsSelfFromLastSecond(10, WarlordsDamageHealingFinalEvent::isDamageInstance);
                if (events.isEmpty()) return false;
                float lastHealth = events.get(0).getFinalHealth();
                float totalAbsorbed = 0;
                for (WarlordsDamageHealingFinalEvent event : events) {
                    if (event.isHasFlag() && lastHealth == event.getFinalHealth()) {
                        totalAbsorbed += event.getValue();
                        if (totalAbsorbed >= 9000) {
                            return true;
                        }
                    } else {
                        lastHealth = event.getFinalHealth();
                        totalAbsorbed = 0;
                    }
                }
                return false;
            }
    ),
    CLERICAL_PRODIGY("Clerical Prodigy",
            "Heal your carrier for over 80k damage within a game.",
            GameMode.CAPTURE_THE_FLAG,
            Specializations.AQUAMANCER,
            warlordsEntity -> warlordsEntity.getMinuteStats().total().getHealingOnCarrier() >= 80000
    ),
    ASSASSINATE("Assassinate",
            "Land 7 critical hits on the enemy carrier in a row.",
            GameMode.CAPTURE_THE_FLAG,
            Specializations.ASSASSIN,
            warlordsEntity -> {
                List<WarlordsDamageHealingFinalEvent> events = warlordsEntity.getSecondStats().getEventsAsAttackerFromLastSecond(10);
                int critsOnCarrier = 0;
                for (WarlordsDamageHealingFinalEvent event : events) {
                    if (event.isCrit() && event.isHasFlag()) {
                        critsOnCarrier++;
                        if (critsOnCarrier == 7) {
                            return true;
                        }
                    } else {
                        critsOnCarrier = 0;
                    }
                }
                return false;
            }
    ),
    SILENCE_PEON("Silence, peon!",
            "Kill the enemy flag carrier (that you silenced) while the silence duration is still up.",
            GameMode.CAPTURE_THE_FLAG,
            Specializations.VINDICATOR,
            warlordsEntity -> {
                WarlordsDamageHealingFinalEvent lastDamageEvent = warlordsEntity.getSecondStats().getLastEventAsAttacker();
                if (lastDamageEvent.isDead()) {
                    return new CooldownFilter<>(lastDamageEvent.getPlayer(), RegularCooldown.class)
                            .filterCooldownFrom(warlordsEntity)
                            .filterCooldownClassAndMapToObjectsOfClass(SoulShackle.class)
                            .findAny()
                            .isPresent();
                }
                return false;
            }
    ),
    LYCHEESIS("Lycheesis",
            "Generate over 3k healing by inflicting one instance of LEECH on the enemy flag carrier.",
            GameMode.CAPTURE_THE_FLAG,
            Specializations.APOTHECARY,
            warlordsEntity -> {
                return PlayerFilter.playingGame(warlordsEntity.getGame())
                        .enemiesOf(warlordsEntity)
                        .filter(enemy -> new CooldownFilter<>(enemy, RegularCooldown.class)
                                .filterCooldownFrom(warlordsEntity)
                                .filterCooldownClassAndMapToObjectsOfClass(ImpalingStrike.class)
                                .anyMatch(impalingStrike -> impalingStrike.getHealingDoneFromEnemyCarrier() >= 3000))
                        .findAny()
                        .isPresent();
            }
    ),
    EXTENDED_COMBAT("Extended Combat",
            "Stay in combat for over 40 seconds and deal 10k damage to the enemy carrier.",
            GameMode.CAPTURE_THE_FLAG,
            Specializations.BERSERKER,
            warlordsEntity -> {
                List<WarlordsDamageHealingFinalEvent> events = warlordsEntity.getSecondStats().getEventsAsAttackerFromLastSecond(40);
                float totalDamageToCarrier = 0;
                for (WarlordsDamageHealingFinalEvent event : events) {
                    if (event.isHasFlag()) {
                        if (event.isAttackerInCombat()) {
                            totalDamageToCarrier += event.getValue();
                        } else {
                            return false;
                        }
                    }
                }

                return totalDamageToCarrier >= 10000;
            }
    ),
    SPLIT_SECOND("Split Second",
            "Prevent over 2k damage dealt to the flag carrier within 1s of the ability activating.",
            GameMode.CAPTURE_THE_FLAG,
            Specializations.DEFENDER,
            warlordsEntity -> {
                return new CooldownFilter<>(warlordsEntity, RegularCooldown.class)
                        .filterCooldownFrom(warlordsEntity)
                        .filter(regularCooldown -> regularCooldown.getStartingTicks() - regularCooldown.getTicksLeft() <= 20)
                        .filterCooldownClassAndMapToObjectsOfClass(Intervene.class)
                        .anyMatch(intervene -> intervene.getDamagePrevented() >= 2000);
            }
    ),
    ORBIFICATOR("Orbificator",
            "Return the flag while being popped from your Undying Army.",
            GameMode.CAPTURE_THE_FLAG,
            Specializations.REVENANT,
            warlordsEntity -> {
                WarlordsDamageHealingFinalEvent lastDamageEvent = warlordsEntity.getSecondStats().getLastEventAsAttacker();
                if (lastDamageEvent.isDead() && lastDamageEvent.isHasFlag()) {
                    return new CooldownFilter<>(warlordsEntity, RegularCooldown.class)
                            .filterCooldownClassAndMapToObjectsOfClass(UndyingArmy.class)
                            .anyMatch(undyingArmy -> undyingArmy.getPlayersPopped().getOrDefault(warlordsEntity, false));
                }
                return false;
            }
    ),
    REVENGE_BLAST("Revenge Blast",
            "Kill 3 enemies within 5s of your flag carrier dying. ",
            GameMode.CAPTURE_THE_FLAG,
            Specializations.AVENGER,
            warlordsEntity -> {
                boolean carrierDeadLast5Seconds = false;
                for (WarlordsEntity player : PlayerFilter.playingGame(warlordsEntity.getGame())
                        .teammatesOf(warlordsEntity)
                        .excluding(warlordsEntity)
                        .stream()
                        .collect(Collectors.toList())
                ) {
                    if (player.getSecondStats().getEventsAsSelfFromLastSecond(5, WarlordsDamageHealingFinalEvent::isHasFlag)
                            .stream()
                            .anyMatch(WarlordsDamageHealingFinalEvent::isDead)
                    ) {
                        carrierDeadLast5Seconds = true;
                        break;
                    }
                }
                if (carrierDeadLast5Seconds) {
                    return warlordsEntity.getSecondStats().getEventsAsAttackerFromLastSecond(5)
                            .stream()
                            .filter(WarlordsDamageHealingFinalEvent::isDead)
                            .count() >= 3;
                } else {
                    return false;
                }
            }
    ),
    HOUR_OF_RECKONING("Hour of Reckoning",
            "Kill the enemy carrier while 4 or more allies are affected by your Inspiring Presence.",
            GameMode.CAPTURE_THE_FLAG,
            Specializations.CRUSADER,
            warlordsEntity -> {
                WarlordsDamageHealingFinalEvent lastDamageEvent = warlordsEntity.getSecondStats().getLastEventAsAttacker();
                if (lastDamageEvent.isDead() && lastDamageEvent.isHasFlag()) {
                    return new CooldownFilter<>(warlordsEntity, RegularCooldown.class)
                            .filterCooldownClassAndMapToObjectsOfClass(InspiringPresence.class)
                            .anyMatch(inspiringPresence -> inspiringPresence.getPlayersAffected().size() >= 4);
                }
                return false;
            }
    ),
    TALENT_SHREDDER("Talent Shredder",
            "Deal 3k damage to the enemy carrier while they have an active shield/damage reduction.",
            GameMode.CAPTURE_THE_FLAG,
            Specializations.PROTECTOR,
            warlordsEntity -> {
                List<WarlordsDamageHealingFinalEvent> events = warlordsEntity.getSecondStats().getEventsAsAttackerFromLastSecond(3);
                WarlordsEntity carrier = null;
                int index = -1;
                for (int i = 0; i < events.size(); i++) {
                    WarlordsDamageHealingFinalEvent event = events.get(i);
                    if (event.isHasFlag() && event.getPlayerCooldowns().stream()
                            .map(WarlordsDamageHealingFinalEvent.CooldownRecord::getAbstractCooldown)
                            .filter(RegularCooldown.class::isInstance)
                            .map(RegularCooldown.class::cast)
                            .anyMatch(regularCooldown ->
                                    regularCooldown.getCooldownObject() instanceof ArcaneShield ||
                                            regularCooldown.getCooldownObject() instanceof IceBarrier ||
                                            regularCooldown.getCooldownObject() instanceof LastStand
                            )
                    ) {
                        carrier = event.getPlayer();
                        index = i;
                        break;
                    }
                }

                if (carrier != null) {
                    int totalDamage = 0;
                    for (int i = index; i < events.size(); i++) {
                        WarlordsDamageHealingFinalEvent event = events.get(i);
                        if (event.getPlayer() == carrier && event.isHasFlag()) {
                            totalDamage += event.getValue();
                        }
                    }

                    return totalDamage >= 3000;
                } else {
                    return false;
                }
            }
    ),
    ROADBLOCK("Roadblock?!",
            "Proc your Capacitor Totem three (or more) times after your carrier passes through the totem.",
            GameMode.CAPTURE_THE_FLAG,
            Specializations.THUNDERLORD,
            warlordsEntity -> {
                WarlordsDamageHealingFinalEvent lastDamageEvent = warlordsEntity.getSecondStats().getLastEventAsAttacker();
                return lastDamageEvent.getAttackerCooldowns().stream()
                        .map(WarlordsDamageHealingFinalEvent.CooldownRecord::getAbstractCooldown)
                        .filter(RegularCooldown.class::isInstance)
                        .map(RegularCooldown.class::cast)
                        .filter(regularCooldown -> Objects.equals(regularCooldown.getCooldownClass(), CapacitorTotem.class))
                        .map(regularCooldown -> ((CapacitorTotem) regularCooldown.getCooldownObject()))
                        .anyMatch(capacitorTotem -> capacitorTotem.getNumberOfProcsAfterCarrierPassed() >= 3);
            }
    ),
    PERSISTENT_THREAT("Persistent Threat",
            "Proc soulbinding healing/cooldown reduction 10 times on the enemy carrier within 20 seconds.",
            GameMode.CAPTURE_THE_FLAG,
            Specializations.SPIRITGUARD,
            warlordsEntity -> {
                List<WarlordsDamageHealingFinalEvent> events = warlordsEntity.getSecondStats().getEventsAsAttackerFromLastSecond(20);
                for (WarlordsDamageHealingFinalEvent event : events) {
                    if (event.isHasFlag()) {
                        return event.getAttackerCooldowns().stream()
                                .map(WarlordsDamageHealingFinalEvent.CooldownRecord::getAbstractCooldown)
                                .filter(PersistentCooldown.class::isInstance)
                                .map(PersistentCooldown.class::cast)
                                .filter(persistentCooldown -> Objects.equals(persistentCooldown.getCooldownClass(), Soulbinding.class))
                                .map(persistentCooldown -> ((Soulbinding) persistentCooldown.getCooldownObject()))
                                .anyMatch(soulbinding -> soulbinding.getAllProcedPlayers().stream()
                                        .filter(wp -> wp == event.getPlayer())
                                        .count() >= 10
                                );
                    }
                }
                return false;
            }
    ),
    WHERE_ARE_YOU_GOING("Where are you going?",
            "Kill the enemy flag carrier after landing 5 or more abilities on them.",
            GameMode.CAPTURE_THE_FLAG,
            Specializations.EARTHWARDEN,
            warlordsEntity -> {
                List<WarlordsDamageHealingFinalEvent> events = warlordsEntity.getSecondStats().getEventsAsAttackerFromLastSecond(10);
                int indexCarrier = -1;
                int indexCarrierKilled = -1;
                WarlordsEntity carrier = null;
                for (int i = 0; i < events.size(); i++) {
                    if (events.get(i).isHasFlag()) {
                        indexCarrier = i;
                        carrier = events.get(i).getPlayer();
                        break;
                    }
                }

                if (indexCarrier != -1) {
                    for (int i = 0; i < events.size(); i++) {
                        WarlordsDamageHealingFinalEvent event = events.get(i);
                        if (event.getPlayer().equals(carrier) && event.isDead()) {
                            indexCarrierKilled = i;
                            break;
                        }
                    }
                } else {
                    return false;
                }

                if (indexCarrierKilled != -1) {
                    WarlordsEntity finalCarrier = carrier;
                    int numberOfAbilityAttackers = (int) events.subList(indexCarrier, indexCarrierKilled).stream()
                            .filter(warlordsDamageHealingFinalEvent -> warlordsDamageHealingFinalEvent.getPlayer().equals(finalCarrier))
                            .filter(warlordsDamageHealingFinalEvent -> !warlordsDamageHealingFinalEvent.getAbility().isEmpty())
                            .count();
                    return numberOfAbilityAttackers >= 5;
                } else {
                    return false;
                }
            }
    ),
    CONTROLLED_FURY("Controlled Fury",
            "Reduce the damage of Ghoulcaller's Fury by the maximum amount three times in a row.",
            GameMode.WAVE_DEFENSE,
            null,
            warlordsEntity -> {
                return PlayerFilterGeneric.playingGameWarlordsNPCs(warlordsEntity.getGame())
                        .filter(warlordsNPC -> warlordsNPC.getMob() instanceof Ghoulcaller)
                        .stream()
                        .map(warlordsNPC -> (Ghoulcaller) warlordsNPC.getMob())
                        .anyMatch(ghoulcaller -> ghoulcaller.getTimesInARowDamageMaxReduced() >= 3);
            }
    ) {
        @Override
        public boolean autoGiveToTeammates() {
            return true;
        }
    },
    FISSURED_END("Fissured End",
            "Get teamwiped by Narmer's mega-earthquake.",
            GameMode.WAVE_DEFENSE,
            null,
            warlordsEntity -> {
                return PlayerFilterGeneric.playingGame(warlordsEntity.getGame())
                        .teammatesOf(warlordsEntity)
                        .stream()
                        .allMatch(entity -> entity.getSecondStats().getLastEventAsSelf().isDead());

            }
    ) {
        @Override
        public boolean autoGiveToTeammates() {
            return true;
        }
    },
    SIRE("Sire?",
            "Trigger Boltaro's splitting phase without killing any other mobs.",
            GameMode.WAVE_DEFENSE,
            null,
            warlordsEntity -> {
                return true;
            }
    ),
    NEAR_DEATH_EXPERIENCE("Near-Death Experience",
            "Defeat Narmer after triggering his mega-earthquake twice.",
            GameMode.WAVE_DEFENSE,
            null,
            warlordsEntity -> {
                return PlayerFilterGeneric.playingGameWarlordsNPCs(warlordsEntity.getGame())
                        .filter(warlordsNPC -> warlordsNPC.getMob() instanceof Narmer)
                        .stream()
                        .map(warlordsNPC -> (Narmer) warlordsNPC.getMob())
                        .anyMatch(narmer -> narmer.getTimesMegaEarthQuakeActivated() >= 2);
            }
    ) {
        @Override
        public boolean autoGiveToTeammates() {
            return true;
        }
    },
    LASER_FOCUSED("Laser Focused",
            "Land 10 critical hits in a row while Inferno is active.",
            GameMode.WAVE_DEFENSE,
            Specializations.PYROMANCER,
            warlordsEntity -> {
                List<WarlordsDamageHealingFinalEvent> lastEventsAsAttacker = warlordsEntity.getSecondStats().getLastEventsAsAttacker(10, WarlordsDamageHealingFinalEvent::isDamageInstance);
                return lastEventsAsAttacker.size() >= 10 && lastEventsAsAttacker
                        .stream()
                        .allMatch(event -> event.getAttackerCooldowns().stream().anyMatch(cooldownRecord -> Objects.equals(cooldownRecord.getAbstractCooldown().getCooldownClass(), Inferno.class)) && event.isCrit());
            }
    ),
    DUCK_TANK_PVE("Duck Tank",
            "Tank 8,000 damage within the duration of 1 Ice Barrier.",
            GameMode.WAVE_DEFENSE,
            Specializations.CRYOMANCER,
            warlordsEntity -> {
                WarlordsDamageHealingFinalEvent lastEventAsSelf = warlordsEntity.getSecondStats().getLastEventAsSelf();
                for (WarlordsDamageHealingFinalEvent.CooldownRecord playerCooldown : lastEventAsSelf.getPlayerCooldowns()) {
                    if (Objects.equals(playerCooldown.getAbstractCooldown().getCooldownClass(), IceBarrier.class)) {
                        int secondsLeft = playerCooldown.getTicksLeft() / 20;
                        int totalDamage = 0;
                        for (WarlordsDamageHealingFinalEvent event : warlordsEntity.getSecondStats().getEventsAsSelfFromLastSecond(secondsLeft, WarlordsDamageHealingFinalEvent::isDamageInstance)) {
                            if (event.getPlayerCooldowns().stream().anyMatch(cooldownRecord -> Objects.equals(cooldownRecord.getAbstractCooldown().getCooldownClass(), IceBarrier.class))) {
                                totalDamage += event.getValue();
                            }
                        }
                        return totalDamage >= 8000;
                    }
                }
                return false;
            }
    ),
    CLEANSING_RITUAL("Cleansing Ritual",
            "Clear 7 debuffs in 1 Water Breath activation.",
            GameMode.WAVE_DEFENSE,
            Specializations.AQUAMANCER,
            WARLORDS_ENTITY -> {
                return true; //logic in WaterBreath.java
            }
    ),
    LAWNMOWER("Lawnmower",
            "Land 40 strikes and kill 12 enemies in 1 wrath activation.",
            GameMode.WAVE_DEFENSE,
            Specializations.AVENGER,
            warlordsEntity -> {
                return new CooldownFilter<>(warlordsEntity, RegularCooldown.class)
                        .filterCooldownClassAndMapToObjectsOfClass(AvengersWrath.class)
                        .anyMatch(avengersWrath -> avengersWrath.getPlayersStruckDuringWrath() >= 40 && avengersWrath.getPlayersKilledDuringWrath() >= 12);
            }
    ),
    PORTABLE_ENERGIZER("Portable Energizer",
            "Provide 400 total energy to teammates (both strike and presence) within the duration of 1 Inspiring Presence.",
            GameMode.WAVE_DEFENSE,
            Specializations.CRUSADER,
            warlordsEntity -> {
                return new CooldownFilter<>(warlordsEntity, RegularCooldown.class)
                        .filterCooldownClassAndMapToObjectsOfClass(InspiringPresence.class)
                        .anyMatch(inspiringPresence -> inspiringPresence.getEnergyGivenFromStrikeAndPresence() >= 800);
            }
    ),
    CROWN_OF_RESURGENCE("Crown of Resurgence",
            "Heal allies for 15,000 within the duration of 1 Hammer/Crown of Light.",
            GameMode.WAVE_DEFENSE,
            Specializations.PROTECTOR,
            warlordsEntity -> {
                return new CooldownFilter<>(warlordsEntity, RegularCooldown.class)
                        .filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.class)
                        .anyMatch(hammerOfLight -> hammerOfLight.getAmountHealed() >= 15000);
            }
    ),
    BLOODSOAKED_CHAMPION("Bloodsoaked Champion",
            "Heal yourself with Blood Lust for 18,000 within the duration of 1 Blood Lust.",
            GameMode.WAVE_DEFENSE,
            Specializations.BERSERKER,
            warlordsEntity -> {
                return new CooldownFilter<>(warlordsEntity, RegularCooldown.class)
                        .filterCooldownClassAndMapToObjectsOfClass(BloodLust.class)
                        .anyMatch(bloodLust -> bloodLust.getAmountHealed() >= 18000);
            }
    ),
    HARDENED_SCALES("Hardened Scales",
            "Prevent 30,000 damage to your allies within the duration of 1 Last Stand.",
            GameMode.WAVE_DEFENSE,
            Specializations.DEFENDER,
            warlordsEntity -> {
                return true;
            }
    ),
    ORBIFICATION("Orbification",
            "Create 50 Orbs within the duration of 1 Orbs of Life.",
            GameMode.WAVE_DEFENSE,
            Specializations.REVENANT,
            warlordsEntity -> {
                return true;
            }
    ),
    LIGHTNING_EXECUTION("Lightning Executioner",
            "Kill 15 mobs with Capacitor Totem landing the final hit within the duration of 1 Capacitor Totem.",
            GameMode.WAVE_DEFENSE,
            Specializations.THUNDERLORD,
            warlordsEntity -> {
                return true;
            }
    ),
    RETRIBUTION_OF_THE_DEAD("Retribution of the Deader",
            "Deal an instance of 5,000 damage to over 5 targets with Death's Debt.",
            GameMode.WAVE_DEFENSE,
            Specializations.SPIRITGUARD,
            warlordsEntity -> {
                return true;
            }
    ),

    ;

    //TODO test EXTENDED_COMBAT
    public static final ChallengeAchievements[] DAMAGE_ACHIEVEMENTS_ATTACKER = new ChallengeAchievements[]{
            BLITZKRIEG, SNIPE_SHOT, REVENGE_BLAST, ROADBLOCK, LASER_FOCUSED, LAWNMOWER, BLOODSOAKED_CHAMPION
    };
    public static final ChallengeAchievements[] DAMAGE_ACHIEVEMENTS_ATTACKER_FLAG = new ChallengeAchievements[]{
            ASSASSINATE, SILENCE_PEON, ORBIFICATOR, HOUR_OF_RECKONING, TALENT_SHREDDER, PERSISTENT_THREAT, WHERE_ARE_YOU_GOING, EXTENDED_COMBAT
    };
    public static final ChallengeAchievements[] DAMAGE_ACHIEVEMENTS_SELF = new ChallengeAchievements[]{
            DUCK_TANK, SPLIT_SECOND, DUCK_TANK_PVE
    };
    public static final ChallengeAchievements[] HEALING_ACHIEVEMENTS_ATTACKER = new ChallengeAchievements[]{
            LYCHEESIS, CROWN_OF_RESURGENCE
    };
    public static final ChallengeAchievements[] HEALING_ACHIEVEMENTS_ATTACKER_FLAG = new ChallengeAchievements[]{
            REJUVENATION, CLERICAL_PRODIGY
    };

    public final String name;
    public final String description;
    public final GameMode gameMode;
    public final Specializations spec;
    public final Predicate<WarlordsEntity> warlordsEntityPredicate;

    ChallengeAchievements(String name, String description, GameMode gameMode, Specializations spec, Predicate<WarlordsEntity> warlordsEntityPredicate) {
        this.name = name;
        this.description = description;
        this.gameMode = gameMode;
        this.spec = spec;
        this.warlordsEntityPredicate = warlordsEntityPredicate;
    }

    public static void checkForAchievement(WarlordsEntity player, ChallengeAchievements achievement) {
        if (achievement.gameMode == player.getGame().getGameMode() &&
                achievement.spec == player.getSpecClass() &&
                achievement.warlordsEntityPredicate.test(player)
        ) {
            if (achievement.autoGiveToTeammates()) {
                ChallengeAchievements.giveTeammatesSameAchievement(player, achievement);
            } else if (achievement.checkTeammates()) {
                ChallengeAchievements.checkTeammatesForSameAchievement(player, achievement);
            } else {
                //if(!player.hasAchievement(achievement)) {
                player.unlockAchievement(achievement);
                //}
            }
        }
    }

    public static void giveTeammatesSameAchievement(WarlordsEntity player, ChallengeAchievements achievement) {
        player.getGame().warlordsPlayers()
                .filter(warlordsPlayer -> warlordsPlayer.getTeam() == player.getTeam())
                //.filter(warlordsEntity -> !warlordsEntity.hasAchievement(achievement))
                .forEachOrdered(warlordsEntity -> warlordsEntity.unlockAchievement(achievement));
    }

    public static void checkTeammatesForSameAchievement(WarlordsEntity player, ChallengeAchievements achievement) {
        player.getGame().warlordsPlayers()
                .filter(warlordsPlayer -> warlordsPlayer.getTeam() == player.getTeam())
                //.filter(warlordsEntity -> !warlordsEntity.hasAchievement(achievement))
                .filter(achievement.warlordsEntityPredicate)
                .forEachOrdered(warlordsEntity -> warlordsEntity.unlockAchievement(achievement));
    }

    public boolean checkTeammates() {
        return false;
    }

    public boolean autoGiveToTeammates() {
        return false;
    }

    @Override
    public void sendAchievementUnlockMessage(Player player) {
        TextComponent message = new TextComponent(ChatColor.GREEN + ">>  Achievement Unlocked: " + ChatColor.GOLD + name + ChatColor.GREEN + "  <<");
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(WordWrap.wrapWithNewlineWithColor(description, 200, ChatColor.GREEN)).create()));
        ChatUtils.sendMessageToPlayer(player, Collections.singletonList(message), ChatColor.GREEN, true);
    }

    @Override
    public void sendAchievementUnlockMessageToOthers(WarlordsEntity warlordsEntity) {
        TextComponent message = new TextComponent(ChatColor.GREEN + ">>  " + ChatColor.AQUA + warlordsEntity.getName() + ChatColor.GREEN + " unlocked: " + ChatColor.GOLD + name + ChatColor.GREEN + "  <<");
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(WordWrap.wrapWithNewlineWithColor(description, 200, ChatColor.GREEN)).create()));
        warlordsEntity.getGame().warlordsPlayers()
                //.filter(wp -> wp.getTeam() == warlordsEntity.getTeam())
                .filter(wp -> wp != warlordsEntity)
                .filter(wp -> wp.getEntity() instanceof Player)
                .map(wp -> (Player) wp.getEntity())
                .forEachOrdered(player -> ChatUtils.sendMessageToPlayer(player, Collections.singletonList(message), ChatColor.GREEN, true));
    }

    public static class ChallengeAchievementRecord extends AbstractAchievementRecord<ChallengeAchievements> {

        public ChallengeAchievementRecord() {
        }

        public ChallengeAchievementRecord(ChallengeAchievements achievement) {
            super(achievement);
        }

        public ChallengeAchievementRecord(ChallengeAchievements achievement, Instant date) {
            super(achievement, date);
        }

        @Override
        public String getName() {
            return getAchievement().name;
        }

        @Override
        public String getDescription() {
            return getAchievement().description;
        }

        @Override
        public GameMode getGameMode() {
            return getAchievement().gameMode;
        }

        @Override
        public ChallengeAchievements[] getAchievements() {
            return ChallengeAchievements.values();
        }

    }
}
