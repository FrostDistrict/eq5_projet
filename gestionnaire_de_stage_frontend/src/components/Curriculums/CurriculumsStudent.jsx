import React, {useEffect, useState} from "react";
import {getAllCurriculumsByStudentWithPrincipal, setPrincipalCurriculum} from "../../services/curriculum-service";
import {useAuth} from "../../services/use-auth";
import {Table, TableHeader, TableRow} from "../SharedComponents/Table/Table";
import {AiOutlineCloseCircle, GoStar, MdOutlinePendingActions} from "react-icons/all";
import {downloadFile, toPdfBlob} from "../../utility";
import MessageNothingToShow from "../SharedComponents/MessageNothingToShow/MessageNothingToShow";

export default function CurriculumsStudent() {
    let auth = useAuth();
    const [curriculumsWithPrincipal, setCurriculumsWithPrincipal] = useState({});

    useEffect(() => {
        getAllCurriculumsByStudentWithPrincipal(auth.user.id)
            .then(curriculums => {
                curriculums.curriculumList.sort((a, b) => {
                    if (b.isValid === null || (a.isValid === false && b.isValid === true))
                        return 1;
                    if (a.isValid === null || (a.isValid === true && b.isValid === false))
                        return -1;
                    return 0
                });
                setCurriculumsWithPrincipal(curriculums);
            })
            .catch(e => {
                setCurriculumsWithPrincipal({})
                console.error(e);
            });
    }, [auth.user.id]);


    const setPrincipal = cv => e => {
        e.preventDefault();

        setPrincipalCurriculum(auth.user.id, cv.id).then(
            (success) => {
                if (success)
                    setCurriculumsWithPrincipal(prev => ({...prev, "principal": cv}))
            }
        );
    }

    const isPrincipal = (cv) => {
        return curriculumsWithPrincipal.principal &&
            cv.id === curriculumsWithPrincipal.principal.id;
    }


    const getIcon = cv => {
        if (isPrincipal(cv)) {
            return <GoStar color="orange" title="C.V. par défaut" size="20"/>
        } else {
            if (cv.isValid)
                return <button className="link-button" onClick={setPrincipal(cv)}>
                    <GoStar color="grey" title="En attente de validation" size="20"/>
                </button>
            else if (cv.isValid === null)
                return <MdOutlinePendingActions color="#304c7b" title="En attente de validation" size="20"/>
            else
                return <AiOutlineCloseCircle color="D00" title="Invalid" size="20"/>
        }
    };

    if (!curriculumsWithPrincipal.curriculumList || curriculumsWithPrincipal.curriculumList.length === 0)
        return <MessageNothingToShow message="Aucun C.V. à afficher"/>

    return (
        <>
            <Table>
                <TableHeader>
                    <th>Principal</th>
                    <th>Nom</th>
                </TableHeader>

                {curriculumsWithPrincipal.curriculumList.map((cv, index) =>
                    <TableRow key={index}>
                        <td>{getIcon(cv)}</td>
                        <td className={cv.isValid === false ? "text-danger" : ""}>
                            <button className="link-button" onClick={() => downloadFile(toPdfBlob(cv.data), cv.name)}>
                                {cv.name}
                            </button>
                        </td>
                    </TableRow>)}
            </Table>
        </>
    )
}