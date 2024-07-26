package com.ly.gpt.common;


import cn.hutool.json.JSONObject;

public class ApiResult extends JSONObject {

	private static final long serialVersionUID = -1073411906807304534L;


	public static ApiResult success(Object data) {
		ApiResult apiResult = new ApiResult();
		apiResult.put(ApiConsts.CODE_KEY, ApiConsts.CODE_SUCCESS);
		apiResult.put(ApiConsts.MSG_KEY, ApiConsts.MSG_OK);
		apiResult.put(ApiConsts.DATA_KEY, data);
		return apiResult;
	}
//
//	public static ApiResult success() {
//		ApiResult apiResult = new ApiResult();
//		apiResult.put(ApiConsts.CODE_KEY, ApiConsts.CODE_SUCCESS);
//		apiResult.put(ApiConsts.MSG_KEY, ApiConsts.MSG_OK);
//		return apiResult;
//	}




	public static ApiResult error(Object msg) {
		ApiResult apiResult = new ApiResult();
		apiResult.put(ApiConsts.CODE_KEY, ApiConsts.CODE_SERVER_ERR);
		apiResult.put(ApiConsts.MSG_KEY, msg);
		return apiResult;
	}


	public static ApiResult error(Integer code, Object msg) {
		ApiResult apiResult = new ApiResult();
		apiResult.put(ApiConsts.CODE_KEY, code);
		apiResult.put(ApiConsts.MSG_KEY, msg);
		return apiResult;
	}


	public static ApiResult done(Integer code, Object msg, Object data) {
		ApiResult apiResult = new ApiResult();
		apiResult.put(ApiConsts.CODE_KEY, code);
		apiResult.put(ApiConsts.MSG_KEY, msg);
		apiResult.put(ApiConsts.DATA_KEY, data);
		return apiResult;
	}
}
