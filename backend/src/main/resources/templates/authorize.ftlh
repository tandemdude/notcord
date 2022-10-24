<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="/output.css" rel="stylesheet">
    <title>Notcord</title>
</head>

<body class="static bg-slate-100 min-h-screen min-w-screen dark">
<div class="flex px-10 min-h-screen min-w-screen">
    <div class="relative p-4 w-full max-w-lg rounded-lg border shadow-md sm:p-6 md:p-8 bg-dark-mode-grey border-gray-700 m-auto">
        <div class="absolute top-0 -left-4 w-72 h-72 bg-indigo-700 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob -z-10"></div>
        <div class="absolute top-0 -right-4 w-72 h-72 bg-yellow-200 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob animation-delay-2000 -z-10"></div>
        <div class="absolute -bottom-12 left-20 w-72 h-72 bg-pink-400 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob animation-delay-4000 -z-10"></div>
        <div class="space-y-6 relative">
            <div>
                <div class="flex justify-center mb-4">
                    <div class="flex flex-col mx-4 my-2">
                        ${userIcon?no_esc}
                        <h5 class="text-base font-medium text-gray-300 text-center pt-2">${userName}</h5>
                    </div>
                    <div class="flex my-auto mx-4">
                        <svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" width="32" height="32"
                             viewBox="0 0 41.915 41.916" xml:space="preserve" fill="#666f78" class="animate-pulse">
                            <g>
                                <path d="M11.214,20.956c0,3.091-2.509,5.589-5.607,5.589C2.51,26.544,0,24.046,0,20.956c0-3.082,2.511-5.585,5.607-5.585 C8.705,15.371,11.214,17.874,11.214,20.956z"/>
                                <path d="M26.564,20.956c0,3.091-2.509,5.589-5.606,5.589c-3.097,0-5.607-2.498-5.607-5.589c0-3.082,2.511-5.585,5.607-5.585 C24.056,15.371,26.564,17.874,26.564,20.956z"/>
                                <path d="M41.915,20.956c0,3.091-2.509,5.589-5.607,5.589c-3.097,0-5.606-2.498-5.606-5.589c0-3.082,2.511-5.585,5.606-5.585 C39.406,15.371,41.915,17.874,41.915,20.956z"/>
                            </g>
                        </svg>
                    </div>
                    <div class="flex flex-col mx-4 my-2">
                        ${appIcon?no_esc}
                        <h5 class="text-base font-medium text-gray-300 text-center pt-2">${appName}</h5>
                    </div>
                </div>
                <h5 class="text-md font-medium text-gray-300 text-center"><span class="text-white">${appName}</span> is
                    requesting access to your account</h5>
                <div class="flex text-gray-500 text-center pt-2 text-xs justify-center">
                    <p>Logged in as <span class="text-gray-300">${userName}</span>.&nbsp;</p>
                    <a class="text-indigo-500 hover:underline cursor-pointer" href="${appBaseUrl}/logout">Not you?</a>
                </div>
            </div>
            <hr class="w-full border-gray-700 m-auto">
            <div class="text-white">
                <p class="block mb-2 text-md font-medium text-gray-300">This will allow <span class="text-white">${appName}</span> to:</p>
                <div class="grid text-sm text-gray-300">
                    <ul>
                        <#list scopes as scope>
                            <li><span class="text-indigo-500">•</span> ${scope}</li>
                        </#list>
                    </ul>
                </div>
            </div>
            <hr class="w-full border-gray-700 m-auto">
            <div class="text-gray-500 text-xs">
                <ul>
                    <li><span class="text-indigo-500">•</span> After you authorize, you will be redirected
                        to ${redirectUri}</li>
                    <li><span class="text-indigo-500">•</span> The developer of ${appName}'s TOS and privacy policy
                        apply to this app
                    </li>
                    <li><span class="text-indigo-500">•</span> This app cannot read or send messages on your behalf</li>
                </ul>
            </div>
            <div class="flex items-center">
                <a href="/oauth/complete?token=${denyToken}&userToken=${userToken}"
                   class="w-full mr-4 text-white bg-red-500 hover:bg-red-700 focus:outline-none font-medium rounded-lg text-sm px-5 py-2.5 text-center">Deny</a>
                <a href="/oauth/complete?token=${allowToken}&userToken=${userToken}"
                   class="w-full ml-4 text-white bg-indigo-500 hover:bg-indigo-700 focus:outline-none font-medium rounded-lg text-sm px-5 py-2.5 text-center">Authorize</a>
            </div>
        </div>
    </div>
</div>
</body>
</html>
