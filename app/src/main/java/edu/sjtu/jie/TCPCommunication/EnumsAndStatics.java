package edu.sjtu.jie.TCPCommunication;

public class EnumsAndStatics {
    // 互发消息类型
    public enum MessageTypes {
        PrinterStatus, UpdateList, Stop, Shutdown, UpdatePeriod, Online
    }

    // json对象子标签名
    public static final String MESSAGE_TYPE_FOR_JSON = "messageType";
    public static final String MESSAGE_CONTENT_FOR_JSON = "messageContent";
    public static final String MESSAGE_RECEIVER = "recver";
    public static final String MESSAGE_SENDER = "sender";


    // 打印机状态信息标签名
    public static final String MESSAGE_STATUS_IMG_FOR_JSON = "messageStatusImg";
    public static final String MESSAGE_STATUS_TEXT_FOR_JSON = "messageStatusText";

    // intent 间状态码
    public static final int LIST_REQUEST_CODE=10;
    public static final int LIST_RESULT_CODE=1;
    public static final int PERIOD_REQUEST_CODE=11;
    public static final int PERIOD_RESULT_CODE=2;

    public static MessageTypes getMessageTypeByString(String messageInString) {
        if (messageInString.equals(MessageTypes.PrinterStatus.toString()))
            return MessageTypes.PrinterStatus;
        if (messageInString.equals(MessageTypes.UpdateList.toString()))
            return MessageTypes.UpdateList;
        if (messageInString.equals(MessageTypes.Stop.toString()))
            return MessageTypes.Stop;
        if (messageInString.equals(MessageTypes.Shutdown.toString()))
            return MessageTypes.Shutdown;
        if (messageInString.equals(MessageTypes.UpdatePeriod.toString()))
            return MessageTypes.UpdatePeriod;
        return null;

    }
}
