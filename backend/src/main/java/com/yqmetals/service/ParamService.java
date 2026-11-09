package com.yqmetals.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yqmetals.entity.Param;
import java.util.List;

public interface ParamService extends IService<Param> {
    List<Param> listAll();
    void toggleEnabled(Long id);
}
