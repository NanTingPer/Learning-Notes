package Regex_

object regex_email {
    def main(args: Array[String]): Unit = {
        var regex = """.+@.+\..+""".r;
        var email = "234111@de.com";

        var e = regex.findAllMatchIn(email);
        if(e.size != 0){
            print("可以");
        }
    }
}
