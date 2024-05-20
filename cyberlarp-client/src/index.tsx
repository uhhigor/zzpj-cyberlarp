import React from 'react';
import ReactDOM from 'react-dom/client';
import './styles/index.css';
import App from './App';
import {Auth0Provider} from '@auth0/auth0-react';
import 'bootstrap/dist/css/bootstrap.min.css';

const root = ReactDOM.createRoot(
    document.getElementById('root') as HTMLElement
);
root.render(
    <React.StrictMode>
        <Auth0Provider
            domain="dev-i3psyoxzboeoyhet.us.auth0.com"
            clientId="UaPOvHvFEUslszHvQUev1uhI3lkIwusb"
            authorizationParams={{
                redirect_uri: window.location.origin
            }}
        >
            <App/>
        </Auth0Provider>
    </React.StrictMode>
);
