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

export default function SignUpBanner({setShowSignUp}) {
    return (
        <div className="flex fixed z-50 gap-8 justify-between items-start py-3 px-4 w-full sm:items-center lg:py-4 bg-dark-mode-grey text-gray-300 animate-fade-in-down">
            <p className="text-sm">Thanks for signing up to Notcord! An email has been sent to your inbox in order
                to verify your email address.</p>
            <button onClick={() => setShowSignUp({showSelf: false, showBanner: false})} type="button"
                    className="flex items-center text-gray-400 rounded-lg text-sm p-1.5 hover:bg-gray-600 hover:text-white">
                <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                    <path fillRule="evenodd"
                          d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                          clipRule="evenodd"></path>
                </svg>
            </button>
        </div>
    );
}
