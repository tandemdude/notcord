import Link from "next/link"

export default function LandingPage() {
    return (
            <div className="m-auto">
                <Link href="/app">
                    <a>GOTO app</a>
                </Link>
            </div>
    )
}
