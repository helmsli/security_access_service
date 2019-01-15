package com.company.security.Const;

public class SecurityUserConst {
	
	//范围1000 到2000
	//成功
    public static final int RESULT_SUCCESS = 0;
    
    public static final int RESULT_Error_startCode = 1000;
    /**
     * 角色不存在
     */
    public static final int RESULT_Error_roleNotExist = RESULT_Error_startCode+1;
    
    /**
     * 电话号码不存在
     */
    public static final int RESULT_Error_PhoneExist = RESULT_Error_startCode+2;
    /**
     * 电话号码格式错误
     */
    public static final int RESULT_Error_PhoneError = RESULT_Error_startCode+3;
    /**
     * 数据库操作错误
     */
    public static final int RESULT_Error_DbError = RESULT_Error_startCode+5;
    /**
     * 电话号码还没有认证过
     */
    public static final int RESULT_Error_PhoneNotVerified = RESULT_Error_startCode+6;
    
}
