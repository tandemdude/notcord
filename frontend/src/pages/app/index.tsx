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

import React, { useEffect, useState } from "react";
import AuthenticationView from "../../components/views/AuthenticationView";
import AppView from "../../components/views/AppView";
import { retrieveCredentials } from "../../utils/credentials";

export default function App() {
    const [signedIn, setSignedIn] = useState(false);

    const [context, setContext] = useState({accessToken: null, refreshToken: null, expiresAt: null, store: null});

    useEffect(() => {
        let credentials = retrieveCredentials(context);
        if (credentials == null) {
            return;
        }
        setSignedIn(true);
    }, []);

    return signedIn ? <AppView/> : <AuthenticationView setSignedIn={setSignedIn} setContext={setContext}/>;
}
