package per.jm.container.domain;

import java.util.Date;

public class SessionVO {
    //过期时间
    private long exprise;
    private Object data;
    private Date createTime;

    public long getExprise() {
        return exprise;
    }

    public void setExprise(long exprise) {
        this.exprise = exprise;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
