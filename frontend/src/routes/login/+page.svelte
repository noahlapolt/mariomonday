<script lang="ts">
  import { PUBLIC_API_URL } from "$env/static/public";

  let username = $state();
  let password = $state();
</script>

<h1>Login</h1>
<label>
  Username
  <input bind:value={username} type="text" />
</label>
<label>
  Password
  <input bind:value={password} type="password" />
</label>
<button
  onclick={() => {
    const Login_INIT: RequestInit = {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        username: username,
        password: password,
      }),
    };
    fetch(`${PUBLIC_API_URL}/login`, Login_INIT)
      .then((response) => response.text())
      .then((data) => {
        console.log(data);
      })
      .catch((e) => {
        console.log("There was an error logging you in.", e);
      });
  }}>Submit</button
>
