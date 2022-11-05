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

import { useEffect, useState } from "react";
import SignInForm from "../../../components/forms/SignInForm";
import { retrieveCredentials } from "../../../utils/credentials";
import Card from "../../../components/Card";

function handleReturn(returnTo: string, accessToken: string): boolean {
    let url = new URL(String(returnTo));
    if (url.hostname === window.location.hostname) {
        // TODO - ensure that the access token is valid (refresh if required)
        // TODO - maybe do this in `retrieveCredentials` instead?
        url.searchParams.append("userToken", accessToken);
        window.location.href = url.toString();
        return true;
    }
    return false;
}

export default function Oauth(): JSX.Element {
    const [showSignIn, setShowSignIn] = useState(false);
    const [signedIn, setSignedIn] = useState(false);
    const [context, setContext] = useState({accessToken: null, refreshToken: null, expiresAt: null, store: null});

    useEffect(() => {
        // @ts-ignore
        let params = new Proxy(new URLSearchParams(window.location.search), {get: (sp, p) => sp.get(p)});
        let credentials = retrieveCredentials(context);
        if (credentials === null || context.accessToken === null) {
            setShowSignIn(true);
            return;
        }

        // TODO - we need to make sure the access token is valid - refresh if necessary
        // @ts-ignore
        let returned = handleReturn(params.returnTo, credentials.accessToken);
        if (!returned) {
            window.location.href = "/404";
        }
    }, []);

    useEffect(() => {
        if (signedIn === false) {
            return;
        }

        // @ts-ignore
        let params = new Proxy(new URLSearchParams(window.location.search), {get: (sp, p) => sp.get(p)});
        // @ts-ignore
        let returned = handleReturn(params.returnTo, context.accessToken);
        if (!returned) {
            window.location.href = "/404";
        }
    }, [signedIn]);

    // Display sign-in if credentials are not available
    return (
        <Card>
            {showSignIn ? <SignInForm setShowSignUp={(_) => {
            }} setSignedIn={setSignedIn} setContext={setContext}/> : <p>Loading</p>}
        </Card>
    );
}
