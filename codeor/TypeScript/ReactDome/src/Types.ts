export interface Todo {
    id: number,
    name: string,
    isye : boolean
}

export interface AddTodoFunc {
    apply : (text : string) => void
}

export interface TodoListJson {
    todos : Todo[],
    delete : (id : number) => void,
    toggle : (id : number) => void
}

export interface TodoItemJson {
    todo : Todo,
    delete : (id : number) => void,
    toggle : (id : number) => void
}

export enum TodoStatu {
    yes = "yes",
    no = "no",
    all = "all"
}