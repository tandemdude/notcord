<#macro header>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="/output.css" rel="stylesheet">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script src="/js.cookie-1.5.1.min.js"></script>
    <script src="/flash.js"></script>
    <title>Notcord</title>
</#macro>

<#macro floatingCircles>
    <div class="absolute top-0 -left-4 w-72 h-72 bg-indigo-700 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob -z-10"></div>
    <div class="absolute top-0 -right-4 w-72 h-72 bg-yellow-200 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob animation-delay-2000 -z-10"></div>
    <div class="absolute -bottom-12 left-20 w-72 h-72 bg-pink-400 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob animation-delay-4000 -z-10"></div>
</#macro>

<#macro flashes>
    <div class="flex fixed w-full py-3 px-4 z-50">
        <div class="ml-auto flex flex-col relative">
            <button id="notification-button" onclick="toggleDropdown()" class="ml-auto inline-flex items-center text-sm font-medium text-center focus:outline-none hover:text-gray-300 text-dark-mode-grey" type="button">
                <svg class="w-6 h-6" aria-hidden="true" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path d="M10 2a6 6 0 00-6 6v3.586l-.707.707A1 1 0 004 14h12a1 1 0 00.707-1.707L16 11.586V8a6 6 0 00-6-6zM10 18a3 3 0 01-3-3h6a3 3 0 01-3 3z"></path></svg>
                <div class="flex relative w-2 h-2 -top-2 right-3">
                    <div id="ping" class="inline-flex relative w-full h-full bg-indigo-500 rounded-full animate-ping"></div>
                </div>
            </button>
            <div id="notification-content" class="w-52 rounded shadow bg-dark-mode-grey transition-all duration-300 ease-in-out opacity-0 mt-2">
                <ul id="notif-list" class="py-1 text-xs text-gray-300 break-words divide-y divide-gray-600" aria-labelledby="notification-button"></ul>
                <p id="empty-notifs" class="hidden text-gray-300 text-xs py-2 px-4 border-0">No notifications yet...</p>
            </div>
        </div>
        <script>
            function toggleDropdown() {
                let elem = document.getElementById("notification-content");
                if (elem.classList.contains("opacity-0")) {
                    elem.classList.remove("opacity-0");
                } else {
                    elem.classList.add("opacity-0");
                }
            }

            function handleNoNotifs() {
                $("#notif-list").hide();
                $("#empty-notifs").show();
                $("#ping").hide();
            }

            function handleNotifAcknowledge() {
                this.parentNode.parentNode.removeChild(this.parentNode);

                if (document.querySelectorAll("#notif-list li").length === 0) {
                    handleNoNotifs();
                }
            }

            function createNotif(message) {
                let elem = document.createElement("li")
                elem.setAttribute("class", "flex justify-between");
                elem.innerHTML = `
                    <p class="block py-2 px-4">${r"${message}"}</p>
                    <button class="notif-hide flex items-center text-gray-300 rounded-lg text-sm p-1.5 hover:text-white">
                        <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"></path></svg>
                    </button>
                `
                return elem;
            }

            $(document).ready(() => {
                let messages = getFlashes();

                if (messages.length === 0) {
                    handleNoNotifs();
                }

                let notifList = document.getElementById("notif-list");
                for (const msg of messages) {
                    notifList.appendChild(createNotif(msg));
                }

                const lis = [...document.querySelectorAll("#notif-list li .notif-hide")]

                for (const li of lis) {
                    li.addEventListener("click", handleNotifAcknowledge)
                }
            });
        </script>
    </div>
</#macro>

<#macro submitBtn content>
    <button id="submit-btn" type="submit" class="flex w-full text-white font-medium rounded-lg text-sm px-5 py-2.5 text-center bg-indigo-500 hover:bg-indigo-700">
        <div class="flex m-auto">
            <svg id="loading-spinner" class="inline mr-2 my-auto w-4 h-4 animate-spin text-gray-300" viewBox="0 0 100 101" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z" fill="currentColor"/><path d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z" fill="#4338CA"/></svg>
            <p id="submit-btn-content">${content}</p>
        </div>
    </button>
    <script>
        const SubmitButton = {
            setInteractable: function () {
                let elem = $("#submit-btn");
                elem.addClass("hover:bg-indigo-700 bg-indigo-500");
                elem.removeClass("bg-indigo-700");
                $("#submit-btn-content").text("${content}");
                $("#loading-spinner").hide();
            },
            setLoading: function () {
                let elem = $("#submit-btn");
                elem.prop("disabled", true);
                elem.removeClass("hover:bg-indigo-700 bg-indigo-500");
                elem.addClass("bg-indigo-700");
                $("#submit-btn-content").text("Loading");
                $("#loading-spinner").show();
            },
        }

        $(document).ready(() => {
            SubmitButton.setInteractable();
        });
    </script>
</#macro>
