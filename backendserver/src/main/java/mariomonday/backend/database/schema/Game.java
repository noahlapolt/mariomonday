package mariomonday.backend.database.schema;

import org.springframework.data.annotation.Id;
import lombok.Data;

import java.util.Set;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A game
 */
@Data
@Document
public class Game {

    /**
     * The game's ID
     */
    @Id
    private String id;

    /**
     * The ID of the set this game belongs to
     */
    private String setId;

    /**
     * The player IDs of the winners of the game
     */
    private Set<String> winners;

    /**
     * The player IDs of the players in the game
     */
    private Set<String> players;
}