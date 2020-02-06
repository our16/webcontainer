package per.jm.container.scan;

import per.jm.container.domain.ServletVO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 用来解析子项目配置文件
 */

public class MyProperties {
    private String proConfigPath = "/src/main/java/config/web/web.properties";
    private String localPath = System.getProperty("user.dir") + "/src/main/java/";
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
                   String strvalue = pro.getProperty(kv[0]);
                   String str = "servlet.route"+kv[0].substring(12);
                   String route = pro.getProperty(str).replaceAll("\"","");
                   String classPath = "per.jm.container.webapp."+projectName+".src.main.java."+strvalue.replaceAll("\"","");
                   Class<?> c = Class.forName(classPath);
                   ServletVO servletVO = new ServletVO();
                   servletVO.setClazz(c);
                   servletVO.setRoute(route);
                   this.servletVOList.add(servletVO);
               }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ServletVO> getServletVOList(){
        return this.servletVOList;
    }

}
