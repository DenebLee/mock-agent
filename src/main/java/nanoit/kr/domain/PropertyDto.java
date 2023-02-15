package nanoit.kr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.ibatis.session.SqlSession;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

public class PropertyDto {
    private String url;
    private String dbDriver;
    private String dbUsername;
    private String dbPwd;
    private String mapper;
    private String tcpUrl;
    private int port;

    private int userAgent;
    private String userId;
    private String userPwd;
    private String userEmail;
    private String Database;
}
