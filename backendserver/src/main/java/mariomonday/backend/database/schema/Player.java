package mariomonday.backend.database.schema;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A single player, consistent across games
 */
@Data
@Document
public class Player {

    /** Player id */
    @Id
    private String id;

    /**
     * Player name, must be unique
     */
    @Indexed(unique = true)
    private String name;
}