import React from "react";
import {Col, Row} from "react-bootstrap";
import "../styles/Dashboard.css";
import {useNavigate} from "react-router-dom";

export const Dashboard = () => {
    let navigate = useNavigate();
    const goToProfile = () =>{
        let path = `/user`;
        navigate(path);
    }
    const goToChats = () =>{
        let path = `/chat`;
        navigate(path);
    }

    return (
        <div className="Dashboard ps-5">
            <Row className="buttons-top">
                <Col>
                    <button className="btn chats-btn" onClick={goToChats}>Chats</button>
                    <button className="btn logout-btn">Logout</button>
                    <button className="btn" onClick={goToProfile}>Profile</button>
                </Col>
            </Row>
            <h1 className="pt-3 pb-5">Dashboard</h1>
            <Row className="Tables me-5">
                <Col>
                    <h2>Games</h2>
                    <div>
                        <table className="custom-table mt-4">
                            <thead>
                            <tr>
                                <th scope="col">Title</th>
                                <th scope="col">Players</th>
                                <th scope="col">Characters</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>Game 1</td>
                                <td>3</td>
                                <td>5</td>
                            </tr>
                            <tr>
                                <td>Game 2</td>
                                <td>5</td>
                                <td>8</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                    <Row className="buttons-down mt-5">
                        <Col>
                            <button className="btn create-btn">Create New Game</button>
                            <button className="btn join-btn">Join Game</button>
                        </Col>
                    </Row>
                </Col>
                <Col>
                    <h2>Characters</h2>
                    <div className="table-container">
                        <table className="custom-table mt-4">
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th>Game</th>
                                <th>Level</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>Some name</td>
                                <td>Game 1</td>
                                <td>5</td>
                            </tr>
                            <tr>
                                <td>Different name</td>
                                <td>Game 2</td>
                                <td>3</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                    <Row className="buttons-down mt-5">
                        <Col>
                            <button className="btn create-btn">Create New Character</button>
                        </Col>
                    </Row>
                </Col>
            </Row>
        </div>
    )
}