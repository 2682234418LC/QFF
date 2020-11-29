package download;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/DownloadServlet")
public class DownloadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //下载的本质就是告诉浏览器这个资源需要下载，不是打开
        request.setCharacterEncoding("utf-8");
        //相对路径
        String filename = request.getParameter("filename");
        //绝对路径
        String realPath = getServletContext().getRealPath("/downloads/" + filename);
        System.out.println(filename);
        //把要下载的资源的流传给浏览器
        //设置响应的文件类型
        String mimeType = getServletContext().getMimeType(filename);
        response.setContentType(mimeType);
        //解决中文乱码
        String string = new String(filename.getBytes("gbk"), "iso8859-1");
        //设置资源的处理方式
        response.setHeader("Content-Disposition","attachment;filename="+string);
        //设置文件大小 response.setContentLength();
        FileInputStream inputStream = new FileInputStream(realPath);
        ServletOutputStream outputStream = response.getOutputStream();
        IOUtils.copy(inputStream,outputStream);
        //关闭流
        inputStream.close();
        outputStream.close();
        response.sendRedirect(request.getContextPath()+"/index.jsp");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
