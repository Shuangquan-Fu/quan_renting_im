package com.quan.controller;

import com.quan.pojo.Message;
import com.quan.pojo.User;
import com.quan.pojo.UserData;
import com.quan.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("user")
@CrossOrigin
public class UserController {
    @Autowired
    private MessageService messageService;

    //get all friend list
    @GetMapping
    public List<Map<String,Object>> queryUserList(@RequestParam("fromId") Long fromId){
        List<Map<String,Object>> result = new ArrayList<>();
        //mock
        for(Map.Entry<Long, User> userEntry : UserData.USER_MAP.entrySet()){
            Map<String,Object> map = new HashMap<>();
            map.put("id",userEntry.getValue().getId());
            map.put("avatar", "http://itcast-haoke.oss-cnqingdao.aliyuncs.com/images/2018/12/08/15442410962743524.jpg");
            map.put("from_user", fromId);
            map.put("info_type", null);
            map.put("to_user", map.get("id"));
            map.put("username", userEntry.getValue().getUsername());
            //get oldest message
            List<Message>  messages = messageService.queryMessageList(fromId,userEntry.getValue().getId(),1,1);
            if(messages != null && !messages.isEmpty()){
                Message message = messages.get(0);
                map.put("chat_msg", message.getMsg());
                map.put("chat_time", message.getSendDate().getTime());
            }
            result.add(map);
        }
        return result;
    }
}
