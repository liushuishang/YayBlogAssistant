package blogAssistant.logic.model.metaweblog;

/**
 * Created by ucs_yuananyun on 2016/8/19.
 */
public class FileData {
    private byte[]  bits;
    private String name;
    private String type;

    public byte[] getBits() {
        return bits;
    }

    public void setBits(byte[] bits) {
        this.bits = bits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
