<script lang="ts">
  let {
    newPlayerSet,
    onAddPlayerSet,
    onCancel,
  }: {
    newPlayerSet: PlayerSet;
    onAddPlayerSet: (playerSet: PlayerSet) => void;
    onCancel: () => void;
  } = $props();
</script>

<div class="newSetBackground">
  <div class="newSet">
    {#if newPlayerSet.players.length > 1}
      <label>
        Team Name: <input type="text" defaultValue={newPlayerSet.name} />
      </label>
    {/if}
  </div>
  <div class="buttons">
    <button aria-label="Add">
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640">
        <!--!Font Awesome Free v7.1.0 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2025 Fonticons, Inc.-->
        <path
          d="M352 128C352 110.3 337.7 96 320 96C302.3 96 288 110.3 288 128L288 288L128 288C110.3 288 96 302.3 96 320C96 337.7 110.3 352 128 352L288 352L288 512C288 529.7 302.3 544 320 544C337.7 544 352 529.7 352 512L352 352L512 352C529.7 352 544 337.7 544 320C544 302.3 529.7 288 512 288L352 288L352 128z"
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
  .newSetBackground {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background-color: #00000055;
    display: grid;
    place-content: center;
    overflow: hidden;
    z-index: 30;
  }

  .newSet {
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

  .buttons {
    display: flex;
    justify-content: space-around;
  }

  .buttons button {
    flex-grow: 1;
  }
</style>
