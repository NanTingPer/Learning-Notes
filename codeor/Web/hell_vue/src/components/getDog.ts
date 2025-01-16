import { ref, type Ref } from "vue";

export default function() {
    let sum : Ref<number,number> = ref(0);

    function AddSum(){
        sum.value += 1;
    }

    return {sum,AddSum};
};
