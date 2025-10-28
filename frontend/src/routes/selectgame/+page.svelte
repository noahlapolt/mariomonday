<script lang="ts">
  import smashImg from "$lib/assets/Smash.png";
  import kartImg from "$lib/assets/kart_8.png";

  const games = [
    { name: "Mario Kart 8", src: kartImg, mode: "MARIO_KART_8" },
    {
      name: "Smash Ultimate Singles",
      src: smashImg,
      mode: "SMASH_ULTIMATE_SINGLES",
    },
    {
      name: "Smash Ultimate Doubles",
      src: smashImg,
      mode: "SMASH_ULTIMATE_DOUBLES",
    },
  ];
</script>

<div id="selectGame">
  {#each games as game}
    <a class="option" href={`setup?mode=${game.mode}`}>
      <img src={game.src} alt={game.name} width="100%" />
      <h2>{game.name}</h2>
    </a>
  {/each}
</div>

<style>
  #selectGame {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-wrap: wrap;
    width: 100vw;
  }

  .option {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    background-color: var(--prime);
    color: var(--text-prime);
    width: 20rem;
    height: 20rem;
    margin: 1rem;
    padding: 1rem;
    border-radius: 1rem;
  }
</style>
