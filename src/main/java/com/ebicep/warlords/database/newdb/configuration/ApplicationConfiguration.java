package com.ebicep.warlords.database.newdb.configuration;

import com.ebicep.warlords.database.newdb.DatabaseManager;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.annotation.Nonnull;

@Configuration
@ComponentScan(basePackages = "com.ebicep.warlords.database.newdb")
@EnableMongoRepositories({"com.ebicep.warlords.database.newdb.repositories"})
public class ApplicationConfiguration extends AbstractMongoClientConfiguration {

    public static String key;

    @Nonnull
    @Bean
    public MongoClient mongoClient() {
        System.out.println("Getting mongoClient");
        MongoClient mongoClient = MongoClients.create(key);
        DatabaseManager.mongoClient = mongoClient;
        DatabaseManager.warlordsDatabase = mongoClient.getDatabase("Warlords");
        DatabaseManager.gamesInformation = DatabaseManager.warlordsDatabase.getCollection("Warlords_Information");
        return mongoClient;
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }

    @Nonnull
    @Override
    protected String getDatabaseName() {
        return "Warlords";
    }

    @Override
    public boolean autoIndexCreation() {
        return true;
    }

}
