import React from "react";
import "../styles/UserPage.css"
import 'react-icons'
import {FaHeart} from "react-icons/fa";
import {GiAbdominalArmor, GiAngelOutfit, GiAttachedShield, GiRunningNinja, GiStrong} from "react-icons/gi";
import {useAuth0} from "@auth0/auth0-react";

export const UserPage = () => {

    const {logout, user} = useAuth0();

    return (
        <div className='userPage'>
            <div className="black-panel">
                <div className="row">
                    <div className="mt-5 m-5 col">
                        <div className="h5 my-4">Name: {user && user.email}</div>
                        <div className="my-4">Description:</div>
                        <div className="mt-4 mb-5">Fraction:</div>
                        <div className="h5 mt-4 mb-5">Balance: E$</div>
                        <div className="h5 mt-5 mb-3">Attributes</div>
                        <div className="my-4 ms-3">Strength: <GiAttachedShield /></div>
                        <div className="my-4 ms-3">Agility: <GiRunningNinja /></div>
                        <div className="my-4 ms-3">Presence: <GiAngelOutfit /></div>
                        <div className="my-4 ms-3">Toughness: <GiStrong /></div>
                        <div className="my-4">Max HP: <FaHeart color="red"/></div>
                        <div className="my-4">Current HP: <FaHeart color="red"/></div>
                        <div className="my-4">Armor: <GiAbdominalArmor/></div>
                    </div>
                    <div className="col m-5">
                        <img src={require("../resources/W3jGDqd.png")} alt="avatar" className="avatar"/>
                    </div>
                    <div className="col m-5">
                        <button className="btn btn-lg text-white m-5" onClick={() => logout()}>Logout</button>
                    </div>
                </div>
            </div>
            <button className="btn mt-5 ms-5" onClick={goBackToDashBoard}>Back to dashboard</button>
        </div>

    );
}