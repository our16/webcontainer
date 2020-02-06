package per.jm.container.scan;

import per.jm.container.domain.ServletVO;
import per.jm.container.filter.FilterChain;
import per.jm.container.filter.FilterConfig;
import per.jm.container.filter.MyFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 用来解析子项目配置文件
 */

public class MyProperties {
    private String proConfigPath = "/src/main/java/config/web/web.properties";
    private String localPath = System.getProperty("user.dir") + "/src/main/java/";
    private String rootPath = "per.jm.container.webapp.";
    private String projectModle = ".src.main.java.";
    private List<ServletVO> servletVOList = new ArrayList<ServletVO>();
    public MyProperties() {
        init();
    }

    private void init() {
        //从配置文件获取 server.properties
        localPath += "per/jm/container/webapp/";

        File file = new File(localPath);
        if (null != file) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    String filePath = f.getPath();
                    String projectName = filePath.substring(filePath.lastIndexOf("/")+1);
                    loadProject(filePath+proConfigPath,projectName);
                }
            }
        }
    }

    /**
     * 加载配置文件信息
     *
     * */
    private void loadProject(String path,String projectName) {

        try {
            InputStream in = new FileInputStream(path);
            //2、新建一个Properties对象
            Properties pro = new Properties();
            pro.load(in);    //properties对象封装配置文件的输入流，现在文件里面的信息都已被封装成String

           Set set = pro.entrySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext()){
               String[] kv = iterator.next().toString().split("=");
               if(kv[0].contains("servlet.path")){
                   //解析路由，servlet
                   this.routeConfig(projectName,pro,kv[0],kv[1]);
               }else if(kv[0].contains("filter.path")){
                   //过滤器
                   this.filterConfig(projectName,pro,kv[0],kv[1]);
               }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 解析路由和servlet 配置信息
     *
     * */
    private void routeConfig(String projectName,Properties pro,String k1,String classPackagePath) throws ClassNotFoundException {
        //后面拼接编号
        String routeKey = "servlet.route"+k1.substring(12);
        //取得路由
        String route = pro.getProperty(routeKey).replaceAll("\"","");
        //目标servlet包路径
        String classPath = rootPath+projectName+projectModle+classPackagePath.replaceAll("\"","");
        Class<?> c = Class.forName(classPath);
        ServletVO servletVO = new ServletVO();
        servletVO.setClazz(c);
        servletVO.setRoute(route);
        this.servletVOList.add(servletVO);
    }
    public List<ServletVO> getServletVOList(){
        return this.servletVOList;
    }
    /**
     * 过滤器配置信息加载
     *
     * */
    public void filterConfig(String projectName,Properties pro,String k1,String classPackagePath) throws Exception {
        String num = k1.substring(11);
        //目标servlet包路径
        String classPath = rootPath+projectName+projectModle+classPackagePath.replaceAll("\"","");
        Class<?> c = Class.forName(classPath);
        MyFilter myFilter = (MyFilter)c.getDeclaredConstructor().newInstance();
        FilterConfig config = new FilterConfig();
        myFilter.init(config);
        FilterChain.addFilter(Integer.parseInt(num),myFilter);
    }

}
