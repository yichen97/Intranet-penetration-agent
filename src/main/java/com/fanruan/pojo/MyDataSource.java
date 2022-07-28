package com.fanruan.pojo;

import lombok.Data;

/**
 * 封装数据源信息
 */
@Data
public class MyDataSource {
    private String DataSourceName;
    private String URL;
    private String userName;
    private String passWord;
}
