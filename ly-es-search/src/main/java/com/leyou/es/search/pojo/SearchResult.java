package com.leyou.es.search.pojo;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 扩展了PageResult<Goods>
 * 扩展的属性List<Category> categories 和 List<Brand> brands
 * 是为了在页面展示过滤商品分类和商品品牌待选项
 * 再扩展一个属性 List<Map<String,String>> specs 用于封装规格参数及其对应的待选项
 */
@Data
public class SearchResult extends PageResult<Goods> {

    private List<Category> categories;//商品分类过滤条件值的待选项

    private List<Brand> brands;      //商品品牌过滤条件值的待选项

    /**
     * 规格参数过滤条件其中Map的key是规格参数的名称,value是规格参数名称对应的可选项值
     * 举例：
     * key:value  即 '内存'：'4GB,6GB,8GB'
     */
    private List<Map<String,Object>> specs;

    public SearchResult() {
    }

    public SearchResult(Long total,
                        Long totalPage,
                        List<Goods> items,
                        List<Category> categories,
                        List<Brand> brands,
                        List<Map<String, Object>> specs) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }

    public SearchResult(Long total,
                        Long totalPage,
                        List<Goods> items,
                        List<Category> categories,
                        List<Brand> brands) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
    }
}