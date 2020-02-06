package per.jm.container.util;

public enum  DateTimeUtil {
    Mill(1000),
    Minter(60000),
    Hour(3600000);

    private int value;

    private DateTimeUtil(int ti){
        this.value = ti;
    }
    public int getValue(){
        return this.value;
    }
}
