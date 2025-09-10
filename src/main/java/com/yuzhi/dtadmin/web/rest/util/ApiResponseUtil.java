package com.yuzhi.dtadmin.web.rest.util;

import java.util.HashMap;
import java.util.Map;

/**
 * API响应工具类，用于创建标准的API响应格式
 */
public class ApiResponseUtil {

    /**
     * 创建成功响应
     * 
     * @param data 响应数据
     * @return 标准成功响应格式
     */
    public static Map<String, Object> createSuccessResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "success");
        response.put("data", data);
        return response;
    }

    /**
     * 创建成功响应（带自定义消息）
     * 
     * @param data 响应数据
     * @param message 自定义消息
     * @return 标准成功响应格式
     */
    public static Map<String, Object> createSuccessResponse(Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    /**
     * 创建错误响应
     * 
     * @param message 错误消息
     * @return 标准错误响应格式
     */
    public static Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 500);
        response.put("message", message);
        response.put("data", null);
        return response;
    }

    /**
     * 创建错误响应（带自定义状态码）
     * 
     * @param status 状态码
     * @param message 错误消息
     * @return 标准错误响应格式
     */
    public static Map<String, Object> createErrorResponse(int status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("message", message);
        response.put("data", null);
        return response;
    }
}