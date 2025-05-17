package com.ebicep.warlords.database.repositories.config;

record Result<T>(T value, ValueResult valueResult) {

    public Result(ValueResult valueResult) {
        this(null, valueResult);
    }

}
