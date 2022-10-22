export default function Card({children}) {
    return (
        <div className="flex relative px-10 min-h-screen min-w-screen">
            <div className="relative p-4 w-full max-w-lg rounded-lg border shadow-md sm:p-6 md:p-8 bg-dark-mode-grey border-gray-700 m-auto">
                <div className="absolute top-0 -left-4 w-72 h-72 bg-indigo-700 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob -z-10"></div>
                <div className="absolute top-0 -right-4 w-72 h-72 bg-yellow-200 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob animation-delay-2000 -z-10"></div>
                <div className="absolute -bottom-12 left-20 w-72 h-72 bg-pink-400 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob animation-delay-4000 -z-10"></div>
                {children}
            </div>
        </div>
    );
}
