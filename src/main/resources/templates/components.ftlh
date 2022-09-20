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
