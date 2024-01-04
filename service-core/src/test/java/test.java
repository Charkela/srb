import com.atguigu.srb.base.util.JwtUtils;
import com.atguigu.srb.core.mapper.DictMapper;
import com.atguigu.srb.core.pojo.entity.Dict;
import org.junit.Test;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

public class test {

        @Resource
        private RedisTemplate redisTemplate;
        @Resource
        private DictMapper dictMapper;
        @org.junit.Test
        public void saveDict(){
            Dict dict = dictMapper.selectById(1);
            //向数据库中存储string类型的键值对, 过期时间5分钟
            redisTemplate.opsForValue().set("dict", dict);
        }

        @Test
    public void checkToken(){
            String token="eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJSCg5y0g0Ndg1S0lFKrShQsjI0NzAxtDAwMzLXUSotTi3yTFGysoAw_RJzU4E6DE2NTE3NLU3NDCyUagEAOnMuRwAAAA.NnLsnucIolg3rm5UMnZFYn0WFZ5t-NyOzSYTMr8MpJk1QVqcykhDaIxCLaXi7cFBudr2Rhat5GXS1fI6YyCWYA";
            boolean b = JwtUtils.checkToken(token);
            System.out.println(b);
        }
    }

