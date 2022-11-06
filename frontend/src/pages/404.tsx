/*
 * Copyright 2022 tandemdude
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
