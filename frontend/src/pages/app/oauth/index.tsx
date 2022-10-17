import {useEffect, useState} from "react";
import SignInForm from "../../../components/forms/SignInForm";
import {retrieveCredentials} from "../../../utils/credentials";
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
        console.log(credentials);
        if (credentials === null || context.accessToken === null) {
            setShowSignIn(true);
            return;
        }

        // @ts-ignore
        let returned = handleReturn(params.returnTo, credentials.accessToken);
        if (!returned) {
            window.location.href = "/404";
        }
    }, [])

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
    }, [signedIn])
    // If credentials can be retrieved from browser storage, immediately redirect to given URL
    // If credentials cannot be retrieved then display sign in form (exclude sign up link and forgot password)
    return (
        <Card>
            {showSignIn ? <SignInForm setShowSignUp={(_) => {}} setSignedIn={setSignedIn} setContext={setContext}/> : <p>Loading</p>}
        </Card>
    );
}
