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

1. 引入 `reactive` 与 `ref` 一样 将数据传入方法

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

p13 [012.reactive创建_对象类型的响应式数据_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV1Za4y1r7KE?spm_id_from=333.788.videopod.episodes&vd_source=6cfabdd9118b8397a529eb6df87378b6&p=12)