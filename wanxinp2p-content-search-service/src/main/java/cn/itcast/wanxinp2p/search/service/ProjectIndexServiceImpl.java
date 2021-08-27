package cn.itcast.wanxinp2p.search.service;

import cn.itcast.wanxinp2p.api.search.model.ProjectQueryParamsDTO;
import cn.itcast.wanxinp2p.api.transaction.model.ProjectDTO;
import cn.itcast.wanxinp2p.common.domain.PageVO;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProjectIndexServiceImpl implements ProjectIndexService{

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Value("${wanxinp2p.es.index}")
    private String projectIndex;

    @Override
    public PageVO<ProjectDTO> queryProjectIndex(ProjectQueryParamsDTO projectQueryParamsDTO, Integer pageNo, Integer pageSize, String sortBy, String order) {
        //1.创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(projectIndex);
        //2.搜索条件封装
        //2.1创建条件封装对象
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //2.2非空判断并封装条件
        if(StringUtils.isNotBlank(projectQueryParamsDTO.getName())){
            queryBuilder.must(QueryBuilders.termQuery("name",projectQueryParamsDTO.getName()));
        }
        if(projectQueryParamsDTO.getStartPeriod()!=null){
            queryBuilder.must(QueryBuilders.rangeQuery("period").gte(projectQueryParamsDTO.getStartPeriod()));
        }
        if(projectQueryParamsDTO.getEndPeriod()!=null){
            queryBuilder.must(QueryBuilders.rangeQuery("period").lte(projectQueryParamsDTO.getEndPeriod()));
        }
        //3.创建SearchSourceBuilder对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //3.1封装条件
        searchSourceBuilder.query(queryBuilder);
        //3.2设置排序参数
        if(StringUtils.isNotBlank(sortBy)&&StringUtils.isNotBlank(order)){
            if(order.toLowerCase().equals("asc")){
                searchSourceBuilder.sort(sortBy, SortOrder.ASC);
            }
            if(order.toLowerCase().equals("desc")){
                searchSourceBuilder.sort(sortBy, SortOrder.DESC);
            }
        }else{
            searchSourceBuilder.sort("createdate",SortOrder.DESC);
        }
        //3.3.设置分页信息
        searchSourceBuilder.from((pageNo-1)*pageSize);
        searchSourceBuilder.size(pageSize);

        //4.完成封装
        searchRequest.source(searchSourceBuilder);
        //5.执行搜索
        List<ProjectDTO> list = new ArrayList<>();
        PageVO<ProjectDTO> pageVO = new PageVO<>();
        try{
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //6.获取响应结果
            SearchHits hits = searchResponse.getHits();
            long totalHits = hits.getTotalHits().value;//匹配的总记录数
            pageVO.setTotal(totalHits);
            SearchHit[] searchHits = hits.getHits();//获取匹配数据
            //7.循环封装DTO
            for(SearchHit hit:searchHits) {
                ProjectDTO projectDTO = new ProjectDTO();
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Double amount = (Double) sourceAsMap.get("amount");
                String projectstatus = (String)
                        sourceAsMap.get("projectstatus");
                Integer period =
                        Integer.parseInt(sourceAsMap.get("period").toString());
                String name = (String) sourceAsMap.get("name");
                String description = (String) sourceAsMap.get("description");
                BigDecimal annualRat = new BigDecimal(sourceAsMap.get("annualrate").toString());
                Long id = (Long)sourceAsMap.get("id");
                projectDTO.setId(id);
                projectDTO.setAmount(new BigDecimal(amount));
                projectDTO.setProjectStatus(projectstatus);
                projectDTO.setPeriod(period);
                projectDTO.setName(name);
                projectDTO.setDescription(description);
                projectDTO.setAnnualRate(annualRat);
                list.add(projectDTO);
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

        //8.封装为PageVO对象并返回
        pageVO.setContent(list);
        pageVO.setPageSize(pageSize);
        pageVO.setPageNo(pageNo);
        return pageVO;
    }
}
