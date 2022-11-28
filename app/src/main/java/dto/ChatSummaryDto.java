package dto;

import java.io.Serializable;

public class ChatSummaryDto implements Serializable {

    public String chatId; /* El id no se muestra */
    public String receiverName;
    public String lastMessage;
    public String lastMessageTimestamp;
    public String receiverProfileImage;
}
