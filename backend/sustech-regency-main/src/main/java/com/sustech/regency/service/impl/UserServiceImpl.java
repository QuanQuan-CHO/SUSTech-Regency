package com.sustech.regency.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sustech.regency.db.dao.LoginLogDao;
import com.sustech.regency.db.dao.UserDao;
import com.sustech.regency.db.dao.UserWithRoleDao;
import com.sustech.regency.db.po.LoginLog;
import com.sustech.regency.db.po.User;
import com.sustech.regency.db.po.UserWithRole;
import com.sustech.regency.db.util.Redis;
import com.sustech.regency.model.vo.UserInfo;
import com.sustech.regency.service.UserService;
import com.sustech.regency.util.FileUtil;
import com.sustech.regency.util.VerificationUtil;
import com.sustech.regency.web.util.JwtUtil;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

import static com.sustech.regency.web.util.AssertUtil.asserts;
import static com.sustech.regency.util.VerificationUtil.validateCode;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private Redis redis;
    @Resource
    private UserDao userDao;
    @Resource
    private UserWithRoleDao userWithRoleDao;
    @Resource
    private LoginLogDao loginLogDao;

    @Override
    public Map<String,Object> register(String verificationCode, String email, String username, String password, Integer roleId) {
        String trueCode = redis.getObject("verification:" + email);
        validateCode(verificationCode,trueCode);
        //判断该email是否已被注册
        User user = userDao.selectOne(new LambdaQueryWrapper<User>()
                                         .eq(User::getEmail, email));
        if (user == null) { //email未被注册
            user=userDao.selectOne(new LambdaQueryWrapper<User>()
                                      .eq(User::getName,username));
            asserts(user==null,"该用户名已被注册");
            user=User.builder()
                     .name(username)
                     .password(passwordEncoder.encode(password))
                     .email(email)
                     .build();
            userDao.insert(user);
        } else {//email已被注册
            //查询是否已为该role
            UserWithRole userWithRole = userWithRoleDao.selectOne(
                                         new LambdaQueryWrapper<UserWithRole>()
                                            .eq(UserWithRole::getUserId, user.getId())
                                            .eq(UserWithRole::getRoleId, roleId));
            asserts(userWithRole==null,"该邮箱已被注册为"+(roleId==1?"消费者":"商家"));
        }
        userWithRoleDao.insert(new UserWithRole(user.getId(), roleId, new Date()));
        //直接认证通过，就不经过AuthenticationManager#authenticate了
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getId(), user.getPassword(), null);
        SecurityContextHolder.getContext().setAuthentication(authentication); //存入SecurityContext
        redis.setObject("login:" + user.getId(), user, 60 * 60 * 2); //把完整用户信息存入Redis, sid作为key, ttl为2h
        String jwt = JwtUtil.createJwt(String.valueOf(user.getId()));//使用id生成JWT返回
        return Map.of("token",jwt,
                      "userInfo",new UserInfo(user.getId(),username,email));
    }

    @Override
    public void findPassword(String verificationCode, String email, String newPassword) {
        User user = userDao.selectOne(new LambdaQueryWrapper<User>()
                                         .eq(User::getEmail, email));
        asserts(user!=null,"邮箱未被绑定");
        String trueCode = redis.getObject("verification:" + email);
        validateCode(verificationCode,trueCode);
        user.setPassword(passwordEncoder.encode(newPassword));
        userDao.updateById(user);
    }

    @Override
    public Map<String,Object> login(String usernameOrEmail, String password) {
        LambdaQueryWrapper<User> wrapper=new LambdaQueryWrapper<>();
        if(usernameOrEmail.contains("@")){ //邮箱
            wrapper.eq(User::getEmail,usernameOrEmail);
        }else{wrapper.eq(User::getName,usernameOrEmail);}//用户名
        User user = userDao.selectOne(wrapper);
        asserts(user!=null,"User doesn't exists, please register first");
        asserts(passwordEncoder.matches(password, user.getPassword()),"Password wrong");
        //生成LoginLog
        @SuppressWarnings("ConstantConditions")
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ipAddress = request.getRemoteAddr();
        int port = request.getRemotePort();
        loginLogDao.insert(new LoginLog(user.getId(), new Date(), ipAddress, port));
        //直接认证通过，就不经过AuthenticationManager#authenticate了
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getId(), user.getPassword(), null);
        SecurityContextHolder.getContext().setAuthentication(authentication); //存入SecurityContext
        redis.setObject("login:" + user.getId(), user, 60 * 60 * 2); //把完整用户信息存入Redis, sid作为key, ttl为2h
        String jwt = JwtUtil.createJwt(String.valueOf(user.getId()));//使用id生成JWT返回
        return Map.of("token",jwt,
                      "userInfo",new UserInfo(user.getId(),user.getName(),user.getEmail()));
    }

    @Resource
    private JavaMailSender javaMailSender;

    @Override
    public void sendVerificationCode(String email) {
        //1.发送验证码
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("836200779@qq.com");
        message.setTo(email);
        message.setSubject("SUSTech-Regency邮箱验证");
        String randomCode = VerificationUtil.generateVerificationCode();
        message.setText("验证码:" + randomCode + ", 有效期2分钟");
        javaMailSender.send(message);
        //2.存入Redis
        redis.setObject("verification:" + email, randomCode, 120);
    }

    @Resource
    private FileUtil fileUtil;
    @Override
    public String uploadHeadShot(MultipartFile file, Integer userId) {
        return fileUtil.uploadDisplayCover(file,userDao,userId);
    }
}
