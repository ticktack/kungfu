package ai.llm.qanything.entity;

/**
 * Simple Server Sent Event representation
 */
public class SSE {
    private static final String DONE_DATA = "[DONE]";
    private static final String DONE_EVENT = "message_stop";
    
    private final String data;

    public SSE(String data){
        this.data = data;
    }

    public String getData(){
        return this.data;
    }


    public byte[] toBytes(){
        return String.format("data: %s\n\n", this.data).getBytes();
    }

}