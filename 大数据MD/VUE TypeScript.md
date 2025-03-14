# Vue TypeScript

```sh
#如果报错 在此系统上禁止运行脚本
Set-ExecutionPolicy -Scope CurrentUser
RemoteSigned
#使用 get-ExecutionPolicy查看权限
```



# 1 工程创建

1. 创建Vue项目  在需要创建项目的目录输入创建命令并按提示进行输入

```python
npm create vue@latest
```

2. 使用vscode进行开发 安装插件
   - Vue - offcial
3. 右键项目文件夹 在vscode打开
4. 使用`npm i`安装全部依赖
5. 使用`npm run dev`运行 短命令在 `package.json`





# 2 编写App组件

1. 一个`.vue`文件就是一个一个组件

```ts
import { createApp } from 'vue' 	//引入vue下的创建APP
import App from './App.vue' 		//引入本地组件(根组件) 名为App
createApp(App).mount('#App') 		//将组件加入根，并挂载到id为App的位置
```

2. 副组件放在`components` 根组件在`App.vue`

3. 删除src下的全部文件 自行创建`main.ts` `App.vue`

4. `vue`下必须包含三个标签

   - `<template>` 这里写 `html`
   - `<script lang="ts">` 这里放`ts / js`
   - `<style>` 这样放 `css`

5. 在`App.vue`的`<script>`标签内 将组件暴露，至于样式与HTML可随意

   ```ts
   export default{
       name:'App' //组件名称
   }
   ```

6. 全部

   ```vue
   <template>
       <div>
           <p></p>
           <h1 class="run">Hello</h1>
       </div>    
   </template>
   
   <script lang="ts">
       export default{
           name: "App"
       }
   </script>
   
   <style>
       .run{
           font-size: 20px;
       }
   </style>
   ```

   ```ts
   import { createApp } from 'vue'
   import App from './App.vue'
   
   createApp(App).mount("#app")
   ```





# 3 编写简单的效果 vue2语法

1. 创建其他组件存放目录 `componenits` 创建我们自己的组件 `Preson.vue`
2. 在`Preson.vue`中写入 `ts / html / style`
3. 在`App.vue`的`ts`中引入 `import Person from './components/Person.vue';`
4. 在`App.vue`的`ts`中的`export`注册`components: {Person}`
5. 在`App.vue`的`HTML`中使用`<Person/>`

```vue
<template>
    <div class="div1">
        <h1>
            姓名: {{ name }} <br>
            年龄: {{ age }} <br>
            <button @click="ShowTel"> 显示Tel </button>
            <button @click="AddAge"> 年龄变大 </button>
        </h1>
    </div>
</template>

<script lang="ts">
    export default{
        name : 'Person',
        //data是个函数 返回数据
        data() {
            return{
                name: '张三',
                age : 12,
                tel : '13489723333'
            }
        },
        //methods是方法 用来存放改组件的全部方法
        methods:{
            ShowTel(){
                alert(this.tel)
            },
            AddAge(){
                this.age += 1
            }
        }
    }
</script>

<style scoped>
    .div1{
        font-size: 15px;
    }
</style>
```

```vue
<template>
    <div>
        <p></p>
        <h1 class="run">Hello</h1>
        <Person/>
    </div>    
</template>

<script lang="ts">
    //引入组件
    import Person from './components/Person.vue';

    export default{
        name: "App",
        //注册组件
        components: {Person}
    }
</script>

<style>
    .run{
        font-size: 20px;
    }
</style>
```







# 4 Vue3核心语法

---

## 4.1 OptionsAPI 和 CompositionAPI

### OptionsAPI 选项式

1. 功能分散 改一处便要修改多处
   - 方法放在methods
   - 数据放在data
   - 组件放在 computed



### CompositionAPI 组合式

1. 功能集中 放在一块
2. 每个都是一个`functio`函数 ，找功能不用再去找数据了，都在这个函数里



## 4.2 拉开序幕Setup

> 概述：setup是Vue3的一个配置项，值是一个函数

1. 为了方便 将App的div删除 只保留`<Person/>`

   ```vue
   <template>
       <Person/>
   </template>
   ```

2. 打开Person 进行优化 在 `export default{` 添加`setup()`方法

3. 将先前选项式的 `data() methods:`删除

   ```vue
   <template>
       <div class="div1">
           <h1>
               姓名: {{ name }} <br>
               年龄: {{ age }} <br>
               <button @click="ShowTel"> 显示Tel </button>
               <button @click="AddAge"> 年龄变大 </button>
           </h1>
       </div>
   </template>
   
   <script lang="ts">
       export default{
           name : 'Person',
           setup(){
               //数据 setup中的this是undefined,vue3弱化了this
               //这样的name不是响应式的 页面是没有变化了
               //但是naem的值确实改了
               let name = "张三";   
               var age = 18;
               var tel = "12366666666";
               
               //方法
               function ShowTel(){
                   alert(tel);
               }
   
               function AddAge(){
                   age += 1;
               }
   
               //返回数据 左边是键 右边是值 键可自定义
               //可简写return {name,age}
               return {name:name,age:age,ShowTel,AddAge} 
           }
       }
   </script>
   
   <style scoped>
       .div1{
           font-size: 15px;
       }
   </style>
   ```

   

## 4.3 Setup的返回值

> Setup返回的值，浏览器会直接渲染

```ts
return () => "哈哈"
```

> 也可以返回Setup的内容 然后由模板渲染 如拉开序幕Setup

> 选项式可以读取组合式内的数据，使用`this.` , Setup不可以读取选项式



## 4.4 Setup语法糖

1. 自动返回 无需`return`

```vue
<script setup lang="ts">
    let a = 666
</script>
```

2. 这里面不能写，因此一个`vue`有两个`script`不奇怪

```vue
<script lang="ts">
    export defualt{
        name: "Person",
    }
</script>
```

3. 使用插件 实现 `name="名"` 

```sh
npm i vite-plugin-vue-setup-extend -D
```

4. 引入插件 , 修改vite.config.ts

```ts
import Name from 'vite-pluginxxxxx'

export default defineConfig({
	plugins: [
        Name(),
    ],   
})
```

```vue
<script setup lang="ts" name="Person">
    //数据 setup中的this是undefined,vue3弱化了this
    //这样的name不是响应式的 页面是没有变化了
    //但是naem的值确实改了
    let name = "张三";   
    var age = 18;
    var tel = "12366666666";
    
    //方法
    function ShowTel(){
        alert(tel);
    }

    function AddAge(){
        age += 1;
    }
</script>
```



## 4.5 基本类型响应式数据

1. 从 `vue` 引入 `ref` 
2. 将值放入`ref`方法，需要变化的内容赋给`ref` 我们需要的值在`value` 模板调用不需要 `.value`, `ts`代码更改必须`.value`
3. 可以定义基本类型，对象类型的响应式数据

```vue
<script setup lang="ts" name="Person">
    import { ref } from 'vue'

    let name = ref("张三")
    var age = ref(18)
    var tel = "12366666666"
    
    //方法
    function ShowTel(){
        alert(tel)
    }

    function AddAge(){
        age.value += 1
    }
</script>
```



## 4.6 对象类型的响应式数据

1. 引入 `reactive` 与 `ref` 一样 将数据传入方法，只能定义对象类型，可以传入泛型`reactive<Person>`

```vue
<template>
    <div class="div1">
        <!-- 遍历数组 并打印内容 -->
        <h1 v-for="ca in CatArray" :key="ca.Name">{{ ca.Name }}  {{ ca.Age }}</h1>

        <!-- 显示对象 -->
        <h1> {{ Cat.Lift }} {{ Cat.Nmae }} </h1>
        <h1>
            <button @click="SetCatName"> 更改小猫的名字 </button>
            <button @click="SetArrayOne"> 更改数组第一个元素 </button>
        </h1>
    </div>
</template>

<script setup lang="ts" name="Person">
    import { reactive } from 'vue'
    //创建对象
    let Cat = reactive({Lift:64, Nmae:"小猫"})

    //创建数组
    let CatArray = reactive([
        {Name:"Steam",Age:24},
        {Name:"Epic",Age:31}
    ])

    function SetCatName(){
        Cat.Nmae = "小狗"
    }
    function SetArrayOne(){
        CatArray[0].Name = "腾讯"
    }

</script>

<style scoped>
    .div1{
        font-size: 15px;
    }
</style>
```

p13 [012.reactive创建_对象类型的响应式数据_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV1Za4y1r7KE)

- ref响应式对象

- ref对象可以直接重新复制，reactive对象不能重新赋值新对象
- 当对象层级深的时候可以使用 reactive
- 梭哈ref

```typescript
import { ref } from 'vue'
//创建对象
let Cat = ref({Lift:64, Nmae:"小猫"})
//创建数组
let CatArray = ref([
    {Name:"Steam",Age :24},
    {Name:"Epic",Age:31}
])
function SetCatName(){
    Cat.value.Nmae = "小狗"
}
function SetArrayOne(){
    CatArray.value[0].Name = "腾讯"
}
```



## 4.7  toRefs toRef

​	接收由`reactive`定义的对象，解构后的更改影响原来的对象，解构后属于 `ref(type)`

```typescript
let person = reactive({name:"张三",age:18})
let {name,age} = toRefs(person)

//当name.value += 1 时，person的name也会更改
```

- toRef取出单个 `toRef(peron,'age')`



## 4.8 计算属性

- 其实就一方法，`computed` ，里面写返回逻辑，多次使用也只调一次，有缓存，属性变化一次 计算一次。只读的

这样写可以避免只读

```ts
//只读
let fullName = computed(() => {
    return firstName.value.slice(0,1)
})

let fullName = computed({
    get(){
    	return firstName.value.slice(0,1)    
    }
    set(re){
    	firstName.value = re;
	}
})

//其实更改的是firstName
function changeFullName(){
    fullName.value = 'awd'
}
```



## 4.9 Watch监视

不过是一个函数 `watch`需要引入，数据变了调用函数

​	Watch只能监视以下四种数据

- ref定义的数据
- reactive定义的数据
- 函数返回的值(`getter`)
- 一个包含上述内容的数组

```ts
let sum = ref(1)
function changeSum(){
    sum.value += 1
}

watch(sum, (newvalue,oldvalue) => {
    console.log('sum变了',newvalue,oldvalue)
})
```

`watch`带有返回值，调用返回值可以停止监视

```ts
const stopWatch = watch(sum, (newvalue,oldvalue) => {
    console.log('sum变了',newvalue,oldvalue)
    if(newValue >= 10){
        stopWatch()
    }
})
```

​	如果直接监视对象，只监视对象地址变化，否则开启深度监视，第三个参数输入 `{deep:true}`，大多数情况下不管旧值，传入`value`就行，而且更改内容两个其实是同一个对象



## 5.0 watchEffect

​	无需指定监视对象，直接写个回调，需要从`vue`中引入，页面一加载就会运行一次



## 5.1 标签的ref属性

​	放在HTML标签返回DOM，放在组件标签返回实例

```vue
<template>
    <div class="div1">
        <h1 ref="h1One">内容</h1>
        <button @click="ViewH1One"> 输出h1内容 </button>
    </div>
</template>

<script lang="ts" setup name="Person">
import {ref } from 'vue'
let h1One = ref()
function ViewH1One(){
    console.log(h1One)
}
</script>
```

​	使用`defineExpoe`可以对父组件公开属性，这样父组件打上`ref`时就可以访问子组件的公开数据

```ts
import {ref,defineExpose} from 'vue'
let h1One = ref()
let a = ref(0)
let b = ref(1)
let c = ref(2)
function ViewH1One(){
    console.log(h1One)
}
defineExpose({a,b,c})
```

```vue
<template>
    <Person ref="ren"/>
    <button @click="ShowProper"></button>
</template>

<script lang="ts" setup name="App">
    //引入组件
    import { ref } from 'vue';
    import Person from './components/Person.vue';
    
    let ren = ref()

    function ShowProper(){
        console.log(ren)
    }
</script>
```



## 5.2 defineProps 数据接收

- 父组件给子组件发数据

```vue
<template>
	<Person a="哈哈"/>
</template>
```

- 子组件接收

```ts
import {defineProps} from 'vue'
let a = defineProps(['a'])
```



- 传对象 前面加 `:` 跟内插字符串差不多

```vue
<template>
	<Person :list="脚本中的数据"/>
</template>
```

```cs
import {defineProps} from 'vue'
let list = defineProps(['list'])
```



- 子组件限制接收类型

```ts
import {type Persons} from '@/types/Person'
import {defineProps} from 'vue'

//必须给
defineProps<{list:Persons}>()

//可有可无 可以不给 加个?
defineProps<{list?:Persons}>()

//可有可无 + 默认值
//引入 withDefaults
withDefaults(defineProps<{list?:Persons}>(),{
    list:()=>[{xxx:"xxx",xxx:"xxx"}]
})
```



## 5.3  组件的生命周期

钩子就是生命周期函数

---

Vue2

| 时刻           | 调用特定的函数 之前,完毕  |
| -------------- | ------------------------- |
| 创建           | beforeCreate , created    |
| 挂载(放入页面) | beforeMount , mounted     |
| 更新           | beforeUpdate , updated    |
| 销毁           | beforeDestroy , destroyed |

---

Vue3

| 时刻           | 调用特定的函数 之前,完毕      |
| -------------- | ----------------------------- |
| 创建           | setup                         |
| 挂载(放入页面) | onBeforeMount , onMounted     |
| 更新           | onBeforeUpdate , onUpdated    |
| 卸载           | onBeforeUnmount , onUnmounted |



## 5.4 自定义Hooks 

​	就是将方法 / 属性封装到一个`js` / `ts` ，也可以一上来就调用一次生命周期钩子/函数

```ts
import { ref, type Ref } from "vue";

export default function() {
    let sum : Ref<number,number> = ref(0);

    function AddSum(){
        sum.value += 1;
    }

    return {sum,AddSum};
};
```

```vue
<template>
    <div class="div1">
        <h1>{{ sum }}</h1>
        <button @click="AddSum"> 点我+1 </button>
    </div>
</template>

<script setup lang="ts" name="Person">
    import getDog from './getDog';
    const {sum,AddSum} = getDog();
</script>
```



# 5 路由

​	路由就是对应关系，单页面想要实现切换效果 就用到路由了

## 5.1 基本路由

- 导航区，展示区
- 请来路由器
- 指定路由规则 (什么路径对应什么组件)
- 形成一个一个的vue

路由组件通常放在`pages`文件夹或`views`文件夹，一般组件通常存放在`components`文件夹

通过点击导航，视觉效果上"消失"了的组件，默认是被卸载了。需要的时候再去挂载

​	安装路由器`npm i vue-router`

1. 在`src`文件夹下创建`router`文件夹 ， 创建一个路由器`index.ts`

```ts
import createRouter from 'vue-router'
//模式
import createWebHistory from 'vue-router'
import Home from '@/com/Home.vue'
import r from '@/com/r.vue'
import e from '@/com/e.vue'

//创建路由器 传入配置项
const router = createRouter({
    history:createWebHistory(), //路由器的工作模式
    routes:[//路由规则
        {
            //路径
        	path:'/h',
            component:Home
    	},
        {
            //路径
        	path:'/i',
            component:r
    	},
        {
            //路径
        	path:'/m',
            component:e
    	},
    ]
})

//暴露
export default router
```

- 在`main.ts` 引入路由

```ts
import router from '@xxxx'
//使用路由器 先将 createApp拆开
let app = createApp(App)
//使用路由器
app.use(router)
//挂载App
app.mount('#app')
```

- 在`App.vue` 中标记内容显示区域

- 将`RouterView`组件放在展示区域

```ts
import {RouterView, RouterLink} from 'vue-router'
```

- RouterLink用来指向路由页面，设置 `active-class`可以指定激活时的样式类

```html
<RouterLink to="/home">首页</RouterLink>

<!--展示区-->
<RouterView></RouterView>
```







# 6 TypeScript

1. 在src下新建文件夹 `types`
2. 在新文件夹下创建文件 `Person.ts`

## 6.1 接口

- 定义并暴露，用来约束对象

```ts
export interface Person{
    age : number,
    name : string,
    id : string,
    //可选
    x? : number
}
```

- 在`Person.vue`中引入并创建对象 , 引入类型需要添加 `type`

```ts
import {type Person } from "@/types/Person"
let ren:Person = {name:"ren",id:"1",age:12}
```

- 定义数组

```ts
import {type Person } from "@/types/Person"
let ren:Array<Person> = {name:"ren",id:"1",age:12}
```

