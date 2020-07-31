package com.quan.service;

import com.quan.dao.MessageDao;
import com.quan.pojo.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageDao messageDao;

    public List<Message> queryMessageList(Long toId, Long fromId, Integer page, Integer rows){
        List<Message> list = messageDao.findListByFromAndTo(fromId,toId,page,rows);
        //change status for all message
        for(Message message : list){
            if(message.getStatus().intValue() == 1){
                messageDao.updateMessageState(message.getId(),2);
            }
        }
        return list;
    }
}
