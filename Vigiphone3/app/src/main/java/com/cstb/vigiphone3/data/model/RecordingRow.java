package com.cstb.vigiphone3.data.model;

import com.cstb.vigiphone3.data.database.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * Created by Benjisora on 17/02/2017.
 */
@Table(database = MyDatabase.class)
public class RecordingRow extends BaseModel {

    //region variables
    @PrimaryKey
    @Column
    private long id;

    @Column
    private String model;

    @Column
    private String date;

    @Column
    private long latitude;

    @Column
    private long longitude;

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
    private int value;

    @Column
    private String neighbours;

    @Column
    private float light;

    @Column
    private float proximity;

    @Column
    private SensorThreeComponents accelerometer;

    @Column
    private SensorThreeComponents gyroscope;

    @Column
    private SensorThreeComponents magneticField;
    //endregion

    //region getters/setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
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

    public SensorThreeComponents getAccelerometer() {
        return accelerometer;
    }

    public void setAccelerometer(SensorThreeComponents accelerometer) {
        this.accelerometer = accelerometer;
    }

    public SensorThreeComponents getGyroscope() {
        return gyroscope;
    }

    public void setGyroscope(SensorThreeComponents gyroscope) {
        this.gyroscope = gyroscope;
    }

    public SensorThreeComponents getMagneticField() {
        return magneticField;
    }

    public void setMagneticField(SensorThreeComponents magneticField) {
        this.magneticField = magneticField;
    }
    //endregion

}
