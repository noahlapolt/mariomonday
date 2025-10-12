<!--
    The landing screen is meant to show the next closest game and it's stats on mobile or two different games
    and stats on desktop.
-->

<script lang="ts">
    import smashImg from "$lib/assets/Smash.png";
    import kartImg from "$lib/assets/kart_8.png";

    const games = {
        smash: {
            name: "smash",
            style: "background-color: #0c0c0c",
            img: smashImg,
            alt: "of the smash logo.",
        },
        kart: {
            name: "kart",
            style: "background-color: #47ffe3",
            img: kartImg,
            alt: "of the kart logo.",
        },
    };
    let desktop = $state(false); //TODO calculate this value based on the width of the screen.
    let nextGame = $state(games.smash); //TODO figure out how to calculate this value.
    let stats = [
        { name: "Reed", win: 4, loss: 100 },
        { name: "Zach", win: 3, loss: 100 },
        { name: "Jack", win: 2, loss: 100 },
        { name: "Noah", win: 1, loss: 100 },
    ]; //api.getStats(); I assume this will be sorted by win/loss ratio? Needs to be a state.
</script>

<div id="landing">
    {#each [games.smash, games.kart] as game}
        {#if nextGame.name === game.name || desktop}
            <div class="background" style={game.style}>
                <div class="startScreen">
                    <img src={game.img} alt={game.alt} />
                    <div class="leaderBoard">
                        {#each [1, 0, 2] as top}
                            <div
                                class="leader"
                                style={`height: ${["75%", "50%", "25%"][top]};`}
                            >
                                {top + 1}
                                <p class="user">{stats[top].name}</p>
                            </div>
                        {/each}
                    </div>
                    <a href={`setup?mode=${game.name}`}>New Tournament</a>
                </div>
                <div class="stats">
                    {#each stats as stat}
                        <div class="stat">
                            <span>Player: {stat.name}</span>
                            <span>
                                {stat.win > 1 ? "Wins" : "Win"}: {stat.win}
                            </span>
                            <span>
                                {stat.loss > 1 ? "Losses" : "Loss"}: {stat.loss}
                            </span>
                            <span>Ratio: {stat.win / stat.loss}</span>
                        </div>
                    {/each}
                </div>
            </div>
        {/if}
    {/each}
</div>

<style>
    #landing {
        display: flex;
        flex-wrap: wrap;
        width: 100vw;
    }

    .background {
        padding: 0rem 2rem;
        flex-grow: 1;
    }

    .startScreen {
        display: grid;
        grid-template-rows: 20vh 60vh 1.5rem;
        grid-template-columns: 1fr;
        height: 100vh;
        gap: 1rem;
    }

    .startScreen > img {
        height: 100%;
        justify-self: center;
    }

    .stats {
        display: flex;
        flex-direction: column;
    }

    .stat {
        display: flex;
        justify-content: space-around;
        padding: 0.5rem 0;
        margin: 0.5rem 0;
        width: 100%;
        background-color: #999999;
    }

    .leaderBoard {
        display: flex;
        justify-content: center;
        align-items: end;
        gap: 1rem;
    }

    .leader {
        background-color: #999999;
        position: relative;
        width: calc(33% - 2rem);
        text-align: center;
        font-size: xx-large;
        padding: 1rem;
    }

    .user {
        position: absolute;
        top: -4rem;
        left: 0rem;
        width: 100%;
        text-align: center;
        color: #999999;
    }
</style>
