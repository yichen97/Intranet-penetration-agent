package com.fanruan.pojo.message;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Yichen Dai
 */
@Data
@Accessors(chain = true)
public class RpcRequest {
    private boolean reply;
    private String ID;
    private boolean binding;
    private String IDToInvoke;
    private Class<?> serviceClass;
    private String methodName;
    private Object[] args;
    private Class<?>[] argTypes;
}
