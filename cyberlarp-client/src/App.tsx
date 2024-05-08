import React from 'react';
import './styles/App.css';
import {BrowserRouter, Route, Routes} from 'react-router-dom';
import {Login} from './components/Login';
import {Main} from "./components/Main";
import {UserPage} from "./components/UserPage";
import {Dashboard} from "./components/Dashboard";


function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path='/' element={<Main/>}/>
                <Route path='/login' element={<Login/>}/>
                <Route path='/dashboard' element={<Dashboard/>}/>
                {/*<Route path='/userPage' element={<UserPage/>}/>*/}
            </Routes>
        </BrowserRouter>
    );
}

export default App;
