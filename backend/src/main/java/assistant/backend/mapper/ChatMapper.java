package assistant.backend.mapper;

import assistant.backend.pojo.Chat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ChatMapper extends BaseMapper<Chat> {

    @Select("select nextval(pg_get_serial_sequence('chat', 'id'))")
    Long allocateId();
}
