package nanoit.kr.resource;

import nanoit.kr.db.DatabaseHandler;
import nanoit.kr.service.SendMessageService;

import java.net.Socket;

public class UserResource {
    private Socket socket;
    private DatabaseHandler databaseHandler;
    private SendMessageService sendMessageService;

    public void UserResource(){
        this.socket = new Socket();
        // main 과 같이 세팅 하도록 함
    }

    public void serve(){

    }

}
