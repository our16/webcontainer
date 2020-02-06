package per.jm.container.filter;

import per.jm.container.http.MyRequest;
import per.jm.container.http.MyResponse;
import per.jm.container.servlet.Servlet;

public interface MyFilter {
    public void init(FilterConfig filterConfig);
    public void doFilter(MyRequest request, MyResponse response,FilterChain filterChain) throws Exception;
    public void destroy(Object o);
}
