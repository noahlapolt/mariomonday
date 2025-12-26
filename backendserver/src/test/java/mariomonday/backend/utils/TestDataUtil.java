package mariomonday.backend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import mariomonday.backend.database.schema.Bracket;
import mariomonday.backend.database.schema.Game;
import mariomonday.backend.database.schema.GameSet;
import mariomonday.backend.database.schema.Player;
import mariomonday.backend.database.schema.PlayerSet;

public class TestDataUtil {

  public static Player.PlayerBuilder createFakePlayer() {
    return Player.builder().name("Big Dick Joe").id(UUID.randomUUID().toString());
  }

  public static GameSet.GameSetBuilder createFakeGameSet() {
    return GameSet.builder().id(UUID.randomUUID().toString());
  }

  public static PlayerSet.PlayerSetBuilder createFakePlayerSet() {
    return PlayerSet.builder()
      .players(Set.of(createFakePlayer().build()))
      .name("Yo, you seen this guy?")
      .id(UUID.randomUUID().toString());
  }

  public static Game.GameBuilder createFakeGame() {
    return Game.builder().id(UUID.randomUUID().toString());
  }

  public static Set<PlayerSet> createNFakePlayers(int numberOfPlayers) {
    return IntStream.range(0, numberOfPlayers)
      .boxed()
      .map(num -> createFakePlayerSet().id(String.valueOf(num)).build())
      .collect(Collectors.toSet());
  }

  public static Bracket loadLazyBracket(Bracket bracket) throws JsonProcessingException {
    var objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper.readValue(objectMapper.writeValueAsString(bracket), Bracket.class);
  }
}
