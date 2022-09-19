<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="/output.css" rel="stylesheet">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <title>Notcord</title>
</head>
<body class="static bg-slate-100 flex min-h-screen min-w-screen dark px-10">
    <div class="relative p-4 w-full max-w-lg rounded-lg border shadow-md sm:p-6 md:p-8 bg-dark-mode-grey border-gray-700 m-auto">
        <div class="absolute top-0 -left-4 w-72 h-72 bg-indigo-700 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob -z-10"></div>
        <div class="absolute top-0 -right-4 w-72 h-72 bg-yellow-200 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob animation-delay-2000 -z-10"></div>
        <div class="absolute -bottom-12 left-20 w-72 h-72 bg-pink-400 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob animation-delay-4000 -z-10"></div>
        <form class="space-y-6 relative" action="#" name="signup" id="signup-form" onsubmit="validateForm()">
            <h5 class="text-xl font-medium text-white">Sign up to Notcord</h5>
            <div>
                <label for="email" class="block mb-2 text-sm font-medium text-gray-300">Email</label>
                <input type="email" name="email" id="email" class="border text-sm rounded-lg block w-full p-2.5 bg-gray-600 border-gray-500 placeholder-gray-400 text-white" placeholder="name@notcord.io" required style="background-image: url(&quot;data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAASCAYAAABSO15qAAAAAXNSR0IArs4c6QAAAPhJREFUOBHlU70KgzAQPlMhEvoQTg6OPoOjT+JWOnRqkUKHgqWP4OQbOPokTk6OTkVULNSLVc62oJmbIdzd95NcuGjX2/3YVI/Ts+t0WLE2ut5xsQ0O+90F6UxFjAI8qNcEGONia08e6MNONYwCS7EQAizLmtGUDEzTBNd1fxsYhjEBnHPQNG3KKTYV34F8ec/zwHEciOMYyrIE3/ehKAqIoggo9inGXKmFXwbyBkmSQJqmUNe15IRhCG3byphitm1/eUzDM4qR0TTNjEixGdAnSi3keS5vSk2UDKqqgizLqB4YzvassiKhGtZ/jDMtLOnHz7TE+yf8BaDZXA509yeBAAAAAElFTkSuQmCC&quot;); background-repeat: no-repeat; background-attachment: scroll; background-size: 16px 18px; background-position: 98% 50%;">
            </div>
            <div>
                <label for="username" class="block mb-2 text-sm font-medium text-gray-300">Username</label>
                <input type="text" name="username" id="username" minlength="5" maxlength="40" class="border text-sm rounded-lg block w-full p-2.5 bg-gray-600 border-gray-500 placeholder-gray-400 text-white" placeholder="notcorduser" required style="background-image: url(&quot;data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAASCAYAAABSO15qAAAAAXNSR0IArs4c6QAAAPhJREFUOBHlU70KgzAQPlMhEvoQTg6OPoOjT+JWOnRqkUKHgqWP4OQbOPokTk6OTkVULNSLVc62oJmbIdzd95NcuGjX2/3YVI/Ts+t0WLE2ut5xsQ0O+90F6UxFjAI8qNcEGONia08e6MNONYwCS7EQAizLmtGUDEzTBNd1fxsYhjEBnHPQNG3KKTYV34F8ec/zwHEciOMYyrIE3/ehKAqIoggo9inGXKmFXwbyBkmSQJqmUNe15IRhCG3byphitm1/eUzDM4qR0TTNjEixGdAnSi3keS5vSk2UDKqqgizLqB4YzvassiKhGtZ/jDMtLOnHz7TE+yf8BaDZXA509yeBAAAAAElFTkSuQmCC&quot;); background-repeat: no-repeat; background-attachment: scroll; background-size: 16px 18px; background-position: 98% 50%;">
                <p id="username-taken-alert" class="text-rose-400 text-xs">Username is not available</p>
            </div>
            <div>
                <label for="password" class="block mb-2 text-sm font-medium text-gray-300">Password</label>
                <input type="password" name="password" id="password" minlength="12" placeholder="••••••••" class="border text-sm rounded-lg block w-full p-2.5 bg-gray-600 border-gray-500 placeholder-gray-400 text-white" required style="background-image: url(&quot;data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAASCAYAAABSO15qAAAAAXNSR0IArs4c6QAAAPhJREFUOBHlU70KgzAQPlMhEvoQTg6OPoOjT+JWOnRqkUKHgqWP4OQbOPokTk6OTkVULNSLVc62oJmbIdzd95NcuGjX2/3YVI/Ts+t0WLE2ut5xsQ0O+90F6UxFjAI8qNcEGONia08e6MNONYwCS7EQAizLmtGUDEzTBNd1fxsYhjEBnHPQNG3KKTYV34F8ec/zwHEciOMYyrIE3/ehKAqIoggo9inGXKmFXwbyBkmSQJqmUNe15IRhCG3byphitm1/eUzDM4qR0TTNjEixGdAnSi3keS5vSk2UDKqqgizLqB4YzvassiKhGtZ/jDMtLOnHz7TE+yf8BaDZXA509yeBAAAAAElFTkSuQmCC&quot;); background-repeat: no-repeat; background-attachment: scroll; background-size: 16px 18px; background-position: 98% 50%;">
            </div>
            <div>
                <label for="password-confirm" class="block mb-2 text-sm font-medium text-gray-300">Confirm Password</label>
                <input type="password" name="password-confirm" id="password-confirm" placeholder="••••••••" class="border text-sm rounded-lg block w-full p-2.5 bg-gray-600 border-gray-500 placeholder-gray-400 text-white" required style="background-image: url(&quot;data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAASCAYAAABSO15qAAAAAXNSR0IArs4c6QAAAPhJREFUOBHlU70KgzAQPlMhEvoQTg6OPoOjT+JWOnRqkUKHgqWP4OQbOPokTk6OTkVULNSLVc62oJmbIdzd95NcuGjX2/3YVI/Ts+t0WLE2ut5xsQ0O+90F6UxFjAI8qNcEGONia08e6MNONYwCS7EQAizLmtGUDEzTBNd1fxsYhjEBnHPQNG3KKTYV34F8ec/zwHEciOMYyrIE3/ehKAqIoggo9inGXKmFXwbyBkmSQJqmUNe15IRhCG3byphitm1/eUzDM4qR0TTNjEixGdAnSi3keS5vSk2UDKqqgizLqB4YzvassiKhGtZ/jDMtLOnHz7TE+yf8BaDZXA509yeBAAAAAElFTkSuQmCC&quot;); background-repeat: no-repeat; background-attachment: scroll; background-size: 16px 18px; background-position: 98% 50%;">
                <p id="pass-conf-alert" class="text-rose-400 text-xs">Passwords do not match</p>
            </div>
            <button id="submit-btn" type="submit" class="w-full text-white bg-indigo-500 hover:bg-indigo-700 focus:outline-none font-medium rounded-lg text-sm px-5 py-2.5 text-center">
                Sign Up
            </button>
            <div class="text-sm font-medium text-gray-300">
                Already registered? <a href="/app/signIn" class="hover:underline text-indigo-500">Sign in</a>
            </div>
        </form>
    </div>
    <script>
        document.getElementById("signup-form").addEventListener("submit", (event) => {
            event.preventDefault();

            if (validateForm()) {
                let submitBtn = document.getElementById("submit-btn");
                submitBtn.disabled = true;
                submitBtn.classList.remove("hover:bg-indigo-700", "bg-indigo-500");
                submitBtn.classList.add("bg-indigo-700");
                submitBtn.innerHTML = '<svg class="inline mr-2 my-auto w-4 h-4 animate-spin text-gray-300" viewBox="0 0 100 101" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z" fill="currentColor"/><path d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z" fill="#4338CA"/></svg>Loading';

                $.ajax({
                    url: "/app/signUp",
                    method: "POST",
                    data: JSON.stringify({
                        email: document.getElementById("email").value,
                        username: document.getElementById("username").value,
                        password: document.getElementById("password").value,
                    }),
                    contentType: "application/json",
                    complete: (jqXHR, textStatus) => {
                        console.log("Signup request status: " + textStatus);
                        if (jqXHR.status === 200) {
                            window.location.href = "/app/signIn?ctx=signup";
                        } else if (jqXHR.status === 409) {
                            $("#username-taken-alert").show();
                            submitBtn.disabled = false;
                            submitBtn.classList.add("hover:bg-indigo-700", "bg-indigo-500");
                            submitBtn.classList.remove("bg-indigo-700");
                            submitBtn.innerHTML = "Sign in";
                        }
                    }
                })
            }
        })

        function validateForm() {
            let password = document.forms["signup"]["password"].value;
            if (password !== document.forms["signup"]["password-confirm"].value) {
                $("#pass-conf-alert").show();
            } else {
                $("#pass-conf-alert").hide();
            }
            return password === document.forms["signup"]["password-confirm"].value;
        }

        $(document).ready(() => {
            $("#pass-conf-alert").hide();
            $("#username-taken-alert").hide();
        });
    </script>
</body>
</html>
