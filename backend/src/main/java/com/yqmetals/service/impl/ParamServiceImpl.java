package com.yqmetals.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yqmetals.entity.Param;
import com.yqmetals.mapper.ParamMapper;
import com.yqmetals.service.ParamService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParamServiceImpl extends ServiceImpl<ParamMapper, Param> implements ParamService {

    @Override
    public List<Param> listAll() {
        return list();
    }

    @Override
    public void toggleEnabled(Long id) {
        Param param = getById(id);
        if (param != null) {
            param.setEnabled(!param.getEnabled());
            if (!param.getEnabled() && param.getAssignedCategoryIds() != null) {
                param.setAssignedCategoryIds("[]");
            }
            updateById(param);
        }
    }
}
