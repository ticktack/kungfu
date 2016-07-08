package org.kungfu.core;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by yangfq on 15/11/19.
 */
public class Constants {
	
	public static String BASE_PATH = System.getProperty("user.dir");
	public static String RESOURCES_PATH = System.getProperty("user.dir")+ "/src/main/resources/";
	public static String ROOT_PATH = "./";
	public static String DATE_TIME = DateFormat.getDateTimeInstance().format(new Date());
	public static String PLACEHOLDER = "%s";

    public static class ResultCode {
        public static final int SUCCESS = 200;
        public static final int FAILURE = 201;
    }

    public static class ResultDesc {
        public static final String SUCCESS = "操作成功(Success)";
        public static final String FAILURE = "操作失败(Error)";
    }

    public static class RequestMethod {
        public static final String GET = "get";
        public static final String POST = "post";
    }

    public static String SUCCESS_MASSAGE = "success";
    public static String ERROR_MASSAGE = "error";
	
    public static String SUCCESS_SAVE = "成功保存记录";
    public static String SUCCESS_UPDATE = "成功修改记录";
    public static String SUCCESS_DELETE = "成功删除记录";
	
	
    public static String ERROR_SAVE = "保存记录出错";
    public static String ERROR_UPDATE = "修改记录出错";
    public static String ERROR_DELETE = "删除记录出错";
}
