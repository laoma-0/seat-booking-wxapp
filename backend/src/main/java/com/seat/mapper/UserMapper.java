package com.seat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seat.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM t_user WHERE open_id = #{openid}")
    User selectByOpenId(String openid);
}
