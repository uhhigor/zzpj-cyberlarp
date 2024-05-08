import React from 'react';
import './styles/App.css';
import {BrowserRouter, Route, Routes} from 'react-router-dom';
import {Login} from './components/Login';
import {Main} from "./components/Main";
import {UserPage} from "./components/UserPage";
import {Chat} from "./components/Chat";


function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path='/' element={<Main/>}/>
                <Route path='/login' element={<Login/>}/>
                <Route path='/userPage' element={<UserPage/>}/>
                <Route path='/chat' element={<Chat/>}/>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
