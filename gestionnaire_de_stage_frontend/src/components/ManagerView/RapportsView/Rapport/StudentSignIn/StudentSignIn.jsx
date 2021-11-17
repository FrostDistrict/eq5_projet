import React, {useEffect, useState} from "react";
import {getAllStudents} from "../../../../../services/user-service";
import {Table, TableHeader, TableRow} from "../../../../SharedComponents/Table/Table";


export default function StudentSignIn() {

    const [studentList, setStudentList] = useState([])

    useEffect(() => {
        getAllStudents()
            .then(studentList => {
                setStudentList(studentList)
            })
            .catch(e => {
                setStudentList([])
                console.error(e);
            })
    }, [])

    if (studentList.length === 0) {
        return <div className={'bg-secondary d-flex py-3 align-items-center justify-content-center text-white'}>
            Aucun étudiant inscrit
        </div>
    }
    return (
        <>
            <Table className={"w-75 mx-auto"}>
                <TableHeader>
                    <th>#</th>
                    <th>Étudiant</th>
                    <th>Matricule</th>
                    <th>Adresse électronique</th>
                </TableHeader>
                {studentList.map((student, index) =>
                    <TableRow key={index}>
                        <th>{student.id}</th>
                        <td>{student.firstName} {student.lastName}</td>
                        <td>{student.matricule}</td>
                        <td>{student.email}</td>
                    </TableRow>
                )}
            </Table>
        </>
    )
}
