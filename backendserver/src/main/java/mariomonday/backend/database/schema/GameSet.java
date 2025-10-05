package mariomonday.backend.database.schema;

import java.util.Set;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A set of games within a bracket, where the winner moves on
 */
@Data
@Document
public class GameSet {

    @Id
    private String id;

    /**
     * The ID of the bracket
     */
    private String bracketId;

    /**
     * The round this game took place in
     */
    private int round;

    /**
     * The number of games in the set
     */
    private int gameCount;

    /**
     * The winners of the set
     */
    private Set<String> winners;

    /**
     * The players in the set
     */
    private Set<String> players;
}
