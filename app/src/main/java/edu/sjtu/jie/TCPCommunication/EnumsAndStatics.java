package edu.sjtu.jie.TCPCommunication;

public class EnumsAndStatics {
    // 互发消息类型
    public enum MessageTypes {
        Message_Printer_Status, Message_Update_List, Message_Stop, Message_Shutdown, Message_Update_Period
    }

    // json对象子标签名
    public static final String MESSAGE_TYPE_FOR_JSON = "messageType";
    public static final String MESSAGE_CONTENT_FOR_JSON = "messageContent";
    public static final String MESSAGE_PRINTER_NAME_FOR_JSON = "printerName";

    // 打印机状态信息标签名
    public static final String MESSAGE_STATUS_IMG_FOR_JSON = "messageStatusImg";
    public static final String MESSAGE_STATUS_TEXT_FOR_JSON = "messageStatusText";

    public static MessageTypes getMessageTypeByString(String messageInString) {
        if (messageInString.equals(MessageTypes.Message_Printer_Status.toString()))
            return MessageTypes.Message_Printer_Status;
        if (messageInString.equals(MessageTypes.Message_Update_List.toString()))
            return MessageTypes.Message_Update_List;
        if (messageInString.equals(MessageTypes.Message_Stop.toString()))
            return MessageTypes.Message_Stop;
        if (messageInString.equals(MessageTypes.Message_Shutdown.toString()))
            return MessageTypes.Message_Shutdown;
        if (messageInString.equals(MessageTypes.Message_Update_Period.toString()))
            return MessageTypes.Message_Update_Period;
        return null;

    }
}
