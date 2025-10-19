<script lang="ts">
  import { SvelteSet } from "svelte/reactivity";
  import Search from "$lib/components/Search.svelte";
  import TeamInfo from "$lib/components/TeamInfo.svelte";
  import { PUBLIC_API_URL } from "$env/static/public";
  import { onMount } from "svelte";

  /* Manage player information. */
  let players: Player[] = $state([]);
  let playingTeams: SvelteSet<PlayerSet> = $state(new SvelteSet());
  let playerOptions: SvelteSet<Player> = $state(new SvelteSet());
  let selectedTeam: PlayerSet | undefined = $state();
  let playerCount: number = $state(0);

  /* Manage game type and search. */
  let searchTerm: string = $state("");
  let gameType: GameType = $state({
    maxPlayerSets: 2,
    playerSetsToMoveOn: 1,
    playersOnATeam: 1,
  });

  onMount(() => {
    /* Fetch players */
    const Players_INIT: RequestInit = {
      method: "GET",
    };
    fetch(`${PUBLIC_API_URL}/player`, Players_INIT)
      .then((response) => response.json())
      .then((data) => {
        players = data;
        return true;
      })
      .catch((e) => {
        console.log("There was an error getting the players.", e);
        return false;
      });

    /* Get game type */
    const urlParams = new URL(window.location.href).searchParams;
    gameType = {
      maxPlayerSets: parseInt(urlParams.get("max") || "2"),
      playerSetsToMoveOn: parseInt(urlParams.get("move") || "1"),
      playersOnATeam: parseInt(urlParams.get("team") || "1"),
    };
  });

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
    if (selectedTeam === undefined || gameType.playersOnATeam === 1) {
      selectedTeam = {
        id: "",
        name: "New Team",
        players: new SvelteSet([player]),
      };

      playingTeams.add(selectedTeam);
    } else {
      selectedTeam.players.add(player);
      selectedTeam = undefined;
    }
    playerCount++;
  };

  /**
   * Creates a player from a name.
   * @param name The name of the player.
   */
  const createPlayer = () => {
    const newPlayer = {
      id: "",
      name: searchTerm,
      elo: new Map<GameType, number>(),
    };
    const player_INIT: RequestInit = {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(newPlayer),
    };
    fetch(`${PUBLIC_API_URL}/player`, player_INIT)
      .then(() => {
        addPlayer(newPlayer);
        searchTerm = "";
      })
      .catch((err) => {
        console.log("Failed to create the player" + err);
        return false;
      });
  };
</script>

<div id="setup">
  <div id="players">
    <Search id="search" bind:searchTerm search={searchPlayers} {createPlayer}>
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
    <div id="playing">
      <!--This is hella slow should be optimized.-->
      {#each Array.from(playingTeams).reverse() as team}
        <TeamInfo
          {team}
          {gameType}
          add={() => {
            document.getElementById("search")?.focus();
            selectedTeam = team;
          }}
          remove={() => {
            playingTeams.delete(team);
          }}
          removePlayer={() => {
            playerCount--;
          }}
        />
      {/each}
    </div>
    <button>
      Start Tournament: {playerCount} player{playerCount > 1 ? "s" : ""}
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
    display: flex;
    flex-direction: column-reverse;
    background-color: var(--prime);
    flex-grow: 1;
    border-radius: 0.5rem;
    margin-top: 0.15rem;
    color: var(--text-prime);
    padding: 0.5rem;
    overflow: scroll;
  }
</style>
