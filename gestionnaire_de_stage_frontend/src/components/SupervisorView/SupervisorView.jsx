import React from "react";
import {ContainerBox} from "../SharedComponents/ContainerBox/ContainerBox";
import {Route, useRouteMatch} from "react-router-dom";
import {useAuth} from "../../services/use-auth";
import SupervisorVisitForm from "./VisitForm/SupervisorVisitForm";
import StudentStatusView from "./StudentStatusView/StudentStatusView";

export default function SupervisorView() {
    const {path} = useRouteMatch();
    let auth = useAuth();
    return (<ContainerBox>
        <Route exact path={`${path}/supervisor_form_visit_company`}>
            <SupervisorVisitForm/>
        </Route>
        <Route exact path={`${path}/students/status`}>
            <StudentStatusView/>
        </Route>
        <Route exact path={`${path}`}>
            <h1 className="text-center">Bonjour {auth.user.firstName}!</h1>
        </Route>
    </ContainerBox>);
}
