import React, {useContext, useEffect, useState} from "react";
import AuthenticationView from "../../components/views/AuthenticationView";
import AppView from "../../components/views/AppView";
import {retrieveCredentials} from "../../utils/credentials";
import {useRouter} from "next/router";

const session = {
    accessToken: null,
    refreshToken: null,
    expiresAt: null,
    store: null,
};
export const SessionContext = React.createContext(session);

export default function App() {
    const [signedIn, setSignedIn] = useState(false);
    const context = useContext(SessionContext);

    const router = useRouter();
    const returnTo = router.query["returnTo"];
    const [returned, setReturned] = useState(false);

    useEffect(() => {
        let credentials = retrieveCredentials(context);
        if (credentials == null) {
            return;
        }
        setSignedIn(true);
    }, []);

    useEffect(() => {
        if (!signedIn || !returnTo || (returnTo && returned)) {
            return;
        }

        if (returnTo) {
            let url = new URL(String(returnTo));
            if (url.hostname === window.location.hostname) {
                setReturned(true);
                // TODO - ensure that the access token is valid (refresh if required)
                // TODO - maybe do this in `retrieveCredentials` instead?
                url.searchParams.append("user", context.accessToken);
                window.location.href = url.toString();
            }
        }
    }, [signedIn]);

    return (
        <SessionContext.Provider value={session}>
            { signedIn ? <AppView/> : <AuthenticationView setSignedIn={setSignedIn}/> }
        </SessionContext.Provider>
    );
}
