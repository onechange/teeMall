package com.cwang.web.servlet;

import com.cwang.domain.User;
import com.cwang.utils.CommonUtils;
import com.cwang.service.UserService;
import com.cwang.utils.MailUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class RegisterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Map<String,String[]> properties = req.getParameterMap();
        User user = new User();
        try {
            //指定一个指定类型转换器(string转换成date)
            ConvertUtils.register(new Converter() {
                @Override
                public Object convert(Class aClass, Object value) {
                    //将string转成date
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date parse = null;
                    try {
                        parse = format.parse(value.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return parse;
                }
            }, Date.class);
            BeanUtils.populate(user,properties);
//            private String uid;
            user.setUid(CommonUtils.getUUID());
//            private String telephone;
            user.setTelephone(null);
//            private int state;//是否注册
            user.setState(0);
//            private String code;//
            String activeCode = CommonUtils.getUUID();
            user.setCode(activeCode);

            System.out.println("开始传到service"+System.currentTimeMillis());
            //传递到service层
            UserService service = new UserService();
            boolean isRegisterSuccess = service.regist(user);

            if (isRegisterSuccess)
            {
                String emailMsg = "恭喜您注册成功,点击激活"
                        + "<a href='http://localhost:8080/active?activeCode="+activeCode+"'>"+"http://localhost:8080/active?activeCode="+activeCode+"</a>";
                try {
                    MailUtils.sendMail(user.getEmail(),emailMsg);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                System.out.println("注册成功"+System.currentTimeMillis());
                resp.sendRedirect(req.getContextPath()+"/registerSuccess.jsp");
            }else {
                resp.sendRedirect(req.getContextPath()+"/registerFail.jsp");
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        doGet(req, resp);
    }
}
