package mariomonday.backend.database.schema;

import java.time.Instant;
import java.util.Set;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * A tournament, containing multiple games
 */
@Data
public class Bracket {

    /**
     * ID for the bracket
     */
    @Id
    private String id;

    /**
     * The moment the bracket started
     */
    private Instant date;

    /**
     * The number of rounds in the bracket
     */
    private int rounds;

    /**
     * The winners
     */
    private Set<String> winners;

    /**
     * The type of game this bracket was for
     */
    private GameType gameType;

    /**
     * The players who participated in this bracket
     */
    private Set<String> players;
}
