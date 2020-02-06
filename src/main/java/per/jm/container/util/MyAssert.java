package per.jm.container.util;

public class MyAssert {
    public static  void isTrue(boolean t,String message){
        if(!t){
            throw new RuntimeException(message);
        }
    }
}
