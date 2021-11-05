import React from 'react';
import {validateOffer} from '../../../services/offer-service';
import "./ValidateOffer.css";
import PreviewOffer from "../../PreviewOffer/PreviewOffer";

export default function ValidateOffer({offer, removeFromList}) {
    const validate = (offer, isValid) => {
        validateOffer(offer.id, isValid).then(
            () => removeFromList(offer.id)
        )
    }


    return <>
        <PreviewOffer offer={offer}/>
        <div className="d-flex justify-content-between align-items-center">
            <button id="validateBtn" className="btn btn-success fw-bold text-white w-50"
                    onClick={() => validate(offer, true)}>Valide
            </button>
            <button id="invalidateBtn" className="btn btn-danger fw-bold text-white w-50"
                    onClick={() => validate(offer, false)}>Invalide
            </button>
        </div>
    </>
}
