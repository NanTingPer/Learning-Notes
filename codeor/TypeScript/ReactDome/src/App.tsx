import './App.css';
import AddTodo from "../com/AddTodo";
import TodoList from "../com/TodoList";
import { useState, useMemo } from 'react';
import { TodoStatu, type Todo } from './Types';

function App() {
    const [todos, setTodos] = useState<Todo[]>([])
    const [todoStatu, setTodoStatu] = useState<TodoStatu>(TodoStatu.all)

    /**
     * 新增 todo
     * @param text todo 名称
     */
    const addTodoFunc = (text: string) => {
        const newTodo : Todo = {
            id: Date.now(),
            name: text,
            isye: false
        }

        setTodos(prev => [...prev, newTodo])
    }
    
    /**
     * 删除 todo
     * @param id 要删除的 todo id
     */
    const deleteTodo = (id : number) => {
        setTodos(prev => prev.filter(f => f.id !== id))
    }

    /**
     * 切换 todo 状态
     * @param id 要切换的 id
     */
    const toggleTodo = (id: number) => {
        setTodos(prev => prev.map(f => f.id === id ? { ...f, isye: !f.isye } : f))
    }

    // 不在渲染中 setState，而是计算派生的过滤结果
    const filteredTodos = useMemo(() => {
        switch(todoStatu){
            case TodoStatu.no:
                return todos.filter(f => !f.isye)
            case TodoStatu.yes:
                return todos.filter(f => f.isye)
            default:
                return todos
        }
    }, [todos, todoStatu])

    return (
        <div>
            <h1>TodoList</h1>
            <AddTodo apply={addTodoFunc}></AddTodo>
            <TodoList todos={filteredTodos} delete={deleteTodo} toggle={toggleTodo} ></TodoList>
            {/* <TodoFilter setFilter={setTodoStatu}></TodoFilter> */}
        </div>
        
    )
}

export default App
