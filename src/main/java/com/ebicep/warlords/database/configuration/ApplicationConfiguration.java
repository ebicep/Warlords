package com.ebicep.warlords.database.configuration;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.FutureMessageManager;
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
@ComponentScan(basePackages = "com.ebicep.warlords.database")
@EnableMongoRepositories({"com.ebicep.warlords.database.repositories"})
public class ApplicationConfiguration extends AbstractMongoClientConfiguration {

    public static String key;

    @Nonnull
    @Bean
    @Override
    public MongoClient mongoClient() {
        System.out.println("Getting mongoClient");
        MongoClient mongoClient = MongoClients.create(key);
        DatabaseManager.mongoClient = mongoClient;
        DatabaseManager.warlordsDatabase = mongoClient.getDatabase("Warlords");
        DatabaseManager.gamesInformation = DatabaseManager.warlordsDatabase.getCollection("Warlords_Information");
        FutureMessageManager.futureMessages = DatabaseManager.warlordsDatabase.getCollection("Future_Messages");

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
