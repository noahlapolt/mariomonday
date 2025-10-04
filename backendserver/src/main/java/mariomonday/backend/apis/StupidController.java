package mariomonday.backend.apis;

import mariomonday.backend.database.schema.Player;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StupidController {

    @GetMapping("/stupid")
    public Player fuckYouGuy() {
        var player = new Player();
        player.setName("Fuck you guy!");
        return player;
    }
}
