package alphaclo.bluetooth;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class GattAttributes {
    public static String CLIENT_CHARACTERISTIC_CONFIG;
    public static String LEFT_QUADS_MUSCLE_MEASUREMENT;
    public static String RIGHT_QUADS_MUSCLE_MEASUREMENT;
    public static String LEFT_HAMS_MUSCLE_MEASUREMENT;
    public static String RIGHT_HAMS_MUSCLE_MEASUREMENT;
    private static HashMap<String, String> attributes;

    static {
        attributes = new HashMap();
        LEFT_QUADS_MUSCLE_MEASUREMENT = "c286aa05-dc8d-441c-be1a-dc3c50ff354a";
        RIGHT_QUADS_MUSCLE_MEASUREMENT = "c286aa06-dc8d-441c-be1a-dc3c50ff354a";
        LEFT_HAMS_MUSCLE_MEASUREMENT = "";
        RIGHT_HAMS_MUSCLE_MEASUREMENT = "";
        CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Generic Service");
        attributes.put("00001801-0000-1000-8000-00805f9b34fb", "Generic Attribute");
        attributes.put("c2864e85-dc8d-441c-be1a-dc3c50ff354a", "Left Muscle Gauge Measurement");
        attributes.put("c2864e86-dc8d-441c-be1a-dc3c50ff354a", "Right Muscle Gauge Measurement");
        attributes.put("c286aa05-dc8d-441c-be1a-dc3c50ff354a", "Left Muscle Gauge");
        attributes.put("c286aa06-dc8d-441c-be1a-dc3c50ff354a", "Right Muscle Gauge");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
