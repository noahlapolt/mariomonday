package mariomonday.backend.schema;

import org.springframework.data.annotation.Id;
import lombok.Data;

import java.util.Set;

/**
 * A game
 */
@Data
public class Game {

    /**
     * The game's ID
     */
    @Id
    private String id;

    /**
     * The ID of the bracket this game belongs to
     */
    private String bracket;

    /**
     * The player IDs of the winners of the game
     */
    private Set<String> winners;

    /**
     * The player IDs of the players in the game
     */
    private Set<String> players;

    /**
     * The round this game took place in
     */
    private int round;
}