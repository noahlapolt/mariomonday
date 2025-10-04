package mariomonday.backend;

import mariomonday.backend.database.tables.BracketRepository;
import mariomonday.backend.database.tables.GameRepository;
import mariomonday.backend.database.tables.GameSetRepository;
import mariomonday.backend.database.tables.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

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
}
