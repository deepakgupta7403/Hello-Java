package OOPSConcepts.SerializationDeserialization;

import java.io.Serializable;

public class DemoOneModel implements Serializable {
    public int value;
    public String key;

    // Default constructor
    public DemoOneModel(int value, String key) {
        this.value = value;
        this.key = key;
    }
}
