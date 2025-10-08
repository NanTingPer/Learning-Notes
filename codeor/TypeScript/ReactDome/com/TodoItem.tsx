import { type TodoItemJson } from "../src/Types"
function TodoItem(todo : TodoItemJson) {
    return (
        <li>
            {todo.todo.name}
            <button onClick={() => todo.toggle(todo.todo.id)}>切换</button>
            <button onClick={() => todo.delete(todo.todo.id)}>删除</button>
        </li>
    )
}

export default TodoItem