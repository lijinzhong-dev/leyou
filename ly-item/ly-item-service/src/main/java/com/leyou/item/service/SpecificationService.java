package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyExcception;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Auther: lijinzhong
 * @Date: 2019/10/2
 * @Description: com.leyou.item.service
 * @version: 1.0
 */
@Service
public class SpecificationService {

    @Autowired
    private  SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 根据商品分类cid查询其下的规格组
     * @param cid
     * @return
     */
    public List<SpecGroup> querySpecGroupByCid(Long cid) {
        SpecGroup specGroup =new  SpecGroup();
        specGroup.setCid(cid);

        List<SpecGroup> specGroups = specGroupMapper.select(specGroup);

        if(CollectionUtils.isEmpty(specGroups)){
            throw new LyExcception(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }

        return specGroups;
    }

    public void addSpecGroup(SpecGroup specGroup) {
        specGroupMapper.insert(specGroup);
    }

    public void updateSpecGroup(SpecGroup specGroup) {
        specGroupMapper.updateByPrimaryKeySelective(specGroup);
    }

    public void delSpecGroupById(Long id) {
        specGroupMapper.deleteByPrimaryKey(id);
    }

    public List<SpecParam> querySpecParamByGid(Long gid) {
        //查询条件
        SpecParam specParam =new SpecParam();
        specParam.setGroupId(gid);

        List<SpecParam> specParams = specParamMapper.select(specParam);

        if(CollectionUtils.isEmpty(specParams)){
            throw  new  LyExcception(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return specParams;
    }

    public void updateSpecParamByid(SpecParam specParam) {
        specParamMapper.updateByPrimaryKey(specParam);
    }

    public void addSpecParam(SpecParam specParam) {
        specParamMapper.insert(specParam);
    }

    public void delSpecParam(Long id) {
        specParamMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据分类id查询规格参数
     * @param cid
     * @return
     */
    public List<SpecParam> querySpecParamByCid(Long cid) {

        SpecParam specParam=new SpecParam();
        specParam.setCid(cid);

        List<SpecParam> specParams = specParamMapper.select(specParam);

        if(CollectionUtils.isEmpty(specParams)){
            throw  new  LyExcception(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return specParams;
    }

    /**
     * 根据是否用于前端过滤查询规格参数
     * @param searching
     * @return
     */
    public List<SpecParam> querySpecParamBySearching(Boolean searching) {
        SpecParam specParam=new SpecParam();
        specParam.setSearching(searching);

        List<SpecParam> specParams = specParamMapper.select(specParam);

        if(CollectionUtils.isEmpty(specParams)){
            throw  new  LyExcception(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return specParams;
    }

    public List<SpecParam> querySpecParamByList(Long gid, Long cid, Boolean searching) {
        SpecParam specParam=new SpecParam();
        specParam.setSearching(searching);
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        List<SpecParam> specParams = specParamMapper.select(specParam);

        if(CollectionUtils.isEmpty(specParams)){
            throw  new  LyExcception(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return specParams;
    }

    /**
     * 根据商品分类cid查询其下的规格组及组内的规格参数
     * @param cid
     * @return
     */
    public List<SpecGroup> querySpecGroupAndSpecParamsByCid(Long cid) {
        //根据分类id查询规格组
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> specGroups = specGroupMapper.select(specGroup);

        if(CollectionUtils.isEmpty(specGroups)){
            throw  new  LyExcception(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }

        for (SpecGroup group : specGroups) {
            SpecParam specParam = new SpecParam();
            specParam.setGroupId(group.getId());
            //查询规格组下的规格参数
            List<SpecParam> specParams = specParamMapper.select(specParam);
            if(CollectionUtils.isEmpty(specParams)){
                throw  new  LyExcception(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
            }
            group.setParams(specParams);
        }
        return  specGroups;
    }
}
