<!DOCTYPE html>
<html lang="en">

<#import "components.ftlh" as components>

<head>
    <@components.header/>
</head>

<body class="static bg-slate-100 min-h-screen min-w-screen dark">
    <div class="flex px-10 min-h-screen min-w-screen">
        <div class="relative p-4 w-full max-w-lg rounded-lg border shadow-md sm:p-6 md:p-8 bg-dark-mode-grey border-gray-700 m-auto">
            <@components.floatingCircles/>
            <div>
                <p class="text-4xl text-center text-gray-300 pb-8">${title}</p>
                <p class="text-lg text-center text-gray-300">${message}</p>
            </div>
            <div class="flex mt-8">
                <a href="/app" class="relative m-auto text-white font-medium rounded-lg text-sm px-5 py-2.5 text-center bg-indigo-500 hover:bg-indigo-700">Return to app</a>
            </div>
        </div>
    </div>
</body>
</html>
