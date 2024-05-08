import React from 'react';
import '../styles/UserPage.css';
import {useNavigate} from "react-router-dom";


export const UserPage = () => {

    let navigate = useNavigate();
    const goBackToDashBoard = () =>{
        let path = `/dashboard`;
        navigate(path);
    }

    return (
        <div className="userPage" >
            <div className="UserPageForm">
                <h1 className="mb-5">Profile</h1>
                <div>
                    <form>
                        <div className="form-group">
                            <label htmlFor="email" className="mb-2">Email address</label>
                            <input type="email" className="form-control" id="email" value="eg Email" disabled/>
                        </div>
                    </form>
                </div>
            </div>
            <button className="btn mt-5 ms-5" onClick={goBackToDashBoard}>Back to dashboard</button>
        </div>

    );
}