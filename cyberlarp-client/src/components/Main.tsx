import React, {useEffect, useState} from "react";
import "../styles/Main.css"
import {useAuth0} from "@auth0/auth0-react";
import {useNavigate} from "react-router-dom";
import axios from "axios";

export const Main = () => {
    const [isButtonHovered, setIsButtonHovered] = useState(false);
    const {loginWithRedirect, isAuthenticated,user, logout} = useAuth0();
    let navigate = useNavigate();

    React.useEffect(() => {
        if (isAuthenticated) {
            navigate("/dashboard");
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
            axios.post(`http://localhost:8080/auth/save/${user.email}`)
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
                            <button
                                className="btn btn-lg text-white m-5"
                                onMouseEnter={handleButtonHover}
                                onMouseLeave={handleButtonLeave}
                                onClick={() => loginWithRedirect()}
                            >
                                Start your story now!
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )


};
