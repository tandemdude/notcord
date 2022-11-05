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

import SignUpBanner from "../SignUpBanner";
import Card from "../Card";
import SignUpForm from "../forms/SignUpForm";
import SignInForm from "../forms/SignInForm";
import React, { useState } from "react";

export default function AuthenticationView({setSignedIn, setContext}) {
    const [showSignUp, setShowSignUp] = useState({
        showSelf: false,
        showBanner: false,
    });

    return (
        <>
            {showSignUp.showBanner && <SignUpBanner setShowSignUp={setShowSignUp}/>}
            <Card>
                {showSignUp.showSelf ? <SignUpForm setShowSignUp={setShowSignUp}/> :
                    <SignInForm setShowSignUp={setShowSignUp} setSignedIn={setSignedIn}
                                setContext={setContext}/>}
            </Card>
        </>
    );
}
