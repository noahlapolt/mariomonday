package mariomonday.backend.apis;

import mariomonday.backend.database.schema.Player;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StupidController {

  @GetMapping("/stupid")
  public Player fuckYouGuy() {
    var player = Player.builder().id("Oh").name("Yeah yeah").build();

    return player;
  }

  @PostMapping("/stupid")
  public Player coolGuy() {
    var player = Player.builder().id("Yeah yeah").name("No no").build();

    return player;
  }
}
