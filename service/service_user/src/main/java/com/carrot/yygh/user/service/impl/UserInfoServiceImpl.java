package com.carrot.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.carrot.yygh.common.exception.AppointmentException;
import com.carrot.yygh.common.helper.JwtHelper;
import com.carrot.yygh.common.result.ResultCodeEnum;
import com.carrot.yygh.enums.AuthStatusEnum;
import com.carrot.yygh.model.user.Patient;
import com.carrot.yygh.model.user.UserInfo;
import com.carrot.yygh.user.mapper.UserInfoMapper;
import com.carrot.yygh.user.service.PatientService;
import com.carrot.yygh.user.service.UserInfoService;
import com.carrot.yygh.vo.user.LoginVo;
import com.carrot.yygh.vo.user.UserAuthVo;
import com.carrot.yygh.vo.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private PatientService patientService;

    @Override
    public Map<String, Object> loginUser(LoginVo loginVo) {
        // 获取输入的手机号和验证码
        String email = loginVo.getEmail();
        String code = loginVo.getCode();

        // 判断手机号和验证码是否为空
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(code)) {
            throw new AppointmentException(ResultCodeEnum.PARAM_ERROR);
        }

        // TODO 判断邮箱验证码和输入的验证码是否一致，资源有限，无法开发短信验证码服务，此处使用邮箱验证码
        //判断邮件验证码和输入的验证码是否一致
        String redisCode = redisTemplate.opsForValue().get(email);
        if(!code.equals(redisCode)) {
            throw new AppointmentException(ResultCodeEnum.CODE_ERROR);
        }

        UserInfo userInfo = null;

        // 绑定邮箱，如果是微信登录，此判断只会执行一次在微信号绑定邮箱时
        // 只通过邮箱登录不通过此判断
        if(!StringUtils.isEmpty(loginVo.getOpenid())) {
            userInfo = this.selectWxInfoOpenId(loginVo.getOpenid());
            if(null != userInfo) {
                // 如果此微信用户之前还使用邮箱登陆过，则将微信登录信息和邮箱登录信息进行统一
                QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("email", loginVo.getEmail());
                UserInfo userIsExistByEmail = baseMapper.selectOne(queryWrapper);
                if (userIsExistByEmail != null) { // 说明此账号之前使用邮箱登陆过，这次绑定的邮箱是以前注册过的
                    userIsExistByEmail.setOpenid(userInfo.getOpenid());
                    userIsExistByEmail.setNickName(userInfo.getNickName());
                    baseMapper.updateById(userIsExistByEmail);
                }

                // 删除之前微信callback保存到数据库的重复信息
                baseMapper.deleteById(userInfo.getId());
            } else {
                throw new AppointmentException(ResultCodeEnum.DATA_ERROR);
            }
        }

        // 判断是否是第一次邮箱登录：根据邮箱查询数据库
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        userInfo = baseMapper.selectOne(wrapper);
        if (userInfo == null) { // 数据为空，表示第一次使用此邮箱登录
            userInfo = new UserInfo();
            userInfo.setName("");
            userInfo.setEmail(email);
            userInfo.setStatus(1);
            baseMapper.insert(userInfo);
        }

        // 不是第一次，直接登录
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getEmail();
        }
        // 添加基本信息
        map.put("name", name);
        // 生成token
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);
        return map;
    }

    @Override
    public UserInfo selectWxInfoOpenId(String openid) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",openid);
        return baseMapper.selectOne(queryWrapper);
    }

    // 用户认证
    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        //根据用户id查询用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        //设置认证信息
        //认证人姓名
        userInfo.setName(userAuthVo.getName());
        //其他认证信息
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        //进行信息更新
        baseMapper.updateById(userInfo);
    }

    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo) {
        //UserInfoQueryVo获取条件值
        String name = userInfoQueryVo.getKeyword(); //用户名称
        Integer status = userInfoQueryVo.getStatus();//用户状态
        Integer authStatus = userInfoQueryVo.getAuthStatus(); //认证状态
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin(); //开始时间
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd(); //结束时间
        //对条件值进行非空判断
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(name)) {
            wrapper.like("name",name);
        }
        if(!StringUtils.isEmpty(status)) {
            wrapper.eq("status",status);
        }
        if(!StringUtils.isEmpty(authStatus)) {
            wrapper.eq("auth_status",authStatus);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time",createTimeEnd);
        }
        //调用mapper的方法
        IPage<UserInfo> pages = baseMapper.selectPage(pageParam, wrapper);
        //编号变成对应值封装
        pages.getRecords().stream().forEach(this::packageUserInfo);
        return pages;
    }

    //用户锁定
    @Override
    public void lock(Long userId, Integer status) {
        if(status ==0 || status ==1) {
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }
    }

    //用户详情
    @Override
    public Map<String, Object> show(Long userId) {
        Map<String,Object> map = new HashMap<>();
        //根据userid查询用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        this.packageUserInfo(userInfo);
        map.put("userInfo",userInfo);
        //根据userid查询就诊人信息
        List<Patient> patientList = patientService.findAllByUserId(userId);
        map.put("patientList",patientList);
        return map;
    }

    //认证审批  2通过  -1不通过
    @Override
    public void approval(Long userId, Integer authStatus) {
        if(authStatus == 2 || authStatus == -1) {
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }
    }

    //编号变成对应值封装
    private void packageUserInfo(UserInfo userInfo) {
        //处理认证状态编码
        userInfo.getParam().put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        //处理用户状态 0  1
        String statusString = userInfo.getStatus() == 0 ? "锁定" : "正常";
        userInfo.getParam().put("statusString",statusString);
    }
}
