package mariomonday.backend.utils;

import mariomonday.backend.apis.BracketController;
import mariomonday.backend.database.tables.BracketRepository;
import mariomonday.backend.database.tables.GameSetRepository;
import mariomonday.backend.database.tables.PlayerRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BaseSpringTest {

  @Autowired
  protected PlayerRepository playerRepository;

  @Autowired
  protected GameSetRepository gameSetRepository;

  @Autowired
  protected BracketRepository bracketRepository;

  @Autowired
  protected BracketController bracketController;

  @AfterEach
  public void cleanUp() {
    playerRepository.deleteAll();
    bracketRepository.deleteAll();
    gameSetRepository.deleteAll();
  }
}
