package digui

object jiechen {
    def show(sum : Int):Int ={
        if(sum == 1) {return 1}
        else{
            sum * show(sum - 1);
        }
    }

    def main(args: Array[String]): Unit = {
        print(show(6));
    }
}
