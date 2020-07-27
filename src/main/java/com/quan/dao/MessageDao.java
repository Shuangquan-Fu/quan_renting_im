package com.quan.dao;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.quan.pojo.Message;
import org.bson.types.ObjectId;

import java.util.List;

public interface MessageDao {
    //find the messages from one person to another person
    List<Message> findListByFromAndTo(Long fromId, Long toId, Integer page, Integer rows);

    //find the message by id
    Message findMessageById(String id);

    //update message
    UpdateResult updateMessageState(ObjectId id, Integer status);

    //save new message
    Message saveMessage(Message message);

    //delete Message by id
    DeleteResult deleteMessage(String id);
}
