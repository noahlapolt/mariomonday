<!--
    The landing screen will show information about Mario Monday and the last tourn with it's stats.
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
    let lastGame = $state(games.smash); //TODO figure out how to calculate this value.
    let stats = [
        { name: "Reed", win: 4, loss: 100 },
        { name: "Zach", win: 3, loss: 100 },
        { name: "Jack", win: 2, loss: 100 },
        { name: "Noah", win: 1, loss: 100 },
    ]; //api.getStats(); I assume this will be sorted by win/loss ratio? Needs to be a state.
</script>

<div id="landing">
    <div class="contents">
        <h1>Welcome to the Mario Monday Stats Site!</h1>
        <h2>What?</h2>
        <p>
            First you might be thinking: "What is Mario Monday?" Well Mario
            Monday is an event at The Avenue Bar and Grill that happens each
            Monday at 8:30pm. At this event we will play either Mario Kart or
            Super Smash Ultimate. Sometimes it will be a different game if that
            is what the people want.
        </p>
        <h2>Where?</h2>
        <p>
            Did you read the paragraph before? Its at The Avenue Bar and Grill
            (1249 Commonwealth Ave, Allston, MA 02134)
        </p>
        <h2>When?</h2>
        <p>
            Seriously, did you read the first paragraph? Its every Monday at
            8:30pm
        </p>
    </div>
    <div id="leaderBoard" class="contents">
        <img src={lastGame.img} alt={lastGame.alt} />
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
        <a href={`selectgame`}>New Tournament</a>
    </div>
</div>

<style>
    #landing {
        display: flex;
        flex-wrap: wrap;
        width: calc(100vw - 2rem);
        padding: 1rem;
        justify-content: center;
        background-color: var(--prime);
        color: var(--text-prime);
    }

    #leaderBoard {
        justify-content: space-between;
    }

    #leaderBoard img {
        max-height: 30%;
        object-fit: contain;
    }

    .contents {
        display: flex;
        flex-direction: column;
        flex-grow: 1;
        height: calc(100vh - 2rem);
        text-align: center;
        min-width: 50%;
        max-width: 20rem;
    }

    .leaderBoard {
        display: flex;
        justify-content: center;
        align-items: end;
        gap: 1rem;
        flex-grow: 1;
        max-height: 40%;
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
