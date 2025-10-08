import './App.css';
import AddTodo from "../com/AddTodo";
import TodoList from "../com/TodoList";
import { useState, useMemo } from 'react';
import { TodoStatu, type Todo } from './Types';

function App() {
    const [todos, setTodos] = useState<Todo[]>([])
    const [todoStatu, setTodoStatu] = useState<TodoStatu>(TodoStatu.all)

    /**
     * ���� todo
     * @param text todo ����
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
     * ɾ�� todo
     * @param id Ҫɾ���� todo id
     */
    const deleteTodo = (id : number) => {
        setTodos(prev => prev.filter(f => f.id !== id))
    }

    /**
     * �л� todo ״̬
     * @param id Ҫ�л��� id
     */
    const toggleTodo = (id: number) => {
        setTodos(prev => prev.map(f => f.id === id ? { ...f, isye: !f.isye } : f))
    }

    // ������Ⱦ�� setState�����Ǽ��������Ĺ��˽��
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
