package sv.cmu.edu.ips.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sv.cmu.edu.ips.service.dataCollectors.AudioDataCollector;
import sv.cmu.edu.ips.service.dataCollectors.BluetoothDataCollector;
import sv.cmu.edu.ips.service.dataCollectors.MagneticFiledDataCollector;
import sv.cmu.edu.ips.service.dataCollectors.SensorDataCollector;
import sv.cmu.edu.ips.service.dataCollectors.WiFiSensorDataCollector;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DataCollectorsList {

    /**
     * An array of sample (dummy) items.
     */
    public static List<SensorDataCollector> ITEMS = new ArrayList<SensorDataCollector>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, SensorDataCollector> ITEM_MAP = new HashMap<String, SensorDataCollector>();

    static {
        // Add 3 sample items.
        addItem(new AudioDataCollector("1", "Infrared"));
//        addItem(new SoundDataCollector("2", "Sound"));
        addItem(new WiFiSensorDataCollector("3", "WiFi"));
        addItem(new BluetoothDataCollector("4", "Bluetooth"));
        addItem(new MagneticFiledDataCollector("5", "Magnetic Field"));
//        addItem(new OrientationSensorDataCollector("6", "Orientation"));
        //addItem(new LightDataCollector("7", "Light"));
//        addItem(new LocationDataCollector("8", "GPS Location"));

    }

    private static void addItem(SensorDataCollector item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getId(), item);
    }
}
