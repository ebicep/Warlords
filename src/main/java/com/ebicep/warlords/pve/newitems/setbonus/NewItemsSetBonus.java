package com.ebicep.warlords.pve.newitems.setbonus;

import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.NewItemsSlot;
import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.pve.newitems.setbonus.sets.*;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.java.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;

public enum NewItemsSetBonus implements SetBonus {

    AMBULANCE(new Ambulance()),
    BATTERY(new Battery()),
    BELLICOSE(new Bellicose()),
    BLOODLETTING_BLADE(new BloodlettingBlade()),
    BRITTLE_CROWN(new BrittleCrown()),
    BULWARK(new Bulwark()),
    CENTURION(new Centurion()),
    CHARM(new Charm()),
    CROWN_OF_THORNS(new CrownOfThorns()),
    DETONATOR(new Detonator()),
    DURABLE(new Durable()),
    ECHO_OF_RUIN(new EchoOfRuin()),
    ENERGIZE(new Energize()),
    ENCUMBER(new Encumber()),
    EXERGIES(new Exergis()),
    FORSAKEN_FLUX(new ForsakenFlux()),
    FROSTVEIL(new Frostveil()),
    GAMBLER(new Gambler()),
    GHOSTLY(new Ghostly()),
    GENESIS(new Genesis()),
    GRAVEMIND(new Gravemind()),
    HAND_OF_THE_CORPSE(new HandOfTheCorpse()),
    HEART_OF_GLASS(new HeartOfGlass()),
    HOURGLASS(new Hourglass()),
    ILLUMINATED_PRISM(new IlluminatedPrism()),
    INNER_FLAME(new InnerFlame()),
    IRON_CHAINS(new IronChains()),
    MADRAKAN(new Madrakan()),
    MOONVEIL(new Moonveil()),
    MOURNSONG_VIAL(new MournsongVial()),
    OATHKEEPER(new Oathkeeper()),
    OBELISK(new Obelisk()),
    OCEAN_LIGHT(new OceanLight()),
    OLYMPIC(new Olympic()),
    OMAMORI(new Omamori()),
    OMEN(new Omen()),
    OVERFLOW(new Overflow()),
    PHOENIX(new Phoenix()),
    PULSE_OF_AEONS(new PulseOfAeons()),
    RANDOM_EPIC(new RandomEpic()),
    RANDOM_RARE(new RandomRare()),
    RANDOM_COMMON(new RandomCommon()),
    SACRIFICE(new Sacrifice()),
    SANGUINEOUS(new Sanguineous()),
    SHARPSHOOTER(new Sharpshooter()),
    SHIELD_GATE(new ShieldGate()),
    SOOTHSAYER(new Soothsayer()),
    SOULFLAME(new Soulflame()),
    SOULFORGED(new Soulforged()),
    SPELUNKER(new Spelunker()),
    STONELASH(new Stonelash()),
    STRETCH(new Stretch()),
    SUMMONER(new Summoner()),
    SYNAPTIC_OVERLOAD(new SynapticOverload()),
    THRONE_OF_THE_UNDEAD(new ThroneOfTheUndead()),
    TRANSFERENCE(new Transference()),
    VIAL(new Vial()),
    VOIDCARVER(new Voidcarver()),
    WELLSPRING(new Wellspring()),

    ;

    public static final NewItemsSetBonus[] VALUES = values();
    public static Map<NewItemTier, Set<NewItemsSetBonus>> BY_TIER;
    private final SetBonus setBonus;

    NewItemsSetBonus(SetBonus setBonus) {
        this.setBonus = setBonus;
    }

    @Override
    public boolean isNoBonus() {
        return setBonus.isNoBonus();
    }

    @Override
    public NewItemTier getTier() {
        return setBonus.getTier();
    }

    @Override
    public String getName() {
        return setBonus.getName();
    }

    @Override
    public List<NewItemsSlot> getSlots() {
        return setBonus.getSlots();
    }

    @Override
    public Map<NewItemAttribute, Float> getAttributes() {
        return setBonus.getAttributes();
    }

    @Override
    public Map<NewItemAttribute, Pair<Float, Float>> bonusAttributeRanges() {
        return setBonus.bonusAttributeRanges();
    }

    @Override
    public void init() {
        setBonus.init();
    }

    @Override
    public Bonus create() {
        return setBonus.create();
    }

    @Override
    public String getConfigFieldName() {
        return setBonus.getConfigFieldName();
    }

    @Override
    public List<Object> getVariables() {
        return setBonus.getVariables();
    }

    public SetBonus getSetBonus() {
        return setBonus;
    }

    @Override
    public Map<Spendable, Long> rerollCost() {
        return setBonus.rerollCost();
    }

    @Override
    public void setRerollCost(Map<Spendable, Long> rerollCost) {
        setBonus.setRerollCost(rerollCost);
    }

    @Override
    public Map<Spendable, Long> lockScrollRerollCost() {
        return setBonus.lockScrollRerollCost();
    }

    @Override
    public void setLockScrollRerollCost(Map<Spendable, Long> lockScrollRerollCost) {
        setBonus.setLockScrollRerollCost(lockScrollRerollCost);
    }

}
