import React from "react";
import AddOffer from "../MonitorView/AddOffer/AddOffer";
import OfferValidationView from "./OfferValidationView/OfferValidationView";
import OffersValidView from "./OffersValidView/OffersValidView";
import CurriculumValidation from "./CurriculumValidation/CurriculumValidation";
import ContractsToBeSigned from "../Contract/ContractsToBeSigned";
import LinkSupervisorToStudent from "./LinkSupervisorToStudent/LinkSupervisorToStudent";
import {Route, useRouteMatch} from "react-router-dom";
import {UserType} from "../../enums/UserTypes";
import StartContract from "./StartContract/StartContract";
import {ContainerBox} from "../SharedComponents/ContainerBox/ContainerBox";
import {useAuth} from "../../services/use-auth";
import CreateSession from "./CreateSession/CreateSession";

export default function ManagerView() {
    const {path} = useRouteMatch();
    let auth = useAuth();
    return (<ContainerBox>
            <Route exact path={`${path}/offres/ajouter`}>
                <AddOffer/>
            </Route>
            <Route exact path={`${path}/offres`}>
                <OffersValidView/>
            </Route>
            <Route exact path={`${path}/offres/review`}>
                <OfferValidationView/>
            </Route>
            <Route exact path={`${path}/curriculum/review`}>
                <CurriculumValidation/>
            </Route>
            <Route path={`${path}/students/applied`}>
                <LinkSupervisorToStudent/>
            </Route>
            <Route path={`${path}/students/start`}>
                <StartContract/>
            </Route>
            <Route exact path={`${path}/contrats/a_signer`}>
                <ContractsToBeSigned userType={UserType.MANAGER[0]}/>
            </Route>
            <Route exact path={`${path}`}>
                <h1 className="text-center">Bonjour {auth.user.firstName}!</h1>
            </Route>
            <Route exact path={`${path}/session`}>
                <CreateSession/>
            </Route>
        </ContainerBox>
    )
}