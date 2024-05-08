import React, { useState, useEffect } from "react";
import axios from "axios";

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
            const response = await axios.post("/api/chat/group", { name: chatName });
            setChatName("");
            setChatId(response.data.id);
            setError(null);
        } catch (error) {
            // @ts-ignore
            setError(error.response.data.message || "Wystąpił błąd podczas tworzenia czatu. Spróbuj ponownie później.");
        }
    };

    const searchUsers = async () => {
        try {
            const response = await axios.get(`/api/users?search=${searchTerm}`);
            setSearchResults(response.data);
        } catch (error) {
            console.error("Wystąpił błąd podczas wyszukiwania użytkowników:", error);
        }
    };

    const handleAddUser = async (selectedUserId: any) => {
        try {
            const response = await axios.post(`/api/chat/group/${chatId}/invite`, { userId: selectedUserId });
            setUserId("");
            setError(null);
        } catch (error) {
            // @ts-ignore
            setError(error.response.data.message || "Wystąpił błąd podczas dodawania użytkownika do czatu. Spróbuj ponownie później.");
        }
    };

    const handleMessageChange = (event: { target: { value: React.SetStateAction<string>; }; }) => {
        setMessage(event.target.value);
    };

    const handleSendMessage = async (event: { preventDefault: () => void; }) => {
        event.preventDefault();
        try {
            const response = await axios.post(`/api/chat/group/${chatId}/message`, { content: message, senderId: userId });
            setMessage("");
            setError(null);
        } catch (error) {
            // @ts-ignore
            setError(error.response.data.message || "Wystąpił błąd podczas wysyłania wiadomości. Spróbuj ponownie później.");
        }
    };

    // @ts-ignore
    // @ts-ignore
    // @ts-ignore
    // @ts-ignore
    // @ts-ignore
    return (
        <div>
            <h2>Chat</h2>
            <form onSubmit={handleCreateChat}>
                <input
                    type="text"
                    value={chatName}
                    onChange={(event) => setChatName(event.target.value)}
                    placeholder="Nazwa czatu"
                    required
                />
                <button type="submit">Utwórz czat</button>
            </form>
            <div>
                <input
                    type="text"
                    value={searchTerm}
                    onChange={(event) => setSearchTerm(event.target.value)}
                    placeholder="Wyszukaj użytkownika..."
                />
                <ul>
                    {searchResults.length > 0 ? (
                        <ul>
                            {searchResults.map((user: { id: number; name: string; }) => (
                                <li key={user.id}>
                                    {user.name}{" "}
                                    <button onClick={() => handleAddUser(user.id)}>Dodaj</button>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p>Brak wyników wyszukiwania</p>
                    )}
                </ul>
            </div>
            <form onSubmit={handleSendMessage}>
                <textarea
                    value={message}
                    onChange={handleMessageChange}
                    placeholder="Wpisz swoją wiadomość..."
                    rows={4}
                    cols={50}
                    required
                />
                <button type="submit">Wyślij</button>
            </form>
            {error && <p>{error}</p>}
        </div>
    );
};
