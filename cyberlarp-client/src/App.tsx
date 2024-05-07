import React from 'react';
import './styles/App.css';
import {BrowserRouter, Route, Routes} from 'react-router-dom';
//import {Login} from './components/Login';
import {Main} from "./components/Main";
import {UserPage} from "./components/UserPage";


function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path='/' element={<Main/>}/>
                <Route path='/userPage' element={<UserPage/>}/>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
