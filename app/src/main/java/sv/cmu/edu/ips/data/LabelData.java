package sv.cmu.edu.ips.data;

/**
 * Created by sumeet on 11/29/14.
 */
public class LabelData {

    private String roomInfo;
    private String floorInfo;
    private double lat;
    private double lng;

    public String getRoomInfo() {
        return roomInfo;
    }

    public void setRoomInfo(String roomInfo) {
        this.roomInfo = roomInfo;
    }

    public String getFloorInfo() {
        return floorInfo;
    }

    public void setFloorInfo(String floorInfo) {
        this.floorInfo = floorInfo;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public class Orientation{
        private String orientXValue;
        private String orientYValue;
        private String orientZValue;

        public String getOrientXValue() {
            return orientXValue;
        }

        public void setOrientXValue(String orientXValue) {
            this.orientXValue = orientXValue;
        }

        public String getOrientYValue() {
            return orientYValue;
        }

        public void setOrientYValue(String orientYValue) {
            this.orientYValue = orientYValue;
        }

        public String getOrientZValue() {
            return orientZValue;
        }

        public void setOrientZValue(String orientZValue) {
            this.orientZValue = orientZValue;
        }
    }
}
