//import * as React from "react"
import { type TodoItemJson, type TodoListJson} from "../src/Types"
import TodoItem  from "./TodoItem"

function TodoList(json : TodoListJson) {
    // 不要在这里做全局赋值，直接渲染
    return (
        <ul>
            {
                json.todos.map(f => {
                    const todoObj : TodoItemJson = {
                        todo : f,
                        delete : json.delete,
                        toggle : json.toggle
                    }
                    return (<TodoItem key={f.id} {...todoObj}></TodoItem>)
                })
            }
        </ul>
    )
}

export default TodoList;