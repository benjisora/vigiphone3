package com.cstb.vigiphone3.data.model;

import com.cstb.vigiphone3.data.database.MyDatabase;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

/**
 * RecordingRow class, representing a recording entity for a precise time
 */
@Table(database = MyDatabase.class)
public class RecordingRow extends BaseModel implements Serializable {

    //region variables
    @PrimaryKey(autoincrement = true)
    @Column
    @SerializedName("ID")
    private int id;

    @Column
    @SerializedName("TABLE_NAME")
    private String tableName;

    @Column
    @SerializedName("IMEI")
    private String imei;

    @Column
    @SerializedName("MODEL")
    private String model;

    @Column
    @SerializedName("DATE")
    private String date;

    @Column
    @SerializedName("LATITUDE")
    private double latitude;

    @Column
    @SerializedName("LONGITUDE")
    private double longitude;

    @Column
    @SerializedName("CELL_ID")
    private int CID;

    @Column
    @SerializedName("LOCATION_AREA_CODE")
    private int LAC;

    @Column
    @SerializedName("MOBILE_COUNTRY_CODE")
    private int MCC;

    @Column
    @SerializedName("MOBILE_NETWORK_CODE")
    private int MNC;

    @Column
    @SerializedName("NETWORK_TYPE")
    private String type;

    @Column
    @SerializedName("OPERATOR")
    private String name;

    @Column
    @SerializedName("SIGNAL_STRENGTH")
    private int strength;

    @Column
    @SerializedName("NEIGHBOURS")
    private String neighbours;

    @Column
    @SerializedName("LIGHT")
    private float light;

    @Column
    @SerializedName("PROXIMITY")
    private float proximity;

    @Column
    @SerializedName("ACCELEROMETER_X")
    private float accelerometerX;

    @Column
    @SerializedName("ACCELEROMETER_Y")
    private float accelerometerY;

    @Column
    @SerializedName("ACCELEROMETER_Z")
    private float accelerometerZ;

    @Column
    @SerializedName("GYROSCOPE_X")
    private float gyroscopeX;

    @Column
    @SerializedName("GYROSCOPE_Y")
    private float gyroscopeY;

    @Column
    @SerializedName("GYROSCOPE_Z")
    private float gyroscopeZ;

    @Column
    @SerializedName("MAGNETIC_FIELD_X")
    private float magneticFieldX;

    @Column
    @SerializedName("MAGNETIC_FIELD_Y")
    private float magneticFieldY;

    @Column
    @SerializedName("MAGNETIC_FIELD_Z")
    private float magneticFieldZ;

    //endregion

    //region getters/setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getCID() {
        return CID;
    }

    public void setCID(int CID) {
        this.CID = CID;
    }

    public int getLAC() {
        return LAC;
    }

    public void setLAC(int LAC) {
        this.LAC = LAC;
    }

    public int getMCC() {
        return MCC;
    }

    public void setMCC(int MCC) {
        this.MCC = MCC;
    }

    public int getMNC() {
        return MNC;
    }

    public void setMNC(int MNC) {
        this.MNC = MNC;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int value) {
        this.strength = value;
    }

    public String getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(String neighbours) {
        this.neighbours = neighbours;
    }

    public float getLight() {
        return light;
    }

    public void setLight(float light) {
        this.light = light;
    }

    public float getProximity() {
        return proximity;
    }

    public void setProximity(float proximity) {
        this.proximity = proximity;
    }

    public float getAccelerometerX() {
        return accelerometerX;
    }

    public void setAccelerometerX(float accelerometerX) {
        this.accelerometerX = accelerometerX;
    }

    public float getAccelerometerY() {
        return accelerometerY;
    }

    public void setAccelerometerY(float accelerometerY) {
        this.accelerometerY = accelerometerY;
    }

    public float getAccelerometerZ() {
        return accelerometerZ;
    }

    public void setAccelerometerZ(float accelerometerZ) {
        this.accelerometerZ = accelerometerZ;
    }

    public float getGyroscopeX() {
        return gyroscopeX;
    }

    public void setGyroscopeX(float gyroscopeX) {
        this.gyroscopeX = gyroscopeX;
    }

    public float getGyroscopeY() {
        return gyroscopeY;
    }

    public void setGyroscopeY(float gyroscopeY) {
        this.gyroscopeY = gyroscopeY;
    }

    public float getGyroscopeZ() {
        return gyroscopeZ;
    }

    public void setGyroscopeZ(float gyroscopeZ) {
        this.gyroscopeZ = gyroscopeZ;
    }

    public float getMagneticFieldX() {
        return magneticFieldX;
    }

    public void setMagneticFieldX(float magneticFieldX) {
        this.magneticFieldX = magneticFieldX;
    }

    public float getMagneticFieldY() {
        return magneticFieldY;
    }

    public void setMagneticFieldY(float magneticFieldY) {
        this.magneticFieldY = magneticFieldY;
    }

    public float getMagneticFieldZ() {
        return magneticFieldZ;
    }

    public void setMagneticFieldZ(float magneticFieldZ) {
        this.magneticFieldZ = magneticFieldZ;
    }

    //endregion

    /**
     * Writes the RecordingRow as an xml entity
     * @return The string representation of the entity in xml
     */
    public String XMLDescription() {
        return "<table name=\"" + this.getTableName() + "\">\n" +
                "\t<column name=\"ID" + "\">" + this.getImei() + "</column>\n" +
                "\t<column name=\"DATE" + "\">" + this.getDate() + "</column>\n" +
                "\t<column name=\"MODEL" + "\">" + this.getModel() + "</column>\n" +
                "\t<column name=\"LATITUDE" + "\">" + this.getLatitude() + "</column>\n" +
                "\t<column name=\"LONGITUDE" + "\">" + this.getLongitude() + "</column>\n" +
                "\t<column name=\"LOCATION_AREA_CODE" + "\">" + this.getLAC() + "</column>\n" +
                "\t<column name=\"CELL_ID" + "\">" + this.getCID() + "</column>\n" +
                "\t<column name=\"MOBILE_COUNTRY_CODE" + "\">" + this.getMCC() + "</column>\n" +
                "\t<column name=\"MOBILE_NETWORK_CODE" + "\">" + this.getMNC() + "</column>\n" +
                "\t<column name=\"NETWORK_TYPE" + "\">" + this.getType() + "</column>\n" +
                "\t<column name=\"OPERATOR" + "\">" + this.getName() + "</column>\n" +
                "\t<column name=\"SIGNAL_STRENGTH" + "\">" + this.getStrength() + "</column>\n" +
                "\t<column name=\"NEIGHBOURS" + "\">" + this.getNeighbours() + "</column>\n" +
                "\t<column name=\"ACCELEROMETER_X" + "\">" + this.getAccelerometerX() + "</column>\n" +
                "\t<column name=\"ACCELEROMETER_Y" + "\">" + this.getAccelerometerY() + "</column>\n" +
                "\t<column name=\"ACCELEROMETER_Z" + "\">" + this.getAccelerometerZ() + "</column>\n" +
                "\t<column name=\"GYROSCOPE_X" + "\">" + this.getGyroscopeX() + "</column>\n" +
                "\t<column name=\"GYROSCOPE_Y" + "\">" + this.getGyroscopeY() + "</column>\n" +
                "\t<column name=\"GYROSCOPE_Z" + "\">" + this.getGyroscopeZ() + "</column>\n" +
                "\t<column name=\"MAGNETIC_FIELD_X" + "\">" + this.getMagneticFieldX() + "</column>\n" +
                "\t<column name=\"MAGNETIC_FIELD_Y" + "\">" + this.getMagneticFieldY() + "</column>\n" +
                "\t<column name=\"MAGNETIC_FIELD_Z" + "\">" + this.getMagneticFieldZ() + "</column>\n" +
                "\t<column name=\"PROXIMITY" + "\">" + this.getLight() + "</column>\n" +
                "\t<column name=\"LIGHT" + "\">" + this.getProximity() + "</column>\n" +
                "</table>\n";
    }

}
