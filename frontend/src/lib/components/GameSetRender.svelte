<script lang="ts">
  import type { SvelteMap } from "svelte/reactivity";
  import PlayerSetRender from "./PlayerSetRender.svelte";
  import { GameTypes } from "./Utils.svelte";

  let {
    gameType,
    gameSet,
    disabled,
    playerSetMap,
    onRevive,
    onAddPlayerSet,
  }: {
    gameType: string;
    gameSet: GameSet;
    disabled: boolean;
    playerSetMap: SvelteMap<string, PlayerSet>;
    onRevive: (gameSet: GameSet) => void;
    onAddPlayerSet: (playerSet: PlayerSet) => void;
  } = $props();

  const GAMEINFO = GameTypes[gameType];
  const PLAYERSETHEIGHT =
    GAMEINFO.maxPlayerSets * 40 + (GAMEINFO.playersOnATeam > 1 ? 30 : 0);

  /**
   * Adds a win to the current gameSet.
   * @param playerSet The player set that got the win.
   */
  const addWin = (playerSetId: string) => {
    gameSet.games.push({
      id: "",
      playerSets: gameSet.playerSets,
      winners: [playerSetId],
    });
    // TODO Let the server know
  };

  /**
   * Removes a player/team from the bracket.
   * @param playerSetIndex The index of the set to remove.
   */
  const surrender = (playerSetIndex: number) => {
    gameSet.playerSets.splice(playerSetIndex, 1);

    // TODO Let the server know
  };

  /**
   * Builds an empty set to add new players.
   */
  const addPlayerSet = () => {
    // Adds all of the players
    let newPlayers: Player[] = [];
    for (let i = 0; i < GAMEINFO.playersOnATeam; i++) {
      newPlayers.push({ id: "", name: "New Player", eloMap: {} });
    }

    // Builds the player set
    let newSet: PlayerSet = {
      id: "",
      name: "New Team",
      players: newPlayers,
    };
    onAddPlayerSet(newSet);
  };
</script>

<div class="gameSet">
  {#each gameSet.playerSets as playerSetId, playerSetIndex}
    <div class="set">
      <button
        class="playerSet"
        disabled={disabled || gameSet.winners.length > 0}
        onclick={() => {
          addWin(playerSetId);
        }}
      >
        <PlayerSetRender playerSet={playerSetMap.get(playerSetId)} {gameType} />
        <div>
          {#each gameSet.games as game}
            {#each game.winners as winner}
              {#if winner === playerSetId}
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
                  <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
                  <path
                    d="M64 320C64 178.6 178.6 64 320 64C461.4 64 576 178.6 576 320C576 461.4 461.4 576 320 576C178.6 576 64 461.4 64 320z"
                  />
                </svg>
              {/if}
            {/each}
          {/each}
          {#each gameSet.winners as winner}
            {#if winner === playerSetId}
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
                <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
                <path
                  d="M345 151.2C354.2 143.9 360 132.6 360 120C360 97.9 342.1 80 320 80C297.9 80 280 97.9 280 120C280 132.6 285.9 143.9 295 151.2L226.6 258.8C216.6 274.5 195.3 278.4 180.4 267.2L120.9 222.7C125.4 216.3 128 208.4 128 200C128 177.9 110.1 160 88 160C65.9 160 48 177.9 48 200C48 221.8 65.5 239.6 87.2 240L119.8 457.5C124.5 488.8 151.4 512 183.1 512L456.9 512C488.6 512 515.5 488.8 520.2 457.5L552.8 240C574.5 239.6 592 221.8 592 200C592 177.9 574.1 160 552 160C529.9 160 512 177.9 512 200C512 208.4 514.6 216.3 519.1 222.7L459.7 267.3C444.8 278.5 423.5 274.6 413.5 258.9L345 151.2z"
                />
              </svg>
            {/if}
          {/each}
        </div>
      </button>
      <button
        aria-label="Surrender"
        onclick={() => {
          surrender(playerSetIndex);
        }}
      >
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
          <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
          <path
            d="M64 128L288 128L288 137.8C288 176.8 264.3 211.8 228.1 226.2C167.6 250.4 128 308.9 128 374.1L128 447.9L64 447.9L64 127.9zM352 128L576 128L576 448L508.3 448L504.6 443.5L429.4 353.3C420.3 342.4 406.8 336 392.5 336L321.4 336L280.4 272.9C280.1 272.4 279.8 271.9 279.4 271.5C324.1 242.5 351.9 192.5 351.9 137.9L351.9 128.1zM425 448L398.4 448L441.1 512L592 512C618.5 512 640 490.5 640 464L640 112C640 85.5 618.5 64 592 64L48 64C21.5 64 0 85.5 0 112L0 464C0 490.5 21.5 512 48 512L308.2 512L341.4 561.8C351.2 576.5 371.1 580.5 385.8 570.7C400.5 560.9 404.5 541 394.7 526.3L310.5 400.1L385.1 400.1L425.1 448.1zM265.5 448L192 448L192 374.2C192 364 193.6 354.1 196.7 344.7L265.5 448zM192 192C192 165.5 170.5 144 144 144C117.5 144 96 165.5 96 192C96 218.5 117.5 240 144 240C170.5 240 192 218.5 192 192z"
          />
        </svg>
      </button>
    </div>
  {/each}
  {#each { length: GAMEINFO.maxPlayerSets - gameSet.playerSets.length }}
    <div class="revive">
      <button
        aria-label="Revive Player/Team"
        style={`height: ${PLAYERSETHEIGHT}px;`}
        {disabled}
        onclick={() => {
          onRevive(gameSet);
        }}
      >
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
          <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
          <path
            d="M311.6 95C297.5 75.5 274.9 64 250.9 64C209.5 64 176 97.5 176 138.9L176 141.3C176 205.7 258 274.7 298.2 304.6C311.2 314.3 328.7 314.3 341.7 304.6C381.9 274.6 463.9 205.7 463.9 141.3L463.9 138.9C463.9 97.5 430.4 64 389 64C365 64 342.4 75.5 328.3 95L320 106.7L311.6 95zM141.3 405.5L98.7 448L64 448C46.3 448 32 462.3 32 480L32 544C32 561.7 46.3 576 64 576L384.5 576C413.5 576 441.8 566.7 465.2 549.5L591.8 456.2C609.6 443.1 613.4 418.1 600.3 400.3C587.2 382.5 562.2 378.7 544.4 391.8L424.6 480L312 480C298.7 480 288 469.3 288 456C288 442.7 298.7 432 312 432L384 432C401.7 432 416 417.7 416 400C416 382.3 401.7 368 384 368L231.8 368C197.9 368 165.3 381.5 141.3 405.5z"
          />
        </svg>
      </button>
      <button
        aria-label="Add Player/Team"
        style={`height: ${PLAYERSETHEIGHT}px;`}
        disabled={true}
        onclick={addPlayerSet}
      >
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
          <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
          <path
            d="M352 128C352 110.3 337.7 96 320 96C302.3 96 288 110.3 288 128L288 288L128 288C110.3 288 96 302.3 96 320C96 337.7 110.3 352 128 352L288 352L288 512C288 529.7 302.3 544 320 544C337.7 544 352 529.7 352 512L352 352L512 352C529.7 352 544 337.7 544 320C544 302.3 529.7 288 512 288L352 288L352 128z"
          />
        </svg>
      </button>
    </div>
  {/each}
</div>

<style>
  .gameSet {
    width: 20rem;
  }

  .set {
    display: flex;
    justify-content: space-between;
  }

  .set button {
    display: flex;
    align-items: center;
    margin: 5px;
    padding: 0 0.5rem;
  }

  .playerSet {
    justify-content: space-between;
    flex-grow: 1;
  }

  .revive {
    display: flex;
    justify-content: space-between;
  }

  .revive button {
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 5px;
    padding: 0 0.5rem;
    flex-grow: 1;
  }
</style>
