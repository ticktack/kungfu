package org.kungfu.core;

public interface KungfuConstant {
    String HTTP_METHOD_POST = "POST";
    String HTTP_METHOD_GET = "GET";
    String JSON_REQUEST_BODY = "jsonRequestBody";
    String VISITOR = "visitor";

    String UTF8 = "UTF-8";
    String MASSAGE = "msg";
    
    String QUERY_TYPE_PAGE = "page";
    String QUERY_TYPE_LIST = "list";
    String QUERY_TYPE_ONE = "one";


    Integer DEFAULT_PAGE_NUMBER = 1;
    Integer DEFAULT_PAGE_SIZE = 20;
    String DEFAULT_ORDER_COLUMN = "create_time";
    String DEFAULT_ORDER_BY = "desc";
}
