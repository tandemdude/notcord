<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="/output.css" rel="stylesheet">
    <title>Notcord</title>
</head>
<body class="static bg-slate-100 flex min-h-screen min-w-screen dark px-10">
    <div class="relative p-4 w-full max-w-lg rounded-lg border shadow-md sm:p-6 md:p-8 bg-dark-mode-grey border-gray-700 m-auto">
        <div class="absolute top-0 -left-4 w-72 h-72 bg-indigo-700 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob -z-10"></div>
        <div class="absolute top-0 -right-4 w-72 h-72 bg-yellow-200 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob animation-delay-2000 -z-10"></div>
        <div class="absolute -bottom-12 left-20 w-72 h-72 bg-pink-400 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob animation-delay-4000 -z-10"></div>
        <div class="space-y-6 relative">
            <div>
                <h5 class="text-xl font-medium text-white">${appName}</h5>
                <h5 class="text-md font-medium text-gray-300">is requesting access to your account</h5>
            </div>
            <hr class="w-full border-gray-700 m-auto">
            <div class="text-white">
                <p class="block mb-2 text-md font-medium text-gray-300">This will allow ${appName} to:</p>
                <div class="grid grid-cols-2 text-sm">
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
                    <li><span class="text-indigo-500">•</span> After you authorize, you will be redirected to ${redirectUri}</li>
                    <li><span class="text-indigo-500">•</span> The developer of ${appName}'s TOS and privacy policy apply to this app</li>
                    <li><span class="text-indigo-500">•</span> This app cannot read or send messages on your behalf</li>
                </ul>
            </div>
            <div class="flex items-center">
                <a href="/auth/oauth/complete?token=${denyToken}" class="w-full mr-4 text-white bg-red-500 hover:bg-red-700 focus:outline-none font-medium rounded-lg text-sm px-5 py-2.5 text-center">Deny</a>
                <a href="/auth/oauth/complete?token=${allowToken}" class="w-full ml-4 text-white bg-indigo-500 hover:bg-indigo-700 focus:outline-none font-medium rounded-lg text-sm px-5 py-2.5 text-center">Authorize</a>
            </div>
        </div>
    </div>
</body>
</html>
