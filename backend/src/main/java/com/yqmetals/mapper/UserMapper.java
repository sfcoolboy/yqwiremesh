package com.yqmetals.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yqmetals.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Delete("DELETE FROM sys_user WHERE id = #{id}")
    int deletePhysically(@Param("id") Long id);

    @Update("UPDATE inquiry SET assigned_to = NULL, status = 'NEW' WHERE assigned_to = #{id} AND deleted = 0")
    int releaseInquiries(@Param("id") Long id);
}
