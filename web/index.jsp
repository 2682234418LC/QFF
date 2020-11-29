<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>文件管理</title>
  </head>
  <body>
  <%
    Object user = session.getAttribute("user");
    System.out.println(user);
  %>
  <h1><%=user%>,欢迎你!</h1>
  <form action="FileuploadServlet" method="post" enctype="multipart/form-data">
    文件：<input name="<%=user%>" type="file">
    <button id="subbutton">上传</button>
  </form>
  <form action="DownloadServlet" method="post" enctype="multipart/form-data">
    <a href="DownloadServlet?filename=QQ视频20201129122129.mp4">点击下载</a><br>
    <a href="DownloadServlet?filename=1000856.jpg">点击下载</a><br>
  </form>
  </body>
</html>
