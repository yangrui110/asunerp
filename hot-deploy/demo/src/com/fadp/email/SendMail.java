package com.fadp.email;

import com.hanlin.fadp.base.util.FileUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Date;
import java.util.Properties;

/**
 * @autor 杨瑞
 * @date 2018/12/6 10:46
 */
public class SendMail {
    //发件人地址
    public static String senderAddress = "service@jiyuan.com";
    //发件人账户名
    public static String senderAccount = "service@jiyuan.com";
    //发件人账户密码
    public static String senderPassword = "LCzk543";
    //配对端口
    public static String port="465";

    public static String sendMail(String code,String email,String name) throws Exception {
        //1、连接邮件服务器的参数配置
        Properties props = new Properties();
        //设置用户的认证方式
        props.setProperty("mail.smtp.auth", "true");
        //设置传输协议
        props.setProperty("mail.transport.protocol", "smtp");
        //设置发件人的SMTP服务器地址
        props.setProperty("mail.smtp.host", "smtp.exmail.qq.com");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.port", port);
        props.setProperty("mail.smtp.socketFactory.port", port);
        //2、创建定义整个应用程序所需的环境信息的 Session 对象
        Session session = Session.getInstance(props);
        //设置调试信息在控制台打印出来
        session.setDebug(true);
        //3、创建邮件的实例对象
        Message msg = getMimeMessage(session,code,email,name);
        //4、根据session对象获取邮件传输对象Transport
        Transport transport = session.getTransport();
        //设置发件人的账户名和密码
        transport.connect(senderAccount, senderPassword);
        //发送邮件，并发送到所有收件人地址，message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(msg,msg.getAllRecipients());

        //如果只想发送给指定的人，可以如下写法
        //transport.sendMessage(msg, new Address[]{new InternetAddress("xxx@qq.com")});

        //5、关闭邮件连接
        transport.close();
        return "";
    }


    public static MimeMessage getMimeMessage(Session session,String code,String email,String name) throws Exception{
        //创建一封邮件的实例对象
        MimeMessage msg = new MimeMessage(session);
        //设置发件人地址
        msg.setFrom(new InternetAddress(senderAddress));
        /**
         * 设置收件人地址（可以增加多个收件人、抄送、密送），即下面这一行代码书写多行
         * MimeMessage.RecipientType.TO:发送
         * MimeMessage.RecipientType.CC：抄送
         * MimeMessage.RecipientType.BCC：密送
         */
        msg.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress(email));
        //设置邮件主题
        msg.setSubject("极元理财助手","UTF-8");
        //设置邮件正文
        File file = FileUtil.getFile("component://demo/template/email_jy.html");
        String content=FileUtil.readString("utf-8",file);
        content=content.replaceAll("1236523",code);
        content=content.replaceAll("qqfwf",name);
        msg.setContent(content, "text/html;charset=UTF-8");
        //设置邮件的发送时间,默认立即发送
        msg.setSentDate(new Date());

        return msg;
    }



    public static void main(String[] args) throws Exception {
        sendMail("112333","1341672184@qq.com","张三");
    }
}
