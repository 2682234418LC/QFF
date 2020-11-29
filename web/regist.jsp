<%--
  Created by IntelliJ IDEA.
  User: asus
  Date: 2020/11/28
  Time: 14:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>注册页面</title>
    <style>
        *{
            margin: 0px;
            padding: 0px;
            box-sizing: border-box;
        }
        .rg_layout{
            width: 500px;
            height: 500px;
            border: 8px solid #EEEEEE;
            background-color: white;
            margin: auto;
        }
        .rg_left{
            float: left;
            margin: 15px;
        }
        .rg_left > p:first-child{
            color:#FFD026;
            font-size: 20px;
        }
        .rg_left > p:last-child{
            color:#A6A6A6;
            font-size: 20px;

        }
        .rg_center{
            float: left;
        }
        .rg_right > p:first-child{
            font-size: 15px;
        }
        .rg_right p a {
            color:pink;
        }
        .td_left{
            width: 100px;
            text-align: right;
            height: 45px;
        }
        .td_right{
            padding-left: 50px ;
        }

        #username,#password{
            width: 251px;
            height: 32px;
            border: 1px solid #A6A6A6 ;
            /*设置边框圆角*/
            border-radius: 5px;
            padding-left: 10px;
        }

        #btn_sub{
            width: 150px;
            height: 40px;
            background-color: #FFD026;
            border: 1px solid #FFD026 ;
        }

    </style>

</head>
<body>

<div class="rg_layout">
    <div class="rg_left">
        <p>新用户注册</p>
        <p>USER REGISTER</p>

    </div>

    <div class="rg_center">
        <div class="rg_form">
            <!--定义表单 form-->
            <form action="${pageContext.request.contextPath}/RegistServlet" method="post">
                <table>
                    <tr>
                        <td class="td_left"><label for="username">用户名</label></td>
                        <td class="td_right"><input type="text" name="username" id="username" placeholder="请输入用户名"></td>
                    </tr>

                    <tr>
                        <td class="td_left"><label for="password">密码</label></td>
                        <td class="td_right"><input type="password" name="password" id="password" placeholder="请输入密码"></td>
                    </tr>
                    <tr>
                        <td colspan="2" align="center"><input type="submit" id="btn_sub" value="注册"></td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>

</body>
</html>