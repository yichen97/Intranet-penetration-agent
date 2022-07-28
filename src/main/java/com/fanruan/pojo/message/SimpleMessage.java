package com.fanruan.pojo.message;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SimpleMessage {
    private String msg;

    public SimpleMessage(String msg){
        this.msg = msg;
    }
}