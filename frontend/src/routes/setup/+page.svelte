<script lang="ts">
  import qrCode from "$lib/assets/qrcode.png";
  import Search from "$lib/components/Search.svelte";

  let players: Player[] = $state([]);
  let playingPlayers: Player[] = $state([]);
  let playerOptions: Player[] = $state([]);
  let searchTerm: string = $state("");

  // Evil data
  players = [
    { id: "0", name: "Noah", elo: new Map<GameType, number>() },
    { id: "0", name: "Reed", elo: new Map<GameType, number>() },
    { id: "0", name: "Jack", elo: new Map<GameType, number>() },
    { id: "0", name: "Zach", elo: new Map<GameType, number>() },
  ];

  /**
   * Searches through all of the players. Picks the first four players
   * that match the search term.
   *
   * Time complexity O(n), where n is the length of players.
   */
  const searchPlayers = () => {
    let found = 0;
    let index = 0;
    playerOptions = [];
    while (index < players.length && found < 4) {
      if (
        players[index].name.toLowerCase().includes(searchTerm.toLowerCase()) &&
        playingPlayers.find((player) => {
          players[index].id = player.id;
        }) === undefined
      ) {
        playerOptions.push(players[index]);
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
    playingPlayers.push(player);

    // TODO Save this player in a cookie or somthing.
  };

  /**
   * Creates a player from a name.
   * @param name The name of the player.
   */
  const createPlayer = () => {
    // TODO use API to create the player.
  };
</script>

<div id="setup">
  <div>
    <img src={qrCode} alt="of a QR code to Jake's Venmo" />
    <a href="https://venmo.com/Jake-Donovan-9?amount=5">Jake's Venmo</a>
  </div>
  <div>
    <div>
      <Search bind:searchTerm search={searchPlayers}>
        <button
          aria-label="clear"
          onclick={() => {
            searchTerm = "";
          }}
        >
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
            <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
            <path
              d="M504.6 148.5C515.9 134.9 514.1 114.7 500.5 103.4C486.9 92.1 466.7 93.9 455.4 107.5L320 270L184.6 107.5C173.3 93.9 153.1 92.1 139.5 103.4C125.9 114.7 124.1 134.9 135.4 148.5L278.3 320L135.4 491.5C124.1 505.1 125.9 525.3 139.5 536.6C153.1 547.9 173.3 546.1 184.6 532.5L320 370L455.4 532.5C466.7 546.1 486.9 547.9 500.5 536.6C514.1 525.3 515.9 505.1 504.6 491.5L361.7 320L504.6 148.5z"
            />
          </svg>
        </button>
        <button aria-label="add" onclick={createPlayer}>
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
            <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
            <path
              d="M352 128C352 110.3 337.7 96 320 96C302.3 96 288 110.3 288 128L288 288L128 288C110.3 288 96 302.3 96 320C96 337.7 110.3 352 128 352L288 352L288 512C288 529.7 302.3 544 320 544C337.7 544 352 529.7 352 512L352 352L512 352C529.7 352 544 337.7 544 320C544 302.3 529.7 288 512 288L352 288L352 128z"
            />
          </svg>
        </button>
      </Search>
      <div>
        {#each playerOptions as option}
          <button
            onclick={() => {
              addPlayer(option);
              playerOptions = [];
            }}
          >
            {option.name}
          </button>
        {/each}
      </div>
    </div>
    <div>
      {#each playingPlayers as player}
        <div>{player.name}</div>
      {/each}
    </div>
    <button>Start Tournament</button>
  </div>
</div>

<style>
  #setup {
    display: flex;
    flex-wrap: wrap;
    width: 100vw;
  }
</style>
