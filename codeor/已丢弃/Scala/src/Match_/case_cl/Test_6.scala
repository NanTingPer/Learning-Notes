package Match_.case_cl

object Test_6 {
    def main(args: Array[String]): Unit = {
        //定义一个列表 1-10
        /*
        * 将1-3转为[1-3] String
        * 4-8同上
        * 其他转换为 (8-*]
        * */

        var arr = (1 to 10).toArray;
//        var list : List[String] = arr.map((f : Int)=>{
//            var Is : PartialFunction[Int,String] = {
//                case 1 || 2 || 3 => "[1-3]";
//                case 4 || 5 || 6 || 7 || 8 => "[4-8]";
//                case _ => "(8-*]";
//            }
//            Is(f);
//        })

        var arr1 = arr.map{
            case x if x>=1 && x<=3 => "[1-3]";
            case x if x>=4 && x<=8 => "[4-8]";
            case _ => "(8-*]";
        }

        arr1.foreach((f : String)=>{
            print(f);
        })
    }
}
