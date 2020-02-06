package per.jm.container.session;

import per.jm.container.domain.SessionVO;
import per.jm.container.util.DateTimeUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Session {
    private Map<String, SessionVO> session = new HashMap<String, SessionVO>();
    public Session (){
        //启动定时器
    }

    public void setSession(String key,String value){
        this.setSession(key,value,30,DateTimeUtil.Minter);
    }

    public void setSession(String key,String value,long exprise,DateTimeUtil type){
        if(exprise < 1){
            throw new RuntimeException("定时时间不能为负数");
        }
        SessionVO sessionVO = new SessionVO();
        sessionVO.setCreateTime(new Date());
        sessionVO.setData(value);
        sessionVO.setExprise(exprise*type.getValue());
        this.session.put(key,sessionVO);
    }

    public SessionVO getSession(String key){
        //加锁防止数据不一致
        synchronized (this.session) {
            SessionVO o = session.get(key);
            if (null != o) {
                Object obj = o.getData();
                //创建时的毫秒
                long createTime = o.getCreateTime().getTime();
                //当前毫秒
                long currentTime = System.currentTimeMillis();
                if (currentTime - createTime <= o.getExprise()) {
                    return o;
                }
                session.remove(key);
            }
            return null;
        }
    }
}
