import { useFormik } from "formik";
import {useContext, useState} from "react";
import {SubmitButtonInteractible, SubmitButtonLoading} from "./SubmitButton";
import axios from "axios";
import {SessionContext} from "../../pages/app";
import {saveCredentials} from "../../utils/credentials";

export default function SignInForm({ setShowSignUp, setSignedIn }) {
    const [loading, setLoading] = useState(false);
    const [incorrectCredentials, setIncorrectCredentials] = useState(false);
    const context = useContext(SessionContext);

    const formik = useFormik({
        initialValues: {
            email: "",
            password: "",
            remember: false,
        },
        onSubmit: values => {
            setLoading(true);
            axios.post("http://localhost:8080/client/sign-in", values)
                .then(response => {
                    if (response.status == 200) {
                        context.store = values.remember ? "persistent" : "session";
                        context.accessToken = response.data.access_token;
                        context.refreshToken = response.data.refresh_token;

                        let expiresIn = parseInt(response.data.expires_in);
                        context.expiresAt = Math.floor(Date.now() / 1000) + expiresIn;
                        setSignedIn(true);

                        saveCredentials(context);
                    }
                })
                .catch(error => {
                    if (error.response && error.response.status == 401) {
                        setLoading(false);
                        setIncorrectCredentials(true);
                    } else {
                        alert("An unknown error occurred: " + error.message);
                    }
                })
        }
    })

    return (
        <form className="sign-in-form space-y-6 relative" onSubmit={formik.handleSubmit}>
            <h5 className="text-xl font-medium text-white">Sign in to Notcord</h5>
            <div>
                <label htmlFor="email" className="block mb-2 text-sm font-medium text-gray-300">Email</label>
                <input id="email" name="email" type="email" onChange={formik.handleChange} value={formik.values.email} placeholder="name@notcord.io" required={true} className="border text-sm rounded-lg block w-full p-2.5 bg-gray-600 border-gray-500 placeholder-gray-400 text-white"/>
            </div>
            <div>
                <label htmlFor="password" className="block mb-2 text-sm font-medium text-gray-300">Password</label>
                <input id="password" name="password" type="password" onChange={formik.handleChange} value={formik.values.password} placeholder="••••••••" required={true} className="border text-sm rounded-lg block w-full p-2.5 bg-gray-600 border-gray-500 placeholder-gray-400 text-white"/>
                { incorrectCredentials ? <p id="incorrect-credentials-alert" className="text-rose-400 text-xs">Password is incorrect, or an account with the given email does not exist</p> : null }
            </div>
            <div className="flex items-start">
                <div className="flex items-start">
                    <div className="flex items-center h-5">
                        <input id="remember" type="checkbox" className="w-4 h-4 rounded bg-gray-700" onChange={formik.handleChange} checked={formik.values.remember}/>
                    </div>
                    <div className="ml-3 text-sm">
                        <label htmlFor="remember" className="text-gray-300">Remember me</label>
                    </div>
                </div>
                <a href="#" className="ml-auto text-sm hover:underline text-indigo-500">Lost Password?</a>
            </div>
            { loading ? <SubmitButtonLoading/> : <SubmitButtonInteractible content={"Sign In"}/>}
            <div className="text-sm font-medium text-gray-300">
                Not registered? <a onClick={() => setShowSignUp({showSelf: true, showBanner: false})} className="hover:underline text-indigo-500 cursor-pointer">Create account</a>
            </div>
        </form>
    )
}
