package upload;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@WebServlet("/FileuploadServlet")
public class FileuploadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //先创建一个工厂实例
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //创建一个专门用来处理Servlet文件上传的对象
        ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
        try {
            //解析文件上传的请求，封装了FileItem
            //封装了请求的流中的每一个部件，每一个部件就对应一个FileItem
            List<FileItem> list = servletFileUpload.parseRequest(request);
            for (FileItem fileItem : list) {
                if (fileItem.isFormField()){
                    //true代表是简单的key-value
                    //获取表单项的name值
                    //获取的是文件名
                    String fieldName = fileItem.getFieldName();
                    String name = fileItem.getName();
                }else {
                    ServletContext context = getServletContext();
                    String realPath = context.getRealPath("/uploads");
                    //false代表是一个文件
                    //文件名
                    String name = fileItem.getName();
                    //用户名
                    String fieldName = fileItem.getFieldName();
                    System.out.println("fieldName:"+fieldName);
                    System.out.println("name："+name);
                    System.out.println("realPath："+realPath);

                    //给用户建文件夹，分用户存放文件
                    File file = new File(realPath,fieldName);
                    if (!file.exists() && !file.isDirectory()){
                        System.out.println("不存在！");
                        file.mkdirs();
                    }else {
                        System.out.println("存在");
                    }

                    //文件分类
                    String Oldname = name;
                    int indexof = Oldname.indexOf(".");
                    String Newname = Oldname.substring(indexof + 1);
                    File file1 = new File(realPath+"/"+fieldName,Newname);
                    if (!file1.exists() && !file1.isDirectory()){
                        System.out.println("不存在！");
                        file1.mkdir();
                    }else {
                        System.out.println("存在了！");
                    }

                    //文件项保存流,获取文件项的流
                    InputStream inputStream = fileItem.getInputStream();
                    String string = UUID.randomUUID().toString();
                    name = string+"_"+name;
                    //保存流
                    FileOutputStream outputStream = new FileOutputStream(realPath+"/"+fieldName+"/"+Newname+"/"+name);
                    //将输入流的内容写在输出流里
                    IOUtils.copy(inputStream,outputStream);
                    //跳转页面
                    request.getRequestDispatcher("/index.jsp").forward(request,response);
                    //关闭流
                    outputStream.close();
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
