package cn.itcast.wanxinp2p.api.search;

import cn.itcast.wanxinp2p.api.search.model.ProjectQueryParamsDTO;
import cn.itcast.wanxinp2p.api.transaction.model.ProjectDTO;
import cn.itcast.wanxinp2p.common.domain.PageVO;
import cn.itcast.wanxinp2p.common.domain.RestResponse;

/**
* <P>
* 内容检索服务API
* </p>
*/
public interface ContentSearchApi {
    /**
    * 检索标的
    * @param projectQueryParamsDTO
    * @return
    */
    RestResponse<PageVO<ProjectDTO>> queryProjectIndex(ProjectQueryParamsDTO projectQueryParamsDTO,
        Integer pageNo,Integer pageSize,String sortBy,String order);
}