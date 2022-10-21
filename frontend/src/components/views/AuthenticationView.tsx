import SignUpBanner from "../SignUpBanner";
import Card from "../Card";
import SignUpForm from "../forms/SignUpForm";
import SignInForm from "../forms/SignInForm";
import React, {useState} from "react";

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
