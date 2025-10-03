package mariomonday.backend;

import java.util.List;
import java.util.Set;
import mariomonday.backend.database.schema.Game;
import mariomonday.backend.database.schema.Player;
import mariomonday.backend.database.tables.BracketRepository;
import mariomonday.backend.database.tables.GameRepository;
import mariomonday.backend.database.tables.GameSetRepository;
import mariomonday.backend.database.tables.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main implements CommandLineRunner {

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private GameSetRepository gameSetRepo;

    @Autowired
    private BracketRepository bracketRepo;

    @Autowired
    private PlayerRepository playerRepository;

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        playerRepository.deleteAll();
        gameRepo.deleteAll();
        var player1 = new Player();
        player1.setName("Nicky");
        var player2 = new Player();
        player2.setName("Jake");
        var player3 = new Player();
        player3.setName("Fuck you guy");
        playerRepository.saveAll(List.of(player1, player2, player3));
        var game = new Game();
        game.setPlayers(Set.of(player1.getId(), player2.getId()));
        game.setWinners(Set.of(player2.getId()));
        var game2 = new Game();
        game2.setPlayers(Set.of(player2.getId(), player3.getId()));
        game2.setWinners(Set.of(player3.getId()));
        gameRepo.saveAll(List.of(game, game2));

        System.out.println(gameRepo.findByWinnersContains(player3.getId()));
    }
}
