package com.ebicep.warlords.database.newdb.repositories.player.pojos;


public class DatabaseWarrior extends DatabaseWarlordsClass {

    private DatabaseSpecialization berserker = new DatabaseSpecialization();
    private DatabaseSpecialization defender = new DatabaseSpecialization();
    private DatabaseSpecialization revenant = new DatabaseSpecialization();

    public DatabaseWarrior() {
        super();
    }
}
