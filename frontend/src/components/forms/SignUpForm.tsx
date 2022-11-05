import { useFormik } from "formik";
import { useState } from "react";
import { SubmitButtonInteractible, SubmitButtonLoading } from "./SubmitButton";
import axios from "axios";

export default function SignUpForm({setShowSignUp}) {
    const [loading, setLoading] = useState(false);
    const [usernameTaken, setUsernameTaken] = useState(false);
    const [usernameValid, setUsernameValid] = useState(true);
    const [passwordsMatch, setPasswordsMatch] = useState(true);

    const formik = useFormik({
        initialValues: {
            email: "",
            username: "",
            password: "",
            passwordConf: "",
        },
        validate: values => {
            const errors = {};

            if (values.passwordConf !== "" && values.password != values.passwordConf) {
                setPasswordsMatch(false);
                // @ts-ignore
                errors.passwordConf = "Passwords do not match";
            } else {
                setPasswordsMatch(true);
            }

            if (values.username === "" || /^[\w\-.]{5,40}$/.test(values.username)) {
                setUsernameValid(true);
            } else {
                setUsernameValid(false);

                if (values.username.length < 5 || values.username.length > 40) {
                    // @ts-ignore
                    errors.username = "Username must be between 5 and 40 characters in length";
                } else {
                    // @ts-ignore
                    errors.username = "Username may only contain: a-z, A-Z, 0-9, _, -, .";
                }
            }

            return errors;
        },
        onSubmit: values => {
            setLoading(true);
            axios.post("http://localhost:8081/client/sign-up", values)
                 .then(response => {
                     if (response.status == 200) {
                         setShowSignUp({
                             showSelf: false,
                             showBanner: true,
                         });
                     }
                 })
                 .catch(error => {
                     if (error.response && error.response.status == 409) {
                         setLoading(false);
                         setUsernameTaken(true);
                     } else {
                         alert("An unknown error occurred: " + error.message);
                         setLoading(false);
                     }
                 });
        },
    });

    return (
        <form className="sign-up-form space-y-6 relative" onSubmit={formik.handleSubmit}>
            <h5 className="text-xl font-medium text-white">Sign up to Notcord</h5>
            <div>
                <label htmlFor="email" className="block mb-2 text-sm font-medium text-gray-300">Email</label>
                <input id="email" name="email" type="email" onChange={formik.handleChange}
                       value={formik.values.email} placeholder="name@notcord.io" required={true}
                       className="border text-sm rounded-lg block w-full p-2.5 bg-gray-600 border-gray-500 placeholder-gray-400 text-white outline-none"/>
            </div>
            <div>
                <label htmlFor="username" className="block mb-2 text-sm font-medium text-gray-300">Username</label>
                <input id="username" name="username" type="username" minLength={5} maxLength={40}
                       onChange={formik.handleChange} value={formik.values.username} placeholder="notcorduser"
                       required={true}
                       className="border text-sm rounded-lg block w-full p-2.5 bg-gray-600 border-gray-500 placeholder-gray-400 text-white outline-none"/>
                {usernameTaken ? <p className="text-rose-400 text-xs">Username is not available</p> : null}
                {usernameValid ? null : <p className="text-rose-400 text-xs">{formik.errors.username}</p>}
            </div>
            <div>
                <label htmlFor="password" className="block mb-2 text-sm font-medium text-gray-300">Password</label>
                <input id="password" name="password" type="password" minLength={12} onChange={formik.handleChange}
                       value={formik.values.password} placeholder="••••••••" required={true}
                       className="border text-sm rounded-lg block w-full p-2.5 bg-gray-600 border-gray-500 placeholder-gray-400 text-white outline-none"/>
            </div>
            <div>
                <label htmlFor="passwordConf" className="block mb-2 text-sm font-medium text-gray-300">Confirm
                    Password</label>
                <input id="passwordConf" name="passwordConf" type="password" onChange={formik.handleChange}
                       value={formik.values.passwordConf} placeholder="••••••••" required={true}
                       className="border text-sm rounded-lg block w-full p-2.5 bg-gray-600 border-gray-500 placeholder-gray-400 text-white outline-none"/>
                {passwordsMatch ? null :
                    <p id="pass-conf-alert" className="text-rose-400 text-xs">{formik.errors.passwordConf}</p>}
            </div>
            {loading ? <SubmitButtonLoading/> : <SubmitButtonInteractible content={"Sign Up"}/>}
            <div className="text-sm font-medium text-gray-300">
                Already registered?
                <a onClick={() => setShowSignUp(false)}
                   className="hover:underline text-indigo-500 cursor-pointer">Sign In</a>
            </div>
        </form>
    );
}
