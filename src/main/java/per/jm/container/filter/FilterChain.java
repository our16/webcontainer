package per.jm.container.filter;

import per.jm.container.http.MyRequest;
import per.jm.container.http.MyResponse;
import per.jm.container.servlet.Servlet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FilterChain {
    //所有的过滤器在加载时放入这个map中按照优先级和先后顺序，interger 则为序号
    private static Map<Integer,MyFilter> filters = new HashMap<Integer, MyFilter>();
    private Servlet servlet;
    private int index=1;
    private static int totcalFilter = 0;
    public FilterChain(Servlet servlet,MyRequest request,MyResponse response) throws Exception {
        this.servlet = servlet;
        this.doFilter(request,response);
    }
    public void doFilter(MyRequest request, MyResponse response) throws Exception {
        MyFilter myFilter = filters.get(index);
        if(null != myFilter) {
            index++;
            myFilter.doFilter(request, response, this);
        }
        if(index > totcalFilter){
            System.out.println("调用servlet");
            System.out.println("调用路由:"+request.getUrl());
           Object o = servlet.service(request, response);
            //调用destroy方法
            index--;
            while(index > 0){
                MyFilter myFilter2 = filters.get(index);
                myFilter2.destroy(o);
                index--;
            }
        }
    }

    public static void addFilter(Integer num,MyFilter myFilter) throws Exception{
        if(null != filters.get(num) || null == myFilter){
            throw new RuntimeException("Filter is exeisted or is null");
        }
        totcalFilter++;
        filters.put(num,myFilter);
    }
}
