package com.cstb.vigiphone3.data.model;

import com.cstb.vigiphone3.data.database.MyDatabase;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

/**
 * Emitter class, representing an emitter to display on the map from the emitters database
 */
@Table(database = MyDatabase.class)
public class Emitter extends BaseModel implements Serializable {

    //region variables
    @PrimaryKey(autoincrement = true)
    @Column
    @SerializedName("ID")
    private int id;

    @Column
    @SerializedName("TABLE_NAME")
    private String tableName;

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
    @SerializedName("CARTORADIO_STATION")
    private int cartoradioStation;

    @Column
    @SerializedName("CARTORADIO_SUPPORT")
    private int cartoradioSupport;

    @Column
    @SerializedName("CARTORADIO_ANTENNE")
    private int cartoradioAntenne;

    @Column
    @SerializedName("CARTORADIO_COMSIS")
    private int cartoradioComsis;

    @Column
    @SerializedName("CARTORADIO_BANDE")
    private int cartoradioBande;

    @Column
    @SerializedName("CARTORADIO_EMETTEUR")
    private int cartoradioEmetteur;

    @Column
    @SerializedName("GROUND_ALTITUDE")
    private int groundAltitude;

    @Column
    @SerializedName("SUPPORT_HEIGHT")
    private int supportHeight;

    @Column
    @SerializedName("AZIMUT")
    private float azimut;

    @Column
    @SerializedName("ANTENNA_HEIGHT")
    private int antennaHeight;

    @Column
    @SerializedName("ANTENNA_ALTITUDE")
    private int antennaAltitude;

    @Column
    private double passloss;

    @Column
    private int colorIndex;


    //endregion

    //region getters/setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
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

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getCartoradioStation() {
        return cartoradioStation;
    }

    public void setCartoradioStation(int cartoradioStation) {
        this.cartoradioStation = cartoradioStation;
    }

    public int getCartoradioSupport() {
        return cartoradioSupport;
    }

    public void setCartoradioSupport(int cartoradioSupport) {
        this.cartoradioSupport = cartoradioSupport;
    }

    public int getCartoradioAntenne() {
        return cartoradioAntenne;
    }

    public void setCartoradioAntenne(int cartoradioAntenne) {
        this.cartoradioAntenne = cartoradioAntenne;
    }

    public int getCartoradioComsis() {
        return cartoradioComsis;
    }

    public void setCartoradioComsis(int cartoradioComsis) {
        this.cartoradioComsis = cartoradioComsis;
    }

    public int getCartoradioBande() {
        return cartoradioBande;
    }

    public void setCartoradioBande(int cartoradioBande) {
        this.cartoradioBande = cartoradioBande;
    }

    public int getCartoradioEmetteur() {
        return cartoradioEmetteur;
    }

    public void setCartoradioEmetteur(int cartoradioEmetteur) {
        this.cartoradioEmetteur = cartoradioEmetteur;
    }

    public int getGroundAltitude() {
        return groundAltitude;
    }

    public void setGroundAltitude(int groundAltitude) {
        this.groundAltitude = groundAltitude;
    }

    public int getSupportHeight() {
        return supportHeight;
    }

    public void setSupportHeight(int supportHeight) {
        this.supportHeight = supportHeight;
    }

    public float getAzimut() {
        return azimut;
    }

    public void setAzimut(float azimut) {
        this.azimut = azimut;
    }

    public int getAntennaHeight() {
        return antennaHeight;
    }

    public void setAntennaHeight(int antennaHeight) {
        this.antennaHeight = antennaHeight;
    }

    public int getAntennaAltitude() {
        return antennaAltitude;
    }

    public void setAntennaAltitude(int antennaAltitude) {
        this.antennaAltitude = antennaAltitude;
    }

    public double getPassloss() {
        return passloss;
    }

    public void setPassloss(double passloss) {
        this.passloss = passloss;
    }

    public int getColorIndex() {
        return strength;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }


    //endregion

}
