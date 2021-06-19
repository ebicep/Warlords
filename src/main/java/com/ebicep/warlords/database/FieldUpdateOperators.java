package com.ebicep.warlords.database;

public enum FieldUpdateOperators {

    CURRENTDATE("$currentDate"),
    INCREMENT("$inc"),
    MINIMIZE("$min"),
    MAXIMIZE("$max"),
    MULTIPLY("$mul"),
    RENAME("$rename"),
    SET("$set"),
    SET_ON_INSERT("$setOnInsert"),
    UNSET("$unset");

    public String operator;

    FieldUpdateOperators(String operator) {
        this.operator = operator;
    }
}