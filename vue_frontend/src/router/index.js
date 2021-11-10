import {createRouter, createWebHistory} from 'vue-router'
import Register from "../views/Register";
import Login from "../views/Login";
import LoggedIn from "../views/LoggedIn";
import RegisterStudent from "../views/RegisterStudent";
import RegisterSupervisor from "../views/RegisterSupervisor";
import RegisterMonitor from "../views/RegisterMonitor";
import TeleverserCv from "../views/TeleverserCv";
import authService from "../services/auth-service";
import MonitorCreateOffer from "../views/MonitorCreateOffer";
import ManagerCreateOffer from "../views/ManagerCreateOffer";


function guardRoute(to, from, next) {
    if (!authService.isAuthenticated())
        next('/login');
    else
        next();
}

const routes = [
    {
        path: '/register',
        name: 'Créer un compte',
        component: Register
    },
    {
        path: '/login',
        name: 'Se connecter',
        component: Login
    },
    {
        path: '/logged-in',
        name: 'Connecté',
        component: LoggedIn,
        beforeEnter: guardRoute
    },
    {
        path: '/register/supervisor',
        name: 'Inscription superviseur',
        component: RegisterSupervisor
    },
    {
        path: '/register/student',
        name: 'Inscription student',
        component: RegisterStudent
    },
    {
        path: '/register/monitor',
        name: 'Inscription monitor',
        component: RegisterMonitor
    },
    {
        path: '/televerser_cv',
        name: 'Televerser CV',
        component: TeleverserCv,
        beforeEnter: guardRoute
    },
    {
        path: '/monitor/create_offer',
        name: 'Créer une offre en tant que Moniteur',
        component: MonitorCreateOffer,
        beforeEnter: guardRoute
    },
    {
        path: '/manager/create_offer',
        name: 'Créer une offre en tant que Gestionnaire',
        component: ManagerCreateOffer,
        beforeEnter: guardRoute
    }
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

export default router