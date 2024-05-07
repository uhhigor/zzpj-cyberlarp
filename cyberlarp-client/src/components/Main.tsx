import React, {useEffect, useState} from "react";
import "../styles/Main.css"
import {useAuth0} from "@auth0/auth0-react";
import {useNavigate} from "react-router-dom";
import axios from "axios";

export const Main = () => {
    const [isButtonHovered, setIsButtonHovered] = useState(false);
    const {loginWithRedirect, isAuthenticated,user} = useAuth0();
    let navigate = useNavigate();

    React.useEffect(() => {
        if (isAuthenticated) {
            navigate("/userPage");
        }
    }, [isAuthenticated, navigate]);

    useEffect(() => {
        userToDatabase();
    }, [user]);

    const handleButtonHover = () => {
        setIsButtonHovered(true);
    };

    const handleButtonLeave = () => {
        setIsButtonHovered(false);
    };

    const userToDatabase = () => {
        if (user) {
            console.log("Sending user to database");
            console.log(user.email);
            console.log(typeof user.email);
            axios.post(`http://localhost:8080/auth/login`, { email: user.email })
                .then(response => {
                    console.log(response.data);
                })
                .catch(error => {
                    console.error("An error occurred while sending user to database:", error);
                });
        }
    };

    return (
        <div className={"Main"}>
            <div className="d-flex justify-content-center align-items-center min-vh-100">
                <div className="container-fluid">
                    <div className="row">
                        <div className="col text-center">
                            <div className={`welcomeText ${isButtonHovered ? 'paused' : ''}`}>CyberLarp
                            </div>
                            {!isAuthenticated ? (
                                    <button
                                        className="btn btn-lg text-white m-5"
                                        onMouseEnter={handleButtonHover}
                                        onMouseLeave={handleButtonLeave}
                                        onClick={() => loginWithRedirect()}
                                    >
                                        Start your story now!
                                    </button>)
                                : (
                                    <div/>
                                    // <button onClick={() => logout({logoutParams: {returnTo: window.location.origin}})}>
                                    //     Log Out
                                    // </button>
                                )
                            }

                        </div>
                    </div>
                </div>
            </div>
        </div>
    )


};
