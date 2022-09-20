<!DOCTYPE html>
<html lang="en">

<#import "components.ftlh" as components>

<head>
    <@components.header/>
</head>

<body class="static bg-slate-100 min-h-screen min-w-screen dark">
    <#if ref?? && ref?lower_case == "signup">
        <div id="banner" class="flex fixed z-50 gap-8 justify-between items-start py-3 px-4 w-full sm:items-center lg:py-4 bg-dark-mode-grey text-gray-300 animate-fade-in-down">
            <p class="text-sm">Thanks for signing up to Notcord! An email has been sent to your inbox in order to verify your email address.</p>
            <button onclick="$('#banner').hide()" type="button" class="flex items-center text-gray-400 rounded-lg text-sm p-1.5 hover:bg-gray-600 hover:text-white">
                <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"></path></svg>
            </button>
        </div>
    </#if>
    <div class="flex relative px-10 min-h-screen min-w-screen">
        <div class="relative p-4 w-full max-w-lg rounded-lg border shadow-md sm:p-6 md:p-8 bg-dark-mode-grey border-gray-700 m-auto">
            <@components.floatingCircles/>
            <form id="signin-form" class="space-y-6 relative" action="#">
                <h5 class="text-xl font-medium text-white">Sign in to Notcord</h5>
                <div>
                    <label for="email" class="block mb-2 text-sm font-medium text-gray-300">Email</label>
                    <input type="email" name="email" id="email" class="border text-sm rounded-lg block w-full p-2.5 bg-gray-600 border-gray-500 placeholder-gray-400 text-white" placeholder="name@notcord.io" required style="background-image: url(&quot;data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAASCAYAAABSO15qAAAAAXNSR0IArs4c6QAAAPhJREFUOBHlU70KgzAQPlMhEvoQTg6OPoOjT+JWOnRqkUKHgqWP4OQbOPokTk6OTkVULNSLVc62oJmbIdzd95NcuGjX2/3YVI/Ts+t0WLE2ut5xsQ0O+90F6UxFjAI8qNcEGONia08e6MNONYwCS7EQAizLmtGUDEzTBNd1fxsYhjEBnHPQNG3KKTYV34F8ec/zwHEciOMYyrIE3/ehKAqIoggo9inGXKmFXwbyBkmSQJqmUNe15IRhCG3byphitm1/eUzDM4qR0TTNjEixGdAnSi3keS5vSk2UDKqqgizLqB4YzvassiKhGtZ/jDMtLOnHz7TE+yf8BaDZXA509yeBAAAAAElFTkSuQmCC&quot;); background-repeat: no-repeat; background-attachment: scroll; background-size: 16px 18px; background-position: 98% 50%;">
                </div>
                <div>
                    <label for="password" class="block mb-2 text-sm font-medium text-gray-300">Password</label>
                    <input type="password" name="password" id="password" placeholder="••••••••" class="border text-sm rounded-lg block w-full p-2.5 bg-gray-600 border-gray-500 placeholder-gray-400 text-white" required style="background-image: url(&quot;data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAASCAYAAABSO15qAAAAAXNSR0IArs4c6QAAAPhJREFUOBHlU70KgzAQPlMhEvoQTg6OPoOjT+JWOnRqkUKHgqWP4OQbOPokTk6OTkVULNSLVc62oJmbIdzd95NcuGjX2/3YVI/Ts+t0WLE2ut5xsQ0O+90F6UxFjAI8qNcEGONia08e6MNONYwCS7EQAizLmtGUDEzTBNd1fxsYhjEBnHPQNG3KKTYV34F8ec/zwHEciOMYyrIE3/ehKAqIoggo9inGXKmFXwbyBkmSQJqmUNe15IRhCG3byphitm1/eUzDM4qR0TTNjEixGdAnSi3keS5vSk2UDKqqgizLqB4YzvassiKhGtZ/jDMtLOnHz7TE+yf8BaDZXA509yeBAAAAAElFTkSuQmCC&quot;); background-repeat: no-repeat; background-attachment: scroll; background-size: 16px 18px; background-position: 98% 50%;">
                    <p id="incorrect-credentials-alert" class="text-rose-400 text-xs">Password is incorrect, or an account with the given email does not exist</p>
                </div>
                <div class="flex items-start">
                    <a href="#" class="ml-auto text-sm hover:underline text-indigo-500">Lost Password?</a>
                </div>
                <@components.submitBtn content="Sign In"/>
                <div class="text-sm font-medium text-gray-300">
                    Not registered? <a href="/app/signUp" class="hover:underline text-indigo-500">Create account</a>
                </div>
            </form>
        </div>
    </div>
    <script>
        document.getElementById("signin-form").addEventListener("submit", (event) => {
            event.preventDefault();
            SubmitButton.setLoading();

            $.ajax({
                url: "/app/signIn",
                method: "POST",
                data: JSON.stringify({
                    email: document.getElementById("email").value,
                    password: document.getElementById("password").value,
                }),
                contentType: "application/json",
                complete: (jqXHR, textStatus) => {
                    console.log("Signin request status: " + textStatus);
                    if (jqXHR.status === 200) {
                        $.cookie("session", JSON.parse(jqXHR.responseText).access_token);
                        window.location.href = "/app";
                    } else if (jqXHR.status === 401) {
                        $("#incorrect-credentials-alert").show();
                        SubmitButton.setInteractable();
                    }
                }
            })
        });

        $(document).ready(() => {
            $("#incorrect-credentials-alert").hide();
        })
    </script>
</body>
</html>
