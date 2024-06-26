package assistant.backend.mapper;

import assistant.backend.pojo.ChatMember;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMemberMapper extends BaseMapper<ChatMember> {
}
