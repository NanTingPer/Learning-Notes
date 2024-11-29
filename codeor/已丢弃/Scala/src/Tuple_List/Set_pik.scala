package Tuple_List

object Set_pik {
  def main(args: Array[String]): Unit = {
    var setname : Set[Int]  = Set(1,2,3,4,5,7,8,6,5,4,3,2);
    var st : String = "2123334155";
    var sb : StringBuilder = new StringBuilder();
    for(i <- 0 to st.length-1){
      if(i == 0){
        sb += st.charAt(0);
        sb ++= "2024";
      }else{
        sb += st.charAt(i);
      }
    }
    println(sb.toString());
  }
}
