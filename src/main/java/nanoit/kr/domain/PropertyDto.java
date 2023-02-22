package nanoit.kr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PropertyDto {
    private String Dbms;
    private String url;
    private String dbDriver;
    private String dbUsername;
    private String dbPwd;

    private String tcpUrl;
    private int port;
    private int userAgent;
    private String userId;
    private String userPwd;
    private String userEmail;
}
