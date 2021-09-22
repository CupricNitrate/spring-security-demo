package com.cupricnitrate.model;

import lombok.Data;

import java.util.Date;

/**
 * 荷载类
 * 为了方便后期获取token中的用户信息，将token中载荷部分单独封装成一个对象
 * @author 硝酸铜
 * @date 2021/9/22
 */
@Data
public class Payload<T> {
    private String id;
    private T userInfo;
    private Date expiration;
    private Date issuedAt;
}
