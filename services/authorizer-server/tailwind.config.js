/** @type {import("tailwindcss").Config} */
module.exports = {
    content: ["./src/**/*.{html,js,ftlh}"],
    theme: {
        extend: {
            animation: {
                blob: "blob 7s infinite",
                "fade-in-down": "fade-in-down 0.5s ease-out",
            },
            keyframes: {
                blob: {
                    "0%": {
                        transform: "translate(0px, 0px) scale(1)",
                    },
                    "33%": {
                        transform: "translate(30px, -50px) scale(1.1)",
                    },
                    "66%": {
                        transform: "translate(-20px, 20px) scale(0.9)",
                    },
                    "100%": {
                        transform: "translate(0px, 0px) scale(1)",
                    },
                },
                "fade-in-down": {
                    "0%": {
                        opacity: "0",
                        transform: "translateY(-10px)"
                    },
                    "100%": {
                        opacity: "1",
                        transform: "translateY(0)"
                    },
                },
            },
            colors: {
                "dark-mode-grey": "#2C2F33",
            }
        },
    },
    plugins: [],
    darkMode: "class",
}
