package mariomonday.backend.database.schema;

import lombok.Getter;

/**
 * Represents the type of game that was played
 */
@Getter
public enum GameType {
  /**
   * Mario Kart World
   */
  MARIO_KART_WORLD(4, 2, 1),

  /**
   * Mario Kart 8
   */
  MARIO_KART_8(4, 2, 1),

  /**
   * Why did I write these docs? Who cares. Of course this means Smash Ultimate
   */
  SMASH_ULTIMATE_SINGLES(2, 1, 1), SMASH_ULTIMATE_DOUBLES(2, 1, 2);


  private final int maxPlayerSets;
  private final int playerSetsToMoveOn;
  private final int playersOnATeam;

  GameType(int maxPlayerSets, int playerSetsToMoveOn, int playersOnATeam) {
    this.maxPlayerSets = maxPlayerSets;
    this.playerSetsToMoveOn = playerSetsToMoveOn;
    this.playersOnATeam = playersOnATeam;
  }


}