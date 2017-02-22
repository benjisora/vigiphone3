package com.cstb.vigiphone3.data.model;

import com.cstb.vigiphone3.data.database.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

@Table(database = MyDatabase.class)
public class RecordingRow extends BaseModel {

    //region variables
    @PrimaryKey(autoincrement = true)
    @Column
    private long id;

    @Column
    private String imei;

    @Column
    private String model;

    @Column
    private String date;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @Column
    private int CID;

    @Column
    private int LAC;

    @Column
    private int MCC;

    @Column
    private int MNC;

    @Column
    private String type;

    @Column
    private String name;

    @Column
    private int strength;

    @Column
    private String neighbours;

    @Column
    private float light;

    @Column
    private float proximity;

    @Column
    private float accelerometerX;

    @Column
    private float accelerometerY;

    @Column
    private float accelerometerZ;

    @Column
    private float gyroscopeX;

    @Column
    private float gyroscopeY;

    @Column
    private float gyroscopeZ;

    @Column
    private float magneticFieldX;

    @Column
    private float magneticFieldY;

    @Column
    private float magneticFieldZ;

    //endregion

    //region getters/setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImei() {
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

    public String getDate() {
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

}
