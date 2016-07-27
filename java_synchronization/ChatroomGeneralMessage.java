/** Chatroom message types are defined in these file.
*/

import java.io.*;

class ChatroomGeneralMessage implements Serializable {

    int 		type = ChatroomCons.MSG_GENERAL;
    String		source = "C.R. SVR",
			message = null;

    public  ChatroomGeneralMessage(int t, String src, String msg) { type = t; source = src; message = msg; }

    public  String toString() { return source + ": " + message; };
    public  int    getMessageType() { return type; }

}

class PrivateMessage extends ChatroomGeneralMessage {

    String		destination = null;

    public  PrivateMessage(int t, String src, String msg) { super(t, src, msg); }

    public  String toString() { return source + ": " + message; };
    public  int    getMessageType() { return type; }

}


