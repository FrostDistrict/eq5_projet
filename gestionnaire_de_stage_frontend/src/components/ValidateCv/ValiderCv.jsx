import './ValiderCv.css'
import React, {Component} from "react";
import CurriculumService, {getAllStudents} from "../../services/curriculum-service";
import ListStudentView from "./ListStudentView/ListStudentView";
import AuthService from "../../services/auth-service";


export default class ValiderCv extends Component{

    constructor(props) {
        super(props);
        console.log(AuthService.user);
        this.state = {
            students: []
        };
        getAllStudents()
            .then(students => this.setState({students}))
            .catch(e => {
                this.setState({students: []})
                console.trace(e);
            });
    }

    render() {
        return (
            <div className='container'>
                <h2 className="text-center">Liste étudiants</h2>
                <ul>
                    {this.state.students.map((student, index) =>
                        <li key={index}><ListStudentView student={student}/></li>)}
                </ul>
            </div>
        )
    }
}


