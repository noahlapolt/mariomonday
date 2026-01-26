<script lang="ts">
  import { GameTypes, globalStates } from "./Utils.svelte";
  import PlayerSetRender from "$lib/components/PlayerSetRender.svelte";

  let {
    gameType,
    pool,
    onCancel,
    onRevived,
  }: {
    gameType: string;
    pool: PlayerSet[];
    onCancel: () => void;
    onRevived: (playerSetId: string) => void;
  } = $props();

  // Vars to manage what is displayed.
  const pause = 100;
  let highlighted = $state(-1);
  let found = false;
  let steps = 10 + Math.floor(Math.random() * 10);
  let currentCycle: number | undefined = undefined;

  /**
   * Sets the revived player set and cleans the cycle up
   * so that it can run again.
   */
  const reset = () => {
    // Set winner and close.
    onRevived(pool[highlighted].id);
    pool.splice(highlighted, 1);

    // Reset vars.
    found = false;
    highlighted = -1;
    steps = 10 + Math.floor(Math.random() * 10);
  };

  /**
   * Cycle that runs all the time. Displays a slots
   * like random picker.
   */
  const cycle = () => {
    // Increments the highlighted item while running out of steps.
    highlighted += 1;
    highlighted %= pool.length;
    steps -= 1;

    if (steps === 0) currentCycle = setTimeout(reset, pause * 3);
    else currentCycle = setTimeout(cycle, pause);
  };

  /**
   * Starts the random pick cycle.
   */
  const start = () => {
    if (currentCycle !== undefined) clearTimeout(currentCycle);
    currentCycle = setTimeout(cycle, pause);
  };

  /**
   * Skips the random selection and just adds this player.
   */
  const add = (setIndex: number) => {
    highlighted = setIndex;
    currentCycle = setTimeout(reset, pause * 3);
  };

  /**
   * Makes the new player set popup display.
   */
  const newPlayerSet = () => {
    globalStates.newPlayerSet = GameTypes[gameType].playersOnATeam;
  };

  /**
   * Removes a player set from the random pool.
   * @param setIndex The player set to remove.
   */
  const remove = (setIndex: number) => {
    pool.splice(setIndex, 1);
  };
</script>

<div class="randomBackground">
  <div class="random">
    {#each pool as playerSet, setIndex}
      <div class={`revive ${setIndex === highlighted ? "highlighted" : ""}`}>
        {#if playerSet.players.length > 1}
          <div class="team">{playerSet.name}</div>
        {:else}
          <PlayerSetRender {playerSet} {gameType} />
        {/if}
        <button
          aria-label="Remove"
          onclick={() => {
            remove(setIndex);
          }}
        >
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
            <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
            <path
              d="M183.1 137.4C170.6 124.9 150.3 124.9 137.8 137.4C125.3 149.9 125.3 170.2 137.8 182.7L275.2 320L137.9 457.4C125.4 469.9 125.4 490.2 137.9 502.7C150.4 515.2 170.7 515.2 183.2 502.7L320.5 365.3L457.9 502.6C470.4 515.1 490.7 515.1 503.2 502.6C515.7 490.1 515.7 469.8 503.2 457.3L365.8 320L503.1 182.6C515.6 170.1 515.6 149.8 503.1 137.3C490.6 124.8 470.3 124.8 457.8 137.3L320.5 274.7L183.1 137.4z"
            />
          </svg>
        </button>
        <button
          aria-label="Add"
          onclick={() => {
            add(setIndex);
          }}
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
  <button aria-label="Add new player/team" onclick={newPlayerSet}>
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
      <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
      <path
        d="M352 128C352 110.3 337.7 96 320 96C302.3 96 288 110.3 288 128L288 288L128 288C110.3 288 96 302.3 96 320C96 337.7 110.3 352 128 352L288 352L288 512C288 529.7 302.3 544 320 544C337.7 544 352 529.7 352 512L352 352L512 352C529.7 352 544 337.7 544 320C544 302.3 529.7 288 512 288L352 288L352 128z"
      />
    </svg>
  </button>
  <div class="buttons">
    <button aria-label="Roll" onclick={start}>
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
        <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
        <path
          d="M205.4 66.3C167 56 127.5 78.8 117.3 117.2L66.5 306.7C56.2 345.1 79 384.6 117.4 394.9L306.9 445.7C345.3 456 384.8 433.2 395.1 394.8L445.9 205.3C456.2 166.9 433.4 127.4 395 117.1L205.4 66.3zM228.4 272C222.3 262.1 222.1 249.6 227.8 239.5C233.5 229.4 244.3 223.2 256 223.3C267.6 223.4 278.2 229.8 283.8 240C289.9 249.9 290.1 262.4 284.4 272.5C278.7 282.6 267.9 288.8 256.2 288.7C244.6 288.6 234 282.2 228.4 272zM143.2 284.3C153.1 278.2 165.6 278 175.7 283.7C185.8 289.4 192 300.2 191.9 311.9C191.8 323.5 185.4 334.1 175.2 339.7C165.3 345.8 152.8 346 142.7 340.3C132.6 334.6 126.4 323.8 126.5 312.1C126.6 300.5 133 289.9 143.2 284.3zM328.2 380.7C318.3 386.8 305.8 387 295.7 381.3C285.6 375.6 279.4 364.8 279.5 353.1C279.6 341.5 286 330.9 296.2 325.3C306.1 319.2 318.6 319 328.7 324.7C338.8 330.4 345 341.2 344.9 352.9C344.8 364.5 338.4 375.1 328.2 380.7zM337.2 172.3C347.1 166.2 359.6 166 369.7 171.7C379.8 177.4 386 188.2 385.9 199.9C385.8 211.5 379.4 222.1 369.2 227.7C359.3 233.8 346.8 234 336.7 228.3C326.6 222.6 320.4 211.8 320.5 200.1C320.6 188.5 327 177.9 337.2 172.3zM216.2 186.7C206.3 192.8 193.8 193 183.7 187.3C173.6 181.6 167.4 170.8 167.5 159.1C167.6 147.5 174 136.9 184.2 131.3C194.1 125.2 206.6 125 216.7 130.7C226.8 136.4 233 147.2 232.9 158.9C232.8 170.5 226.4 181.1 216.2 186.7zM482 256L441.4 407.2C424.2 471.2 358.4 509.2 294.4 492.1L256.1 481.8L256.1 512C256.1 547.3 284.8 576 320.1 576L512.1 576C547.4 576 576.1 547.3 576.1 512L576.1 320C576.1 284.7 547.4 256 512.1 256L482 256z"
        />
      </svg>
    </button>
    <button aria-label="Cancel" onclick={onCancel}>
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
        <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
        <path
          d="M183.1 137.4C170.6 124.9 150.3 124.9 137.8 137.4C125.3 149.9 125.3 170.2 137.8 182.7L275.2 320L137.9 457.4C125.4 469.9 125.4 490.2 137.9 502.7C150.4 515.2 170.7 515.2 183.2 502.7L320.5 365.3L457.9 502.6C470.4 515.1 490.7 515.1 503.2 502.6C515.7 490.1 515.7 469.8 503.2 457.3L365.8 320L503.1 182.6C515.6 170.1 515.6 149.8 503.1 137.3C490.6 124.8 470.3 124.8 457.8 137.3L320.5 274.7L183.1 137.4z"
        />
      </svg>
    </button>
  </div>
</div>

<style>
  .randomBackground {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background-color: #00000055;
    display: grid;
    place-content: center;
    overflow: hidden;
    z-index: 20;
  }

  .random {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 1rem;
    background-color: #ffffff;
    padding: 1rem;
    border-radius: 1rem;
    overflow: auto;
  }

  .revive {
    display: flex;
  }

  .highlighted {
    background-color: var(--second);
    color: var(--text-second);
  }

  .team {
    padding: 0.5rem;
    margin: 0.15rem;
  }

  .buttons {
    display: flex;
    justify-content: space-around;
  }

  .buttons button {
    flex-grow: 1;
  }
</style>
