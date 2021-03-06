package cn.crap.service;

import cn.crap.adapter.Adapter;
import cn.crap.dao.mybatis.ModuleDao;
import cn.crap.enu.LogType;
import cn.crap.enu.TableId;
import cn.crap.framework.MyException;
import cn.crap.model.Log;
import cn.crap.model.Module;
import cn.crap.model.ModuleCriteria;
import cn.crap.query.ModuleQuery;
import cn.crap.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ModuleService extends BaseService<Module, ModuleDao>  implements ILogConst, IConst{
    @Autowired
    private LogService logService;
    private ModuleDao moduleDao;
    @Resource
    public void ModuleDao(ModuleDao moduleDao) {
        this.moduleDao = moduleDao;
        super.setBaseDao(moduleDao, TableId.MODULE);
    }

    /**
     * 更新模块
     * @param model
     * @param needAddLog 是否需要添加日志
     * @return
     */
    public boolean update(Module model, boolean needAddLog) throws MyException{
        if (model == null) {
            return false;
        }
        if (needAddLog) {
            Module dbModule = getById(model.getId());
            Log log = Adapter.getLog(dbModule.getId(), L_MODULE_CHINESE, dbModule.getName(), LogType.UPDATE, dbModule.getClass(), dbModule);
            logService.insert(log);
        }

        model.setProjectId(null);
        if (model.getUrl() == null){
            model.setUrl("");
        }
        return super.update(model);
    }

    public boolean delete(String id) throws MyException{
        Assert.notNull(id, "id can't be null");

        Module dbModule = getById(id);
        Log log = Adapter.getLog(dbModule.getId(), L_MODULE_CHINESE, dbModule.getName(), LogType.DELTET, dbModule.getClass(), dbModule);
        logService.insert(log);

        return super.delete(id);
    }
    /**
     * 查询模块
     * @param query
     * @return
     * @throws MyException
     */
    public List<Module> query(ModuleQuery query) throws MyException {
        Assert.notNull(query);

        Page page = new Page(query);
        ModuleCriteria example = getModuleCriteria(query);
        if (query.getPageSize() != ALL_PAGE_SIZE) {
            example.setLimitStart(page.getStart());
            example.setMaxResults(page.getSize());
        }
        example.setOrderByClause(query.getSort() == null ? TableField.SORT.SEQUENCE_DESC : query.getSort());

        return moduleDao.selectByExample(example);
    }

    /**
     * 查询模块数量
     * @param query
     * @return
     * @throws MyException
     */
    public int count(ModuleQuery query) throws MyException {
        Assert.notNull(query);

        ModuleCriteria example = getModuleCriteria(query);
        return moduleDao.countByExample(example);
    }

    private ModuleCriteria getModuleCriteria(ModuleQuery query) throws MyException {
        ModuleCriteria example = new ModuleCriteria();
        ModuleCriteria.Criteria criteria = example.createCriteria();
        if (query.getName() != null) {
            criteria.andNameLike("%" + query.getName() + "%");
        }
        if (query.getStatus() != null) {
            criteria.andStatusEqualTo(query.getStatus());
        }
        if (query.getUserId() != null) {
            criteria.andUserIdEqualTo(query.getUserId());
        }
        if (query.getProjectId() != null) {
            criteria.andProjectIdEqualTo(query.getProjectId());
        }
        return example;
    }
    
    /**
     * 根据模块id查询分类
     * @param moduleId
     * @return
     */
    public List<String> queryCategoryByModuleId(String moduleId){
        Assert.notNull(moduleId);
        Module module = super.getById(moduleId);
        if (module == null || MyString.isEmpty(module.getCategory())){
            return new ArrayList<>();
        }
        List<String> categories = Arrays.asList(module.getCategory().split(","));
        categories.remove("");
        return categories;
    }
}
