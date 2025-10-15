<script lang="ts">
  import { SvelteSet } from "svelte/reactivity";
  import Search from "$lib/components/Search.svelte";
  import TeamInfo from "$lib/components/TeamInfo.svelte";

  let players: Player[] = $state([]);
  let playingTeams: SvelteSet<PlayerSet> = $state(new SvelteSet());
  let playerOptions: SvelteSet<Player> = $state(new SvelteSet());
  let searchTerm: string = $state("");
  let gameType: GameType = $state({
    maxPlayerSets: 2,
    playerSetsToMoveOn: 1,
    playersOnATeam: 2,
  });

  // Evil data
  players = [
    { id: "0", name: "Noah", elo: new Map<GameType, number>() },
    { id: "0", name: "Reed", elo: new Map<GameType, number>() },
    { id: "0", name: "Jack", elo: new Map<GameType, number>() },
    { id: "0", name: "Zach", elo: new Map<GameType, number>() },
    { id: "10", name: "Zach1", elo: new Map<GameType, number>() },
  ];

  // Evil Code
  for (let i = 0; i < 20; i++) {
    playingTeams.add({
      id: "",
      players: new SvelteSet([
        {
          id: (Math.random() * 100).toString(),
          name: "Noah",
          elo: new Map<GameType, number>([[gameType, 15]]),
        },
        {
          id: (Math.random() * 100).toString(),
          name: "Harrison Hull",
          elo: new Map<GameType, number>([[gameType, 20]]),
        },
      ]),
      name: "Testing",
    });
  }

  /**
   * Searches through all of the players. Picks the first four players
   * that match the search term.
   *
   * Time complexity O(n), where n is the length of players.
   */
  const searchPlayers = () => {
    let found = 0;
    let index = 0;
    playerOptions = new SvelteSet();
    while (searchTerm !== "" && index < players.length && found < 4) {
      // Checks if the player is already playing.
      let alreadyPlaying = false;
      playingTeams.forEach((team) => {
        if (!alreadyPlaying) alreadyPlaying = team.players.has(players[index]);
      });

      // Adds the player to the list if they are not already playing, exist, and there are
      // less than 4 results
      if (
        players[index].name.toLowerCase().includes(searchTerm.toLowerCase()) &&
        !alreadyPlaying
      ) {
        playerOptions.add(players[index]);
        found++;
      }
      index++;
    }
  };

  /**
   * Adds a player to the current players on screen.
   * @param player The player being added.
   */
  const addPlayer = (player: Player) => {
    playingPlayers.add({
      id: "aaa",
      players: new SvelteSet([player]),
      name: "Team Based",
    });

    // Create player set on API
  };

  /**
   * Creates a player from a name.
   * @param name The name of the player.
   */
  const createPlayer = () => {
    // TODO add authentication.
    const HEADER = { method: "POST" };
    fetch(`${API}/player`, HEADER)
      .then((response) => {
        return response.json();
      })
      .then((data) => {
        console.log(data);
        // TODO figure out what data was added to the database.
        return true;
      })
      .catch(() => {
        console.log("Failed to create the player");
        // TODO tell the user the error
        return false;
      });

    playingTeams.add({
      id: "",
      players: new SvelteSet([
        {
          id: "Random",
          name: searchTerm,
          elo: new Map<GameType, number>(),
        },
      ]),
      name: "",
    });
  };

  /**
   * Simple function to count the number of players.
   */
  const countPlayers = () => {
    let count = 0;

    playingTeams.forEach((team) => {
      team.players.forEach(() => {
        count++;
      });
    });

    return count;
  };
</script>

<div id="setup">
  <div id="players">
    <div>
      <Search bind:searchTerm search={searchPlayers} {createPlayer}>
        {#each playerOptions as option}
          <button
            onclick={() => {
              addPlayer(option);
              playerOptions = new SvelteSet();
              searchTerm = "";
            }}
          >
            {option.name}
          </button>
        {/each}
      </Search>
    </div>
    <div id="playing">
      {#each playingTeams as team}
        <TeamInfo
          {team}
          {gameType}
          remove={() => {
            playingTeams.delete(team);
          }}
        />
      {/each}
    </div>
    <button>
      Start Tournament: {countPlayers()} player{countPlayers() > 1 ? "s" : ""}
    </button>
  </div>
</div>

<style>
  #setup {
    display: flex;
    flex-wrap: wrap;
    flex-direction: row-reverse;
    width: calc(100vw - 2rem);
    justify-content: space-around;
    padding: 1rem;
  }

  #players {
    display: flex;
    flex-direction: column;
    flex-grow: 1;
    justify-content: space-between;
    max-width: 40rem;
    height: calc(100vh - 2rem);
  }

  #playing {
    background-color: var(--prime);
    flex-grow: 1;
    border-radius: 0.5rem;
    margin-top: 0.15rem;
    color: var(--text-prime);
    padding: 0.5rem;
    overflow: scroll;
  }
</style>
