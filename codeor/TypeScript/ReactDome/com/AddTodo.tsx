import { useState, type FormEvent } from "react";
import { type AddTodoFunc } from "../src/Types"

function AddTodo(apply: AddTodoFunc) {
    const [text, setText] = useState<string>("")

    const handelSubmit = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault(); //拦截默认提交
        if (text.trim() == '') {
            console.log("内容为空，不提交")
            return;
        } else {
            apply.apply(text)
            setText("")
            console.log("提交")
        }
    }

    return (
        <form onSubmit={handelSubmit}>
            <input type="text" value={text} onChange={(e) => setText(e.target.value)} />
            <button>新建事项</button>
        </form>

    )
}

export default AddTodo;