<script lang="ts">
  import { PUBLIC_API_URL } from "$env/static/public";
  import { globalStates } from "./Utils.svelte";

  let username = $state("");
  let password = $state("");
</script>

{#if globalStates.login}
  <div class="login">
    <div>
      <h2>Sign In</h2>
      <label>
        Username:
        <input bind:value={username} type="text" />
      </label>
      <label>
        Password:
        <input bind:value={password} type="password" />
      </label>
      <div class="loginButtons">
        <button
          onclick={() => {
            globalStates.login = false;
          }}
        >
          Cancel
        </button>
        <button
          onclick={() => {
            const login_INIT: RequestInit = {
              method: "POST",
              headers: {
                "Content-Type": "application/json",
                Authorization:
                  "Basic " +
                  new TextEncoder()
                    .encode(username + ":" + password)
                    .toBase64(),
              },
              body: JSON.stringify({
                username: username,
                password: password,
              }),
            };
            fetch(`${PUBLIC_API_URL}/login`, login_INIT).then((response) => {
              if (response.status === 200) globalStates.login = false;
              else console.log("There was an error logging you in.", response);
            });
          }}
        >
          Submit
        </button>
      </div>
    </div>
  </div>
{/if}

<style>
  .login {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background-color: #00000055;
    z-index: 10;
  }

  .login div {
    background-color: #ffffff;
    padding: 1rem;
    border-radius: 1rem;
  }

  .loginButtons {
    flex-direction: row;
  }

  div {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 1rem;
  }
</style>
