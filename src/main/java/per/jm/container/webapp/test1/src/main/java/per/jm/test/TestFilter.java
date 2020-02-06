package per.jm.container.webapp.test1.src.main.java.per.jm.test;

import per.jm.container.filter.FilterChain;
import per.jm.container.filter.FilterConfig;
import per.jm.container.filter.MyFilter;
import per.jm.container.http.MyRequest;
import per.jm.container.http.MyResponse;

public class TestFilter implements MyFilter {
    public void init(FilterConfig filterConfig) {

    }

    public void doFilter(MyRequest request, MyResponse response, FilterChain filterChain) throws Exception {
        System.out.println("经过filter");
        filterChain.doFilter(request,response);
    }

    public void destroy(Object o) {
        System.out.println("destroy");
    }
}
