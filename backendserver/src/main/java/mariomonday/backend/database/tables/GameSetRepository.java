package mariomonday.backend.database.tables;

import java.util.List;
import mariomonday.backend.database.schema.GameSet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameSetRepository extends MongoRepository<GameSet, String> {}
