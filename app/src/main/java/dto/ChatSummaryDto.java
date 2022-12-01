package dto;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Map;

public class ChatSummaryDto implements Serializable {

    @Exclude
    public String chatId;

    @Exclude
    public String receiverName;

    public String receiverUid;
    public String lastMessage;
    public String lastMessageTimestamp;
    public String receiverProfileImage;
    public Map<String, Object> messages;
}
