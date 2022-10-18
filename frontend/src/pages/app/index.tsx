import React, {useEffect, useState} from "react";
import AuthenticationView from "../../components/views/AuthenticationView";
import AppView from "../../components/views/AppView";
import {retrieveCredentials} from "../../utils/credentials";

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
