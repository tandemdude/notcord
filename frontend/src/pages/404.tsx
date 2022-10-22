import Card from "../components/Card";
import Link from "next/link";

export default function NotFound() {
    return (
            <Card>
                <div>
                    <p className="text-7xl text-center text-gray-300 animate-pulse">404</p>
                    <p className="text-3xl text-center text-gray-300">Page not found.</p>
                </div>
                <div className="flex mt-12">
                    <Link href="/app">
                        <a className="relative m-auto text-white font-medium rounded-lg text-sm px-5 py-2.5 text-center bg-indigo-500 hover:bg-indigo-700">Return
                            to app</a>
                    </Link>
                </div>
            </Card>
    );
}
