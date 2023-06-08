package ds;

import java.math.BigInteger;

public class ResponseMessage {
    Meta meta;
    Response response;

    public class Meta {
        int code;
        String disclaimer;
    }

    public class Response {
        BigInteger timestamp;
        String date;
        String from;
        String to;
        double amount;
        double value;
    }

}
