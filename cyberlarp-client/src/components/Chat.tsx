import React, {useState, useEffect} from "react";
import axios from "axios";
import "../styles/Chat.css";
import {Col, Row} from "react-bootstrap";
import {useNavigate} from "react-router-dom";

export const Chat = () => {
    const [chatName, setChatName] = useState("");
    const [userId, setUserId] = useState("");
    const [message, setMessage] = useState("");
    const [error, setError] = useState(null);
    const [chatId, setChatId] = useState(null);
    const [searchTerm, setSearchTerm] = useState("");
    const [searchResults, setSearchResults] = useState([]);

    useEffect(() => {
        if (searchTerm) {
            searchUsers();
        }
    }, [searchTerm]);

    const handleCreateChat = async (event: { preventDefault: () => void; }) => {
        event.preventDefault();
        try {
            const response = await axios.post("/api/chat/group", {name: chatName});
            setChatName("");
            setChatId(response.data.id);
            setError(null);
        } catch (error: any) {
            setError(error.response.data.message || "There was an error creating the chat. Please try again later.");
        }
    };

    const searchUsers = async () => {
        try {
            const response = await axios.get(`/api/users?search=${searchTerm}`);
            setSearchResults(response.data);
        } catch (error) {
            console.error("There was an error searching for users.", error);
        }
    };

    const handleAddUser = async (selectedUserId: any) => {
        try {
            const response = await axios.post(`/api/chat/group/${chatId}/invite`, {userId: selectedUserId});
            setUserId("");
            setError(null);
        } catch (error: any) {
            setError(error.response.data.message || "There was an error adding the user to the chat. Please try again later.");
        }
    };

    const handleMessageChange = (event: { target: { value: React.SetStateAction<string>; }; }) => {
        setMessage(event.target.value);
    };

    const handleSendMessage = async (event: { preventDefault: () => void; }) => {
        event.preventDefault();
        try {
            const response = await axios.post(`/api/chat/group/${chatId}/message`, {
                content: message,
                senderId: userId
            });
            setMessage("");
            setError(null);
        } catch (error: any) {
            setError(error.response.data.message || "There was an error sending the message. Please try again later.");
        }
    };

    let navigate = useNavigate();
    const goBackToDashBoard = () =>{
        let path = `/dashboard`;
        navigate(path);
    }

    return (
        <div className="Chat">
            <h2 className="p-5">Chats</h2>
            <Row className="px-5">
            <Col>
                <form onSubmit={handleCreateChat}>
                    <input
                        type="text"
                        value={chatName}
                        onChange={(event) => setChatName(event.target.value)}
                        placeholder="Chat name"
                        required
                    />
                    <button type="submit" className="btn">Create chat</button>
                </form>
                <div>
                    <input
                        type="text"
                        value={searchTerm}
                        onChange={(event) => setSearchTerm(event.target.value)}
                        placeholder="Search users..."
                    />
                    <ul>
                        {searchResults.length > 0 ? (
                            <ul>
                                {searchResults.map((user: { id: number; name: string; }) => (
                                    <li key={user.id}>
                                        {user.name}{" "}
                                        <button onClick={() => handleAddUser(user.id)}>Add</button>
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p>No users found</p>
                        )}
                    </ul>
                </div>
            </Col>
            <Col>
                <form onSubmit={handleSendMessage}>
                <textarea
                    value={message}
                    onChange={handleMessageChange}
                    placeholder="Write your message here..."
                    rows={4}
                    cols={50}
                    required
                />
                    <button type="submit" className="btn">Send</button>
                </form>
                {error && <p>{error}</p>}
            </Col>
            </Row>
            <button className="btn goBack-btn" onClick={goBackToDashBoard}>Go back to dashboard</button>
        </div>
    );
};
