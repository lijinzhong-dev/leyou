package com.leyou.common.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.test.pojo.User;
import com.leyou.common.utils.JsonUtils;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @Auther: lijinzhong
 * @Date: 2019/9/22
 * @Description: 测试公用方法JsonUtils的测试类
 * @version: 1.0
 */
public class TestJsonUtils {


    /**
     * 序列化：将对象转为字符串
     */
    @Test
    public void test01(){
        User user = new User("张三", 23);
        //toString
        String json = JsonUtils.toString(user);
        System.out.println("json---:"+json);
    }

    /**
     * 反序列化：将字符串转为对象
     */
    @Test
    public void test02(){

        String str="{\"name\":\"zhangsan\",\"age\":23}";

        User user = new User("张三", 23);
        String json = JsonUtils.toString(user);

        //toBean
        User user1 = JsonUtils.toBean(json, User.class);
        System.out.println("user1---:"+user1);
    }
    /**
     * 反序列化：将字符串转为List<E>
     */
    @Test
    public void test03(){
        String json="[20,-10,50,15]";
        //toList
        List<Integer> list = JsonUtils.toList(json, Integer.class);
        System.out.println("list---:"+list);
    }

    /**
     * 反序列化：将字符串转为Map<K, V>
     */
    @Test
    public void test04(){



        String json="{\"name\":\"李四\",\"age\":\"25\"}";
        //toMap
        Map<String, String> map = JsonUtils.toMap(json, String.class, String.class);
        System.out.println("map---:"+map);
    }
    /**
     * 反序列化针对各种情况(包括上面toBean、toList、toMap)
     * 只要根据反序列化后的类型 传入具体TypeReference<T>的T即可
     */
    @Test
    public void test05(){
        /**
         *  toList  List中存放的是多个对象
         *  这时在反序列化时可以考虑转成List<Map<String,String>>
         *  Map存放的是对象的key-value
         */
        String json="[{\"name\":\"王五\",\"age\":\"28\"},{\"name\":\"赵六\",\"age\":\"26\"}]";
        List<Map<String, String>> maps = JsonUtils.nativeRead(json, new TypeReference<List<Map<String, String>>>() {
        });
        System.out.println("maps---:"+maps);
    }
}
