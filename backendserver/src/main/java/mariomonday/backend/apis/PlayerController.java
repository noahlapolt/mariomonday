package mariomonday.backend.apis;

import java.util.List;
import mariomonday.backend.database.schema.Player;
import mariomonday.backend.database.tables.PlayerRepository;
import mariomonday.backend.error.exceptions.AlreadyExistsException;
import mariomonday.backend.error.exceptions.InvalidRequestException;
import mariomonday.backend.error.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * API endpoint for everything related to players
 */
@RestController
public class PlayerController {

    /**
     * Player table
     */
    @Autowired
    PlayerRepository playerRepo;

    /**
     * Mongo template object, used for updates/deletions that require more precision
     * than the Spring Repository infrastructure provides
     */
    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * Get all players
     */
    @GetMapping("/player")
    List<Player> getAllPlayers() {
        return playerRepo.findAll();
    }

    /**
     * Get information about a single player with the given ID
     * @param playerId The ID of the player to learn about
     */
    @GetMapping("/player/{playerId}")
    Player getPlayer(@PathVariable String playerId) {
        return playerRepo.findById(playerId)
            .orElseThrow(() -> new NotFoundException("Player not found with id: " + playerId));
    }

    /**
     * Create a new player
     * @param newPlayer The new player to create. Does not need to include the ID
     */
    @PostMapping("/player")
    void postPlayer(@RequestBody Player newPlayer) {
        if (newPlayer.getName() == null || newPlayer.getName().isEmpty()) {
            throw new InvalidRequestException("Invalid player name");
        }
        try {
            playerRepo.save(newPlayer);
        } catch (DuplicateKeyException e) {
            throw new AlreadyExistsException("Given player name is already in use");
        }
    }

    /**
     * Update an existing player, currently only supports name changes
     * @param playerId The player ID of the player to update
     * @param player The new player information
     */
    @PatchMapping("/player/{playerId}")
    void patchPlayer(@PathVariable String playerId, @RequestBody Player player) {
        if (player.getName() == null || player.getName().isEmpty()) {
            throw new InvalidRequestException("Invalid player name");
        }
        try {
            // We only allow updating the name
            var query = new Query(Criteria.where("id").is(playerId));
            var update = new Update().set("name", player.getName());
            var result = mongoTemplate.findAndModify(query, update, Player.class);
            if (result == null) {
                throw new NotFoundException("Player not found with id: " + playerId);
            }
        } catch (DuplicateKeyException e) {
            throw new AlreadyExistsException("Given player name is already in use");
        }
    }

    /**
     * Delete the given player
     * @param playerId The player to delete
     */
    @DeleteMapping("/player/{playerId}")
    void deletePlayer(@PathVariable String playerId) {
        var query = new Query(Criteria.where("id").is(playerId));
        var result = mongoTemplate.remove(query, Player.class);

        if (result.getDeletedCount() == 0) {
            throw new NotFoundException("Player not found with id: " + playerId);
        }
    }
}
