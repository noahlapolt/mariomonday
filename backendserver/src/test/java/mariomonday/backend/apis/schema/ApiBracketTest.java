package mariomonday.backend.apis.schema;

import java.time.Clock;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import mariomonday.backend.database.schema.Bracket;
import mariomonday.backend.database.schema.GameType;
import mariomonday.backend.database.schema.Player;
import mariomonday.backend.utils.BaseSpringTest;
import mariomonday.backend.utils.TestDataUtil;
import org.assertj.core.data.MapEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

public class ApiBracketTest extends BaseSpringTest {

  @Spy
  private Clock clock = Clock.systemDefaultZone();

  private Set<Player> players;

  @BeforeEach
  public void setUp() {
    players = TestDataUtil.createNFakePlayers(16)
      .stream()
      .map(playerSet -> playerSet.getPlayers().stream().findFirst().get())
      .collect(Collectors.toSet());
    playerRepository.insert(players);
  }

  @Test
  public void testFromBracket_shouldOrderGameSetsCorrectly() {
    // Setup
    var bracketReq = CreateBracketRequest.builder()
      .teams(players.stream().collect(Collectors.toMap(Player::getId, player -> List.of(player.getId()))))
      .gameType(GameType.SMASH_ULTIMATE_SINGLES)
      .build();

    // Act
    var bracket = Bracket.loadLazyBracket(
      bracketRepository.findById(bracketController.postBracket(bracketReq).getId()).get()
    );
    var apiBracket = ApiBracket.fromBracket(bracket);
    var apiBracket2 = ApiBracket.fromBracket(bracket);

    // Verify
    Assertions.assertEquals(apiBracket.getDate(), bracket.getDate());
    Assertions.assertEquals(apiBracket.getGameType(), bracket.getGameType());
    Assertions.assertEquals(apiBracket.getId(), bracket.getId());
    Assertions.assertEquals(apiBracket.getTeams(), bracket.getTeams());
    Assertions.assertEquals(apiBracket.getRounds(), bracket.getRounds());
    Assertions.assertEquals(apiBracket.getWinners(), bracket.getWinners());
    Assertions.assertEquals(apiBracket.getRounds(), apiBracket.getGameSets().size());
    var totalSize = 0;
    for (int i = 0; i < apiBracket.getRounds(); i++) {
      for (var set : apiBracket.getGameSets().get(i)) {
        Assertions.assertEquals(apiBracket.getRounds() - i - 1, set.getRoundIndex());
        Assertions.assertTrue(bracket.getGameSets().contains(set));
        totalSize += 1;
      }
    }
    Assertions.assertEquals(bracket.getGameSets().size(), totalSize);

    // Confirm that there is no randomization when creating API bracket from bracket
    Assertions.assertEquals(apiBracket, apiBracket2);
  }
}
