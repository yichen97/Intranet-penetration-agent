package com.fanruan.pojo.message;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RpcRequest {
    private boolean reply;
    private String ID;
    private boolean binding;
    private String IDtoInvoke;
    private Class ServiceClass;
    private String MethodName;
    private Object[] args;
    private Class[] argTypes;
}
