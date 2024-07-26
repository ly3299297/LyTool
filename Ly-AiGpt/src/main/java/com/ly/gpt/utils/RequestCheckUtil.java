package com.ly.gpt.utils;

import java.util.List;

public class  RequestCheckUtil {




    public static boolean usernameCheck(String username){
        boolean flag=true;
        if (username==null || username.isEmpty()) {
            throw new IllegalArgumentException( "username 参数缺失");
        }
        return flag;
    }


    public static boolean resultCheck(String result){
        boolean flag=true;
            if(result.length()==0){
                throw new IllegalArgumentException( "第三方接口返回为空");
            }
        return flag;
    }
    public static boolean resultListCheck(List result){
        boolean flag=true;
            if(result.size()==0){
                throw new IllegalArgumentException( "第三方接口返回为空");
            }
        return flag;
    }




}
