package com.stereo.study.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by liuj-ai on 2018/8/28.
 */
public class DataLine implements Serializable {

    @CsvBindByName(column = "user_id")
    //@CsvBindByPosition(position = 0)
    private long userId;

    @CsvBindByName(column = "item_id")
    //@CsvBindByPosition(position = 1)
    private long itemId;

    @CsvBindByName(column = "behavior_type")
    //@CsvBindByPosition(position = 2)
    private int behaviorType;

    @CsvBindByName(column = "user_geohash")
    //@CsvBindByPosition(position = 3)
    private String userGeoHash;

    @CsvBindByName(column = "item_category")
    //@CsvBindByPosition(position = 4)
    private int itemCategory;

    @CsvBindByName(column = "time")
    @CsvDate("yyyy-MM-dd HH")
    //@CsvBindByPosition(position = 5)
    private Date time;

    public DataLine() {
    }

    public DataLine(long userId, long itemId, int behaviorType, String userGeoHash, int itemCategory, Date time) {
        this.userId = userId;
        this.itemId = itemId;
        this.behaviorType = behaviorType;
        this.userGeoHash = userGeoHash;
        this.itemCategory = itemCategory;
        this.time = time;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public int getBehaviorType() {
        return behaviorType;
    }

    public void setBehaviorType(int behaviorType) {
        this.behaviorType = behaviorType;
    }

    public String getUserGeoHash() {
        return userGeoHash;
    }

    public void setUserGeoHash(String userGeoHash) {
        this.userGeoHash = userGeoHash;
    }

    public int getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(int itemCategory) {
        this.itemCategory = itemCategory;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "DataLine{" +
            "userId=" + userId +
            ", itemId=" + itemId +
            ", behaviorType=" + behaviorType +
            ", userGeoHash='" + userGeoHash + '\'' +
            ", itemCategory=" + itemCategory +
            ", time=" + time +
            '}';
    }
}
