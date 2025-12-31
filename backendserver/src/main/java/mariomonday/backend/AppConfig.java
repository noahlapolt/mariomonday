package mariomonday.backend;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.time.Clock;
import mariomonday.backend.managers.ratingcalculators.AbstractEloManager;
import mariomonday.backend.managers.ratingcalculators.IndifferentEloManager;
import mariomonday.backend.managers.seeders.AbstractSeeder;
import mariomonday.backend.managers.seeders.LocalRandomSeeder;
import mariomonday.backend.managers.tournamentcreators.AbstractBracketCreator;
import mariomonday.backend.managers.tournamentcreators.MaxSetsStrategyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class AppConfig {

  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }

  @Bean
  public AbstractBracketCreator bracketCreator(Clock clock) {
    return new MaxSetsStrategyCreator(clock);
  }

  @Bean
  public AbstractSeeder seeder() {
    return new LocalRandomSeeder();
  }

  @Bean
  public AbstractEloManager eloManager() {
    return new IndifferentEloManager();
  }

  @Bean
  MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
    return new MongoTransactionManager(dbFactory);
  }

  @Bean
  MongoTemplate mongoTemplate(MongoDatabaseFactory dbFactory) {
    return new MongoTemplate(dbFactory);
  }

  @Bean
  public MongoClient mongoClient() {
    return MongoClients.create("mongodb://localhost:27017?replicaSet=rs0");
  }
}
