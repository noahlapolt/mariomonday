package mariomonday.backend.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import mariomonday.backend.database.schema.Game;
import mariomonday.backend.database.schema.GameSet;
import mariomonday.backend.database.schema.Player;
import mariomonday.backend.database.schema.PlayerSet;

public class TestDataUtil {

  public static Player.PlayerBuilder createFakePlayer() {
    return Player.builder().name("Big Dick Joe").id("He's swangin");
  }

  public static GameSet.GameSetBuilder createFakeGameSet() {
    return GameSet.builder().id("Idk im tired of dick stuff");
  }


  public static PlayerSet.PlayerSetBuilder createFakePlayerSet() {
    return PlayerSet.builder().players(Set.of(createFakePlayer().build())).name("Yo, you seen this guy?").id("It's so big");
  }

  public static Game.GameBuilder createFakeGame() {
    return Game.builder().id("Whip those bad boys out");
  }

  public static Set<PlayerSet> createNFakePlayers(int numberOfPlayers) {
    return new HashSet<PlayerSet>(Collections.nCopies(numberOfPlayers, createFakePlayerSet().build()));
  }

}
