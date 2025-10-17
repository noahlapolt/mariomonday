<script lang="ts">
  import { SvelteSet } from "svelte/reactivity";
  import Search from "$lib/components/Search.svelte";
  import TeamInfo from "$lib/components/TeamInfo.svelte";
  import { PUBLIC_API_URL } from "$env/static/public";
  import { onMount } from "svelte";

  /* Manage players */
  let players: Player[] = $state([]);
  let playingTeams: SvelteSet<PlayerSet> = $state(new SvelteSet());
  let playerOptions: SvelteSet<Player> = $state(new SvelteSet());
  let selectedTeam: PlayerSet | undefined = $state();
  let playerCount: number = $state(0);

  /* Search text, used for new player. Initial game type */
  let searchTerm: string = $state("");
  let gameType: GameType = $state({
    maxPlayerSets: 2,
    playerSetsToMoveOn: 1,
    playersOnATeam: 1,
  });

  onMount(() => {
    /* Setup the game type from URL. */
    const urlParams = new URL(window.location.toString()).searchParams;
    gameType = {
      maxPlayerSets: parseInt(urlParams.get("max") || "2"),
      playerSetsToMoveOn: parseInt(urlParams.get("move") || "1"),
      playersOnATeam: parseInt(urlParams.get("team") || "1"),
    };

    /* Setup players from database. */
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

    /* Load playing teams from cookies. */
  });

  /**
   * Searches through all of the players. Picks the first four players
   * that match the search term.
   *
   * Time complexity O(n), where n is the length of players. Optimize if needed.
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
   * Creates a player from the current searchTerm value.
   */
  const createPlayer = () => {
    // TODO add authentication.
    const INIT: RequestInit = {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      mode: "no-cors",
      body: JSON.stringify({
        id: "",
        name: searchTerm,
        elo: new Map<GameType, number>(),
      }),
    };
    fetch(`${PUBLIC_API_URL}/player`, INIT)
      .then((response) => {
        return response.json();
      })
      .then((data) => {
        console.log(data);
        // TODO figure out what data was added to the database.
        return true;
      })
      .catch((err) => {
        console.log("Failed to create the player.", err);
        return false;
      });

    addPlayer({ id: "", name: searchTerm, elo: new Map<GameType, number>() });
    searchTerm = "";
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
      <!--Uh oh this takes alot of time? Optimize if needed.-->
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
    flex-direction: column;
    background-color: var(--prime);
    flex-grow: 1;
    border-radius: 0.5rem;
    margin-top: 0.15rem;
    color: var(--text-prime);
    padding: 0.5rem;
    overflow: scroll;
  }
</style>
