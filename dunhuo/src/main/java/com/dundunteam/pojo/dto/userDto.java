package com.dundunteam.pojo.dto;

import com.dundunteam.pojo.entity.wxUserInfo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class userDto implements Serializable {
    private Long id;
    private String gender;
    private String nickName;
    private String openId;
    private String password;
    private String phoneNumber;
    private String portrait;
    private String username;
    private String wxUnionid;

    private  String token;
    List<String> permissions;
    List<String> roles;
    private  String code;

    public  void  from(wxUserInfo wxUserInfo){
        this.nickName=wxUserInfo.getNikeName();
        this.portrait=wxUserInfo.getAvatarUrl();
        this.username="";
        this.password="";
        this.phoneNumber="";
        this.gender=wxUserInfo.getGender();
        this.openId=wxUserInfo.getOpenId();
        this.wxUnionid=wxUserInfo.getUnionId();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNickname() {
        return nickName;
    }

    public void setNickname(String nickname) {
        this.nickName = nickname;
    }

    public String getOpenid() {
        return openId;
    }

    public void setOpenid(String openid) {
        this.openId = openid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWxUnionid() {
        return wxUnionid;
    }

    public void setWxUnionid(String wxUnionid) {
        this.wxUnionid = wxUnionid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
